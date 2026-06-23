package dev.apolo.plugin;

import org.bukkit.command.CommandSender;

public final class PermissionUtil {
    private PermissionUtil() {}

    public static boolean hasPermission(CommandSender sender, String permission) {
        return sender.hasPermission(permission);
    }

    public static boolean hasAnyPermission(CommandSender sender, String... permissions) {
        for (String permission : permissions) {
            if (sender.hasPermission(permission)) return true;
        }
        return false;
    }
}
