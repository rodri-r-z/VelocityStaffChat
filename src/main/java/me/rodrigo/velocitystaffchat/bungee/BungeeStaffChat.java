package me.rodrigo.velocitystaffchat.bungee;

import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import me.rodrigo.velocitystaffchat.commands.bungee.StaffChat;
import me.rodrigo.velocitystaffchat.lib.MinecraftColorCode;
import me.rodrigo.velocitystaffchat.lib.Parser;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BungeeStaffChat extends Plugin implements Listener {
    private ProxyServer proxy;
    public Parser config;
    public final List<ProxiedPlayer> players = new ArrayList<>();

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        proxy = getProxy();
        Logger logger = getSLF4JLogger();
        final Path dataFolder = getDataFolder().toPath();
        final File DataFolder = dataFolder.toFile();
        if (!DataFolder.exists() && !DataFolder.mkdir()) {
            logger.error("Could not create data folder!");
            return;
        }
        try {
            final InputStream stream = getResourceAsStream("config.yml");
            if (stream == null) {
                logger.error("Could not download the config file!");
                return;
            }
            Files.copy(stream, dataFolder.resolve("config.yml"));
            config = new Parser(dataFolder.resolve("config.yml"));
        } catch (IOException e) {
            logger.error("Could not download the config file due to "+e);
            return;
        }
        proxy.getPluginManager().registerCommand(this, new StaffChat(this));
    }

    @EventHandler
    public void onChatEvent(ChatEvent e) {
        final Connection sender = e.getSender();
        if (!(sender instanceof ProxiedPlayer)) return;
        if (e.getMessage().startsWith("/")) return;
        final ProxiedPlayer player = (ProxiedPlayer) sender;
        if (players.stream().anyMatch(p -> p.getUniqueId().toString().equals(player.getUniqueId().toString()))) {
            e.setCancelled(true);
            BroadcastMessage(e.getMessage());
        }
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent e) {
        players.removeIf(player -> player.getUniqueId().toString().equals(e.getPlayer().getUniqueId().toString()));
    }

    public void BroadcastMessage(String message) {
        proxy.getScheduler().runAsync(this, () -> {
            for (ProxiedPlayer player : proxy.getPlayers().stream().filter(a -> a.hasPermission("staffchat.use")).collect(Collectors.toList())) {
                player.sendMessage(TextComponent.fromLegacyText(MinecraftColorCode.ReplaceAllAmpersands(config.AsString("chat_format").replace("%player%", player.getName()).replace("%message%", message))));
            }
        });
    }
}
