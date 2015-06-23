Will add a more comprehensive README later, but the basic usage of the project is:
Add a [dependency injection](https://docs.spongepowered.org/en/plugin/basics/injection.html) point for an 'net.serverpeon.sponge.akka.AkkaSponge' object.
Add 'required-after:akka-sponge' to the `dependencies` field of your '@Plugin' annotation.

**After** the `InitializationEvent`, you can start calling `system()` on the `AkkaSponge` object, this will provide a utility object that wraps the [ActorSystem/ActorContext].actorOf calls.
See [WrappedActorSystem](src/main/java/net/serverpeon/sponge/akka/WrappedActorSystem.java) for more information. Actors created through these objects execute on the primary Sponge server thread, making it safe to perform thread-unsafe actions.

To create normal actors (not bound to the server thread), use the `underlyingSystem()` to retrieve the base ActorSystem and create actors through the normal interface instead.

**Notice:** actors inherit their context from their parents, for this reason actors created through the WrappedActorSystem should not have child actors.
