package me.wyzebb.TownyDiscordBridge.listeners;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import me.wyzebb.TownyDiscordBridge.TownyDiscordBridge;
import me.wyzebb.TownyDiscordBridge.TDBManager;
import com.palmergames.bukkit.towny.event.*;
import com.palmergames.bukkit.towny.event.town.TownKickEvent;
import com.palmergames.bukkit.towny.event.town.TownLeaveEvent;
import com.palmergames.bukkit.towny.object.Resident;

import com.palmergames.bukkit.towny.object.Town;
import me.wyzebb.TownyDiscordBridge.util.IntermediaryMethods;
import me.wyzebb.TownyDiscordBridge.util.SyncMethods;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TownyListener implements Listener {

    @EventHandler
    public void onNewDay(NewDayEvent event) {
        TownyDiscordBridge.plugin.getLogger().warning("NewDayEvent fired!");

        SyncMethods.syncAllTownsAllNations();
        SyncMethods.syncTextChannelCheckAllTownsAllNations();
        SyncMethods.syncVoiceChannelCheckAllTownsAllNations();
    }

    @EventHandler
    public void onPlayerJoinTown(TownAddResidentEvent event) {
        TownyDiscordBridge.plugin.getLogger().warning("TownAddResidentEvent fired!");

        UUID uuid = event.getResident().getUUID();
        Town town = event.getTown();

        TDBManager.givePlayerRole(uuid, town);
    }

    @EventHandler
    public void onPlayerKickedTown(TownKickEvent event) {
        TownyDiscordBridge.plugin.getLogger().warning("TownKickEvent fired!");

        UUID uuid = event.getKickedResident().getUUID();
        Town town = event.getTown();

        IntermediaryMethods.removePlayerRole(uuid, town);

        if (town.hasNation()) {
            IntermediaryMethods.removePlayerNationRole(uuid, Objects.requireNonNull(town.getNationOrNull()));
        }
    }

    @EventHandler
    public void onPlayerLeave(TownLeaveEvent event) {
        TownyDiscordBridge.plugin.getLogger().warning("TownLeaveEvent fired!");

        UUID uuid = event.getResident().getUUID();
        Town town = event.getTown();

        IntermediaryMethods.removePlayerRole(uuid, town);

        if (town.hasNation()) {
            IntermediaryMethods.removePlayerNationRole(uuid, Objects.requireNonNull(town.getNationOrNull()));
        }
    }

    @EventHandler
    public void onPlayerLeaveTown(TownRemoveResidentEvent event) {
        TownyDiscordBridge.plugin.getLogger().warning("TownRemoveResidentEvent fired!");

        UUID uuid = event.getResident().getUUID();
        Town town = event.getTown();

        IntermediaryMethods.removePlayerRole(uuid, town);

        if (town.hasNation()) {
            IntermediaryMethods.removePlayerNationRole(uuid, Objects.requireNonNull(town.getNationOrNull()));
        }
    }

    @EventHandler
    public void onTownJoinNation(NationAddTownEvent event) {
        TownyDiscordBridge.plugin.getLogger().warning("NationAddTownEvent fired!");

        List<Resident> townResidents = event.getTown().getResidents();

        for (Resident townResident : townResidents) {
            TDBManager.givePlayerRole(townResident.getUUID(), event.getNation());
        }
    }

    @EventHandler
    public void onTownLeaveNation(NationRemoveTownEvent event) {
        TownyDiscordBridge.plugin.getLogger().warning("NationRemoveTownEvent fired!");

        for (Resident townResident : event.getTown().getResidents()) {
            IntermediaryMethods.removePlayerNationRole(townResident.getUUID(), event.getNation());
        }
    }

    @EventHandler
    public void onRenameTown(RenameTownEvent event) {
        TownyDiscordBridge.plugin.getLogger().warning("RenameTownEvent fired!");

        final String OLD_NAME = event.getOldName();
        final String NEW_NAME = event.getTown().getName();

        IntermediaryMethods.renameTown(OLD_NAME, NEW_NAME);
    }

    @EventHandler
    public void onRenameNation(RenameNationEvent event) {
        TownyDiscordBridge.plugin.getLogger().warning("RenameNationEvent fired!");

        final String OLD_NAME = event.getOldName();
        final String NEW_NAME = event.getNation().getName();

        IntermediaryMethods.renameNation(OLD_NAME, NEW_NAME);
    }

    @EventHandler
    public void onDeleteTown(DeleteTownEvent event) {
        TownyDiscordBridge.plugin.getLogger().warning("DeleteTownEvent fired!");

        IntermediaryMethods.deleteRoleAndChannelsFromTown(event.getTownName());
    }

    @EventHandler
    public void onDeleteNation(DeleteNationEvent event) {
        TownyDiscordBridge.plugin.getLogger().warning("DeleteNationEvent fired!");

        IntermediaryMethods.deleteRoleAndChannelsFromNation(event.getNationName());
    }
}
