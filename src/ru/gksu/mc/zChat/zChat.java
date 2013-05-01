package ru.gksu.mc.zChat;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;

public final class zChat extends JavaPlugin {
    public static Chat chat = null;
    public static Permission permission = null;
    private zChatListener listener;
    public YamlConfiguration config;

    public void onEnable() {
        //setup the config
        setupConfig();

        //Chatlistener - can you hear me?
        this.listener = new zChatListener(this);
        this.getServer().getPluginManager().registerEvents(listener, this);

        //Vault chat hooks
        setupChat();
        
        //Vault permission hooks (for primary group search)
        setupPermission();


        System.out.println("[zChat] Enabled!");
    }

    private void setupConfig() {
        File configFile = new File(this.getDataFolder() + File.separator + "config.yml");
        try {
            if (!configFile.exists()) {
                this.saveDefaultConfig();
            }
        } catch (Exception ex) {
            Logger.getLogger(zChat.class.getName()).log(Level.SEVERE, null, ex);
        }
        config = new YamlConfiguration();
        config = YamlConfiguration.loadConfiguration(configFile);

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
    private boolean setupPermission(){
    	RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }

        return (permission != null);
    }

    //
    //  Begin methods from Functions.java
    //
    public String replacePlayerPlaceholders(Player player, String format) {
        String worldName = player.getWorld().getName();
        return format.replace("%prefix", this.getPlayerPrefix(player))
                .replace("%suffix", this.getPlayerSuffix(player))
                .replace("%world", worldName)
                .replace("%player", player.getName())
                .replace("%displayname", player.getDisplayName())
                .replace("%group", chat.getPrimaryGroup(player));
    }
    
    private String getPlayerPrefix(Player player){
    	String prefix = chat.getPlayerPrefix(player);
    	if(prefix == null){
    		String group = permission.getPrimaryGroup(player);
    		prefix = chat.getGroupPrefix(player.getWorld().getName(),group);
    		if(prefix == null){
    			prefix = "";
    		}
    	}
    	return prefix;
    }
    
    private String getPlayerSuffix(Player player){
    	String suffix = chat.getPlayerPrefix(player);
    	if(suffix == null){
    		String group = permission.getPrimaryGroup(player);
    		suffix = chat.getGroupPrefix(player.getWorld().getName(),group);
    		if(suffix == null){
    			suffix = "";
    		}
    	}
    	return suffix;
    }

    public String colorize(String string) {
        if (string == null) {
            return "";
        }
        return string.replaceAll("&([a-z0-9])", "\u00A7$1");
    }

    public List<Player> getLocalRecipients(Player sender, String message, double range) {
        Location playerLocation = sender.getLocation();
        List<Player> recipients = new LinkedList<Player>();
        double squaredDistance = Math.pow(range, 2);
        for (Player recipient : getServer().getOnlinePlayers()) {
            // Recipient are not from same world
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
