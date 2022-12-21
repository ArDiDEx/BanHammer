package fr.ardidex.banhammer.listeners;

import fr.ardidex.banhammer.api.PunishmentAPI;
import fr.ardidex.banhammer.punishments.PunishmentEntry;
import fr.ardidex.banhammer.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.net.InetSocketAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ConnectionListener implements Listener {

    @EventHandler
    public void onPreJoin(AsyncPlayerPreLoginEvent e) {
        PunishmentEntry banned = PunishmentAPI.isBanned(e.getUniqueId());
        if (banned == null) return;
        e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
        StringBuilder builder = new StringBuilder();
        builder.append("§c[You are banned from this server]");
        builder.append("\n\n");
        builder.append("§cBanned by: ").append(banned.getBanner()).append("\n");
        builder.append("§cReason: ").append(banned.getReason()).append("\n");
        Date date = new Date(banned.getStartTime());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String strDate = dateFormat.format(date);
        builder.append("§cBanned on: ").append(strDate).append("\n").append("\n");
        builder.append("§cTime remaining: ").append(TimeUtils.formatTime(banned.getEndTime() - System.currentTimeMillis()));

        e.setKickMessage(builder.toString());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        InetSocketAddress address = e.getPlayer().getAddress();
        if (address == null) return;

        String hostString = address.getHostString();
        List<UUID> ipUsers = PunishmentAPI.getIPUsers(hostString);
        if(ipUsers.size() > 0){
            StringBuilder builder = new StringBuilder("§c"+e.getPlayer().getName() + " is using a known ip: ");
            for (UUID ipUser : ipUsers) {
                builder.append("\n§7- §f").append(Bukkit.getOfflinePlayer(ipUser).getName());
            }
            String s = builder.toString();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if(onlinePlayer.hasPermission("banhammer.alerts"))
                    onlinePlayer.sendMessage(s);
            }
            Bukkit.getConsoleSender().sendMessage(s);
        }
        PunishmentAPI.setLastIp(e.getPlayer().getUniqueId(), hostString);

    }
}
