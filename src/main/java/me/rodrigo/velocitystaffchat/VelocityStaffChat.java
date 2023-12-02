package me.rodrigo.velocitystaffchat;

import com.google.inject.Inject;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import me.rodrigo.velocitystaffchat.commands.StaffChat;
import me.rodrigo.velocitystaffchat.lib.MinecraftColorCode;
import me.rodrigo.velocitystaffchat.lib.Parser;
import me.rodrigo.velocitystaffchat.network.FileDownloader;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Plugin(
        id = "velocitystaffchat",
        name = "VelocityStaffChat",
        version = "1.0-SNAPSHOT",
        authors = {"Rodrigo R."}
)
public class VelocityStaffChat {
    private final Path dataFolder;
    private final ProxyServer proxy;
    private final Logger logger;
    public Parser config;
    public final List<Player> players = new ArrayList<>();

    @Inject
    public VelocityStaffChat(ProxyServer proxy, Logger logger, @DataDirectory Path dataFolder) {
        this.dataFolder = dataFolder;
        this.proxy = proxy;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        final File DataFolder = dataFolder.toFile();
        if (!DataFolder.exists() && !DataFolder.mkdir()) {
            logger.error("Could not create data folder!");
            return;
        }
        try {
            FileDownloader.downloadFile("https://raw.githubusercontent.com/rodri-r-z/VelocityStaffChat/main/src/main/resources/config.yml",
                    dataFolder.resolve("config.yml").toString());
            config = new Parser(dataFolder.resolve("config.yml"));
        } catch (IOException e) {
            logger.error("Could not download the config file due to "+e);
            return;
        }
        proxy.getCommandManager().register("sc", new StaffChat(this), "staffchat");
    }

    @Subscribe
    public void onPlayerChat(PlayerChatEvent e) {
        if (players.contains(e.getPlayer())) {
            e.setResult(PlayerChatEvent.ChatResult.denied());
            BroadcastMessage(e.getMessage());
        }
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent e) {
        players.removeIf(player -> player.getUniqueId().toString().equals(e.getPlayer().getUniqueId().toString()));
    }

    public void BroadcastMessage(String message) {
        proxy.getScheduler().buildTask(this, () -> {
            for (Player player : proxy.getAllPlayers().stream().filter(a -> a.hasPermission("staffchat.use")).collect(Collectors.toList())) {
                player.sendMessage(Component.text(MinecraftColorCode.ReplaceAllAmpersands(config.AsString("chat_format").replace("%player%", player.getUsername()).replace("%message%", message))));
            }
        }).schedule();
    }
}
