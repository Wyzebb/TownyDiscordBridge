package me.wyzebb.TownyDiscordBridge;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.google.common.base.Preconditions;

import java.util.UUID;

import me.wyzebb.TownyDiscordBridge.util.IntermediaryMethods;
import me.wyzebb.TownyDiscordBridge.util.SyncMethods;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class TDBCommand implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = ((Player) sender).getPlayer();

        if (args.length == 0) {
            String msg = String.join("\n", ChatColor.DARK_GREEN + "------------------------", ChatColor.DARK_GREEN + "Plugin: " + ChatColor.DARK_GREEN + "TownyDiscordBridge", ChatColor.DARK_GREEN + "Version: " + ChatColor.DARK_GREEN + "1.0.0", ChatColor.DARK_GREEN + "Root Cmd: " + ChatColor.DARK_GREEN + "/TownyDiscordBridge", ChatColor.DARK_GREEN + "Alias: " + ChatColor.DARK_GREEN + "/TDB", ChatColor.DARK_GREEN + "---------------------------------");

            TDBMessages.sendMessageToPlayerGame(player, msg);

            return true;
        }


        if ("Check".equalsIgnoreCase(args[0])) {

            if (args.length >= 2 && "Role".equalsIgnoreCase(args[1])) {

                if (args.length >= 3 && "AllLinked".equalsIgnoreCase(args[2])) {


                    if (sender.hasPermission("TownyDiscordBridge.Admin") || sender.hasPermission("TownyDiscordBridge.Check.Role.AllLinked")) {
                        TDBMessages.sendMessageToPlayerGame(player, TDBMessages.getConfigMsgCommandWait());
                        IntermediaryMethods.syncAllUsersRolesToDiscord();

                    } else {
                        TDBMessages.sendMessageToPlayerGame(player, TDBMessages.getConfigMsgCommandNoPerms());
                    }
                    return true;
                }
                if (args.length >= 3 && "CreateAllTownsAndNations".equalsIgnoreCase(args[2])) {


                    if (sender.hasPermission("TownyDiscordBridge.Admin") || sender.hasPermission("TownyDiscordBridge.Check.Role.CreateAllTownsAndNations")) {
                        TDBMessages.sendMessageToPlayerGame(player, TDBMessages.getConfigMsgCommandWait());
                        SyncMethods.syncAllTownsAllNations();
                    } else {
                        TDBMessages.sendMessageToPlayerGame(player, TDBMessages.getConfigMsgCommandNoPerms());
                    }
                    return true;
                }

                if (sender.hasPermission("TownyDiscordBridge.Admin") || sender.hasPermission("TownyDiscordBridge.Player") || sender.hasPermission("TownyDiscordBridge.Check.Role")) {
                    UUID uuid = player.getUniqueId();
                    String discordId = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(uuid);

                    Preconditions.checkNotNull(uuid, "UUID null in onCommand()!");
                    Preconditions.checkNotNull(uuid, "discordId null in onCommand()!");

                    TDBMessages.sendMessageToPlayerGame(player, TDBMessages.getConfigMsgCommandWait());
                    SyncMethods.syncUserRolesToDiscord(discordId, uuid);
                } else {
                    TDBMessages.sendMessageToPlayerGame(player, TDBMessages.getConfigMsgCommandNoPerms());
                }
                return true;
            }
            if (args.length >= 2 && "TextChannel".equalsIgnoreCase(args[1])) {

                if (args.length >= 3 && "AllTownsAndNations".equalsIgnoreCase(args[2])) {


                    if (sender.hasPermission("TownyDiscordBridge.Admin") || sender.hasPermission("TownyDiscordBridge.Check.TextChannel.AllTownsAndNations")) {
                        TDBMessages.sendMessageToPlayerGame(player, TDBMessages.getConfigMsgCommandWait());
                        SyncMethods.syncTextChannelCheckAllTownsAllNations();
                    } else {
                        TDBMessages.sendMessageToPlayerGame(player, TDBMessages.getConfigMsgCommandNoPerms());
                    }
                    return true;
                }
                return true;
            }
            if (args.length >= 2 && "VoiceChannel".equalsIgnoreCase(args[1])) {

                if (args.length >= 3 && "AllTownsAndNations".equalsIgnoreCase(args[2])) {


                    if (sender.hasPermission("TownyDiscordBridge.Admin") || sender.hasPermission("TownyDiscordBridge.Check.VoiceChannel.AllTownsAndNations")) {
                        TDBMessages.sendMessageToPlayerGame(player, TDBMessages.getConfigMsgCommandWait());
                        SyncMethods.syncVoiceChannelCheckAllTownsAllNations();
                    } else {
                        TDBMessages.sendMessageToPlayerGame(player, TDBMessages.getConfigMsgCommandNoPerms());
                    }
                    return true;
                }
                return true;
            }
            return true;
        }
        return true;
    }
}