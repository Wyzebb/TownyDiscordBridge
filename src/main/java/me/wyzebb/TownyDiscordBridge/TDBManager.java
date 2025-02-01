package me.wyzebb.TownyDiscordBridge;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.scheduling.impl.FoliaTaskScheduler;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.google.common.base.Preconditions;
import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import github.scarsz.discordsrv.dependencies.jda.api.entities.*;
import github.scarsz.discordsrv.dependencies.jda.api.requests.restaction.ChannelAction;
import github.scarsz.discordsrv.dependencies.jda.api.requests.restaction.RoleAction;
import github.scarsz.discordsrv.util.DiscordUtil;

import java.awt.Color;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static me.wyzebb.TownyDiscordBridge.TownyDiscordBridge.plugin;


public class TDBManager {
    public static void discordUserRoleCheckAllLinked() {
        plugin.getLogger().warning("1");
        Map<String, UUID> linkedAccounts = DiscordSRV.getPlugin().getAccountLinkManager().getLinkedAccounts();
        linkedAccounts.forEach(TDBManager::discordUserRoleCheck);
    }

    private static int boolToInt(boolean val) {
        return val ? 1 : 0;
    }


    public static void discordUserRoleCheck(String discordId, UUID uuid) {
        plugin.getLogger().warning("2");
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        if (!offlinePlayer.hasPlayedBefore()) {
            return;
        }

        Guild guild = DiscordSRV.getPlugin().getMainGuild();
        Resident resident = TownyUniverse.getInstance().getResident(uuid);

        if (resident == null) {
            return;
        }

        Member member = DiscordUtil.getMemberById(discordId);
        if (member == null) {
            return;
        }

        List<Role> memberRoles = member.getRoles();
        List<Role> memberTownRoles = new ArrayList<>();
        List<Role> memberNationRoles = new ArrayList<>();

        for (Role role : memberRoles) {
            if (role.getName().startsWith("town-")) {
                memberTownRoles.add(role);
                continue;
            }
            if (role.getName().startsWith("nation-")) {
                memberNationRoles.add(role);
            }
        }

        Town town = null;
        boolean hasTown = resident.hasTown();
        if (hasTown) {
            try {
                town = resident.getTown();
            } catch (NotRegisteredException notRegisteredException) {
                plugin.getLogger().warning("Expected error occurred");
            }
        }

        Nation nation = null;
        boolean hasNation = resident.hasNation();

        if (hasNation) {
            try {
                nation = resident.getNation();
            } catch (TownyException e) {
                plugin.getLogger().warning("Nation not found");
                throw new RuntimeException(e);
            }
        }


        boolean townRoleExists = false;
        if (town != null) {
            List<Role> roles = guild.getRolesByName("town-" + town.getName(), true);
            if (!roles.isEmpty()) {
                townRoleExists = guild.getRoles().contains(roles.getFirst());
            }
        }

        boolean nationRoleExists = false;
        if (nation != null) {
            List<Role> roles = guild.getRolesByName("nation-" + nation.getName(), true);
            if (!roles.isEmpty()) {
                nationRoleExists = guild.getRoles().contains(roles.getFirst());
            }
        }

        int i = (!memberTownRoles.isEmpty() ? 1 : 0) & boolToInt(townRoleExists);
        int j = (!memberNationRoles.isEmpty() ? 1 : 0) & ((nation != null) ? 1 : 0) & boolToInt(nationRoleExists);

        if (((!hasTown ? 1 : 0) & (!hasNation ? 1 : 0) & ((i == 0) ? 1 : 0) & ((j == 0) ? 1 : 0)) != 0) {
            TDBMessages.sendMessageToPlayerGameAndLog(uuid, TDBMessages.getConfigMsgRoleDoNothingSuccess() + "[1]");
        } else if (((!hasTown ? 1 : 0) & boolToInt(hasNation) & ((i == 0) ? 1 : 0) & ((j == 0) ? 1 : 0)) != 0) {
            TDBMessages.sendMessageToDiscordLogChannel(uuid, TDBMessages.getConfigMsgRoleDoNothingSuccess() + " [5]");
        } else if (((!hasTown ? 1 : 0) & boolToInt(hasNation) & ((i == 0) ? 1 : 0) & j) != 0) {
            TDBMessages.sendMessageToDiscordLogChannel(uuid, TDBMessages.getConfigMsgRoleDoNothingSuccess() + " [6]");
        } else if (((!hasNation ? 1 : 0) & ((i == 0) ? 1 : 0) & ((j == 0) ? 1 : 0)) != 0) {
            if (town != null) {
                memberTownRoles.add(guild.getRolesByName("town-" + town.getName(), true).getFirst());
            }
            for (Role memberTownRole : memberTownRoles) {
                guild.addRoleToMember(discordId, memberTownRole).queueAfter(10L, TimeUnit.SECONDS, success -> TDBMessages.sendMessageToPlayerGameAndLog(uuid, TDBMessages.getConfigMsgRoleAddSuccess() + " " + TDBMessages.getConfigMsgRoleAddSuccess() + " [9]"), failure -> TDBMessages.sendMessageToPlayerGameAndLog(uuid, TDBMessages.getConfigMsgRoleAddFailure() + " " + TDBMessages.getConfigMsgRoleAddFailure() + " [9]"));
            }
        } else if (((!hasNation ? 1 : 0) & i & ((j == 0) ? 1 : 0)) != 0) {
            TDBMessages.sendMessageToPlayerGameAndLog(uuid, TDBMessages.getConfigMsgRoleDoNothingSuccess() + " [11]");
        } else if ((((i == 0) ? 1 : 0) & ((j == 0) ? 1 : 0)) != 0) {
            if (town != null) {
                memberTownRoles.add(guild.getRolesByName("town-" + town.getName(), true).getFirst());
            }
            for (Role memberTownRole : memberTownRoles) {
                guild.addRoleToMember(discordId, memberTownRole).queueAfter(10L, TimeUnit.SECONDS, success -> TDBMessages.sendMessageToPlayerGameAndLog(uuid, TDBMessages.getConfigMsgRoleAddSuccess() + " " + TDBMessages.getConfigMsgRoleAddSuccess() + " [13]"), failure -> TDBMessages.sendMessageToPlayerGameAndLog(uuid, TDBMessages.getConfigMsgRoleAddFailure() + " " + TDBMessages.getConfigMsgRoleAddFailure() + " [13]"));
            }
            if (nation != null) {
                memberNationRoles.add(guild.getRolesByName("nation-" + nation.getName(), true).getFirst());
            }
            for (Role memberNationRole : memberNationRoles) {
                guild.addRoleToMember(discordId, memberNationRole).queueAfter(10L, TimeUnit.SECONDS, success -> TDBMessages.sendMessageToPlayerGameAndLog(uuid, TDBMessages.getConfigMsgRoleAddSuccess() + " " + TDBMessages.getConfigMsgRoleAddSuccess() + " [13]"), failure -> TDBMessages.sendMessageToPlayerGameAndLog(uuid, TDBMessages.getConfigMsgRoleAddFailure() + " " + TDBMessages.getConfigMsgRoleAddFailure() + " [13]"));
            }

        } else if ((((i == 0) ? 1 : 0) & j) != 0) {

            if (town != null) {
                memberTownRoles.add(guild.getRolesByName("town-" + town.getName(), true).getFirst());
            }
            for (Role memberTownRole : memberTownRoles) {
                guild.addRoleToMember(discordId, memberTownRole).queueAfter(10L, TimeUnit.SECONDS, success -> TDBMessages.sendMessageToPlayerGameAndLog(uuid, TDBMessages.getConfigMsgRoleAddSuccess() + " " + TDBMessages.getConfigMsgRoleAddSuccess() + " [14]"), failure -> TDBMessages.sendMessageToPlayerGameAndLog(uuid, TDBMessages.getConfigMsgRoleAddFailure() + " " + TDBMessages.getConfigMsgRoleAddFailure() + " [14]"));


            }

        } else if (j == 0) {

            if (nation != null) {
                memberNationRoles.add(guild.getRolesByName("nation-" + nation.getName(), true).getFirst());
            }
            for (Role memberNationRole : memberNationRoles) {
                guild.addRoleToMember(discordId, memberNationRole).queueAfter(10L, TimeUnit.SECONDS, success -> TDBMessages.sendMessageToPlayerGameAndLog(uuid, TDBMessages.getConfigMsgRoleAddSuccess() + " " + TDBMessages.getConfigMsgRoleAddSuccess() + " [15]"), failure -> TDBMessages.sendMessageToPlayerGameAndLog(uuid, TDBMessages.getConfigMsgRoleAddFailure() + " " + TDBMessages.getConfigMsgRoleAddFailure() + " [15]"));

            }

        } else {


            TDBMessages.sendMessageToPlayerGameAndLog(uuid, TDBMessages.getConfigMsgRoleDoNothingSuccess() + " [16]");
        }
    }


