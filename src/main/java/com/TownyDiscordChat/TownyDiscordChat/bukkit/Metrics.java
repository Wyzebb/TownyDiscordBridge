package com.TownyDiscordChat.TownyDiscordChat.bukkit;

import com.TownyDiscordChat.TownyDiscordChat.MetricsBase;
import com.TownyDiscordChat.TownyDiscordChat.charts.CustomChart;
import com.TownyDiscordChat.TownyDiscordChat.json.JsonObjectBuilder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;


public class Metrics {
    private final Plugin plugin;
    private final MetricsBase metricsBase;

    public Metrics(JavaPlugin plugin, int serviceId) {
        this.plugin = (Plugin) plugin;


        File bStatsFolder = new File(plugin.getDataFolder().getParentFile(), "bStats");
        File configFile = new File(bStatsFolder, "config.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        if (!config.isSet("serverUuid")) {
            config.addDefault("enabled", Boolean.valueOf(true));
            config.addDefault("serverUuid", UUID.randomUUID().toString());
            config.addDefault("logFailedRequests", Boolean.valueOf(false));
            config.addDefault("logSentData", Boolean.valueOf(false));
            config.addDefault("logResponseStatusText", Boolean.valueOf(false));


            config.options().header("bStats (https://bStats.org) collects some basic information for plugin authors, like how\nmany people use their plugin and their total player count. It's recommended to keep bStats\nenabled, but if you're not comfortable with this, you can turn this setting off. There is no\nperformance penalty associated with having metrics enabled, and data sent to bStats is fully\nanonymous.")


                    .copyDefaults(true);
            try {
                config.save(configFile);
            } catch (IOException iOException) {
            }
        }


        boolean enabled = config.getBoolean("enabled", true);
        String serverUUID = config.getString("serverUuid");
        boolean logErrors = config.getBoolean("logFailedRequests", false);
        boolean logSentData = config.getBoolean("logSentData", false);
        boolean logResponseStatusText = config.getBoolean("logResponseStatusText", false);


        Objects.requireNonNull(plugin);
        this.metricsBase = new MetricsBase("bukkit", serverUUID, serviceId, enabled, this::appendPlatformData, this::appendServiceData, submitDataTask -> Bukkit.getScheduler().runTask((Plugin) plugin, submitDataTask), plugin::isEnabled, (message, error) -> this.plugin.getLogger().log(Level.WARNING, message, error), message -> this.plugin.getLogger().log(Level.INFO, message), logErrors, logSentData, logResponseStatusText);
    }


    public void shutdown() {
        this.metricsBase.shutdown();
    }


    public void addCustomChart(CustomChart chart) {
        this.metricsBase.addCustomChart(chart);
    }

    private void appendPlatformData(JsonObjectBuilder builder) {
        builder.appendField("playerAmount", getOfflinePlayerAmount());
        builder.appendField("onlineMode", Bukkit.getOnlineMode() ? 1 : 0);
        builder.appendField("bukkitVersion", Bukkit.getVersion());
        builder.appendField("bukkitName", Bukkit.getName());

        builder.appendField("javaVersion", System.getProperty("java.version"));
        builder.appendField("osName", System.getProperty("os.name"));
        builder.appendField("osArch", System.getProperty("os.arch"));
        builder.appendField("osVersion", System.getProperty("os.version"));
        builder.appendField("coreCount", Runtime.getRuntime().availableProcessors());
    }

    private void appendServiceData(JsonObjectBuilder builder) {
        builder.appendField("pluginVersion", this.plugin.getDescription().getVersion());
    }


    private int getOfflinePlayerAmount() {
        try {
            Method onlinePlayersMethod = Class.forName("org.bukkit.Server").getMethod("getOnlinePlayers", new Class[0]);
            return onlinePlayersMethod.getReturnType().equals(Collection.class) ? (
                    (Collection) onlinePlayersMethod.invoke(Bukkit.getServer(), new Object[0])).size() : (
                    (Player[]) onlinePlayersMethod.invoke(Bukkit.getServer(), new Object[0])).length;
        } catch (Exception e) {
            return Bukkit.getOnlinePlayers().size();
        }
    }
}


/* Location:              /home/sugaku/Development/Minecraft/Plugins/TownyDiscordChat/TownyDiscordChat-Build-1.0.7.jar!/com/TownyDiscordChat/TownyDiscordChat/bukkit/Metrics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */