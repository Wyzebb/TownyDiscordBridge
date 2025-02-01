package me.wyzebb.TownyDiscordBridge.util;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.util.DiscordUtil;
import me.wyzebb.TownyDiscordBridge.TDBMessages;
import org.bukkit.OfflinePlayer;

import static me.wyzebb.TownyDiscordBridge.TownyDiscordBridge.plugin;

public class RetryMethods {
    public static void retryRoleRemoval(Member member, Role role, String roleType, OfflinePlayer offlinePlayer) {
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

    public static void retryRoleAssignment(Member member, Role role, String roleType, OfflinePlayer offlinePlayer) {
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
                plugin.getLogger().severe("Thread sleep error");
            }
        }

    }
}
