package net.serverpeon.sponge.akka;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import javax.annotation.Nonnull;

public interface WrappedActorSystem {
    @Nonnull
    ActorRef actorOf(final @Nonnull Props props);

    @Nonnull
    ActorRef actorOf(final @Nonnull Props props, final @Nonnull String name);

    @Nonnull
    ActorRef actorOf(final @Nonnull ActorContext context, final @Nonnull Props props);

    @Nonnull
    ActorRef actorOf(final @Nonnull ActorContext context, final @Nonnull Props props, final @Nonnull String name);

    @Nonnull
    ActorSystem underlyingSystem();
}
