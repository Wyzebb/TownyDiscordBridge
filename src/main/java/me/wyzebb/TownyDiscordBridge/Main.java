package me.wyzebb.TownyDiscordBridge;

import me.wyzebb.TownyDiscordBridge.listeners.DiscordSRVListener;
import me.wyzebb.TownyDiscordBridge.listeners.TDCTownyListener;
import github.scarsz.discordsrv.DiscordSRV;

import java.util.Objects;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    public static Main plugin;
    public FileConfiguration config = getConfig();

    public void onEnable() {

        saveDefaultConfig();
        reloadConfig();
        saveConfig();
        this.config = getConfig();

        Objects.requireNonNull(getCommand("TownyDiscordChat")).setExecutor(new TDCCommand());
        getLogger().info("TownyDiscordChat has been Enabled!");
        plugin = this;

        new TDCTownyListener(plugin);
        DiscordSRV.api.subscribe(new DiscordSRVListener());
    }

    public void onDisable() {
        getLogger().info("TownyDiscordChat has been Disabled!");
    }
}


