package me.rodrigo.velocitystaffchat;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import org.slf4j.Logger;

@Plugin(
        id = "velocitystaffchat",
        name = "VelocityStaffChat",
        version = "1.0-SNAPSHOT",
        authors = {"Rodrigo R."}
)
public class VelocityStaffChat {

    @Inject
    private Logger logger;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
    }
}
