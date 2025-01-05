package me.wyzebb.TownyDiscordBridge.listeners;

import me.wyzebb.TownyDiscordBridge.TDCManager;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.AccountLinkedEvent;

import java.util.UUID;

import org.bukkit.OfflinePlayer;


public class DiscordSRVListener {
    @Subscribe
    public void accountLinked(AccountLinkedEvent event) {
        OfflinePlayer offlinePlayer = event.getPlayer();

        if (event.getUser().isBot() && !offlinePlayer.hasPlayedBefore()) {
            return;
        }

        String discordId = event.getUser().getId();
        UUID UUID = offlinePlayer.getUniqueId();

        TDCManager.discordUserRoleCheck(discordId, UUID);
    }
}


/* Location:              /home/sugaku/Development/Minecraft/Plugins/TownyDiscordChat/TownyDiscordChat-Build-1.0.7.jar!/com/TownyDiscordChat/TownyDiscordChat/Listeners/TDCDiscordSRVListener.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */