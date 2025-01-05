package com.TownyDiscordChat.TownyDiscordChat;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.google.common.base.Preconditions;
import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Category;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.dependencies.jda.api.entities.VoiceChannel;
import github.scarsz.discordsrv.dependencies.jda.api.requests.restaction.ChannelAction;
import github.scarsz.discordsrv.dependencies.jda.api.requests.restaction.RoleAction;
import github.scarsz.discordsrv.util.DiscordUtil;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class TDCManager {
    public static void discordUserRoleCheckAllLinked() {
        Map<String, UUID> linkedAccounts = DiscordSRV.getPlugin().getAccountLinkManager().getLinkedAccounts();
        linkedAccounts.forEach(TDCManager::discordUserRoleCheck);
        System.out.println("1");
    }

    private static int boolToInt(boolean val) {
        return val ? 1 : 0;
    }


    public static void discordUserRoleCheck(String discordId, UUID uUID) {
        System.out.println("2");
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uUID);
        if (!offlinePlayer.hasPlayedBefore()) {
            return;
        }

        Guild guild = DiscordSRV.getPlugin().getMainGuild();
        Resident resident = TownyUniverse.getInstance().getResident(uUID);
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
                System.out.println("Expected error occurred");
            }
        }


        Nation nation = null;
        boolean hasNation = resident.hasNation();
        if (hasNation) {
            try {
                nation = resident.getTown().getNation();
            } catch (NotRegisteredException notRegisteredException) {
                System.out.println("Expected error occurred");
            }
        }


        boolean townRoleExists = false;
        if (town != null) {
            List<Role> roles = guild.getRolesByName("town-" + town.getName(), true);
            if (!roles.isEmpty()) {
                townRoleExists = guild.getRoles().contains(roles.get(0));
            }
        }

        boolean nationRoleExists = false;
        if (nation != null) {
            List<Role> roles = guild.getRolesByName("nation-" + nation.getName(), true);
            if (!roles.isEmpty()) {
                nationRoleExists = guild.getRoles().contains(roles.get(0));
            }
        }

        int i = (!memberTownRoles.isEmpty() ? 1 : 0) & boolToInt(townRoleExists);
        int j = (!memberNationRoles.isEmpty() ? 1 : 0) & ((nation != null) ? 1 : 0) & boolToInt(nationRoleExists);

        if (((!hasTown ? 1 : 0) & (!hasNation ? 1 : 0) & ((i == 0) ? 1 : 0) & ((j == 0) ? 1 : 0)) != 0) {

            TDCMessages.sendMessageToPlayerGameAndLog(uUID, TDCMessages.getConfigMsgRoleDoNothingSuccess() + "[1]");
        } else if (((!hasTown ? 1 : 0) & boolToInt(hasNation) & ((i == 0) ? 1 : 0) & ((j == 0) ? 1 : 0)) != 0) {

            TDCMessages.sendMessageToDiscordLogChannel(uUID, TDCMessages.getConfigMsgRoleDoNothingSuccess() + " [5]");
        } else if (((!hasTown ? 1 : 0) & boolToInt(hasNation) & ((i == 0) ? 1 : 0) & j) != 0) {

            TDCMessages.sendMessageToDiscordLogChannel(uUID, TDCMessages.getConfigMsgRoleDoNothingSuccess() + " [6]");
        } else if (((!hasNation ? 1 : 0) & ((i == 0) ? 1 : 0) & ((j == 0) ? 1 : 0)) != 0) {

            memberTownRoles.add(guild.getRolesByName("town-" + town.getName(), true).get(0));
            for (Role memberTownRole : memberTownRoles) {
                guild.addRoleToMember(discordId, memberTownRole).queueAfter(10L, TimeUnit.SECONDS, success -> TDCMessages.sendMessageToPlayerGameAndLog(uUID, TDCMessages.getConfigMsgRoleAddSuccess() + " " + TDCMessages.getConfigMsgRoleAddSuccess() + " [9]"), failure -> TDCMessages.sendMessageToPlayerGameAndLog(uUID, TDCMessages.getConfigMsgRoleAddFailure() + " " + TDCMessages.getConfigMsgRoleAddFailure() + " [9]"));
            }
        } else if (((!hasNation ? 1 : 0) & i & ((j == 0) ? 1 : 0)) != 0) {

            TDCMessages.sendMessageToPlayerGameAndLog(uUID, TDCMessages.getConfigMsgRoleDoNothingSuccess() + " [11]");
        } else if ((((i == 0) ? 1 : 0) & ((j == 0) ? 1 : 0)) != 0) {

            memberTownRoles.add(guild.getRolesByName("town-" + town.getName(), true).get(0));
            for (Role memberTownRole : memberTownRoles) {
                guild.addRoleToMember(discordId, memberTownRole).queueAfter(10L, TimeUnit.SECONDS, success -> TDCMessages.sendMessageToPlayerGameAndLog(uUID, TDCMessages.getConfigMsgRoleAddSuccess() + " " + TDCMessages.getConfigMsgRoleAddSuccess() + " [13]"), failure -> TDCMessages.sendMessageToPlayerGameAndLog(uUID, TDCMessages.getConfigMsgRoleAddFailure() + " " + TDCMessages.getConfigMsgRoleAddFailure() + " [13]"));
            }


            memberNationRoles.add(guild.getRolesByName("nation-" + nation.getName(), true).get(0));
            for (Role memberNationRole : memberNationRoles) {
                guild.addRoleToMember(discordId, memberNationRole).queueAfter(10L, TimeUnit.SECONDS, success -> TDCMessages.sendMessageToPlayerGameAndLog(uUID, TDCMessages.getConfigMsgRoleAddSuccess() + " " + TDCMessages.getConfigMsgRoleAddSuccess() + " [13]"), failure -> TDCMessages.sendMessageToPlayerGameAndLog(uUID, TDCMessages.getConfigMsgRoleAddFailure() + " " + TDCMessages.getConfigMsgRoleAddFailure() + " [13]"));


            }

        } else if ((((i == 0) ? 1 : 0) & j) != 0) {

            memberTownRoles.add(guild.getRolesByName("town-" + town.getName(), true).get(0));
            for (Role memberTownRole : memberTownRoles) {
                guild.addRoleToMember(discordId, memberTownRole).queueAfter(10L, TimeUnit.SECONDS, success -> TDCMessages.sendMessageToPlayerGameAndLog(uUID, TDCMessages.getConfigMsgRoleAddSuccess() + " " + TDCMessages.getConfigMsgRoleAddSuccess() + " [14]"), failure -> TDCMessages.sendMessageToPlayerGameAndLog(uUID, TDCMessages.getConfigMsgRoleAddFailure() + " " + TDCMessages.getConfigMsgRoleAddFailure() + " [14]"));


            }

        } else if (j == 0) {

            memberNationRoles.add(guild.getRolesByName("nation-" + nation.getName(), true).get(0));
            for (Role memberNationRole : memberNationRoles) {
                guild.addRoleToMember(discordId, memberNationRole).queueAfter(10L, TimeUnit.SECONDS, success -> TDCMessages.sendMessageToPlayerGameAndLog(uUID, TDCMessages.getConfigMsgRoleAddSuccess() + " " + TDCMessages.getConfigMsgRoleAddSuccess() + " [15]"), failure -> TDCMessages.sendMessageToPlayerGameAndLog(uUID, TDCMessages.getConfigMsgRoleAddFailure() + " " + TDCMessages.getConfigMsgRoleAddFailure() + " [15]"));

            }

        } else {


            TDCMessages.sendMessageToPlayerGameAndLog(uUID, TDCMessages.getConfigMsgRoleDoNothingSuccess() + " [16]");
        }
    }


    public static final void discordRoleCheckAllTownsAllNations() {
        System.out.println("3");

        Guild guild = DiscordSRV.getPlugin().getMainGuild();

        List<Role> allRoles = guild.getRoles();
        List<Town> allTowns = new ArrayList<>(TownyUniverse.getInstance().getTowns());
        List<Nation> allNations = new ArrayList<>(TownyUniverse.getInstance().getNations());
        List<Town> townsWithoutRole = new ArrayList<>(allTowns);
        List<Nation> nationsWithoutRole = new ArrayList<>(allNations);
        System.out.println(allTowns);
        System.out.println(allNations);
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
            Main.plugin.getLogger().info("Reached townsWithoutRole.isEmpty()");

            for (Town town : townsWithoutRole) {
                RoleAction role = guild.createRole().setName("town-" + town.getName()).setColor(Color.decode(Main.plugin.config.getString("town.RoleCreateColorCode")));
                role.queue(success -> TDCMessages.sendMessageToDiscordLogChannel(TDCMessages.getConfigMsgRoleCreateSuccess() + " town-" + TDCMessages.getConfigMsgRoleCreateSuccess() + " [17]"), failure -> TDCMessages.sendMessageToDiscordLogChannel(TDCMessages.getConfigMsgRoleCreateFailure() + " town-" + TDCMessages.getConfigMsgRoleCreateFailure() + " [17]"));
            }
        }


        if (!nationsWithoutRole.isEmpty()) {
            Main.plugin.getLogger().info("Reached nationsWithoutRole.isEmpty()");

            for (Nation nation : nationsWithoutRole) {
                RoleAction role = guild.createRole().setName("nation-" + nation.getName()).setColor(Color.decode(Main.plugin.config.getString("nation.RoleCreateColorCode")));
                role.queue(success -> TDCMessages.sendMessageToDiscordLogChannel(TDCMessages.getConfigMsgRoleCreateSuccess() + " nation-" + TDCMessages.getConfigMsgRoleCreateSuccess() + " [17]"), failure -> TDCMessages.sendMessageToDiscordLogChannel(TDCMessages.getConfigMsgRoleCreateFailure() + " town-" + TDCMessages.getConfigMsgRoleCreateFailure() + " [17]"));
            }
        }
    }


    public static final void discordTextChannelCheckAllTownsAllNations() {
        System.out.println("4");
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
            Main.plugin.getLogger().info("Reached townsWithoutTextChannel.isEmpty()");

            for (Town town : townsWithoutTextChannel) {
                System.out.println(getTownTextCategoryId());
                System.out.println(town);
                try {
                    createChannels(guild, town.getName(), guild.getRolesByName("town-" + town.getName(), true).get(0), false, true, null, getTownTextCategoryId());
                } catch (NullPointerException exception) {
                    Main.plugin.getLogger().warning("Failed to create town text channels. Text category not found.");
                }
            }
        }

        if (!nationsWithoutTextChannel.isEmpty()) {
            Main.plugin.getLogger().info("Reached nationsWithoutVoiceChannel.isEmpty()");

            for (Nation nation : nationsWithoutTextChannel) {
                try {
                    createChannels(guild, nation.getName(), guild.getRolesByName("nation-" + nation.getName(), true).get(0), false, true, null, getNationTextCategoryId());
                } catch (NullPointerException exception) {
                    Main.plugin.getLogger().warning("Failed to create nation text channels. Text category not found.");
                }
            }
        }
    }


    public static final void discordVoiceChannelCheckAllTownsAllNations() {
        System.out.println("5");
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
            Main.plugin.getLogger().info("Reached townsWithoutVoiceChannel.isEmpty()");

            for (Town town : townsWithoutVoiceChannel) {
                System.out.println(getTownVoiceCategoryId());
                try {
                    createChannels(guild, town.getName(), guild.getRolesByName("town-" + town.getName(), true).get(0), true, false, getTownVoiceCategoryId(), null);
                } catch (NullPointerException exception) {
                    Main.plugin.getLogger().warning("Failed to create town voice channels. Voice category not found.");
                }
            }
        }

        if (!nationsWithoutVoiceChannel.isEmpty()) {
            Main.plugin.getLogger().info("Reached nationsWithoutVoiceChannel.isEmpty()");

            for (Nation nation : nationsWithoutVoiceChannel) {
                try {
                createChannels(guild, nation.getName(), guild.getRolesByName("nation-" + nation.getName(), true).get(0), true, false, getNationVoiceCategoryId(), null);
                } catch (NullPointerException exception) {
                    Main.plugin.getLogger().warning("Failed to create nation voice channels. Voice category not found.");
                }
            }
        }
    }

    public static final void renameNation(String oldName, String newName) {
        System.out.println("6");
        rename(oldName, newName, "nation-", getNationTextCategoryId(), getNationVoiceCategoryId());
    }

    public static final void renameTown(String oldName, String newName) {
        System.out.println("7");
        rename(oldName, newName, "town-", getTownTextCategoryId(), getTownVoiceCategoryId());
    }


    public static final void rename(String oldName, String newName, String roleprefix, String townTextCategoryId, String townVoiceCategoryId) {
        System.out.println("8");
        Guild guild = DiscordSRV.getPlugin().getMainGuild();

        getRole(roleprefix + roleprefix).getManager().setName(roleprefix + roleprefix).queue(success -> TDCMessages.sendMessageToDiscordLogChannel(TDCMessages.getConfigMsgRoleRenameSuccess() + " " + TDCMessages.getConfigMsgRoleRenameSuccess() + roleprefix + " to " + oldName + roleprefix + " [18]"), failure -> TDCMessages.sendMessageToDiscordLogChannel(TDCMessages.getConfigMsgRoleRenameFailure() + " " + TDCMessages.getConfigMsgRoleRenameFailure() + roleprefix + " to " + oldName + roleprefix + " [18]"));


        List<TextChannel> discordTextChannels = guild.getTextChannelsByName(oldName, true);
        for (TextChannel discordTextChannel : discordTextChannels) {
            if (townTextCategoryId == null || discordTextChannel.getParent().getId().equals(townTextCategoryId)) {
                discordTextChannel.getManager().setName(newName).queue(success -> TDCMessages.sendMessageToDiscordLogChannel(TDCMessages.getConfigMsgTextChannelRenameSuccess() + " " + TDCMessages.getConfigMsgTextChannelRenameSuccess() + " to " + oldName + " [19]"), failure -> TDCMessages.sendMessageToDiscordLogChannel(TDCMessages.getConfigMsgTextChannelRenameFailure() + " " + TDCMessages.getConfigMsgTextChannelRenameFailure() + " to " + oldName + " [19]"));
            }
        }


        List<VoiceChannel> discordVoiceChannels = guild.getVoiceChannelsByName(oldName, true);
        for (VoiceChannel discordVoiceChannel : discordVoiceChannels) {
            if (townVoiceCategoryId == null || discordVoiceChannel.getParent().getId().equals(townVoiceCategoryId)) {
                discordVoiceChannel.getManager().setName(newName).queue(success -> TDCMessages.sendMessageToDiscordLogChannel(TDCMessages.getConfigMsgVoiceChannelRenameSuccess() + " " + TDCMessages.getConfigMsgVoiceChannelRenameSuccess() + " to " + oldName + " [20]"), failure -> TDCMessages.sendMessageToDiscordLogChannel(TDCMessages.getConfigMsgVoiceChannelRenameFailure() + " " + TDCMessages.getConfigMsgVoiceChannelRenameFailure() + " to " + oldName + " [20]"));
            }
        }
    }


    public static final void deleteRoleAndChannels(Town town) {
        System.out.println("9");
        deleteRoleAndChannelsFromTown(town.getName());
    }


    public static final void deleteRoleAndChannelsFromTown(String townName) {
        System.out.println("10");
        deleteRoleAndChannels("town-" + townName, getRole("town-" + townName), getTownTextCategoryId(), getTownVoiceCategoryId());
    }


    public static final void deleteRoleAndChannels(Nation nation) {
        System.out.println("11");
        deleteRoleAndChannelsFromNation(nation.getName());
    }


    public static final void deleteRoleAndChannelsFromNation(String nationName) {
        System.out.println("12");
        deleteRoleAndChannels("nation-" + nationName, getRole("nation-" + nationName), getNationTextCategoryId(),
                getNationVoiceCategoryId());
    }


    public static final void deleteRoleAndChannels(String name, @Nullable Role role, String textChannelParentId, String voiceChannelParentId) {
        System.out.println("13");
        Guild guild = DiscordSRV.getPlugin().getMainGuild();

        if (role != null) {
            role.delete().queue(success -> TDCMessages.sendMessageToDiscordLogChannel(TDCMessages.getConfigMsgRoleDeleteSuccess() + " " + TDCMessages.getConfigMsgRoleDeleteSuccess() + " [21]"), failure -> TDCMessages.sendMessageToDiscordLogChannel(TDCMessages.getConfigMsgRoleDeleteFailure() + " " + TDCMessages.getConfigMsgRoleDeleteFailure() + " [21]"));
        }


        List<TextChannel> discordTextChannels = guild.getTextChannelsByName(name.substring(name.indexOf("-") + 1), true);
        for (TextChannel discordTextChannel : discordTextChannels) {
            if (textChannelParentId == null || discordTextChannel.getParent().getId().equals(textChannelParentId)) {
                discordTextChannel.delete().queue(success -> TDCMessages.sendMessageToDiscordLogChannel(TDCMessages.getConfigMsgTextChannelDeleteSuccess() + " " + TDCMessages.getConfigMsgTextChannelDeleteSuccess() + " [22]"), failure -> TDCMessages.sendMessageToDiscordLogChannel(TDCMessages.getConfigMsgTextChannelDeleteFailure() + " " + TDCMessages.getConfigMsgTextChannelDeleteFailure() + " [22]"));
            }
        }


        List<VoiceChannel> discordVoiceChannels = guild.getVoiceChannelsByName(name.substring(name.indexOf("-") + 1), true);
        for (VoiceChannel discordVoiceChannel : discordVoiceChannels) {
            if (voiceChannelParentId == null || discordVoiceChannel.getParent().getId().equals(voiceChannelParentId)) {
                discordVoiceChannel.delete().queue(success -> TDCMessages.sendMessageToDiscordLogChannel(TDCMessages.getConfigMsgVoiceChannelDeleteSuccess() + " " + TDCMessages.getConfigMsgVoiceChannelDeleteSuccess() + " [23]"), failure -> TDCMessages.sendMessageToDiscordLogChannel(TDCMessages.getConfigMsgVoiceChannelDeleteFailure() + " " + TDCMessages.getConfigMsgVoiceChannelDeleteFailure() + " [23]"));
            }
        }
    }


    public static void removePlayerTownRole(@NotNull OfflinePlayer offlinePlayer) {
        System.out.println("14");
        Town town = getTown(offlinePlayer);

        if (town == null) {
            TDCMessages.sendMessageToPlayerGame(offlinePlayer, "You're not in a town!");

            return;
        }
        removePlayerRole(offlinePlayer, town);
    }

    public static final void removePlayerRole(@NotNull UUID uUID, @NotNull Town town) {
        System.out.println("15");
        Player player = Bukkit.getPlayer(uUID);
        removePlayerRole(player, town);
    }


    public static final void removePlayerRole(@NotNull OfflinePlayer offlinePlayer, @NotNull Town town) {
        String linkedId = getLinkedId(offlinePlayer);
        System.out.println("16");

        if (linkedId == null) {
            TDCMessages.sendMessageToPlayerGame(offlinePlayer, "You haven't linked your Discord, do /discord link to get started!");

            return;
        }

        Member member = getMember(linkedId);

        if (member == null) {
            TDCMessages.sendMessageToPlayerGame(offlinePlayer, "You are not in the Discord server!");

            return;
        }

        Role townRole = getRole(town);


        if (townRole != null && member.getRoles().contains(townRole)) {


            DiscordUtil.removeRolesFromMember(member, new Role[]{townRole});


            DiscordUtil.privateMessage(member.getUser(), "You have been removed from the discord " + String.valueOf(town) + " channels!");


            TDCMessages.sendMessageToPlayerGame(offlinePlayer, "You have been removed from the discord " + String.valueOf(town) + " channels!");
        }
    }

    public static final void removePlayerRole(@NotNull UUID uUID, @NotNull Nation nation) {
        System.out.println("17");
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uUID);
        removePlayerRole(offlinePlayer, nation);
    }


    public static final void removePlayerRole(@NotNull OfflinePlayer offlinePlayer, @NotNull Nation nation) {
        System.out.println("18");
        String linkedId = getLinkedId(offlinePlayer);

        if (linkedId == null) {
            TDCMessages.sendMessageToPlayerGame(offlinePlayer, "You haven't linked your Discord, do /discord link to get started!");

            return;
        }

        Member member = getMember(linkedId);

        if (member == null) {
            TDCMessages.sendMessageToPlayerGame(offlinePlayer, "You are not in the Discord server!");

            return;
        }

        Role nationRole = getRole(nation);


        if (nationRole != null && member.getRoles().contains(nationRole)) {


            DiscordUtil.removeRolesFromMember(member, new Role[]{nationRole});


            DiscordUtil.privateMessage(member.getUser(), "You have been removed from the discord " + String.valueOf(nation) + " channels!");


            TDCMessages.sendMessageToPlayerGame(offlinePlayer, "You have been removed from the discord " + String.valueOf(nation) + " channels!");
        }
    }


    public static final void givePlayerNationRole(@NotNull OfflinePlayer offlinePlayer) {
        System.out.println("19");
        Nation nation = getNation(offlinePlayer);

        if (nation == null) {
            TDCMessages.sendMessageToPlayerGame(offlinePlayer, "You're not in a nation!");
            return;
        }
        givePlayerRole(offlinePlayer, nation);
    }

    public static final void givePlayerRole(@NotNull UUID uUID, @NotNull Nation nation) {
        System.out.println("20");
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uUID);
        givePlayerRole(offlinePlayer, nation);
    }


    public static final void givePlayerRole(@NotNull OfflinePlayer offlinePlayer, @NotNull Nation nation) {
        System.out.println("21");
        String linkedId = getLinkedId(offlinePlayer);


        if (linkedId == null) {
            TDCMessages.sendMessageToPlayerGame(offlinePlayer, "You haven't linked your Discord, do /discord link to get started!");

            return;
        }

        Member member = getMember(linkedId);

        if (member == null) {
            TDCMessages.sendMessageToPlayerGame(offlinePlayer, "You are not in the Discord server!");

            return;
        }

        Role townRole = getRole(nation);

        if (townRole != null) {

            giveRoleToMember(offlinePlayer, member, townRole);
        } else {

            createRole(offlinePlayer, member, nation);
        }
    }


    public static final void givePlayerTownRole(@NotNull OfflinePlayer offlinePlayer) {
        System.out.println("22");
        Town town = getTown(offlinePlayer);

        if (town == null) {
            TDCMessages.sendMessageToPlayerGame(offlinePlayer, "You're not in a town!");
            return;
        }
        givePlayerRole(offlinePlayer, town);
    }

    public static final void givePlayerRole(@NotNull UUID uUID, @NotNull Town town) {
        System.out.println("23");
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uUID);
        givePlayerRole(offlinePlayer, town);
    }


    public static final void givePlayerRole(@NotNull OfflinePlayer offlinePlayer, @NotNull Town town) {
        System.out.println("24");
        String linkedId = getLinkedId(offlinePlayer);


        if (linkedId == null) {
            TDCMessages.sendMessageToPlayerGame(offlinePlayer, "You haven't linked your Discord, do '/discord link' to get started!");

            return;
        }

        Member member = getMember(linkedId);

        if (member == null) {
            TDCMessages.sendMessageToPlayerGame(offlinePlayer, "You are not in the Discord server!");

            return;
        }

        Role townRole = getRole(town);

        if (townRole != null) {

            giveRoleToMember(offlinePlayer, member, townRole);
        } else {

            createRole(offlinePlayer, member, town);
        }
    }


    private static void giveRoleToMember(@NotNull OfflinePlayer offlinePlayer, @NotNull Member member, @NotNull Role townRole) {
        System.out.println("25");
        Main.plugin.getLogger().info("--------------------------------------------------");
        Main.plugin.getLogger().info(member.getId());
        Main.plugin.getLogger().info("--------------------------------------------------");


        List<Role> usernameDiscordRoles = member.getRoles();
        Main.plugin.getLogger().info("--------------------------------------------------");
        for (Role role : usernameDiscordRoles) {
            Main.plugin.getLogger().info(role.getName() + " | " + role.getName() + " | " + role.getId());
        }
        Main.plugin.getLogger().info("--------------------------------------------------");

        if (member.getRoles().contains(townRole)) {
            TDCMessages.sendMessageToPlayerGame(offlinePlayer, "You are already a part of the " + townRole.getName() + " role!");
        } else {

            DiscordUtil.addRolesToMember(member, new Role[]{townRole});


            DiscordUtil.privateMessage(member.getUser(), "Your account has been linked to " + townRole
                    .getName().substring(townRole.getName().indexOf('-') + 1) + "!");

            TDCMessages.sendMessageToPlayerGame(offlinePlayer, "Your account has been linked to " + townRole
                    .getName().substring(townRole.getName().indexOf('-') + 1) + "!");
        }
    }


    private static void createRole(@NotNull OfflinePlayer offlinePlayer, @NotNull Member member, @NotNull Town town) {
        System.out.println("26");
        Guild guild = member.getGuild();
        if (Main.plugin.config.getBoolean("town.CreateRoleIfNoneExists")) {
            TDCMessages.sendMessageToPlayerGame(offlinePlayer, String.valueOf(town) + " Doesn't have a Role, automatically creating one for you...!");
            guild.createRole().setName("town-" + town.getName())
                    .setColor(Color.decode(Main.plugin.config.getString("town.RoleCreateColorCode"))).queue(role -> {
                        DiscordUtil.addRolesToMember(member, new Role[]{role});
                        createChannels(guild, town, role);
                        TDCMessages.sendMessageToDiscordLogChannel(TDCMessages.getConfigMsgRoleCreateSuccess() + " town-" + TDCMessages.getConfigMsgRoleCreateSuccess() + " [24]");
                    }, failure -> TDCMessages.sendMessageToDiscordLogChannel(TDCMessages.getConfigMsgRoleCreateFailure() + " town-" + TDCMessages.getConfigMsgRoleCreateFailure() + " [24]"));
        }
    }


    private static void createRole(@NotNull OfflinePlayer offlinePlayer, @NotNull Member member, @NotNull Nation nation) {
        System.out.println("27");
        Guild guild = member.getGuild();
        if (Main.plugin.config.getBoolean("nation.CreateRoleIfNoneExists")) {
            TDCMessages.sendMessageToPlayerGame(offlinePlayer, String.valueOf(nation) + " Doesn't have a Role, automatically creating one for you...!");
            guild.createRole().setName("nation-" + nation.getName())
                    .setColor(Color.decode(Main.plugin.config.getString("nation.RoleCreateColorCode"))).queue(role -> {
                        giveRoleToMember(offlinePlayer, member, role);
                        createChannels(guild, nation, role);
                        TDCMessages.sendMessageToDiscordLogChannel(TDCMessages.getConfigMsgRoleCreateSuccess() + " nation-" + TDCMessages.getConfigMsgRoleCreateSuccess() + " [25]");
                    }, failure -> TDCMessages.sendMessageToDiscordLogChannel(TDCMessages.getConfigMsgRoleCreateFailure() + " nation-" + TDCMessages.getConfigMsgRoleCreateFailure() + " [25]"));
        }
    }


    private static void createChannels(Guild guild, Town town, Role role) {
        System.out.println("28");
        createChannels(guild, town.getName(), role, Main.plugin.config.getBoolean("town.CreateVoiceChannelForRole"), Main.plugin.config
                        .getBoolean("town.CreateTextChannelForRole"), getTownVoiceCategoryId(),
                getTownTextCategoryId());
    }


    private static void createChannels(Guild guild, Nation nation, Role role) {
        System.out.println("29");
        createChannels(guild, nation.getName(), role, Main.plugin.config.getBoolean("nation.CreateVoiceChannelForRole"), Main.plugin.config
                        .getBoolean("nation.CreateTextChannelForRole"), getNationVoiceCategoryId(),
                getNationTextCategoryId());
    }


    private static void createChannels(@NotNull Guild guild, @NotNull String name, @NotNull Role role, boolean createVoiceChannel, boolean createTextChannel, @Nullable String voiceChannelCategoryId, @Nullable String textChannelCategoryId) {
        System.out.println("30");
        long viewPermission = Permission.VIEW_CHANNEL.getRawValue();
        long messagePermission = Permission.MESSAGE_WRITE.getRawValue();

        long everyoneRoleId = guild.getPublicRole().getIdLong();
        long roleId = role.getIdLong();
        Member bot = guild.getMember((User) DiscordSRV.getPlugin().getJda().getSelfUser());
        if (bot == null) {
            return;
        }
        long botId = bot.getIdLong();

        Preconditions.checkNotNull(guild, "Channel creation error! @param guild null!");
        Preconditions.checkNotNull(name, "Channel creation error! @param name null!");
        Preconditions.checkNotNull(role, "Channel creation error! @param role null!");

        if (createVoiceChannel) {


            ChannelAction<VoiceChannel> voiceChannelAction = guild.createVoiceChannel(name)
                    .addRolePermissionOverride(everyoneRoleId, 0L, viewPermission)
                    .addRolePermissionOverride(roleId, viewPermission, 0L)
                    .addMemberPermissionOverride(botId, viewPermission, 0L);
            if (voiceChannelCategoryId != null) {
                voiceChannelAction.setParent(guild.getCategoryById(voiceChannelCategoryId));
            }
            voiceChannelAction.queue(success -> TDCMessages.sendMessageToDiscordLogChannel(TDCMessages.getConfigMsgVoiceChannelCreateSuccess() + " " + TDCMessages.getConfigMsgVoiceChannelCreateSuccess() + " [26]"), failure -> TDCMessages.sendMessageToDiscordLogChannel(TDCMessages.getConfigMsgVoiceChannelCreateFailure() + " " + TDCMessages.getConfigMsgVoiceChannelCreateFailure() + " [26]"));
        }
        if (createTextChannel) {
            final ChannelAction<TextChannel> textChannelAction = guild.createTextChannel(name)
                    .addRolePermissionOverride(everyoneRoleId, viewPermission, 0L) // Positive, Negative
                    .addRolePermissionOverride(roleId, viewPermission & messagePermission, 0L)
                    .addMemberPermissionOverride(botId, viewPermission & messagePermission, 0L);
            if (textChannelCategoryId != null) {
                textChannelAction.setParent(guild.getCategoryById(textChannelCategoryId));
            }
            textChannelAction.queue(success -> {
                TDCMessages.sendMessageToDiscordLogChannel(TDCMessages.getConfigMsgTextChannelCreateSuccess() + " " + name + " [27]");
            }, failure -> {
                TDCMessages.sendMessageToDiscordLogChannel(TDCMessages.getConfigMsgTextChannelCreateFailure() + " " + name + " [27]");
            });
        }
    }


    @Nullable
    public static String getLinkedId(@NotNull OfflinePlayer offlinePlayer) {
        System.out.println("31");
        return DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(offlinePlayer.getUniqueId());
    }


    @Nullable
    private static List<Member> getMembers() {
        return DiscordSRV.getPlugin().getMainGuild().getMembers();
    }


    @Nullable
    private static Member getMember(@NotNull String id) {
        System.out.println("32");
        return DiscordSRV.getPlugin().getMainGuild().getMemberById(id);
    }


    @Nullable
    private static Town getTown(@NotNull OfflinePlayer offlinePlayer) {
        System.out.println("33");
        try {
            Resident resident = TownyUniverse.getInstance().getResident(offlinePlayer.getUniqueId());
            if (resident == null) {
                return null;
            }
            return resident.getTown();
        } catch (NotRegisteredException e) {
            return null;
        }
    }


    @Nullable
    private static Nation getNation(@NotNull OfflinePlayer offlinePlayer) {
        System.out.println("34");
        Town town = getTown(offlinePlayer);
        if (town == null) {
            return null;
        }
        try {
            return town.getNation();
        } catch (NotRegisteredException e) {
            return null;
        }
    }


    @Nullable
    private static Role getRole(@NotNull Town town) {
        System.out.println("35");
        return getRole("town-" + town.getName());
    }


    @Nullable
    private static Role getRole(@NotNull Nation nation) {
        System.out.println("36");
        return getRole("nation-" + nation.getName());
    }


    @Nullable
    private static Role getRole(@NotNull String name) {
        System.out.println("37");
        Role role = null;


        try {
            role = DiscordUtil.getJda().getRolesByName(name, true).get(0);
        } catch (Exception exception) {
        }


        return role;
    }

    @Nullable
    private static String getTownVoiceCategoryId() {
        System.out.println("38");
        return Main.plugin.config.getBoolean("town.UseCategoryForText") ?
                Main.plugin.config.getString("town.TextCategoryId") :
                "159361257244327936";
    }

    @Nullable
    private static String getTownTextCategoryId() {
        System.out.println("39");
        return Main.plugin.config.getBoolean("town.UseCategoryForVoice") ?
                Main.plugin.config.getString("town.VoiceCategoryId") :
                "159361257244327936";
    }

    @Nullable
    private static String getNationVoiceCategoryId() {
        System.out.println("40");
        return Main.plugin.config.getBoolean("nation.UseCategoryForText") ?
                Main.plugin.config.getString("nation.TextCategoryId") :
                "159361257244327936";
    }

    @Nullable
    private static String getNationTextCategoryId() {
        System.out.println("41");
        return Main.plugin.config.getBoolean("nation.UseCategoryForVoice") ?
                Main.plugin.config.getString("nation.VoiceCategoryId") :
                "159361257244327936";
    }
}