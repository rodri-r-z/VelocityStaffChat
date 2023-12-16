package dev.rodrigo.staffchat.commands;

import dev.rodrigo.staffchat.bungee.StaffChatBungee;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.stream.Collectors;

public class Bungee extends Command {
    private final StaffChatBungee plugin;

    public Bungee(StaffChatBungee plugin) {
        super("sc", "staffchat.use", "staffchat");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        plugin.proxyServer.getScheduler().runAsync(plugin, () -> {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                if (commandSender.hasPermission("staffchat.reload")) {
                    plugin.reload();
                    commandSender.sendMessage(
                            TextComponent.fromLegacyText(
                                    plugin.config.AsString("config_reload")
                                            .replaceAll("&", "§")
                            )
                    );
                } else {
                    commandSender.sendMessage(
                            TextComponent.fromLegacyText(
                                    plugin.config.AsString("no_permission")
                                            .replaceAll("&", "§")
                            )
                    );
                }
                return;
            }
            if (!(commandSender instanceof ProxiedPlayer)) {
                commandSender.sendMessage(
                        TextComponent.fromLegacyText(
                                plugin.config.AsString("error_console")
                                        .replaceAll("&", "§")
                        )
                );
                return;
            }
            final ProxiedPlayer player = (ProxiedPlayer) commandSender;
            if (!player.hasPermission("staffchat.use")) {
                player.sendMessage(
                        TextComponent.fromLegacyText(
                                plugin.config.AsString("no_permission")
                                        .replaceAll("&", "§")
                        )
                );
                return;
            }
            if (args.length > 0) {
                for (ProxiedPlayer p : plugin.proxyServer.getPlayers().stream().filter(a -> a.hasPermission("staffchat.use")).collect(Collectors.toList())) {
                    p.sendMessage(
                            TextComponent.fromLegacyText(
                                    plugin.config.AsString("on_message")
                                            .replaceAll("(?i)\\{player}", player.getName())
                                            .replaceAll("(?i)\\{message}", String.join(" ", args))
                                            .replaceAll("(?i)\\{server}", plugin.formatString(player.getServer().getInfo().getName()))
                                            .replaceAll("&", "§")
                            )
                    );
                }
                return;
            }
            if (plugin.activePlayers.stream().anyMatch(a -> a.toString().equals(player.getUniqueId().toString()))) {
                plugin.activePlayers.removeIf(a -> a.toString().equals(player.getUniqueId().toString()));
                player.sendMessage(
                        TextComponent.fromLegacyText(
                                plugin.config.AsString("on_disable")
                                        .replaceAll("&", "§")
                        )
                );
                return;
            }
            plugin.activePlayers.add(player.getUniqueId());
            player.sendMessage(
                    TextComponent.fromLegacyText(
                            plugin.config.AsString("on_enable")
                                    .replaceAll("&", "§")
                    )
            );
        });
    }
}
