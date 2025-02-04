package me.wyzebb.TownyDiscordBridge;

import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.scheduling.impl.FoliaTaskScheduler;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import github.scarsz.discordsrv.dependencies.jda.api.entities.*;
import github.scarsz.discordsrv.dependencies.jda.api.requests.restaction.ChannelAction;
import github.scarsz.discordsrv.util.DiscordUtil;
import me.wyzebb.TownyDiscordBridge.util.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;
import java.util.List;

import static me.wyzebb.TownyDiscordBridge.TownyDiscordBridge.plugin;


public class TDBManager {

    public static void rename(String oldName, String newName, String rolePrefix, String townTextCategoryId, String townVoiceCategoryId) {
        Guild guild = DiscordSRV.getPlugin().getMainGuild();

        Objects.requireNonNull(getRole(rolePrefix + rolePrefix)).getManager().setName(rolePrefix + rolePrefix).queue(success -> TDBMessages.sendMessageToDiscordLogChannel(TDBMessages.getConfigMsgRoleRenameSuccess() + " " + TDBMessages.getConfigMsgRoleRenameSuccess() + rolePrefix + " to " + oldName + rolePrefix + " [18]"), failure -> TDBMessages.sendMessageToDiscordLogChannel(TDBMessages.getConfigMsgRoleRenameFailure() + " " + TDBMessages.getConfigMsgRoleRenameFailure() + rolePrefix + " to " + oldName + rolePrefix + " [18]"));

        List<TextChannel> discordTextChannels = guild.getTextChannelsByName(oldName, true);
        for (TextChannel discordTextChannel : discordTextChannels) {
            if (townTextCategoryId == null || Objects.requireNonNull(discordTextChannel.getParent()).getId().equals(townTextCategoryId)) {
                discordTextChannel.getManager().setName(newName).queue(success -> TDBMessages.sendMessageToDiscordLogChannel(TDBMessages.getConfigMsgTextChannelRenameSuccess() + " " + TDBMessages.getConfigMsgTextChannelRenameSuccess() + " to " + oldName + " [19]"), failure -> TDBMessages.sendMessageToDiscordLogChannel(TDBMessages.getConfigMsgTextChannelRenameFailure() + " " + TDBMessages.getConfigMsgTextChannelRenameFailure() + " to " + oldName + " [19]"));
            }
        }

        List<VoiceChannel> discordVoiceChannels = guild.getVoiceChannelsByName(oldName, true);
        for (VoiceChannel discordVoiceChannel : discordVoiceChannels) {
            if (townVoiceCategoryId == null || Objects.requireNonNull(discordVoiceChannel.getParent()).getId().equals(townVoiceCategoryId)) {
                discordVoiceChannel.getManager().setName(newName).queue(success -> TDBMessages.sendMessageToDiscordLogChannel(TDBMessages.getConfigMsgVoiceChannelRenameSuccess() + " " + TDBMessages.getConfigMsgVoiceChannelRenameSuccess() + " to " + oldName + " [20]"), failure -> TDBMessages.sendMessageToDiscordLogChannel(TDBMessages.getConfigMsgVoiceChannelRenameFailure() + " " + TDBMessages.getConfigMsgVoiceChannelRenameFailure() + " to " + oldName + " [20]"));
            }
        }
    }

