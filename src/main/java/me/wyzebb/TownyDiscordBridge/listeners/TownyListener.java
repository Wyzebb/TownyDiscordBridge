package me.wyzebb.TownyDiscordBridge.listeners;

import java.util.List;
import java.util.UUID;

import me.wyzebb.TownyDiscordBridge.TownyDiscordBridge;
import me.wyzebb.TownyDiscordBridge.TDBManager;
import com.palmergames.bukkit.towny.event.*;
import com.palmergames.bukkit.towny.event.town.TownKickEvent;
import com.palmergames.bukkit.towny.event.town.TownLeaveEvent;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;

import com.palmergames.bukkit.towny.object.Town;
import github.scarsz.discordsrv.dependencies.google.common.base.Preconditions;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import github.scarsz.discordsrv.DiscordSRV;

public class TownyListener implements Listener {

    @EventHandler
    public void onNewDay(NewDayEvent event) {
        TownyDiscordBridge.plugin.getLogger().warning("NewDayEvent fired!");

        TDBManager.discordRoleCheckAllTownsAllNations();
        TDBManager.discordTextChannelCheckAllTownsAllNations();
        TDBManager.discordVoiceChannelCheckAllTownsAllNations();
    }

    @EventHandler
    public void onPlayerJoinTown(TownAddResidentEvent event) {
        TownyDiscordBridge.plugin.getLogger().warning("TownAddResidentEvent fired!");

        UUID uuid = event.getResident().getUUID();
        Town town = event.getTown();

        TDBManager.givePlayerRole(uuid, town);

        if (town.hasNation()) {
            Nation nation = null;
            try {
                nation = town.getNation();
            } catch (NotRegisteredException e) {
                e.printStackTrace();
            }
            Preconditions.checkNotNull(nation);
            TDBManager.givePlayerRole(uuid, nation);
        }
    }

    @EventHandler
    public void onPlayerKickedTown(TownKickEvent event) {
        TownyDiscordBridge.plugin.getLogger().warning("TownKickEvent fired!");

        UUID uuid = event.getKickedResident().getUUID();
        Town town = event.getTown();

        Preconditions.checkNotNull(uuid);
        Preconditions.checkNotNull(town);

        TDBManager.removePlayerRole(uuid, town.getNationOrNull(),  town);

        if (town.hasNation()) {
            Nation nation = null;
            try {
                nation = town.getNation();
            } catch (NotRegisteredException e) {
                e.printStackTrace();
            }
            Preconditions.checkNotNull(nation);
            TDBManager.removePlayerRole(uuid, nation, town);
        }
    }

    @EventHandler
    public void onPlayerLeave(TownLeaveEvent event) {
        TownyDiscordBridge.plugin.getLogger().warning("TownLeaveEvent fired!");

        UUID uuid = event.getResident().getUUID();
        Town town = event.getTown();

        Preconditions.checkNotNull(uuid);
        Preconditions.checkNotNull(town);

        TDBManager.removePlayerRole(uuid, town.getNationOrNull(), town);

        if (town.hasNation()) {
            Nation nation = null;
            try {
                nation = town.getNation();
            } catch (NotRegisteredException e) {
                e.printStackTrace();
            }
            Preconditions.checkNotNull(nation);
            TDBManager.removePlayerRole(uuid, nation, town);
        }
    }

    @EventHandler
    public void onPlayerLeaveTown(TownRemoveResidentEvent event) {
        TownyDiscordBridge.plugin.getLogger().warning("TownRemoveResidentEvent fired!");

        UUID uuid = event.getResident().getUUID();
        Town town = event.getTown();

        Preconditions.checkNotNull(uuid);
        Preconditions.checkNotNull(town);

        TDBManager.removePlayerRole(uuid, town.getNationOrNull(), town);

        if (town.hasNation()) {
            Nation nation = null;
            try {
                nation = town.getNation();
            } catch (NotRegisteredException e) {
                e.printStackTrace();
            }
            Preconditions.checkNotNull(nation);
            TDBManager.removePlayerRole(uuid, nation, town);
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
            TDBManager.removePlayerRole(townResident.getUUID(), event.getNation(), event.getTown());
        }
    }

    @EventHandler
    public void onRenameTown(RenameTownEvent event) {
        TownyDiscordBridge.plugin.getLogger().warning("RenameTownEvent fired!");

        final String OLD_NAME = event.getOldName();
        final String NEW_NAME = event.getTown().getName();

        Guild guild = DiscordSRV.getPlugin().getMainGuild();

        TDBManager.renameTown(OLD_NAME, NEW_NAME);
    }

    @EventHandler
    public void onRenameNation(RenameNationEvent event) {
        TownyDiscordBridge.plugin.getLogger().warning("RenameNationEvent fired!");

        final String OLD_NAME = event.getOldName();
        final String NEW_NAME = event.getNation().getName();

        Guild guild = DiscordSRV.getPlugin().getMainGuild();

        TDBManager.renameNation(OLD_NAME, NEW_NAME);
    }

    @EventHandler
    public void onDeleteTown(PreDeleteTownEvent event) throws NotRegisteredException {
        TownyDiscordBridge.plugin.getLogger().warning("DeleteTownEvent fired!");

        Guild guild = DiscordSRV.getPlugin().getMainGuild();

        Nation nation = event.getTown().getNationOrNull();
        if (nation != null)
            for (Resident townResident : event.getTown().getResidents())
                TDBManager.removePlayerRole(townResident.getUUID(), nation, townResident.getTown());

        TDBManager.deleteRoleAndChannelsFromTown(event.getTownName());
    }

    @EventHandler
    public void onDeleteNation(DeleteNationEvent event) {
        TownyDiscordBridge.plugin.getLogger().warning("DeleteNationEvent fired!");

        Guild guild = DiscordSRV.getPlugin().getMainGuild();

        TDBManager.deleteRoleAndChannelsFromNation(event.getNationName());
    }
}
