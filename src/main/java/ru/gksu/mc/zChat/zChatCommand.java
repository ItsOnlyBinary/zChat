package ru.gksu.mc.zChat;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class zChatCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String lbl, String[] args) {
        if (args.length < 1) return false;
        if (args[0].equals("reload")) {
            if (sender.hasPermission("zchat.reload")) {
                zChat.getPlugin().reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "zChat reloaded.");
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
            }
        }
        return true;
    }
}