    public static void discordRoleCheckAllTownsAllNations() {
        plugin.getLogger().warning("3");

        Guild guild = DiscordSRV.getPlugin().getMainGuild();

        List<Role> allRoles = guild.getRoles();
        List<Town> allTowns = new ArrayList<>(TownyUniverse.getInstance().getTowns());
        List<Nation> allNations = new ArrayList<>(TownyUniverse.getInstance().getNations());
        List<Town> townsWithoutRole = new ArrayList<>(allTowns);
        List<Nation> nationsWithoutRole = new ArrayList<>(allNations);
        plugin.getLogger().warning(allTowns.toArray().toString());
        plugin.getLogger().warning(allNations.toArray().toString());
        if (!allRoles.isEmpty())
            for (Role role : allRoles) {

                for (Town town : allTowns) {
                    if (("town-" + town.getName()).equalsIgnoreCase(role.getName())) {
                        townsWithoutRole.remove(town);
                    }
                }


                for (Nation nation : allNations) {
                    if (("nation-" + nation.getName()).equalsIgnoreCase(role.getName())) {
                        nationsWithoutRole.remove(nation);
                    }
                }
            }


        if (!townsWithoutRole.isEmpty()) {
            plugin.getLogger().info("Reached townsWithoutRole.isEmpty()");

            for (Town town : townsWithoutRole) {
                RoleAction role = guild.createRole().setName("town-" + town.getName()).setColor(Color.decode(plugin.config.getString("town.RoleColourCode")));
                role.queue(success -> TDBMessages.sendMessageToDiscordLogChannel(TDBMessages.getConfigMsgRoleCreateSuccess() + " town-" + TDBMessages.getConfigMsgRoleCreateSuccess() + " [17]"), failure -> TDBMessages.sendMessageToDiscordLogChannel(TDBMessages.getConfigMsgRoleCreateFailure() + " town-" + TDBMessages.getConfigMsgRoleCreateFailure() + " [17]"));
            }
        }


        if (!nationsWithoutRole.isEmpty()) {
            plugin.getLogger().info("Reached nationsWithoutRole.isEmpty()");

            for (Nation nation : nationsWithoutRole) {
                RoleAction role = guild.createRole().setName("nation-" + nation.getName()).setColor(Color.decode(plugin.config.getString("nation.RoleColourCode")));
                role.queue(success -> TDBMessages.sendMessageToDiscordLogChannel(TDBMessages.getConfigMsgRoleCreateSuccess() + " nation-" + TDBMessages.getConfigMsgRoleCreateSuccess() + " [17]"), failure -> TDBMessages.sendMessageToDiscordLogChannel(TDBMessages.getConfigMsgRoleCreateFailure() + " town-" + TDBMessages.getConfigMsgRoleCreateFailure() + " [17]"));
            }
        }
    }


