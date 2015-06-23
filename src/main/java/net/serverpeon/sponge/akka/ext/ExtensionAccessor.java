package net.serverpeon.sponge.akka.ext;

import akka.actor.ActorSystem;
import net.serverpeon.sponge.akka.AkkaService;
import org.spongepowered.api.Game;

import javax.annotation.Nonnull;

public class ExtensionAccessor {
    private ExtensionAccessor() {
        throw new AssertionError();
    }

    @Nonnull
    public static AkkaService createService(final @Nonnull ActorSystem system,
                                            final @Nonnull Game game) {
        final SpongeExtensionImpl impl = SpongeExtension.access(system);
        impl.initGame(game);
        return impl;
    }
}
