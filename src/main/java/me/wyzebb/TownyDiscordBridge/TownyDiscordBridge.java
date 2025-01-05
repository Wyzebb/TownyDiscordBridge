package me.wyzebb.TownyDiscordBridge;

import me.wyzebb.TownyDiscordBridge.listeners.DiscordSRVListener;
import me.wyzebb.TownyDiscordBridge.listeners.TDCTownyListener;
import github.scarsz.discordsrv.DiscordSRV;

import java.util.Objects;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class TownyDiscordBridge extends JavaPlugin {
    public static TownyDiscordBridge plugin;
    public FileConfiguration config = getConfig();

    public void onEnable() {

        saveDefaultConfig();
        reloadConfig();
        saveConfig();
        this.config = getConfig();

        Objects.requireNonNull(getCommand("TownyDiscordBridge")).setExecutor(new TDCCommand());
        getLogger().info("TownyDiscordBridge has been Enabled!");
        plugin = this;

        new TDCTownyListener(plugin);
        DiscordSRV.api.subscribe(new DiscordSRVListener());
    }

    public void onDisable() {
        getLogger().info("TownyDiscordBridge has been Disabled!");
    }
}


