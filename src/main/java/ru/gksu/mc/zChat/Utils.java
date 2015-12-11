package ru.gksu.mc.zChat;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public class Utils {
    public static String replacePlayerPlaceholders(Player player, String format) {
        String worldName = player.getWorld().getName();
       
        return format.replace("%prefix", getPlayerPrefix(player))
                .replace("%suffix", getPlayerSuffix(player))
                .replace("%world", worldName)
                .replace("%displayname", player.getDisplayName())
                .replace("%player", player.getName());
    }

    private static String getPlayerPrefix(Player player) {
        String prefix = zChat.getChat().getPlayerPrefix(player);
        if (prefix == null || prefix.equals("")) {
            String group = zChat.getPermission().getPrimaryGroup(player);
            prefix = zChat.getChat().getGroupPrefix(player.getWorld().getName(), group);
            if (prefix == null) {
                prefix = "";
            }
        }
        return prefix;
    }

    private static String getPlayerSuffix(Player player) {
        String suffix = zChat.getChat().getPlayerSuffix(player);
        if (suffix == null || suffix.equals("")) {
            String group = zChat.getPermission().getPrimaryGroup(player);
            suffix = zChat.getChat().getGroupSuffix(player.getWorld().getName(), group);
            if (suffix == null) {
                suffix = "";
            }
        }
        return suffix;
    }

    public static List<Player> getLocalRecipients(Player sender, double range) {
        Location playerLocation = sender.getLocation();
        List<Player> recipients = new LinkedList<Player>();
        double squaredDistance = Math.pow(range, 2);
        for (Player recipient : zChat.getPlugin().getServer().getOnlinePlayers()) {
            // Recipients are not from same world
            if (!recipient.getWorld().equals(sender.getWorld())) continue;
            if (playerLocation.distanceSquared(recipient.getLocation()) > squaredDistance) continue;
            recipients.add(recipient);
        }
        return recipients;
    }

    public static List<Player> getSpies() {
        List<Player> recipients = new LinkedList<Player>();
        for (Player recipient : zChat.getPlugin().getServer().getOnlinePlayers()) {
            if (recipient.hasPermission("zchat.spy")) recipients.add(recipient);
        }
        return recipients;
    }
}