    public static void discordTextChannelCheckAllTownsAllNations() {
        plugin.getLogger().warning("4");
        Guild guild = DiscordSRV.getPlugin().getMainGuild();

        List<Town> allTowns = new ArrayList<>(TownyUniverse.getInstance().getTowns());
        List<Nation> allNations = new ArrayList<>(TownyUniverse.getInstance().getNations());
        List<Town> townsWithoutTextChannel = new ArrayList<>(allTowns);
        List<Nation> nationsWithoutTextChannel = new ArrayList<>(allNations);
        List<TextChannel> allTownTextChannels = new ArrayList<>();
        List<TextChannel> allNationTextChannels = new ArrayList<>();
        String townTextCategoryId = getTownTextCategoryId();
        if (townTextCategoryId != null) {
            Category townTextCategory = guild.getCategoryById(townTextCategoryId);
            if (townTextCategory != null) {
                allTownTextChannels = townTextCategory.getTextChannels();
            }
        }
        String nationTextCategoryId = getNationTextCategoryId();
        if (nationTextCategoryId != null) {
            Category nationTextCategory = guild.getCategoryById(nationTextCategoryId);
            if (nationTextCategory != null) {
                allNationTextChannels = nationTextCategory.getTextChannels();
            }
        }


        for (Town town : allTowns) {
            for (TextChannel textChannel : allTownTextChannels) {
                if (textChannel.getName().equalsIgnoreCase(town.getName())) {
                    townsWithoutTextChannel.remove(town);
                }
            }
        }


        for (Nation nation : allNations) {
            for (TextChannel textChannel : allNationTextChannels) {
                if (textChannel.getName().equalsIgnoreCase(nation.getName())) {
                    nationsWithoutTextChannel.remove(nation);
                }
            }
        }


        if (!townsWithoutTextChannel.isEmpty()) { // todo: Fix Text Channels, Default to visible.
            plugin.getLogger().info("Reached townsWithoutTextChannel.isEmpty()");

            for (Town town : townsWithoutTextChannel) {
                plugin.getLogger().warning(getTownTextCategoryId());
                plugin.getLogger().warning(town.getName());
                try {
                    createChannels(guild, town.getName(), guild.getRolesByName("town-" + town.getName(), true).getFirst(), false, true, null, getTownTextCategoryId());
                } catch (NullPointerException exception) {
                    plugin.getLogger().warning("Failed to create town text channels. Text category not found.");
                }
            }
        }

        if (!nationsWithoutTextChannel.isEmpty()) {
            plugin.getLogger().info("Reached nationsWithoutVoiceChannel.isEmpty()");

            for (Nation nation : nationsWithoutTextChannel) {
                try {
                    createChannels(guild, nation.getName(), guild.getRolesByName("nation-" + nation.getName(), true).getFirst(), false, true, null, getNationTextCategoryId());
                } catch (NullPointerException exception) {
                    plugin.getLogger().warning("Failed to create nation text channels. Text category not found.");
                }
            }
        }
    }


    public static void discordVoiceChannelCheckAllTownsAllNations() {
        plugin.getLogger().warning("5");
        Guild guild = DiscordSRV.getPlugin().getMainGuild();

        List<Town> allTowns = new ArrayList<>(TownyUniverse.getInstance().getTowns());
        List<Nation> allNations = new ArrayList<>(TownyUniverse.getInstance().getNations());
        List<Town> townsWithoutVoiceChannel = new ArrayList<>(allTowns);
        List<Nation> nationsWithoutVoiceChannel = new ArrayList<>(allNations);
        List<VoiceChannel> allTownVoiceChannels = guild.getCategoryById(getTownVoiceCategoryId()).getVoiceChannels();
        List<VoiceChannel> allNationVoiceChannels = guild.getCategoryById(getNationVoiceCategoryId()).getVoiceChannels();

        Preconditions.checkNotNull(allTowns);
        Preconditions.checkNotNull(allNations);
        Preconditions.checkNotNull(allTownVoiceChannels);
        Preconditions.checkNotNull(allNationVoiceChannels);


        for (Town town : allTowns) {
            for (VoiceChannel voiceChannel : allTownVoiceChannels) {
                if (voiceChannel.getName().equalsIgnoreCase(town.getName())) {
                    townsWithoutVoiceChannel.remove(town);
                }
            }
        }


        for (Nation nation : allNations) {
            for (VoiceChannel voiceChannel : allNationVoiceChannels) {
                if (voiceChannel.getName().equalsIgnoreCase(nation.getName())) {
                    nationsWithoutVoiceChannel.remove(nation);
                }
            }
        }


        if (!townsWithoutVoiceChannel.isEmpty()) {
            plugin.getLogger().info("Reached townsWithoutVoiceChannel.isEmpty()");

            for (Town town : townsWithoutVoiceChannel) {
                plugin.getLogger().warning(getTownVoiceCategoryId());
                try {
                    createChannels(guild, town.getName(), guild.getRolesByName("town-" + town.getName(), true).getFirst(), true, false, getTownVoiceCategoryId(), null);
                } catch (NullPointerException exception) {
                    plugin.getLogger().warning("Failed to create town voice channels. Voice category not found.");
                }
            }
        }

        if (!nationsWithoutVoiceChannel.isEmpty()) {
            plugin.getLogger().info("Reached nationsWithoutVoiceChannel.isEmpty()");

            for (Nation nation : nationsWithoutVoiceChannel) {
                try {
                    createChannels(guild, nation.getName(), guild.getRolesByName("nation-" + nation.getName(), true).getFirst(), true, false, getNationVoiceCategoryId(), null);
                } catch (NullPointerException exception) {
                    plugin.getLogger().warning("Failed to create nation voice channels. Voice category not found.");
                }
            }
        }
    }

    public static void renameNation(String oldName, String newName) {
        plugin.getLogger().warning("6");
        rename(oldName, newName, "nation-", getNationTextCategoryId(), getNationVoiceCategoryId());
    }

    public static void renameTown(String oldName, String newName) {
        plugin.getLogger().warning("7");
        rename(oldName, newName, "town-", getTownTextCategoryId(), getTownVoiceCategoryId());
    }


