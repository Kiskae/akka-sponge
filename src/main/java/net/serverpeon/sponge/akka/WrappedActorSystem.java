package net.serverpeon.sponge.akka;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import org.spongepowered.api.plugin.PluginContainer;

import javax.annotation.Nonnull;

/**
 * Factory for creating actors that execute on the <b>Sponge Server Thread</b>.
 * Ownership of the tasks ({@link org.spongepowered.api.service.scheduler.Task}) is bound to the plugin passed to
 * {@link AkkaService#forPlugin(PluginContainer)} during creation of this object.
 *
 * @author Kiskae <kiskae@serverpeon.net>
 * @since 2015-06-24
 */
public interface WrappedActorSystem {
    /**
     * Wrapper around {@link ActorSystem#actorOf(Props)}.
     * The dispatcher will be set to a custom dispatcher which executes on the <b>Sponge Server Thread</b>.
     * The mailbox will be set to {@link akka.dispatch.SingleConsumerOnlyUnboundedMailbox}.
     *
     * @return an actor that will always execute on the <b>Sponge Server Thread</b>.
     * @throws NullPointerException if any arguments are NULL
     */
    @Nonnull
    ActorRef actorOf(final @Nonnull Props props);

    /**
     * Wrapper around {@link ActorSystem#actorOf(Props, String)}.
     * The dispatcher will be set to a custom dispatcher which executes on the <b>Sponge Server Thread</b>.
     * The mailbox will be set to {@link akka.dispatch.SingleConsumerOnlyUnboundedMailbox}.
     *
     * @return an actor that will always execute on the <b>Sponge Server Thread</b>.
     * @throws NullPointerException if any arguments are NULL
     */
    @Nonnull
    ActorRef actorOf(final @Nonnull Props props, final @Nonnull String name);

    /**
     * Wrapper around {@link ActorContext#actorOf(Props)}.
     * The dispatcher will be set to a custom dispatcher which executes on the <b>Sponge Server Thread</b>.
     * The mailbox will be set to {@link akka.dispatch.SingleConsumerOnlyUnboundedMailbox}.
     *
     * @return an actor that will always execute on the <b>Sponge Server Thread</b>.
     * @throws NullPointerException     if any arguments are NULL
     * @throws IllegalArgumentException if the given context is not a part of {@link #underlyingSystem()}
     */
    @Nonnull
    ActorRef actorOf(final @Nonnull ActorContext context, final @Nonnull Props props);

    /**
     * Wrapper around {@link ActorContext#actorOf(Props, String)}.
     * The dispatcher will be set to a custom dispatcher which executes on the <b>Sponge Server Thread</b>.
     * The mailbox will be set to {@link akka.dispatch.SingleConsumerOnlyUnboundedMailbox}.
     *
     * @return an actor that will always execute on the <b>Sponge Server Thread</b>.
     * @throws NullPointerException     if any arguments are NULL
     * @throws IllegalArgumentException if the given context is not a part of {@link #underlyingSystem()}
     */
    @Nonnull
    ActorRef actorOf(final @Nonnull ActorContext context, final @Nonnull Props props, final @Nonnull String name);

    /**
     * @return the actor system in which this object will spawn actors.
     */
    @Nonnull
    ActorSystem underlyingSystem();
}
