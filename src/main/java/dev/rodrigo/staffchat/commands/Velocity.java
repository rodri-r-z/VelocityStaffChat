package dev.rodrigo.staffchat.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import dev.rodrigo.staffchat.StaffChat;
import net.kyori.adventure.text.Component;

import java.util.stream.Collectors;

public class Velocity implements SimpleCommand {
    private final StaffChat plugin;

    public Velocity(StaffChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        plugin.proxyServer.getScheduler().buildTask(plugin, () -> {
            final String[] args = invocation.arguments();
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                if (invocation.source().hasPermission("staffchat.reload")) {
                    plugin.reload();
                    invocation.source().sendMessage(
                            Component.text(
                                    plugin.config.AsString("config_reload")
                                            .replaceAll("&", "§")
                            )
                    );
                } else {
                    invocation.source().sendMessage(
                            Component.text(
                                    plugin.config.AsString("no_permission")
                                            .replaceAll("&", "§")
                            )
                    );
                }
                return;
            }
            if (!(invocation.source() instanceof Player)) {
                invocation.source().sendMessage(
                        Component.text(
                                plugin.config.AsString("error_console")
                                        .replaceAll("&", "§")
                        )
                );
                return;
            }
            final Player player = (Player) invocation.source();
            if (!player.hasPermission("staffchat.use")) {
                player.sendMessage(
                        Component.text(
                                plugin.config.AsString("no_permission")
                                        .replaceAll("&", "§")
                        )
                );
                return;
            }

            if (player.getCurrentServer().isEmpty()) {
                throw new RuntimeException("An invalid server was found for player: "+player.getUsername()+": The player is not connected to any server.");
            }


            if (args.length > 0) {
                for (Player p : plugin.proxyServer.getAllPlayers().stream().filter(a -> a.hasPermission("staffchat.use")).collect(Collectors.toList())) {
                    p.sendMessage(
                            Component.text(
                                    plugin.config.AsString("on_message")
                                            .replaceAll("(?i)\\{player}", player.getUsername())
                                            .replaceAll("(?i)\\{message}", String.join(" ", args))
                                            .replaceAll("(?i)\\{server}", plugin.formatString(player.getCurrentServer().get().getServerInfo().getName()))
                                            .replaceAll("&", "§")
                            )
                    );
                }
                return;
            }
            if (plugin.activePlayers.stream().anyMatch(a -> a.toString().equals(player.getUniqueId().toString()))) {
                plugin.activePlayers.removeIf(a -> a.toString().equals(player.getUniqueId().toString()));
                player.sendMessage(
                        Component.text(
                                plugin.config.AsString("on_disable")
                                        .replaceAll("&", "§")
                        )
                );
                return;
            }
            plugin.activePlayers.add(player.getUniqueId());
            player.sendMessage(
                    Component.text(
                            plugin.config.AsString("on_enable")
                                    .replaceAll("&", "§")
                    )
            );
        }).schedule();
    }
}
