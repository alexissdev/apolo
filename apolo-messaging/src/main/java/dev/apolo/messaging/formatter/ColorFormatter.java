package dev.apolo.messaging.formatter;

import org.bukkit.ChatColor;

public class ColorFormatter {

    public String format(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