    public static void deleteRoleAndChannels(String name, @Nullable Role role, String textChannelParentId, String voiceChannelParentId) {
        Guild guild = DiscordSRV.getPlugin().getMainGuild();

        if (role != null) {
            role.delete().queue(success -> TDBMessages.sendMessageToDiscordLogChannel(TDBMessages.getConfigMsgRoleDeleteSuccess() + " " + TDBMessages.getConfigMsgRoleDeleteSuccess() + " [21]"), failure -> TDBMessages.sendMessageToDiscordLogChannel(TDBMessages.getConfigMsgRoleDeleteFailure() + " " + TDBMessages.getConfigMsgRoleDeleteFailure() + " [21]"));
        }


        List<TextChannel> discordTextChannels = guild.getTextChannelsByName(name.substring(name.indexOf("-") + 1), true);
        for (TextChannel discordTextChannel : discordTextChannels) {
            if (textChannelParentId == null || Objects.requireNonNull(discordTextChannel.getParent()).getId().equals(textChannelParentId)) {
                discordTextChannel.delete().queue(success -> TDBMessages.sendMessageToDiscordLogChannel(TDBMessages.getConfigMsgTextChannelDeleteSuccess() + " " + TDBMessages.getConfigMsgTextChannelDeleteSuccess() + " [22]"), failure -> TDBMessages.sendMessageToDiscordLogChannel(TDBMessages.getConfigMsgTextChannelDeleteFailure() + " " + TDBMessages.getConfigMsgTextChannelDeleteFailure() + " [22]"));
            }
        }


        List<VoiceChannel> discordVoiceChannels = guild.getVoiceChannelsByName(name.substring(name.indexOf("-") + 1), true);
        for (VoiceChannel discordVoiceChannel : discordVoiceChannels) {
            if (voiceChannelParentId == null || Objects.requireNonNull(discordVoiceChannel.getParent()).getId().equals(voiceChannelParentId)) {
                discordVoiceChannel.delete().queue(success -> TDBMessages.sendMessageToDiscordLogChannel(TDBMessages.getConfigMsgVoiceChannelDeleteSuccess() + " " + TDBMessages.getConfigMsgVoiceChannelDeleteSuccess() + " [23]"), failure -> TDBMessages.sendMessageToDiscordLogChannel(TDBMessages.getConfigMsgVoiceChannelDeleteFailure() + " " + TDBMessages.getConfigMsgVoiceChannelDeleteFailure() + " [23]"));
            }
        }
    }

