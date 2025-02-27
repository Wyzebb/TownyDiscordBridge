package me.wyzebb.TownyDiscordBridge.listeners;

import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.AccountLinkedEvent;

import java.util.UUID;

import me.wyzebb.TownyDiscordBridge.util.SyncMethods;
import org.bukkit.OfflinePlayer;


public class DiscordSRVListener {
    @Subscribe
    public void accountLinked(AccountLinkedEvent event) {
        OfflinePlayer offlinePlayer = event.getPlayer();

        if (event.getUser().isBot() && !offlinePlayer.hasPlayedBefore()) {
            return;
        }

        String discordId = event.getUser().getId();
        UUID uuid = offlinePlayer.getUniqueId();

        SyncMethods.syncUserRolesToDiscord(discordId, uuid);
    }
}