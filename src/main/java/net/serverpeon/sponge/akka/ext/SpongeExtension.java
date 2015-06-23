package net.serverpeon.sponge.akka.ext;

import akka.actor.AbstractExtensionId;
import akka.actor.ActorSystem;
import akka.actor.ExtendedActorSystem;
import akka.actor.ExtensionIdProvider;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

class SpongeExtension extends AbstractExtensionId<SpongeExtensionImpl>
        implements ExtensionIdProvider {
    private final static SpongeExtension Provider = new SpongeExtension();

    private SpongeExtension() {
        // NOOP
    }

    @Nonnull
    public static SpongeExtensionImpl access(final @Nonnull ActorSystem system) {
        return SpongeExtension.Provider.get(checkNotNull(system, "system == NULL"));
    }

    @Override
    public SpongeExtensionImpl createExtension(ExtendedActorSystem system) {
        return new SpongeExtensionImpl(system);
    }

    @Override
    public SpongeExtension lookup() {
        return SpongeExtension.Provider;
    }
}
