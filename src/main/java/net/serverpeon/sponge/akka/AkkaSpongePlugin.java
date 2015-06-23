package net.serverpeon.sponge.akka;

import akka.actor.ActorSystem;
import akka.actor.ActorSystemImpl;
import com.google.inject.Inject;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.serverpeon.sponge.akka.ext.ExtensionAccessor;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.InitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.ProviderExistsException;
import org.spongepowered.api.service.ServiceManager;
import org.spongepowered.api.service.command.CommandService;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;
import org.spongepowered.api.util.command.spec.CommandSpec;

import static com.google.common.base.Preconditions.checkNotNull;

@Plugin(id = AkkaSpongePlugin.ID, name = "Akka for Sponge", version = "0.1.0")
public class AkkaSpongePlugin {
    public final static String ID = "akka-sponge";
    private final AkkaService system;
    private final Logger log;
    private final ServiceManager sm;

    @Inject
    protected AkkaSpongePlugin(Game game, Logger log, ServiceManager sm) throws ProviderExistsException {
        this.log = log;
        this.sm = sm;
        this.system = ExtensionAccessor.createService(
                ActorSystem.create("akka-sponge", loadConfig()),
                checkNotNull(game, "game == NULL")
        );
    }

    private static Config loadConfig() {
        final ClassLoader cl = AkkaSpongePlugin.class.getClassLoader();
        return ConfigFactory.load(cl, "akka-sponge/reference").withFallback(ConfigFactory.load(cl));
    }

    @Subscribe
    public void onInitialization(InitializationEvent event) {
        try {
            this.sm.setProvider(this, AkkaService.class, system);
        } catch (ProviderExistsException e) {
            this.log.error("Unable to provide AkkaService since it is already registered!", e);
        }

        this.sm.provide(CommandService.class).get().register(this, createDumpCommand(), "akka-dump");
    }

    private CommandCallable createDumpCommand() {
        return CommandSpec.builder()
                .description(Texts.of("Dump akka actor tree to the console"))
                .permission("akka-sponge.dump")
                .executor(new CommandExecutor() {
                    @Override
                    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
                        final ActorSystem actorSystem = AkkaSpongePlugin.this.system.system();
                        if (actorSystem instanceof ActorSystemImpl) {
                            log.info("Akka actor tree:\n{}", ((ActorSystemImpl) actorSystem).printTree());
                        }
                        return CommandResult.empty();
                    }
                })
                .build();
    }
}
