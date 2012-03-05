package net.rymate.bchatmanager;

import java.io.File;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Ryan
 */
class MeCommand implements CommandExecutor {

    private final bChatManager plugin;
    private final String meFormat;
    private final Functions f;
    private final boolean rangedMode;
    private final double chatRange;
    private Configuration config;


    public MeCommand(File configFile, bChatManager aThis) {
        config = new Configuration(configFile);
        this.meFormat = config.getString("formats.me-format", this.meFormat);
        this.chatRange = config.getDouble("other.chat-range", this.chatRange);
        this.rangedMode = config.getBoolean("toggles.ranged-mode", this.rangedMode);
        this.plugin = aThis;
        this.f = new Functions(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Ya need to type something after it :P");
            return false;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You are not an in-game player!");
            return true;
        }
        Player player = (Player) sender;
        int i;
        StringBuilder me = new StringBuilder();
        for (i = 0; i < args.length; i++) {
            me.append(args[i]);
            me.append(" ");
        }
        String meMessage = me.toString();
        String message = meFormat;
        message = f.colorize(message);

        if (sender.hasPermission("bchatmanager.chat.color")) {
            meMessage = f.colorize(meMessage);
        }

        message = message.replace("%message", meMessage).replace("%displayname", "%1$s");
        message = f.replacePlayerPlaceholders(player, message);
        message = f.replaceTime(message);

        if (rangedMode) {
            List<Player> pl = f.getLocalRecipients(player, message, chatRange);
            for (int j = 0; j < pl.size(); j++) {
                pl.get(j).sendMessage(message);
            }
            sender.sendMessage(message);
            System.out.println(message);
        } else {
            plugin.getServer().broadcastMessage(message);
        }
        return true;
    }
}
