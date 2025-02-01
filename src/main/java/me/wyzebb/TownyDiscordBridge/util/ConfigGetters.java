package me.wyzebb.TownyDiscordBridge.util;

import org.jetbrains.annotations.Nullable;

import static me.wyzebb.TownyDiscordBridge.TownyDiscordBridge.plugin;

public class ConfigGetters {
    @Nullable
    public static String getTownVoiceCategoryId() {
        return plugin.config.getString("town.VoiceCategoryId");
    }

    @Nullable
    public static String getTownTextCategoryId() {
        return plugin.config.getString("town.TextCategoryId");
    }

    @Nullable
    public static String getNationVoiceCategoryId() {
        return plugin.config.getString("nation.VoiceCategoryId");
    }

    @Nullable
    public static String getNationTextCategoryId() {
        return plugin.config.getString("nation.TextCategoryId");
    }
}