    public static void rename(String oldName, String newName, String roleprefix, String townTextCategoryId, String townVoiceCategoryId) {
        plugin.getLogger().warning("8");
        Guild guild = DiscordSRV.getPlugin().getMainGuild();

        getRole(roleprefix + roleprefix).getManager().setName(roleprefix + roleprefix).queue(success -> TDBMessages.sendMessageToDiscordLogChannel(TDBMessages.getConfigMsgRoleRenameSuccess() + " " + TDBMessages.getConfigMsgRoleRenameSuccess() + roleprefix + " to " + oldName + roleprefix + " [18]"), failure -> TDBMessages.sendMessageToDiscordLogChannel(TDBMessages.getConfigMsgRoleRenameFailure() + " " + TDBMessages.getConfigMsgRoleRenameFailure() + roleprefix + " to " + oldName + roleprefix + " [18]"));


        List<TextChannel> discordTextChannels = guild.getTextChannelsByName(oldName, true);
        for (TextChannel discordTextChannel : discordTextChannels) {
            if (townTextCategoryId == null || discordTextChannel.getParent().getId().equals(townTextCategoryId)) {
                discordTextChannel.getManager().setName(newName).queue(success -> TDBMessages.sendMessageToDiscordLogChannel(TDBMessages.getConfigMsgTextChannelRenameSuccess() + " " + TDBMessages.getConfigMsgTextChannelRenameSuccess() + " to " + oldName + " [19]"), failure -> TDBMessages.sendMessageToDiscordLogChannel(TDBMessages.getConfigMsgTextChannelRenameFailure() + " " + TDBMessages.getConfigMsgTextChannelRenameFailure() + " to " + oldName + " [19]"));
            }
        }


        List<VoiceChannel> discordVoiceChannels = guild.getVoiceChannelsByName(oldName, true);
        for (VoiceChannel discordVoiceChannel : discordVoiceChannels) {
            if (townVoiceCategoryId == null || discordVoiceChannel.getParent().getId().equals(townVoiceCategoryId)) {
                discordVoiceChannel.getManager().setName(newName).queue(success -> TDBMessages.sendMessageToDiscordLogChannel(TDBMessages.getConfigMsgVoiceChannelRenameSuccess() + " " + TDBMessages.getConfigMsgVoiceChannelRenameSuccess() + " to " + oldName + " [20]"), failure -> TDBMessages.sendMessageToDiscordLogChannel(TDBMessages.getConfigMsgVoiceChannelRenameFailure() + " " + TDBMessages.getConfigMsgVoiceChannelRenameFailure() + " to " + oldName + " [20]"));
            }
        }
    }


    public static void deleteRoleAndChannelsFromTown(String townName) {
        plugin.getLogger().warning("10");
        deleteRoleAndChannels("town-" + townName, getRole("town-" + townName), getTownTextCategoryId(), getTownVoiceCategoryId());
    }


    public static void deleteRoleAndChannelsFromNation(String nationName) {
        plugin.getLogger().warning("12");
        deleteRoleAndChannels("nation-" + nationName, getRole("nation-" + nationName), getNationTextCategoryId(),
                getNationVoiceCategoryId());
    }


    public static void deleteRoleAndChannels(String name, @Nullable Role role, String textChannelParentId, String voiceChannelParentId) {
        plugin.getLogger().warning("13");
        Guild guild = DiscordSRV.getPlugin().getMainGuild();

        if (role != null) {
            role.delete().queue(success -> TDBMessages.sendMessageToDiscordLogChannel(TDBMessages.getConfigMsgRoleDeleteSuccess() + " " + TDBMessages.getConfigMsgRoleDeleteSuccess() + " [21]"), failure -> TDBMessages.sendMessageToDiscordLogChannel(TDBMessages.getConfigMsgRoleDeleteFailure() + " " + TDBMessages.getConfigMsgRoleDeleteFailure() + " [21]"));
        }


        List<TextChannel> discordTextChannels = guild.getTextChannelsByName(name.substring(name.indexOf("-") + 1), true);
        for (TextChannel discordTextChannel : discordTextChannels) {
            if (textChannelParentId == null || discordTextChannel.getParent().getId().equals(textChannelParentId)) {
                discordTextChannel.delete().queue(success -> TDBMessages.sendMessageToDiscordLogChannel(TDBMessages.getConfigMsgTextChannelDeleteSuccess() + " " + TDBMessages.getConfigMsgTextChannelDeleteSuccess() + " [22]"), failure -> TDBMessages.sendMessageToDiscordLogChannel(TDBMessages.getConfigMsgTextChannelDeleteFailure() + " " + TDBMessages.getConfigMsgTextChannelDeleteFailure() + " [22]"));
            }
        }


        List<VoiceChannel> discordVoiceChannels = guild.getVoiceChannelsByName(name.substring(name.indexOf("-") + 1), true);
        for (VoiceChannel discordVoiceChannel : discordVoiceChannels) {
            if (voiceChannelParentId == null || discordVoiceChannel.getParent().getId().equals(voiceChannelParentId)) {
                discordVoiceChannel.delete().queue(success -> TDBMessages.sendMessageToDiscordLogChannel(TDBMessages.getConfigMsgVoiceChannelDeleteSuccess() + " " + TDBMessages.getConfigMsgVoiceChannelDeleteSuccess() + " [23]"), failure -> TDBMessages.sendMessageToDiscordLogChannel(TDBMessages.getConfigMsgVoiceChannelDeleteFailure() + " " + TDBMessages.getConfigMsgVoiceChannelDeleteFailure() + " [23]"));
            }
        }
    }


