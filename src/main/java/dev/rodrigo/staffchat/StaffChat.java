package dev.rodrigo.staffchat;

import com.google.inject.Inject;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.rodrigo.staffchat.commands.Velocity;
import dev.rodrigo.staffchat.lib.Parser;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Plugin(
    id = "staffchat",
    name = "StaffChat",
    version = "1.6",
    description = "Staff Chat - By Rodrigo R.",
    authors = {"Rodrigo R."}
)
public class StaffChat {
    public final ProxyServer proxyServer;
    public Parser config;
    public final List<UUID> activePlayers = new ArrayList<>();
    private final Path dataDir;
    private final Logger logger;

    @Inject
    public StaffChat(ProxyServer proxyServer, Logger logger, @DataDirectory Path dataDir) {
        this.proxyServer = proxyServer;
        this.dataDir = dataDir;
        this.logger = logger;
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
            logger.info("Plugin loaded!");
        } catch (Exception e) {
            logger.error("Found an error while loading the config file: "+e);
        }

    }

    public void reload() {
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
        } catch (Exception e) {
            logger.error("Found an error while loading the config file: "+e);
        }
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        if (config == null) return;
        proxyServer.getCommandManager().register("sc", new Velocity(this));
        proxyServer.getCommandManager().register("staffchat", new Velocity(this));
    }

    @Subscribe
    public void onPlayerChat(PlayerChatEvent e) {
        if (config == null) return;
        final Player p = e.getPlayer();
        if (!activePlayers.contains(p.getUniqueId())) return;
        e.setResult(PlayerChatEvent.ChatResult.denied());
        proxyServer.getScheduler().buildTask(this, () -> {
            for (Player pt : proxyServer.getAllPlayers().stream().filter(a -> a.hasPermission("staffchat.use")).collect(Collectors.toList())) {
                pt.sendMessage(
                        Component.text(
                                config.AsString("on_message")
                                        .replaceAll("(?i)\\{player}", p.getUsername())
                                        .replaceAll("(?i)\\{message}", e.getMessage())
                                        .replaceAll("&", "§")
                        )
                );
            }
        }).schedule();
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        activePlayers.removeIf(uuid -> uuid.toString().equals(event.getPlayer().getUniqueId().toString()));
    }
}
