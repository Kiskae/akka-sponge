package net.serverpeon.sponge.akka;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.PreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.ProviderExistsException;
import org.spongepowered.api.service.ServiceManager;

@Plugin(id = AkkaSpongePlugin.ID, name = "Akka-Sponge", version = "0.2.0")
public class AkkaSpongePlugin {
    public final static String ID = "akka-sponge";
    private final Logger log;
    private final ServiceManager sm;
    private final AdaptorFactory factory;

    @Inject
    protected AkkaSpongePlugin(Logger log, ServiceManager sm, AdaptorFactory factory) {
        this.log = log;
        this.sm = sm;
        this.factory = factory;
        log.info("Akka-Sponge initialized");
    }

    @Subscribe
    public void onPreInitialization(PreInitializationEvent event) {
        try {
            this.sm.setProvider(this, AkkaService.class, factory);
            this.log.info("AkkaService registered");
        } catch (ProviderExistsException e) {
            this.log.error("Unable to provide AkkaService since it is already registered!", e);
        }
    }
}
