package net.serverpeon.sponge.akka.ext;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import net.serverpeon.sponge.akka.WrappedActorSystem;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

class SpongeActorSystem implements WrappedActorSystem {
    private final static String UNMODIFIED_SYSTEM_ERROR =
            "ActorSystem has not been injected by AkkaService.adapt(), unable to create Sponge actor";
    private final static String MAILBOX_TYPE = "akka-sponge-mailbox";
    private final ActorSystem system;
    private final String dispatcherId;

    public SpongeActorSystem(final @Nonnull ActorSystem system,
                             final @Nonnull String dispatcherId) {
        this.system = system;
        this.dispatcherId = dispatcherId;
    }

    @Nonnull
    @Override
    public ActorRef actorOf(@Nonnull Props props) {
        return system.actorOf(adapt(checkNotNull(props, "props == NULL")));
    }

    @Nonnull
    @Override
    public ActorRef actorOf(@Nonnull Props props, @Nonnull String name) {
        return system.actorOf(adapt(checkNotNull(props, "props == NULL")),
                checkNotNull(name, "name == NULL"));
    }

    @Nonnull
    @Override
    public ActorRef actorOf(@Nonnull ActorContext context, @Nonnull Props props) {
        checkArgument(isModified(checkNotNull(context, "context == NULL")), UNMODIFIED_SYSTEM_ERROR);

        return context.actorOf(adapt(checkNotNull(props, "props == NULL")));
    }

    @Nonnull
    @Override
    public ActorRef actorOf(@Nonnull ActorContext context, @Nonnull Props props, @Nonnull String name) {
        checkArgument(isModified(checkNotNull(context, "context == NULL")), UNMODIFIED_SYSTEM_ERROR);

        return context.actorOf(adapt(checkNotNull(props, "props == NULL")),
                checkNotNull(name, "name == NULL"));
    }

    private boolean isModified(ActorContext context) {
        return context.system().dispatchers().hasDispatcher(dispatcherId);
    }

    private Props adapt(Props base) {
        return base.withDispatcher(dispatcherId).withMailbox(MAILBOX_TYPE);
    }

    @Nonnull
    @Override
    public ActorSystem underlyingSystem() {
        return this.system;
    }
}
