package ru.gksu.mc.zChat;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class zChat extends JavaPlugin {
    private static Chat chat = null;
    private static Permission permission = null;
    private static Plugin plugin;
    private static boolean factionsSupported = false;

    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new zChatListener(), this);
        setupChat();
        setupPermission();
        checkFactions();
        plugin = this;
        getCommand("zchat").setExecutor(new zChatCommand());
    }

    private void setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) chat = chatProvider.getProvider();
    }

    private void setupPermission() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) permission = permissionProvider.getProvider();
    }

    private void checkFactions() {
        if (getConfig().getBoolean("toggles.factions-support")) {
            if (getServer().getPluginManager().getPlugin("Factions") != null) factionsSupported = true;
            else getLogger().warning("Factions plugin not found, disabling Factions support.");
        }
    }

    public static Chat getChat() {
        return chat;
    }

    public static Permission getPermission() {
        return permission;
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    public static boolean isFactionsSupported() {
        return factionsSupported;
    }
}