    public static void removePlayerRole(@NotNull OfflinePlayer offlinePlayer, @NotNull Town town) {
        FoliaTaskScheduler f = new FoliaTaskScheduler(plugin);
        f.runGlobalLater(scheduledTask -> {
            // Pre-check for existing voice or text channels
            plugin.getLogger().warning("18 - Starting role removal process");

            // Step 1: Retrieve the linked Discord ID
            String linkedId = SimpleGetters.getLinkedId(offlinePlayer);
            plugin.getLogger().warning("19 - Linked ID for player: " + (linkedId != null ? linkedId : "null"));

            if (linkedId == null) {
                TDBMessages.sendMessageToPlayerGame(offlinePlayer, "You haven't linked your Discord, do /discord link to get started!");
                return;
            }

            // Step 2: Retrieve the Discord member
            Member member = SimpleGetters.getMember(linkedId);
            plugin.getLogger().warning("20 - Member for linked ID: " + (member != null ? member.getEffectiveName() : "null"));

            if (member == null) {
                TDBMessages.sendMessageToPlayerGame(offlinePlayer, "You are not in the Discord server!");
                return;
            }

            // Step 3: Remove town role
            Role townRole = SimpleGetters.getRole(town);
            plugin.getLogger().warning("21 - Town role: " + (townRole != null ? townRole.getName() : "null"));

            if (townRole != null) {
                if (member.getRoles().contains(townRole)) {
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                            RetryMethods.retryRoleRemoval(member, townRole, "Town", offlinePlayer)
                    );
                } else {
                    plugin.getLogger().warning("23 - Member does not have town role: " + townRole.getName());
                }
            } else {
                plugin.getLogger().warning("24 - Town role not found for town: " + town.getName());
            }
        }, 100);
    }

    public static void removePlayerRole(@NotNull OfflinePlayer offlinePlayer, @NotNull Nation nation) {
        FoliaTaskScheduler f = new FoliaTaskScheduler(plugin);
        f.runGlobalLater(scheduledTask -> {
            // Pre-check for existing voice or text channels
            plugin.getLogger().warning("18 - Starting role removal process");

            // Step 1: Retrieve the linked Discord ID
            String linkedId = SimpleGetters.getLinkedId(offlinePlayer);
            plugin.getLogger().warning("19 - Linked ID for player: " + (linkedId != null ? linkedId : "null"));

            if (linkedId == null) {
                TDBMessages.sendMessageToPlayerGame(offlinePlayer, "You haven't linked your Discord, do /discord link to get started!");
                return;
            }

            // Step 2: Retrieve the Discord member
            Member member = SimpleGetters.getMember(linkedId);
            plugin.getLogger().warning("20 - Member for linked ID: " + (member != null ? member.getEffectiveName() : "null"));

            if (member == null) {
                TDBMessages.sendMessageToPlayerGame(offlinePlayer, "You are not in the Discord server!");
                return;
            }

            // Step 4: Remove nation role
            plugin.getLogger().warning("25 - Nation for town: " + nation.getName());

            Role nationRole = SimpleGetters.getRole(nation);
            plugin.getLogger().warning("26 - Nation role: " + (nationRole != null ? nationRole.getName() : "null"));

            if (nationRole != null) {
                if (member.getRoles().contains(nationRole)) {
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                            RetryMethods.retryRoleRemoval(member, nationRole, "Nation", offlinePlayer)
                    );
                } else {
                    plugin.getLogger().warning("28 - Member does not have nation role: " + nationRole.getName());
                }
            } else {
                plugin.getLogger().warning("29 - Nation role not found for nation: " + nation.getName());
            }
        }, 100);
    }

    public static void givePlayerRole(@NotNull OfflinePlayer offlinePlayer, @NotNull Nation nation) {
        String linkedId = SimpleGetters.getLinkedId(offlinePlayer);

        if (linkedId == null) {
            TDBMessages.sendMessageToPlayerGame(offlinePlayer, "You haven't linked your Discord, do '/discord link' to get started!");
            return;
        }

        Member member = SimpleGetters.getMember(linkedId);

        if (member == null) {
            TDBMessages.sendMessageToPlayerGame(offlinePlayer, "You are not in the Discord server!");
            return;
        }

        Role nationRole = SimpleGetters.getRole(nation);

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
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            givePlayerRole(offlinePlayer, town);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void givePlayerRole(@NotNull OfflinePlayer offlinePlayer, @NotNull Town town) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getLogger().warning("Starting role add process");

            String linkedId = SimpleGetters.getLinkedId(offlinePlayer);

            if (linkedId == null) {
                Bukkit.getScheduler().runTask(plugin, () ->
                        TDBMessages.sendMessageToPlayerGame(offlinePlayer, "You haven't linked your Discord, do /discord link to get started!")
                );
                return;
            }

            // Retrieve the Discord member
            Member member = SimpleGetters.getMember(linkedId);

            if (member == null) {
                Bukkit.getScheduler().runTask(plugin, () ->
                        TDBMessages.sendMessageToPlayerGame(offlinePlayer, "You are not in the Discord server!")
                );
                return;
            }

            // Remove town role
            Role townRole = SimpleGetters.getRole(town);
            plugin.getLogger().warning("21 - Town role: " + (townRole != null ? townRole.getName() : "null"));

            if (townRole != null) {
                if (!member.getRoles().contains(townRole)) {
                    plugin.getLogger().warning("[DEBUG] Member roles before: " + member.getRoles());
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                            RetryMethods.retryRoleAssignment(member, townRole, "Town", offlinePlayer)
                    );
                    giveRoleToMember(offlinePlayer, member, townRole);//TODO
                    plugin.getLogger().warning("[DEBUG] Member roles after: " + member.getRoles());
                } else {
                    plugin.getLogger().warning("Role already assigned: " + townRole.getName());
                }
            } else {
                createRole(offlinePlayer, member, town);
            }

            // Remove nation role if applicable
            if (town.hasNation()) {
                Nation nation = town.getNationOrNull();
                plugin.getLogger().warning("25 - Nation for town: " + (nation != null ? nation.getName() : "null"));

                Role nationRole = SimpleGetters.getRole(Objects.requireNonNull(nation));
                plugin.getLogger().warning("26 - Nation role: " + (nationRole != null ? nationRole.getName() : "null"));

                if (nationRole != null) {
                    if (!member.getRoles().contains(nationRole)) {
                        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                                RetryMethods.retryRoleAssignment(member, nationRole, "Nation", offlinePlayer)
                        );
                        giveRoleToMember(offlinePlayer, member, Objects.requireNonNull(townRole));//TODO
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
        Role existingRole = SimpleGetters.getRole(town);
        if (existingRole != null) {
            plugin.getLogger().warning("Role already exists for town: " + town.getName() + " - Reusing existing role.");
            giveRoleToMember(offlinePlayer, member, existingRole);
            return;
        }

        if (plugin.config.getBoolean("town.CreateRoleIfNoneExists")) {
            TDBMessages.sendMessageToPlayerGame(offlinePlayer, town.getName() + " doesn't have a Role, automatically creating one for you...");
            guild.createRole()
                    .setName("town-" + town.getName())
                    .setColor(Color.decode(Objects.requireNonNull(plugin.config.getString("town.RoleColourCode"))))
                    .queue(role -> {
                        plugin.getLogger().warning("[DEBUG] Successfully created role: " + role.getName());
                        plugin.getLogger().warning("[DEBUG] Member roles before assigning new role: " + member.getRoles());

                        giveRoleToMember(offlinePlayer, member, role);

                        plugin.getLogger().warning("[DEBUG] Member roles after assigning new role: " + member.getRoles());
                        IntermediaryMethods.createChannels(guild, town, role);

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
        Role existingRole = SimpleGetters.getRole(nation);
        if (existingRole != null) {
            plugin.getLogger().warning("Role already exists for nation: " + nation.getName() + " - Reusing existing role.");
            giveRoleToMember(offlinePlayer, member, existingRole);
            return;
        }

        // Synchronize to prevent multiple threads creating the same role
        synchronized (TDBManager.class) {
            if (SimpleGetters.getRole(nation) != null) { // Double-check after acquiring the lock
                plugin.getLogger().warning("Role now exists after re-check: " + nation.getName());
                giveRoleToMember(offlinePlayer, member, Objects.requireNonNull(SimpleGetters.getRole(nation)));
                return;
            }

            // If role still doesn't exist, create it
            if (plugin.config.getBoolean("nation.CreateRoleIfNoneExists")) {
                TDBMessages.sendMessageToPlayerGame(offlinePlayer, nation.getName() + " doesn't have a Role, automatically creating one for you...");
                guild.createRole()
                        .setName("nation-" + nation.getName())
                        .setColor(Color.decode(Objects.requireNonNull(plugin.config.getString("nation.RoleColourCode"))))
                        .queue(role -> {
                            plugin.getLogger().warning("[DEBUG] Successfully created role: " + role.getName());
                            plugin.getLogger().warning("[DEBUG] Member roles before assigning new role: " + member.getRoles());

                            giveRoleToMember(offlinePlayer, member, role);
                            plugin.getLogger().warning("[DEBUG] Member roles after assigning new role: " + member.getRoles());
                            IntermediaryMethods.createChannels(guild, nation, role); // Create channels after successful role creation

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

    public static void createChannels(
            @NotNull Guild guild,
            @NotNull String name,
            @NotNull Role role,
            boolean createVoiceChannel,
            boolean createTextChannel,
            @Nullable String voiceChannelCategoryId,
            @Nullable String textChannelCategoryId
    ) {

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
                } else {
                    plugin.getLogger().warning("Text channel already exists for: " + name);
                }
            }
        }, 100);
    }

    @Nullable
    public static Role getRole(@NotNull String name) {
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
                            return roles.getFirst(); // Return the first matching role
                        }

                        plugin.getLogger().warning("Role not found, retrying... (" + (i + 1) + "/" + retryCount + ")");
                        Thread.sleep(delayMillis); // Wait before retrying

                    } catch (Exception e) {
                        plugin.getLogger().severe("Error retrieving role: " + e.getMessage());
                        break;
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

}