package me.rodrigo.velocitystaffchat.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import me.rodrigo.velocitystaffchat.VelocityStaffChat;
import me.rodrigo.velocitystaffchat.lib.MinecraftColorCode;
import net.kyori.adventure.text.Component;


public class StaffChat implements SimpleCommand {
    private final VelocityStaffChat basePlugin;

    public StaffChat(VelocityStaffChat basePlugin) {
        this.basePlugin = basePlugin;
    }


    @Override
    public void execute(Invocation invocation) {
        final CommandSource source = invocation.source();
        if (!(source instanceof Player)) {
            source.sendMessage(Component.text(MinecraftColorCode.ReplaceAllAmpersands(basePlugin.config.AsString("cannot_exec_from_console"))));
            return;
        }
        if (!source.hasPermission("staffchat.use")) {
            source.sendMessage(Component.text(MinecraftColorCode.ReplaceAllAmpersands(basePlugin.config.AsString("no_permission"))));
            return;
        }
        final Player player = (Player) source;
        final String[] args = invocation.arguments();

        if (args.length == 0) {
            if (basePlugin.players.stream().anyMatch(a -> a.getUniqueId().toString().equals(player.getUniqueId().toString()))) {
                basePlugin.players.removeIf(h -> h.getUniqueId().toString().equals(player.getUniqueId().toString()));
                source.sendMessage(Component.text(MinecraftColorCode.ReplaceAllAmpersands(basePlugin.config.AsString("on_disable"))));
            } else {
                basePlugin.players.add(player);
                source.sendMessage(Component.text(MinecraftColorCode.ReplaceAllAmpersands(basePlugin.config.AsString("on_enable"))));
            }
            return;
        }

        basePlugin.BroadcastMessage(String.join(" ", args));
    }
}
