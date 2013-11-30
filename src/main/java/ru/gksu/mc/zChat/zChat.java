package ru.gksu.mc.zChat;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.UPlayer;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedList;
import java.util.List;

public final class zChat extends JavaPlugin {
    public static Chat chat = null;
    public static Permission permission = null;
    public FileConfiguration config;

    public void onEnable() {
        //setup the config
        saveDefaultConfig();
        config = getConfig();

        //Chat listener - can you hear me?
        this.getServer().getPluginManager().registerEvents(new zChatListener(this), this);

        //Vault chat hooks
        setupChat();

        //Vault permission hooks (for primary group search)
        setupPermission();
    }

    /*
     * Code to setup the Chat variable in Vault. Allows me to hook to all the prefix plugins.
     */
    private boolean setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }

        return (chat != null);
    }

    private boolean setupPermission() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

    /*
     *  Begin methods from Functions.java
     */
    public String replacePlayerPlaceholders(Player player, String format) {
        String worldName = player.getWorld().getName();
        if (config.getBoolean("toggles.factions-support")) {
            format = format.replace("%faction", this.getPlayerFaction(player));
        }
        return format.replace("%prefix", this.getPlayerPrefix(player))
                .replace("%suffix", this.getPlayerSuffix(player))
                .replace("%world", worldName)
                .replace("%displayname", player.getDisplayName())
                .replace("%player", player.getName());
    }

    private String getPlayerPrefix(Player player) {
        String prefix = chat.getPlayerPrefix(player);
        if (prefix == null || prefix.equals("")) {
            String group = permission.getPrimaryGroup(player);
            prefix = chat.getGroupPrefix(player.getWorld().getName(), group);
            if (prefix == null) {
                prefix = "";
            }
        }
        return prefix;
    }

    private String getPlayerSuffix(Player player) {
        String suffix = chat.getPlayerSuffix(player);
        if (suffix == null || suffix.equals("")) {
            String group = permission.getPrimaryGroup(player);
            suffix = chat.getGroupSuffix(player.getWorld().getName(), group);
            if (suffix == null) {
                suffix = "";
            }
        }
        return suffix;
    }

    private String getPlayerFaction(Player player) {
        String tag = "";
        try {
            UPlayer fp = UPlayer.get(player);
            Faction faction = fp.getFaction();
            tag = faction.getName();
        } catch (Exception e) {
            System.out.println("Factions plugin not found");
        }
        return tag;
    }

    public List<Player> getLocalRecipients(Player sender, double range) {
        Location playerLocation = sender.getLocation();
        List<Player> recipients = new LinkedList<Player>();
        double squaredDistance = Math.pow(range, 2);
        for (Player recipient : getServer().getOnlinePlayers()) {
            // Recipients are not from same world
            if (!recipient.getWorld().equals(sender.getWorld())) {
                continue;
            }
            if (playerLocation.distanceSquared(recipient.getLocation()) > squaredDistance) {
                continue;
            }
            recipients.add(recipient);
        }
        return recipients;
    }

    public List<Player> getSpies() {
        List<Player> recipients = new LinkedList<Player>();
        for (Player recipient : this.getServer().getOnlinePlayers()) {
            if (recipient.hasPermission("zchat.spy")) {
                recipients.add(recipient);
            }
        }
        return recipients;
    }
}
