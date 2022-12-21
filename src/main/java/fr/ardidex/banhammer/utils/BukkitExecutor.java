package fr.ardidex.banhammer.utils;

import fr.ardidex.banhammer.BanHammer;
import org.bukkit.scheduler.BukkitRunnable;

public class BukkitExecutor {

    public static void run(Runnable runnable){
        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTask(BanHammer.getInstance());
    }

    public static void run(Runnable runnable, long delay){
        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskLater(BanHammer.getInstance(), delay);
    }
}
