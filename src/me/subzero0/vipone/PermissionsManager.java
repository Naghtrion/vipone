package me.subzero0.vipone;

import java.util.Arrays;
import java.util.List;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class PermissionsManager {

    private Plugin plugin;
    private Chat chat = null;

    public PermissionsManager(final Plugin plugin) {
        this.plugin = plugin;
        setupChat();
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }
        return (chat != null);
    }

    public String getGroup(final Player base) {
        return Main.perms.getPrimaryGroup(base);
    }

    public boolean setGroup(final Player base, final String group) {
        try {
            for (String gr : Main.perms.getPlayerGroups(base)) {
                Main.perms.playerRemove(base, gr);
            }
            Main.perms.playerAdd(base, group);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeGroup(final Player base, final String group) {
        try {
            Main.perms.playerRemove(base, group);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getGroups(final Player base) {
        return Arrays.asList(Main.perms.getPlayerGroups(base));
    }

    public String getPrefix(final Player base) {
        String prefix = "";
        try {
            prefix = chat.getPlayerPrefix(base);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return prefix;
    }

    public String getSuffix(final Player base) {
        String suffix = "";
        try {
            suffix = chat.getPlayerSuffix(base);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return suffix;
    }

    public boolean hasPermission(final Player base, final String node) {
        return Main.perms.has(base, node);
    }
}