    public static void removePlayerRole(@NotNull UUID uuid, @NotNull Town town) {
        plugin.getLogger().warning("17");
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        removePlayerRole(offlinePlayer, town);

        plugin.getLogger().warning("HOPEFULLY REMOVED " + Bukkit.getOfflinePlayer(uuid).getName() + " from " + town.getName() + " and nations if relevant");
    }

//    public static void removePlayerRole(@NotNull OfflinePlayer offlinePlayer, @NotNull Town town) {
//        plugin.getLogger().warning("18 - Starting role removal process");
//
//        String linkedId = getLinkedId(offlinePlayer);
//
//        if (linkedId == null) {
//            TDBMessages.sendMessageToPlayerGame(offlinePlayer, "You haven't linked your Discord, do /discord link to get started!");
//            return;
//        }
//
//        Member member = getMember(linkedId);
//
//        if (member == null) {
//            TDBMessages.sendMessageToPlayerGame(offlinePlayer, "You are not in the Discord server!");
//            return;
//        }
//
//        // Remove town role
//        Role townRole = getRole(town);
//        if (townRole != null) {
//            if (member.getRoles().contains(townRole)) {
//                plugin.getLogger().warning("Removing town role: " + townRole.getName());
//                DiscordUtil.removeRolesFromMember(member, townRole);
//
//                DiscordUtil.privateMessage(member.getUser(), "You have been removed from the Discord " + townRole.getName() + " channels!");
//                TDBMessages.sendMessageToPlayerGame(offlinePlayer, "You have been removed from the Discord " + townRole.getName() + " channels!");
//            } else {
//                plugin.getLogger().warning("Member does not have town role: " + townRole.getName());
//            }
//        } else {
//            plugin.getLogger().warning("Town role not found for: " + town.getName());
//        }
//
//        // Remove nation role if applicable
//        if (town.hasNation()) {
//            Nation nation = town.getNationOrNull();
//            Role nationRole = getRole(nation);
//
//            if (nationRole != null) {
//                if (member.getRoles().contains(nationRole)) {
//                    plugin.getLogger().warning("Removing nation role: " + nationRole.getName());
//                    DiscordUtil.removeRolesFromMember(member, nationRole);
//
//                    DiscordUtil.privateMessage(member.getUser(), "You have been removed from the Discord nation channels!");
//                    TDBMessages.sendMessageToPlayerGame(offlinePlayer, "You have been removed from the Discord nation channels!");
//                } else {
//                    plugin.getLogger().warning("Member does not have nation role: " + nationRole.getName());
//                }
//            } else {
//                plugin.getLogger().warning("Nation role not found for: " + (nation != null ? nation.getName() : "null nation"));
//            }
//        }
//    }

    public static void removePlayerRole(@NotNull OfflinePlayer offlinePlayer, @NotNull Town town) {
        // Run this code asynchronously
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getLogger().warning("18 - Starting role removal process");

            // Step 1: Retrieve the linked Discord ID
            String linkedId = getLinkedId(offlinePlayer);
            plugin.getLogger().warning("19 - Linked ID for player: " + (linkedId != null ? linkedId : "null"));

            if (linkedId == null) {
                Bukkit.getScheduler().runTask(plugin, () ->
                        TDBMessages.sendMessageToPlayerGame(offlinePlayer, "You haven't linked your Discord, do /discord link to get started!")
                );
                return;
            }

            // Step 2: Retrieve the Discord member
            Member member = getMember(linkedId);
            plugin.getLogger().warning("20 - Member for linked ID: " + (member != null ? member.getEffectiveName() : "null"));

            if (member == null) {
                Bukkit.getScheduler().runTask(plugin, () ->
                        TDBMessages.sendMessageToPlayerGame(offlinePlayer, "You are not in the Discord server!")
                );
                return;
            }

            // Step 3: Remove town role
            Role townRole = getRole(town);
            plugin.getLogger().warning("21 - Town role: " + (townRole != null ? townRole.getName() : "null"));

            if (townRole != null) {
                if (member.getRoles().contains(townRole)) {
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                            retryRoleRemoval(member, townRole, "Town", offlinePlayer)
                    );
                } else {
                    plugin.getLogger().warning("23 - Member does not have town role: " + townRole.getName());
                }
            } else {
                plugin.getLogger().warning("24 - Town role not found for town: " + town.getName());
            }

            // Step 4: Remove nation role if applicable
            if (town.hasNation()) {
                Nation nation = town.getNationOrNull();
                plugin.getLogger().warning("25 - Nation for town: " + (nation != null ? nation.getName() : "null"));

                Role nationRole = getRole(nation);
                plugin.getLogger().warning("26 - Nation role: " + (nationRole != null ? nationRole.getName() : "null"));

                if (nationRole != null) {
                    if (member.getRoles().contains(nationRole)) {
                        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                                retryRoleRemoval(member, nationRole, "Nation", offlinePlayer)
                        );
                    } else {
                        plugin.getLogger().warning("28 - Member does not have nation role: " + nationRole.getName());
                    }
                } else {
                    plugin.getLogger().warning("29 - Nation role not found for nation: " + nation.getName());
                }
            }
        });
    }


    private static void retryRoleRemoval(Member member, Role role, String roleType, OfflinePlayer offlinePlayer) {
        int maxAttempts = 3;
        int attempt = 1;

        while (attempt <= maxAttempts) {
            plugin.getLogger().warning(roleType + " role removal attempt " + attempt + " for role: " + role.getName());
            try {
                DiscordUtil.removeRolesFromMember(member, role);
                if (!member.getRoles().contains(role)) {
                    plugin.getLogger().warning(roleType + " role successfully removed: " + role.getName());
                    DiscordUtil.privateMessage(member.getUser(), "You have been removed from the Discord " + role.getName() + " channels!");
                    TDBMessages.sendMessageToPlayerGame(offlinePlayer, "You have been removed from the Discord " + role.getName() + " channels!");
                    break;
                } else {
                    plugin.getLogger().warning(roleType + " role still present: " + role.getName());
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Error removing " + roleType + " role: " + role.getName() + ". Attempt " + attempt + " failed with exception: " + e.getMessage());
            }
            attempt++;
            try {
                Thread.sleep(1000); // Wait 1 second before retrying
            } catch (InterruptedException ignored) {
            }
        }
    }



    private static void retryRoleAssignment(Member member, Role role, String roleType, OfflinePlayer offlinePlayer) {
        int maxAttempts = 3;
        int attempt = 1;
        Guild guild = member.getGuild();

        while (attempt <= maxAttempts) {
            plugin.getLogger().warning(roleType + " role assignment attempt " + attempt + " for role: " + role.getName());
            try {
                guild.addRoleToMember(member, role).queue(
                        success -> {
                            plugin.getLogger().warning("[DEBUG] Successfully assigned role: " + role.getName());
                            plugin.getLogger().warning("[DEBUG] Member roles after: " + member.getRoles());
                            DiscordUtil.privateMessage(
                                    member.getUser(),
                                    "Your account has been linked to " + role.getName().substring(role.getName().indexOf('-') + 1) + "!"
                            );
                            TDBMessages.sendMessageToPlayerGame(
                                    offlinePlayer,
                                    "Your account has been linked to " + role.getName().substring(role.getName().indexOf('-') + 1) + "!"
                            );
                        },
                        failure -> {
                            System.err.println("[ERROR] Failed to assign role: " + role.getName() + " to member: " + member.getEffectiveName());
                            failure.printStackTrace();
                        }
                );
            } catch (Exception e) {
                plugin.getLogger().warning("Error removing " + roleType + " role: " + role.getName() + ". Attempt " + attempt + " failed with exception: " + e.getMessage());
            }
            attempt++;
            try {
                Thread.sleep(1000); // Wait 1 second before retrying
            } catch (InterruptedException ignored) {
            }
        }

    }


    public static void givePlayerRole(@NotNull UUID uuid, @NotNull Nation nation) {
        plugin.getLogger().warning("20");
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        givePlayerRole(offlinePlayer, nation);
    }


    public static void givePlayerRole(@NotNull OfflinePlayer offlinePlayer, @NotNull Nation nation) {
        plugin.getLogger().warning("21");

        String linkedId = getLinkedId(offlinePlayer);

        if (linkedId == null) {
            TDBMessages.sendMessageToPlayerGame(offlinePlayer, "You haven't linked your Discord, do '/discord link' to get started!");
            return;
        }

        Member member = getMember(linkedId);

        if (member == null) {
            TDBMessages.sendMessageToPlayerGame(offlinePlayer, "You are not in the Discord server!");
            return;
        }

        Role nationRole = getRole(nation);

        if (nationRole != null) {
            if (!member.getRoles().contains(nationRole)) {
                plugin.getLogger().warning("[DEBUG] Member roles before: " + member.getRoles());
                giveRoleToMember(offlinePlayer, member, nationRole);
                plugin.getLogger().warning("[DEBUG] Member roles after: " + member.getRoles());
            } else {
                plugin.getLogger().warning("Role already assigned: " + nationRole.getName());
            }
        } else {
            createRole(offlinePlayer, member, nation);
        }
    }

    public static void givePlayerRole(@NotNull UUID uuid, @NotNull Town town) {
        try {
            plugin.getLogger().warning("23");
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            givePlayerRole(offlinePlayer, town);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void givePlayerRole(@NotNull OfflinePlayer offlinePlayer, @NotNull Town town) {
        // Run this code asynchronously
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getLogger().warning("18 - Starting role add process");

            // Step 1: Retrieve the linked Discord ID
            String linkedId = getLinkedId(offlinePlayer);
            plugin.getLogger().warning("19 - Linked ID for player: " + (linkedId != null ? linkedId : "null"));

            if (linkedId == null) {
                Bukkit.getScheduler().runTask(plugin, () ->
                        TDBMessages.sendMessageToPlayerGame(offlinePlayer, "You haven't linked your Discord, do /discord link to get started!")
                );
                return;
            }

            // Step 2: Retrieve the Discord member
            Member member = getMember(linkedId);
            plugin.getLogger().warning("20 - Member for linked ID: " + (member != null ? member.getEffectiveName() : "null"));

            if (member == null) {
                Bukkit.getScheduler().runTask(plugin, () ->
                        TDBMessages.sendMessageToPlayerGame(offlinePlayer, "You are not in the Discord server!")
                );
                return;
            }

            // Step 3: Remove town role
            Role townRole = getRole(town);
            plugin.getLogger().warning("21 - Town role: " + (townRole != null ? townRole.getName() : "null"));

            if (townRole != null) {
                if (!member.getRoles().contains(townRole)) {
                    plugin.getLogger().warning("[DEBUG] Member roles before: " + member.getRoles());
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                            retryRoleAssignment(member, townRole, "Town", offlinePlayer)
                    );
                    giveRoleToMember(offlinePlayer, member, townRole);//TODO
                    plugin.getLogger().warning("[DEBUG] Member roles after: " + member.getRoles());
                } else {
                    plugin.getLogger().warning("Role already assigned: " + townRole.getName());
                }
            } else {
                createRole(offlinePlayer, member, town);
            }

            // Step 4: Remove nation role if applicable
            if (town.hasNation()) {
                Nation nation = town.getNationOrNull();
                plugin.getLogger().warning("25 - Nation for town: " + (nation != null ? nation.getName() : "null"));

                Role nationRole = getRole(nation);
                plugin.getLogger().warning("26 - Nation role: " + (nationRole != null ? nationRole.getName() : "null"));

                if (nationRole != null) {
                    if (!member.getRoles().contains(nationRole)) {
                        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                                retryRoleAssignment(member, nationRole, "Nation", offlinePlayer)
                        );
                        giveRoleToMember(offlinePlayer, member, townRole);//TODO
                    } else {
                        plugin.getLogger().warning("28 - Member does not have nation role: " + nationRole.getName());
                    }
                } else {
                    plugin.getLogger().warning("29 - Nation role not found for nation: " + nation.getName());
                }
            }
        });
    }

    private static void giveRoleToMember(@NotNull OfflinePlayer offlinePlayer, @NotNull Member member, @NotNull Role role) {
        plugin.getLogger().warning("[DEBUG] Attempting to assign role: " + role.getName() + " to member: " + member.getEffectiveName());
        plugin.getLogger().warning("[DEBUG] Member roles before: " + member.getRoles());

        Guild guild = member.getGuild();

        guild.addRoleToMember(member, role).queue(
                success -> {
                    plugin.getLogger().warning("[DEBUG] Successfully assigned role: " + role.getName());
                    plugin.getLogger().warning("[DEBUG] Member roles after: " + member.getRoles());
                    DiscordUtil.privateMessage(
                            member.getUser(),
                            "Your account has been linked to " + role.getName().substring(role.getName().indexOf('-') + 1) + "!"
                    );
                    TDBMessages.sendMessageToPlayerGame(
                            offlinePlayer,
                            "Your account has been linked to " + role.getName().substring(role.getName().indexOf('-') + 1) + "!"
                    );
                },
                failure -> {
                    System.err.println("[ERROR] Failed to assign role: " + role.getName() + " to member: " + member.getEffectiveName());
                    failure.printStackTrace();
                }
        );
    }

    private static void createRole(@NotNull OfflinePlayer offlinePlayer, @NotNull Member member, @NotNull Town town) {
        plugin.getLogger().warning("Attempting to create role for town: " + town.getName());
        Guild guild = member.getGuild();

        // Check if the town role already exists
        Role existingRole = getRole(town);
        if (existingRole != null) {
            plugin.getLogger().warning("Role already exists for town: " + town.getName() + " - Reusing existing role.");
            giveRoleToMember(offlinePlayer, member, existingRole);
            return; // Exit early since the role already exists
        }

        if (plugin.config.getBoolean("town.CreateRoleIfNoneExists")) {
            TDBMessages.sendMessageToPlayerGame(offlinePlayer, town.getName() + " doesn't have a Role, automatically creating one for you...");
            guild.createRole()
                    .setName("town-" + town.getName())
                    .setColor(Color.decode(plugin.config.getString("town.RoleColourCode")))
                    .queue(role -> {
                        plugin.getLogger().warning("[DEBUG] Successfully created role: " + role.getName());
                        plugin.getLogger().warning("[DEBUG] Member roles before assigning new role: " + member.getRoles());

                        giveRoleToMember(offlinePlayer, member, role);

                        plugin.getLogger().warning("[DEBUG] Member roles after assigning new role: " + member.getRoles());
                        createChannels(guild, town, role);

                        TDBMessages.sendMessageToDiscordLogChannel(
                                TDBMessages.getConfigMsgRoleCreateSuccess() + " town-" + town.getName() + " [26]"
                        );
                    }, failure -> {
                        System.err.println("[ERROR] Failed to create role for town: " + town.getName());
                        TDBMessages.sendMessageToDiscordLogChannel(
                                TDBMessages.getConfigMsgRoleCreateFailure() + " town-" + town.getName() + " [26]"
                        );
                    });
        }
    }

    private static void createRole(@NotNull OfflinePlayer offlinePlayer, @NotNull Member member, @NotNull Nation nation) {
        plugin.getLogger().warning("Attempting to create role for nation: " + nation.getName());
        Guild guild = member.getGuild();

        // Check if the nation role already exists
        Role existingRole = getRole(nation);
        if (existingRole != null) {
            plugin.getLogger().warning("Role already exists for nation: " + nation.getName() + " - Reusing existing role.");
            giveRoleToMember(offlinePlayer, member, existingRole);
            return; // Exit early if the role exists
        }

        // Synchronize to prevent multiple threads creating the same role
        synchronized (TDBManager.class) {
            if (getRole(nation) != null) { // Double-check after acquiring the lock
                plugin.getLogger().warning("Role now exists after re-check: " + nation.getName());
                giveRoleToMember(offlinePlayer, member, getRole(nation));
                return;
            }

            // If role still doesn't exist, create it
            if (plugin.config.getBoolean("nation.CreateRoleIfNoneExists")) {
                TDBMessages.sendMessageToPlayerGame(offlinePlayer, nation.getName() + " doesn't have a Role, automatically creating one for you...");
                guild.createRole()
                        .setName("nation-" + nation.getName())
                        .setColor(Color.decode(plugin.config.getString("nation.RoleColourCode")))
                        .queue(role -> {
                            plugin.getLogger().warning("[DEBUG] Successfully created role: " + role.getName());
                            plugin.getLogger().warning("[DEBUG] Member roles before assigning new role: " + member.getRoles());

                            giveRoleToMember(offlinePlayer, member, role);
                            plugin.getLogger().warning("[DEBUG] Member roles after assigning new role: " + member.getRoles());
                            createChannels(guild, nation, role); // Create channels after successful role creation

                            TDBMessages.sendMessageToDiscordLogChannel(
                                    TDBMessages.getConfigMsgRoleCreateSuccess() + " nation-" + nation.getName() + " [27]"
                            );
                        }, failure -> {
                            System.err.println("[ERROR] Failed to create role for nation: " + nation.getName());
                            TDBMessages.sendMessageToDiscordLogChannel(
                                    TDBMessages.getConfigMsgRoleCreateFailure() + " nation-" + nation.getName() + " [27]"
                            );
                        });
            }
        }
    }


    private static void createChannels(Guild guild, Town town, Role role) {
        plugin.getLogger().warning("28");
        createChannels(
                guild,
                town.getName(),
                role,
                plugin.config.getBoolean("town.CreateVoiceChannelForRole"),
                plugin.config.getBoolean("town.CreateTextChannelForRole"),
                getTownVoiceCategoryId(),
                getTownTextCategoryId()
        );
    }

    private static void createChannels(Guild guild, Nation nation, Role role) {
        plugin.getLogger().warning("29");
        createChannels(
                guild,
                nation.getName(),
                role,
                plugin.config.getBoolean("nation.CreateVoiceChannelForRole"),
                plugin.config.getBoolean("nation.CreateTextChannelForRole"),
                getNationVoiceCategoryId(),
                getNationTextCategoryId()
        );
    }

    private static void createChannels(
            @NotNull Guild guild,
            @NotNull String name,
            @NotNull Role role,
            boolean createVoiceChannel,
            boolean createTextChannel,
            @Nullable String voiceChannelCategoryId,
            @Nullable String textChannelCategoryId
    ) {
        plugin.getLogger().warning("30");

        long VIEW_PERM = Permission.VIEW_CHANNEL.getRawValue();
        long VC_CONNECT_PERM = Permission.VOICE_CONNECT.getRawValue();

        long everyoneRoleId = guild.getPublicRole().getIdLong();
        long roleId = role.getIdLong();
        Member bot = guild.getMember(DiscordSRV.getPlugin().getJda().getSelfUser());

        if (bot == null) {
            return;
        }

        long botId = bot.getIdLong();

        FoliaTaskScheduler f = new FoliaTaskScheduler(plugin);
        f.runGlobalLater(scheduledTask -> {
            // Pre-check for existing voice or text channels
            if (createVoiceChannel) {
                List<VoiceChannel> existingVoiceChannels = guild.getVoiceChannelsByName(name, true);
                if (existingVoiceChannels.isEmpty()) { // Only create if no existing channel
                    ChannelAction<VoiceChannel> voiceChannelAction = guild.createVoiceChannel(name)
                            .addRolePermissionOverride(everyoneRoleId, VIEW_PERM, 0L)

                            .addRolePermissionOverride(everyoneRoleId, 0L, VC_CONNECT_PERM)
                            .addRolePermissionOverride(roleId, VC_CONNECT_PERM, 0L)
                            .addRolePermissionOverride(botId, VC_CONNECT_PERM, 0L);

                    if (voiceChannelCategoryId != null) {
                        voiceChannelAction.setParent(guild.getCategoryById(voiceChannelCategoryId));
                    }
                    voiceChannelAction.queue(
                            success -> TDBMessages.sendMessageToDiscordLogChannel(
                                    TDBMessages.getConfigMsgVoiceChannelCreateSuccess() + " [26]"
                            ),
                            failure -> TDBMessages.sendMessageToDiscordLogChannel(
                                    TDBMessages.getConfigMsgVoiceChannelCreateFailure() + " [26]"
                            )
                    );
                } else {
                    plugin.getLogger().warning("Voice channel already exists for: " + name);
                }
            }

            if (createTextChannel) {
                List<TextChannel> existingTextChannels = guild.getTextChannelsByName(name, true);
                if (existingTextChannels.isEmpty()) { // Only create if no existing channel
                    ChannelAction<TextChannel> textChannelAction = guild.createTextChannel(name)
                            .addRolePermissionOverride(everyoneRoleId, 0L, VIEW_PERM)
                            .addRolePermissionOverride(roleId, VIEW_PERM, 0L)
                            .addMemberPermissionOverride(botId, VIEW_PERM, 0L);

                    if (textChannelCategoryId != null) {
                        textChannelAction.setParent(guild.getCategoryById(textChannelCategoryId));
                    }
                    textChannelAction.queue(
                            success -> TDBMessages.sendMessageToDiscordLogChannel(
                                    TDBMessages.getConfigMsgTextChannelCreateSuccess() + " " + name + " [27]"
                            ),
                            failure -> TDBMessages.sendMessageToDiscordLogChannel(
                                    TDBMessages.getConfigMsgTextChannelCreateFailure() + " " + name + " [27]"
                            )
                    );

                    Member member = guild.getMemberById(everyoneRoleId);
                    Member roleMember = guild.getMemberById(roleId);
                    Member botMember = guild.getMemberById(botId);

                    existingTextChannels.getFirst().upsertPermissionOverride(member).deny(Permission.MESSAGE_HISTORY).queue();
                    existingTextChannels.getFirst().upsertPermissionOverride(roleMember).grant(Permission.MESSAGE_HISTORY).queue();
                    existingTextChannels.getFirst().upsertPermissionOverride(botMember).grant(Permission.MESSAGE_HISTORY).queue();
                } else {
                    plugin.getLogger().warning("Text channel already exists for: " + name);
                }
            }
        }, 100); //TODO: Fix delay and test


    }


    @Nullable
    public static String getLinkedId(@NotNull OfflinePlayer offlinePlayer) {
        plugin.getLogger().warning("31");
        return DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(offlinePlayer.getUniqueId());
    }


    @Nullable
    private static Member getMember(@NotNull String id) {
        plugin.getLogger().warning("32");
        return DiscordSRV.getPlugin().getMainGuild().getMemberById(id);
    }


    @Nullable
    private static Role getRole(@NotNull Town town) {
        plugin.getLogger().warning("35");
        return getRole("town-" + town.getName());
    }


    @Nullable
    private static Role getRole(@NotNull Nation nation) {
        plugin.getLogger().warning("36");
        return getRole("nation-" + nation.getName());
    }

