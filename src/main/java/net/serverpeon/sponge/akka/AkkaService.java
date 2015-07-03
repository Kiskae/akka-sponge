package net.serverpeon.sponge.akka;

import akka.actor.ActorSystem;
import com.google.inject.Module;
import com.typesafe.config.Config;
import org.spongepowered.api.plugin.PluginContainer;

import javax.annotation.Nonnull;

/**
 * AkkaService provides the interface to what is effectively an akka extension.
 * <br />
 * By creating a special dispatcher that executes its work through Sponge's scheduling system, it makes it possible
 * to create actors that interact with the world without violating thread safety.
 *
 * @author Kiskae <kiskae@serverpeon.net>
 * @since 2015-07-03
 */
public interface AkkaService {
    /**
     * Calls {@link #module(ActorSystem)} with {@link #defaultSystem()}
     */
    @Nonnull
    Module module();

    /**
     * Create a Guice module that exports {@link ActorSystem} and {@link SpongeAdaptor}
     * <br />
     * Pass this module to {@link com.google.inject.Injector#createChildInjector(Module...)} to support
     * injection of these objects.
     *
     * @param backingSystem The actor system that will be provided to newly injected objects.
     * @return The Guice module object.
     */
    @Nonnull
    Module module(final @Nonnull ActorSystem backingSystem);

    /**
     * Calls {@link #adaptor(ActorSystem, PluginContainer)} with {@link #defaultSystem()}
     */
    @Nonnull
    SpongeAdaptor adaptor(final @Nonnull PluginContainer pc);

    /**
     * Creates an adaptor that is able to intercept actor creation and bind the resulting actors to the
     * Sponge Server Thread.
     * <br />
     * Sponge actors created through the resulting adaptor will always execute through Sponge's
     * {@link org.spongepowered.api.service.scheduler.SchedulerService} as a task of the given plugin.
     *
     * @param backingSystem System that the actors spawned by the SpongeAdaptor will be a part of.
     * @param pc            Plugin that will act as the owner of the new actors from Sponge's perspective.
     * @return The sponge adaptor for the given (system, plugin) pair.
     */
    @Nonnull
    SpongeAdaptor adaptor(final @Nonnull ActorSystem backingSystem, final @Nonnull PluginContainer pc);

    /**
     * @return An actor system with the name "akka-sponge" and normally resolved configuration.
     */
    @Nonnull
    ActorSystem defaultSystem();

    /**
     * Injects configuration data from 'akka-sponge/reference.conf', this enables a
     * specialized high-performance mailbox for the Sponge actors.
     * <br />
     * If an actor system's config is not patched then the mailbox will not be enabled.
     *
     * @param config The configuration to patch.
     * @return A copy of the configuration with additional settings applied.
     */
    @Nonnull
    Config patchConfig(final @Nonnull Config config);
}
