package me.fixeddev.ezchat.util;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Pattern;

public class ColorReplacement {

    public static final Pattern STRIP_FORMAT_PATTERN = Pattern.compile( "(?i)&[K-ORX]" );
    public static final Pattern STRIP_COLOR_PATTERN = Pattern.compile( "(?i)&[0-9A-F]" );

    private final static Pattern HEX_COLOR_PATTERN = Pattern.compile("&\\[([\\dA-Fa-f])([\\dA-Fa-f])," +
            "([\\dA-Fa-f])([\\dA-Fa-f])," +
            "([\\dA-Fa-f])([\\dA-Fa-f])]");
    private final static Pattern SECONDARY_HEX_COLOR_PATTERN = Pattern.compile("&?#([\\dA-Fa-f]{2})([\\dA-Fa-f]{2})([\\dA-Fa-f]{2})");
    private final static String BUKKIT_HEX_COLOR = ChatColor.COLOR_CHAR + "x" +
            ChatColor.COLOR_CHAR + "$1" +
            ChatColor.COLOR_CHAR + "$2" +
            ChatColor.COLOR_CHAR + "$3" +
            ChatColor.COLOR_CHAR + "$4" +
            ChatColor.COLOR_CHAR + "$5" +
            ChatColor.COLOR_CHAR + "$6";
    private final static String EZ_HEX_COLOR_REPLACEMENT = "&[$1,$2,$3]";

    public static String formatHex(String message) {
        String newMessage = SECONDARY_HEX_COLOR_PATTERN.matcher(message).replaceAll(EZ_HEX_COLOR_REPLACEMENT);

        return HEX_COLOR_PATTERN.matcher(newMessage).replaceAll(BUKKIT_HEX_COLOR);
    }

    public static String color(String message) {
        return color(message, true);
    }

    public static String color(String message, boolean hex) {
        if (hex) {
            message = formatHex(message);
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String stripFormat(String message) {
        return STRIP_FORMAT_PATTERN.matcher(message).replaceAll("");
    }

    public static String stripColor(String message) {
        return STRIP_COLOR_PATTERN.matcher(message).replaceAll("");
    }
}
