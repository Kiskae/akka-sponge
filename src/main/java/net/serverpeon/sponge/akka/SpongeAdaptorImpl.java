package net.serverpeon.sponge.akka;

import akka.ConfigurationException;
import akka.actor.ActorContext;
import akka.actor.ActorRefFactory;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.google.common.reflect.Reflection;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.function.Function;

import static com.google.common.base.Preconditions.*;

class SpongeAdaptorImpl implements SpongeAdaptor {
    private final static String UNMODIFIED_SYSTEM_ERROR =
            "ActorSystem has not been injected by AkkaService.adapt(), unable to create Sponge actor";
    private final static String MAILBOX_TYPE = "internal-akka-sponge-mailbox";
    private final String dispatcherId;
    private final ActorSystem actorSystem;

    public SpongeAdaptorImpl(final @Nonnull String dispatcherId, final @Nonnull ActorSystem actorSystem) {
        this.dispatcherId = dispatcherId;
        this.actorSystem = actorSystem;
    }

    @Nonnull
    @Override
    public ActorRefFactory bindToMain() {
        return this.adaptRefFactory(this.actorSystem, hasMailboxRegistered(this.actorSystem));
    }

    @Nonnull
    @Override
    public ActorRefFactory bindToMain(final @Nonnull ActorContext context) {
        checkArgument(isModified(checkNotNull(context, "context == NULL").system()), UNMODIFIED_SYSTEM_ERROR);
        return this.adaptRefFactory(context, hasMailboxRegistered(context.system()));
    }

    @Nonnull
    @Override
    public ActorSystem underlyingSystem() {
        return this.actorSystem;
    }

    private boolean hasMailboxRegistered(ActorSystem system) {
        try {
            return system.mailboxes().lookup(MAILBOX_TYPE) != null;
        } catch (ConfigurationException ignored) {
            //Badly documented in Akka, seemingly only way to detect missing mailbox
            return false;
        }
    }

    private boolean isModified(ActorSystem system) {
        return system.dispatchers().hasDispatcher(this.dispatcherId);
    }

    private ActorRefFactory adaptRefFactory(@Nonnull ActorRefFactory factory, boolean mailboxEnabled) {
        return Reflection.newProxy(ActorRefFactory.class, new PropsInjector(
                factory,
                createPropsAdaptor(mailboxEnabled)
        ));
    }

    private Function<Props, Props> createPropsAdaptor(boolean mailboxEnabled) {
        if (mailboxEnabled) {
            return new Function<Props, Props>() {
                @Override
                public Props apply(Props props) {
                    return props.withDispatcher(dispatcherId).withMailbox(MAILBOX_TYPE);
                }
            };
        } else {
            return new Function<Props, Props>() {
                @Override
                public Props apply(Props props) {
                    return props.withDispatcher(dispatcherId);
                }
            };
        }
    }

    private static class PropsInjector implements InvocationHandler {
        static {
            // Sanity check the ActorRefFactory interface
            for (final Method m : ActorRefFactory.class.getMethods()) {
                if ("actorOf".equals(m.getName())) {
                    final Parameter[] params = m.getParameters();
                    checkState(params.length > 0, "ActorRefFactory assertion failed - no parameters");
                    checkState(
                            Props.class.isAssignableFrom(params[0].getType()),
                            "ActorRefFactory assertion failed - first parameter not Props"
                    );
                }
            }
        }

        private final ActorRefFactory backingFactory;
        private final Function<Props, Props> propsAdaptor;

        public PropsInjector(ActorRefFactory backingFactory, Function<Props, Props> propsAdaptor) {
            this.backingFactory = backingFactory;
            this.propsAdaptor = propsAdaptor;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("actorOf".endsWith(method.getName()) && args[0] != null) {
                args[0] = this.propsAdaptor.apply((Props) args[0]);
            }
            return method.invoke(this.backingFactory, args);
        }
    }
}
