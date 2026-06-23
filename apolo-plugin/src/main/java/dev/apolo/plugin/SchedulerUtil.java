package dev.apolo.plugin;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public final class SchedulerUtil {
    private SchedulerUtil() {}

    public static void sync(Plugin plugin, Runnable task) {
        plugin.getServer().getScheduler().runTask(plugin, task);
    }

    public static void async(Plugin plugin, Runnable task) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, task);
    }

    public static BukkitTask syncDelayed(Plugin plugin, Runnable task, long delayTicks) {
        return plugin.getServer().getScheduler().runTaskLater(plugin, task, delayTicks);
    }

    public static BukkitTask asyncDelayed(Plugin plugin, Runnable task, long delayTicks) {
        return plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, task, delayTicks);
    }

    public static BukkitTask repeat(Plugin plugin, Runnable task, long delayTicks, long periodTicks) {
        return plugin.getServer().getScheduler().runTaskTimer(plugin, task, delayTicks, periodTicks);
    }
}
