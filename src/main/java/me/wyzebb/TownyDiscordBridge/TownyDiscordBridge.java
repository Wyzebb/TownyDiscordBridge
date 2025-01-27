package me.wyzebb.TownyDiscordBridge;

import me.wyzebb.TownyDiscordBridge.listeners.DiscordSRVListener;
import me.wyzebb.TownyDiscordBridge.listeners.TownyListener;
import github.scarsz.discordsrv.DiscordSRV;

import java.util.Objects;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class TownyDiscordBridge extends JavaPlugin {
    public static TownyDiscordBridge plugin;
    public FileConfiguration config = getConfig();

    public void onEnable() {
        getLogger().info("Plugin started!");
        plugin = this;

        // Config
        saveDefaultConfig();
        reloadConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
        this.config = getConfig();

        // Register listeners
        getServer().getPluginManager().registerEvents(new TownyListener(), this);
        DiscordSRV.api.subscribe(new DiscordSRVListener());

        // Register command
        Objects.requireNonNull(getCommand("townydiscordbridge")).setExecutor(new TDBCommand());
    }

    public void onDisable() {
        getLogger().info("Plugin stopped!");
    }
}


