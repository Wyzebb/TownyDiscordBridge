package me.wyzebb.TownyDiscordBridge.util;

import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import me.wyzebb.TownyDiscordBridge.TDBManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

import static me.wyzebb.TownyDiscordBridge.TownyDiscordBridge.plugin;

public class IntermediaryMethods {
    public static void syncAllUsersRolesToDiscord() {
        Map<String, UUID> linkedAccounts = DiscordSRV.getPlugin().getAccountLinkManager().getLinkedAccounts();
        linkedAccounts.forEach(SyncMethods::syncUserRolesToDiscord);
    }

    public static void renameNation(String oldName, String newName) {
        TDBManager.rename(oldName, newName, "nation-", ConfigGetters.getNationTextCategoryId(), ConfigGetters.getNationVoiceCategoryId());
    }

    public static void renameTown(String oldName, String newName) {
        TDBManager.rename(oldName, newName, "town-", ConfigGetters.getTownTextCategoryId(), ConfigGetters.getTownVoiceCategoryId());
    }

    public static void deleteRoleAndChannelsFromTown(String townName) {
        TDBManager.deleteRoleAndChannels("town-" + townName, TDBManager.getRole("town-" + townName), ConfigGetters.getTownTextCategoryId(), ConfigGetters.getTownVoiceCategoryId());
    }

    public static void deleteRoleAndChannelsFromNation(String nationName) {
        TDBManager.deleteRoleAndChannels("nation-" + nationName, TDBManager.getRole("nation-" + nationName), ConfigGetters.getNationTextCategoryId(),
                ConfigGetters.getNationVoiceCategoryId());
    }

    public static void removePlayerRole(@NotNull UUID uuid, @NotNull Town town) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        TDBManager.removePlayerRole(offlinePlayer, town);

        plugin.getLogger().warning("HOPEFULLY REMOVED " + Bukkit.getOfflinePlayer(uuid).getName() + " from " + town.getName() + " town");
    }

    public static void removePlayerNationRole(@NotNull UUID uuid, @NotNull Nation nation) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        TDBManager.removePlayerRole(offlinePlayer, nation);

        plugin.getLogger().warning("HOPEFULLY REMOVED " + Bukkit.getOfflinePlayer(uuid).getName() + " from " + nation.getName() + " nation");
    }

    public static void givePlayerRole(@NotNull UUID uuid, @NotNull Nation nation) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        TDBManager.givePlayerRole(offlinePlayer, nation);
    }

    public static void createChannels(Guild guild, Town town, Role role) {
        TDBManager.createChannels(
                guild,
                town.getName(),
                role,
                plugin.config.getBoolean("town.CreateVoiceChannelForRole"),
                plugin.config.getBoolean("town.CreateTextChannelForRole"),
                ConfigGetters.getTownVoiceCategoryId(),
                ConfigGetters.getTownTextCategoryId()
        );
    }

    public static void createChannels(Guild guild, Nation nation, Role role) {
        TDBManager.createChannels(
                guild,
                nation.getName(),
                role,
                plugin.config.getBoolean("nation.CreateVoiceChannelForRole"),
                plugin.config.getBoolean("nation.CreateTextChannelForRole"),
                ConfigGetters.getNationVoiceCategoryId(),
                ConfigGetters.getNationTextCategoryId()
        );
    }
}
