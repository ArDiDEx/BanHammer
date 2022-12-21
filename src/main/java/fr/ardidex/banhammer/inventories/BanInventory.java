package fr.ardidex.banhammer.inventories;

import fr.ardidex.banhammer.BanHammer;
import fr.ardidex.banhammer.api.PunishmentAPI;
import fr.ardidex.banhammer.settings.PredefinedPunishment;
import fr.mrmicky.fastinv.FastInv;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.stream.IntStream;

public class BanInventory extends FastInv {
    public BanInventory(OfflinePlayer target) {
        super(54, "§cBan > " + target.getName());
        int index = 0;
        int[] interior = getInterior();
        for (PredefinedPunishment predefinedPunishment : BanHammer.getInstance().getSettings().getPredefinedPunishments()) {
            ItemStack itemStack = new ItemStack(predefinedPunishment.getMaterial());
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta == null) continue;
            itemMeta.setLore(predefinedPunishment.getLore());
            itemStack.setItemMeta(itemMeta);
            setItem(interior[index], itemStack, (event) -> {
                Player player = (Player) event.getWhoClicked();
                switch (predefinedPunishment.getType()){
                    case BAN -> {
                        if(!player.hasPermission("banhammer.ban")){
                            player.sendMessage("§cYou don't have the permission to perform this action.");
                            return;
                        }
                        PunishmentAPI.banPlayer(target, player, predefinedPunishment.getTime(), predefinedPunishment.getTitle(), success -> {
                            if(success){
                                player.sendMessage("§c"+target.getName()+" has been banned for "+predefinedPunishment.getTitle()+".");
                                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1f,1f);
                            }else {
                                player.sendMessage("§cCould not ban " + target.getName() + ". The error has been logged.");
                                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f,1f);
                            }
                            player.closeInventory();
                        });
                    }
                    case KICK -> {
                        if(!player.hasPermission("banhammer.kick")){
                            player.sendMessage("§cYou don't have the permission to perform this action.");
                            return;
                        }
                        PunishmentAPI.kickPlayer(target, player, predefinedPunishment.getTitle(), success -> {
                            if(success){
                                player.sendMessage("§c"+target.getName()+" has been kicked for "+predefinedPunishment.getTitle()+".");
                                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1f,1f);
                            }else {
                                player.sendMessage("§cCould not kick " + target.getName() + ". The error has been logged.");
                                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f,1f);
                            }
                            player.closeInventory();
                        });
                    }
                }
            });
            index++;

        }
    }

    public int[] getInterior() {
        int size = getInventory().getSize();
        return IntStream.range(0, size).filter(i -> i > 8 && i < size - 9 && i % 9 != 0 || (i - 8) % 9 != 0).toArray();
    }

}
