package fr.ardidex.banhammer.commands;

import fr.ardidex.banhammer.api.PunishmentAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class KickCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage("§cUsage: /history <username/uuid> [reason>");
            return true;
        }
        Player player = Bukkit.getPlayer(args[0]);


        if (player == null) {
            sender.sendMessage("§cThis player is not online.");
            return true;
        }
        String reason = "";
        if (args.length > 1) {
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                builder.append(args[i]).append(" ");
            }
            reason = builder.toString();
        }

        PunishmentAPI.kickPlayer(player, sender, reason);
        return false;
    }
}
