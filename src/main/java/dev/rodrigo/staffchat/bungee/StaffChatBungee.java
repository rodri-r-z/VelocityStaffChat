package dev.rodrigo.staffchat.bungee;

import dev.rodrigo.staffchat.commands.Bungee;
import dev.rodrigo.staffchat.lib.Parser;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class StaffChatBungee extends Plugin implements Listener {
    public ProxyServer proxyServer;
    public Parser config;
    public final List<UUID> activePlayers = new ArrayList<>();

    public void reload() {
        try {
            if (!getDataFolder().exists() && !getDataFolder().mkdirs()) {
                throw new Exception("Failed to create data folder");
            }
            if (!getDataFolder().toPath().resolve("config.yml").toFile().exists()) {
                final InputStream is = this.getClass().getClassLoader().getResourceAsStream("config.yml");
                if (is == null) {
                    throw new Exception("Failed to load config.yml");
                }
                Files.copy(is, getDataFolder().toPath().resolve("config.yml"));
            }
            this.config = new Parser(getDataFolder().toPath().resolve("config.yml"));
        } catch (Exception e) {
            getLogger().severe("Found an error while loading the config file: "+e);
        }
    }

    @Override
    public void onEnable() {
        this.proxyServer = getProxy();
        final Logger logger = getLogger();
        final Path dataDir = getDataFolder().toPath();
        try {
            if (!dataDir.toFile().exists() && !dataDir.toFile().mkdirs()) {
                throw new Exception("Failed to create data folder");
            }
            if (!dataDir.resolve("config.yml").toFile().exists()) {
                final InputStream is = this.getClass().getClassLoader().getResourceAsStream("config.yml");
                if (is == null) {
                    throw new Exception("Failed to load config.yml");
                }
                Files.copy(is, dataDir.resolve("config.yml"));
            }
            this.config = new Parser(dataDir.resolve("config.yml"));
            proxyServer.getPluginManager().registerCommand(this, new Bungee(this));
            logger.info("Plugin loaded!");
        } catch (Exception e) {
            logger.severe("Found an error while loading the config file: "+e);
        }
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        activePlayers.removeIf(uuid -> uuid.toString().equals(event.getPlayer().getUniqueId().toString()));
    }
}
