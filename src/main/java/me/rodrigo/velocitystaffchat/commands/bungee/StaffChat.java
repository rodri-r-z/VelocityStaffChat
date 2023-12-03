package me.rodrigo.velocitystaffchat.commands.bungee;

import me.rodrigo.velocitystaffchat.bungee.BungeeStaffChat;
import me.rodrigo.velocitystaffchat.lib.MinecraftColorCode;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class StaffChat extends Command {

    private final BungeeStaffChat basePlugin;

    public StaffChat(BungeeStaffChat basePlugin) {
        super("staffchat", "", "sc");
        this.basePlugin = basePlugin;
    }


    @Override
    public void execute(CommandSender source, String[] args) {
        if (!(source instanceof ProxiedPlayer)) {
            source.sendMessage(TextComponent.fromLegacyText(MinecraftColorCode.ReplaceAllAmpersands(basePlugin.config.AsString("cannot_exec_from_console"))));
            return;
        }
        if (!source.hasPermission("staffchat.use")) {
            source.sendMessage(TextComponent.fromLegacyText(MinecraftColorCode.ReplaceAllAmpersands(basePlugin.config.AsString("no_permission"))));
            return;
        }
        final ProxiedPlayer player = (ProxiedPlayer) source;

        if (args.length == 0) {
            if (basePlugin.players.stream().anyMatch(a -> a.getUniqueId().toString().equals(player.getUniqueId().toString()))) {
                basePlugin.players.removeIf(h -> h.getUniqueId().toString().equals(player.getUniqueId().toString()));
                source.sendMessage(TextComponent.fromLegacyText(MinecraftColorCode.ReplaceAllAmpersands(basePlugin.config.AsString("on_disable"))));
            } else {
                basePlugin.players.add(player);
                source.sendMessage(TextComponent.fromLegacyText(MinecraftColorCode.ReplaceAllAmpersands(basePlugin.config.AsString("on_enable"))));
            }
            return;
        }

        basePlugin.BroadcastMessage(String.join(" ", args));
    }
}