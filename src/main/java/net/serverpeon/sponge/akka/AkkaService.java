package net.serverpeon.sponge.akka;

import akka.actor.ActorSystem;
import org.spongepowered.api.plugin.PluginContainer;

import javax.annotation.Nonnull;

public interface AkkaService {
    @Nonnull
    WrappedActorSystem forPlugin(final @Nonnull PluginContainer container);

    @Nonnull
    ActorSystem system();

    @Nonnull
    AkkaService adapt(final @Nonnull ActorSystem system);
}