//    @Nullable
//    private static Role getRole(@NotNull String name) {
//        plugin.getLogger().warning("37: " + name);
//        int retryCount = 5;
//        long delayMillis = 500;
//
//        for (int i = 0; i < retryCount; i++) {
//            try {
//                // Attempt to fetch the role
//                List<Role> roles = DiscordUtil.getJda().getRolesByName(name, true);
//                if (!roles.isEmpty()) {
//                    plugin.getLogger().warning("Role found: " + name);
//                    return roles.get(0); // Return the first matching role
//                }
//
//                plugin.getLogger().warning("Role not found, retrying... (" + (i + 1) + "/" + retryCount + ")");
//                Thread.sleep(delayMillis); // Wait before retrying
//
//            } catch (Exception exception) {
//                plugin.getLogger().warning("AHA GETROLE ERROR: " + exception.getMessage());
//                break; // Exit the loop if an exception occurs
//            }
//        }
//
//        plugin.getLogger().warning("Role not found after retries: " + name);
//        return null;
//    }

    @Nullable
    private static Role getRole(@NotNull String name) {
        try {
            return java.util.concurrent.Executors.newSingleThreadExecutor().submit(() -> {
                plugin.getLogger().warning("37: " + name);
                int retryCount = 5;
                long delayMillis = 500;

                for (int i = 0; i < retryCount; i++) {
                    try {
                        // Attempt to fetch the role
                        List<Role> roles = DiscordUtil.getJda().getRolesByName(name, true);
                        if (!roles.isEmpty()) {
                            plugin.getLogger().warning("Role found: " + name);
                            return roles.get(0); // Return the first matching role
                        }

                        plugin.getLogger().warning("Role not found, retrying... (" + (i + 1) + "/" + retryCount + ")");
                        Thread.sleep(delayMillis); // Wait before retrying

                    } catch (Exception exception) {
                        plugin.getLogger().warning("AHA GETROLE ERROR: " + exception.getMessage());
                        break; // Exit the loop if an exception occurs
                    }
                }

                plugin.getLogger().warning("Role not found after retries: " + name);
                return null;
            }).get(); // Blocks until the task completes and gets the result
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to fetch role: " + e.getMessage());
            return null;
        }
    }

    @Nullable
    private static String getTownVoiceCategoryId() {
        return plugin.config.getString("town.VoiceCategoryId");
    }

    @Nullable
    private static String getTownTextCategoryId() {
        return plugin.config.getString("town.TextCategoryId");
    }

    @Nullable
    private static String getNationVoiceCategoryId() {
        return plugin.config.getString("nation.VoiceCategoryId");
    }

    @Nullable
    private static String getNationTextCategoryId() {
        return plugin.config.getString("nation.TextCategoryId");
    }
}