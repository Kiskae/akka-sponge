package net.serverpeon.sponge.akka.util;

import akka.actor.Actor;
import akka.actor.IndirectActorProducer;
import akka.actor.Props;
import com.google.inject.Injector;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Utility object for Guice-based actor dependency injection
 *
 * @author Kiskae <kiskae@serverpeon.net>
 * @since 2015-07-03
 */
@Singleton
public class GuiceActorFactory {
    private final Injector injector;

    @Inject
    GuiceActorFactory(Injector injector) {
        this.injector = injector;
    }

    /**
     * Creates {@link Props} instance for the given actorClass that will instantiate the actor through
     * Dependency Injection.
     *
     * @param actorClass Class of the actor that needs to be instantiated.
     * @return Props for the given actor class.
     */
    public Props props(Class<? extends Actor> actorClass) {
        return Props.create(InjectedActorProducer.class, this.injector, actorClass);
    }

    private static class InjectedActorProducer<T extends Actor> implements IndirectActorProducer {
        private final Injector injector;
        private final Class<T> actorClass;

        public InjectedActorProducer(Injector injector, Class<T> actorClass) {
            this.injector = injector;
            this.actorClass = actorClass;
        }

        @Override
        public Actor produce() {
            return this.injector.getInstance(this.actorClass);
        }

        @Override
        public Class<? extends Actor> actorClass() {
            return this.actorClass;
        }
    }
}
