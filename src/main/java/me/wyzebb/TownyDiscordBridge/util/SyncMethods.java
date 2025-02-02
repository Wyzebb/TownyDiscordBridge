package me.wyzebb.TownyDiscordBridge.util;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.*;
import github.scarsz.discordsrv.dependencies.jda.api.requests.restaction.RoleAction;
import github.scarsz.discordsrv.util.DiscordUtil;
import me.wyzebb.TownyDiscordBridge.TDBManager;
import me.wyzebb.TownyDiscordBridge.TDBMessages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static me.wyzebb.TownyDiscordBridge.TownyDiscordBridge.plugin;

public class SyncMethods {
    public static void syncUserRolesToDiscord(String discordId, UUID uuid) {
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
            } catch (Exception e) {
                plugin.getLogger().severe("Could not find " + resident.getName() + "'s town");
            }
        }

        Nation nation = null;
        boolean hasNation = resident.hasNation();

        if (hasNation) {
            try {
                nation = resident.getNation();
            } catch (TownyException e) {
                plugin.getLogger().severe("Could not find " + resident.getName() + "'s nation");
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

        int i = (!memberTownRoles.isEmpty() ? 1 : 0) & GeneralUtility.boolToInt(townRoleExists);
        int j = (!memberNationRoles.isEmpty() ? 1 : 0) & ((nation != null) ? 1 : 0) & GeneralUtility.boolToInt(nationRoleExists);

        if (((!hasTown ? 1 : 0) & (!hasNation ? 1 : 0) & ((i == 0) ? 1 : 0) & ((j == 0) ? 1 : 0)) != 0) {
            TDBMessages.sendMessageToPlayerGameAndLog(uuid, TDBMessages.getConfigMsgRoleDoNothingSuccess());
        } else if (((!hasTown ? 1 : 0) & GeneralUtility.boolToInt(hasNation) & ((i == 0) ? 1 : 0) & ((j == 0) ? 1 : 0)) != 0) {
            TDBMessages.sendMessageToDiscordLogChannel(uuid, TDBMessages.getConfigMsgRoleDoNothingSuccess());
        } else if (((!hasTown ? 1 : 0) & GeneralUtility.boolToInt(hasNation) & ((i == 0) ? 1 : 0) & j) != 0) {
            TDBMessages.sendMessageToDiscordLogChannel(uuid, TDBMessages.getConfigMsgRoleDoNothingSuccess());
        } else if (((!hasNation ? 1 : 0) & ((i == 0) ? 1 : 0) & ((j == 0) ? 1 : 0)) != 0) {
            if (town != null) {
                memberTownRoles.add(guild.getRolesByName("town-" + town.getName(), true).getFirst());
            }
            for (Role memberTownRole : memberTownRoles) {
                guild.addRoleToMember(discordId, memberTownRole).queueAfter(10L, TimeUnit.SECONDS, success -> TDBMessages.sendMessageToPlayerGameAndLog(uuid, TDBMessages.getConfigMsgRoleAddSuccess() + " " + TDBMessages.getConfigMsgRoleAddSuccess() + " [9]"), failure -> TDBMessages.sendMessageToPlayerGameAndLog(uuid, TDBMessages.getConfigMsgRoleAddFailure() + " " + TDBMessages.getConfigMsgRoleAddFailure() + " [9]"));
            }
        } else if (((!hasNation ? 1 : 0) & i & ((j == 0) ? 1 : 0)) != 0) {
            TDBMessages.sendMessageToPlayerGameAndLog(uuid, TDBMessages.getConfigMsgRoleDoNothingSuccess());
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

    public static void syncAllTownsAllNations() {
        Guild guild = DiscordSRV.getPlugin().getMainGuild();

        List<Role> allRoles = guild.getRoles();
        List<Town> allTowns = new ArrayList<>(TownyUniverse.getInstance().getTowns());
        List<Nation> allNations = new ArrayList<>(TownyUniverse.getInstance().getNations());
        List<Town> townsWithoutRole = new ArrayList<>(allTowns);
        List<Nation> nationsWithoutRole = new ArrayList<>(allNations);

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
            for (Town town : townsWithoutRole) {
                RoleAction role = guild.createRole().setName("town-" + town.getName()).setColor(Color.decode(Objects.requireNonNull(plugin.config.getString("town.RoleColourCode"))));
                role.queue(success -> TDBMessages.sendMessageToDiscordLogChannel(TDBMessages.getConfigMsgRoleCreateSuccess() + " town-" + TDBMessages.getConfigMsgRoleCreateSuccess() + " [17]"), failure -> TDBMessages.sendMessageToDiscordLogChannel(TDBMessages.getConfigMsgRoleCreateFailure() + " town-" + TDBMessages.getConfigMsgRoleCreateFailure() + " [17]"));
            }
        }


        if (!nationsWithoutRole.isEmpty()) {
            for (Nation nation : nationsWithoutRole) {
                RoleAction role = guild.createRole().setName("nation-" + nation.getName()).setColor(Color.decode(Objects.requireNonNull(plugin.config.getString("nation.RoleColourCode"))));
                role.queue(success -> TDBMessages.sendMessageToDiscordLogChannel(TDBMessages.getConfigMsgRoleCreateSuccess() + " nation-" + TDBMessages.getConfigMsgRoleCreateSuccess() + " [17]"), failure -> TDBMessages.sendMessageToDiscordLogChannel(TDBMessages.getConfigMsgRoleCreateFailure() + " town-" + TDBMessages.getConfigMsgRoleCreateFailure() + " [17]"));
            }
        }
    }

    public static void syncTextChannelCheckAllTownsAllNations() {
        Guild guild = DiscordSRV.getPlugin().getMainGuild();

        List<Town> allTowns = new ArrayList<>(TownyUniverse.getInstance().getTowns());
        List<Nation> allNations = new ArrayList<>(TownyUniverse.getInstance().getNations());
        List<Town> townsWithoutTextChannel = new ArrayList<>(allTowns);
        List<Nation> nationsWithoutTextChannel = new ArrayList<>(allNations);
        List<TextChannel> allTownTextChannels = new ArrayList<>();
        List<TextChannel> allNationTextChannels = new ArrayList<>();
        String townTextCategoryId = ConfigGetters.getTownTextCategoryId();

        if (townTextCategoryId != null) {
            Category townTextCategory = guild.getCategoryById(townTextCategoryId);
            if (townTextCategory != null) {
                allTownTextChannels = townTextCategory.getTextChannels();
            }
        }

        String nationTextCategoryId = ConfigGetters.getNationTextCategoryId();
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


        if (!townsWithoutTextChannel.isEmpty()) {
            for (Town town : townsWithoutTextChannel) {
                plugin.getLogger().warning(ConfigGetters.getTownTextCategoryId());
                plugin.getLogger().warning(town.getName());
                try {
                    TDBManager.createChannels(guild, town.getName(), guild.getRolesByName("town-" + town.getName(), true).getFirst(), false, true, null, ConfigGetters.getTownTextCategoryId());
                } catch (NullPointerException exception) {
                    plugin.getLogger().warning("Failed to create town text channels. Text category not found.");
                }
            }
        }

        if (!nationsWithoutTextChannel.isEmpty()) {
            for (Nation nation : nationsWithoutTextChannel) {
                try {
                    TDBManager.createChannels(guild, nation.getName(), guild.getRolesByName("nation-" + nation.getName(), true).getFirst(), false, true, null, ConfigGetters.getNationTextCategoryId());
                } catch (NullPointerException exception) {
                    plugin.getLogger().warning("Failed to create nation text channels. Text category not found.");
                }
            }
        }
    }

    public static void syncVoiceChannelCheckAllTownsAllNations() {
        Guild guild = DiscordSRV.getPlugin().getMainGuild();

        List<Town> allTowns = new ArrayList<>(TownyUniverse.getInstance().getTowns());
        List<Nation> allNations = new ArrayList<>(TownyUniverse.getInstance().getNations());
        List<Town> townsWithoutVoiceChannel = new ArrayList<>(allTowns);
        List<Nation> nationsWithoutVoiceChannel = new ArrayList<>(allNations);
        List<VoiceChannel> allTownVoiceChannels = Objects.requireNonNull(Objects.requireNonNull(guild.getCategoryById(Objects.requireNonNull(ConfigGetters.getTownVoiceCategoryId())))).getVoiceChannels();
        List<VoiceChannel> allNationVoiceChannels = Objects.requireNonNull(Objects.requireNonNull(guild.getCategoryById(Objects.requireNonNull(ConfigGetters.getNationVoiceCategoryId())))).getVoiceChannels();

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
            for (Town town : townsWithoutVoiceChannel) {
                plugin.getLogger().warning(ConfigGetters.getTownVoiceCategoryId());
                try {
                    TDBManager.createChannels(guild, town.getName(), guild.getRolesByName("town-" + town.getName(), true).getFirst(), true, false, ConfigGetters.getTownVoiceCategoryId(), null);
                } catch (NullPointerException exception) {
                    plugin.getLogger().warning("Failed to create town voice channels. Voice category not found.");
                }
            }
        }

        if (!nationsWithoutVoiceChannel.isEmpty()) {
            for (Nation nation : nationsWithoutVoiceChannel) {
                try {
                    TDBManager.createChannels(guild, nation.getName(), guild.getRolesByName("nation-" + nation.getName(), true).getFirst(), true, false, ConfigGetters.getNationVoiceCategoryId(), null);
                } catch (NullPointerException exception) {
                    plugin.getLogger().warning("Failed to create nation voice channels. Voice category not found.");
                }
            }
        }
    }
}
