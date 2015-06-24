package net.serverpeon.sponge.akka;

import akka.actor.ActorSystem;
import org.spongepowered.api.plugin.PluginContainer;

import javax.annotation.Nonnull;

/**
 * Service to allow the creation of {@link WrappedActorSystem}
 *
 * @author Kiskae <kiskae@serverpeon.net>
 * @since 2015-06-24
 */
public interface AkkaService {
    /**
     * Create a wrapped actor system for the given plugin.
     *
     * @param container the plugin that will act as owner of the actors spawned by the resulting wrapper.
     * @return A wrapper to spawn sponge actors
     * @throws NullPointerException if container is NULL
     */
    @Nonnull
    WrappedActorSystem forPlugin(final @Nonnull PluginContainer container);

    /**
     * @return the internal actor system
     */
    @Nonnull
    ActorSystem system();

    /**
     * Adapt a secondary actor system to allow for the use of sponge actors.
     *
     * @param system the system to adapt
     * @return A service wrapper around the adapted system
     * @throws NullPointerException if system is NULL
     */
    @Nonnull
    AkkaService adapt(final @Nonnull ActorSystem system);
}
