package net.serverpeon.sponge.akka.ext;

import akka.actor.ActorSystem;
import org.spongepowered.api.Game;
import org.spongepowered.api.plugin.PluginContainer;

import javax.annotation.Nonnull;

public class ExtensionAccessor {
    private ExtensionAccessor() {
        throw new AssertionError();
    }

    @Nonnull
    public static String dispatcherIdFor(final @Nonnull ActorSystem system,
                                         final @Nonnull Game game,
                                         final @Nonnull PluginContainer pc) {
        return SpongeExtension.access(system).getDispatcherId(pc, game);
    }
}
