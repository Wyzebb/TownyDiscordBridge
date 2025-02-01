package me.wyzebb.TownyDiscordBridge.util;

import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import me.wyzebb.TownyDiscordBridge.TDBManager;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SimpleGetters {
    @Nullable
    public static String getLinkedId(@NotNull OfflinePlayer offlinePlayer) {
        return DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(offlinePlayer.getUniqueId());
    }

    @Nullable
    public static Member getMember(@NotNull String id) {
        return DiscordSRV.getPlugin().getMainGuild().getMemberById(id);
    }

    @Nullable
    public static Role getRole(@NotNull Town town) {
        return TDBManager.getRole("town-" + town.getName());
    }

    @Nullable
    public static Role getRole(@NotNull Nation nation) {
        return TDBManager.getRole("nation-" + nation.getName());
    }
}
