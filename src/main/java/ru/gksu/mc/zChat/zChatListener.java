package ru.gksu.mc.zChat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class zChatListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();

        String message = zChat.getPlugin().getConfig().getString("formats.message-format");
        String chatMessage = event.getMessage();
        chatMessage = chatMessage.replace("%", "%%");

        if (chatMessage == null) return;
        boolean localChat = zChat.getPlugin().getConfig().getBoolean("toggles.ranged-mode");

        if (localChat) {
            if (!(chatMessage.startsWith("!"))) {
                message = zChat.getPlugin().getConfig().getString("formats.local-message-format");
                double range = zChat.getPlugin().getConfig().getDouble("other.chat-range");
                event.getRecipients().clear();
                event.getRecipients().addAll(Utils.getLocalRecipients(player, range));
                event.getRecipients().addAll(Utils.getSpies());
            } else {
                chatMessage = chatMessage.replaceFirst("!", "");
            }
        }

        message = Utils.replacePlayerPlaceholders(player, message);
        message = ChatColor.translateAlternateColorCodes('&', message);

        if (player.hasPermission("zchat.color")) {
            chatMessage = ChatColor.translateAlternateColorCodes('&', chatMessage);
        }

        message = message.replace("%message", chatMessage);
        event.setFormat(message);
        event.setMessage(chatMessage);
    }
}
