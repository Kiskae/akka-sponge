package net.serverpeon.sponge.akka;

import akka.actor.ActorContext;
import akka.actor.ActorRefFactory;
import akka.actor.ActorSystem;

import javax.annotation.Nonnull;

/**
 * The SpongeAdaptor adapts an ActorSystem or ActorContext, intercepting the actor creation methods and modifying
 * the properties to force execution onto the Sponge Main Thread.
 * <br />
 * Actors created using the result of calling one of the {@code bindToMain} methods will execute on the
 * Sponge Main Thread using the {@link org.spongepowered.api.service.scheduler.SchedulerService} as tasks of the
 * plugin that was provided when the adaptor was created.
 *
 * @author Kiskae <kiskae@serverpeon.net>
 * @since 2015-07-03
 */
public interface SpongeAdaptor {

    /**
     * The {@link ActorRefFactory} returned by this method allows the creation of sponge actors.
     * Sponge actors perform all their processing on the Main Server Thread.
     * <br />
     * The factory returned by this method will create actors in the user root of {@link #underlyingSystem()}
     *
     * @return A modified {@link ActorRefFactory} which creates sponge actors in the user root.
     */
    @Nonnull
    ActorRefFactory bindToMain();

    /**
     * The {@link ActorRefFactory} returned by this method allows the creation of sponge actors.
     * Sponge actors perform all their processing on the Main Server Thread.
     * <br />
     * The factory returned by this method will create actors in the given context, if that context is a part of
     * {@link #underlyingSystem()}
     *
     * @param context The context in which the actors should be created.
     * @return A modified {@link ActorRefFactory} which creates sponge actors as children of the given context.
     * @throws IllegalArgumentException If the given context belongs to a different ActorSystem than {@link #underlyingSystem()}.
     */
    @Nonnull
    ActorRefFactory bindToMain(final @Nonnull ActorContext context);

    /**
     * @return The underlying actor system for which this adaptor was created.
     */
    @Nonnull
    ActorSystem underlyingSystem();

}
