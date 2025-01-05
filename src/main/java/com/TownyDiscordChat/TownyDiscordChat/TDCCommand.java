package com.TownyDiscordChat.TownyDiscordChat;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.google.common.base.Preconditions;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class TDCCommand
        implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = ((Player) sender).getPlayer();

        Preconditions.checkNotNull(player);

        if (args.length == 0) {


            String msg = String.join("\n", new CharSequence[]{String.valueOf(ChatColor.DARK_GREEN) + "------------------------", String.valueOf(ChatColor.DARK_GREEN) + "Plugin: " + String.valueOf(ChatColor.DARK_GREEN) + "TownyDiscordChat", String.valueOf(ChatColor.DARK_GREEN) + "Version: " + String.valueOf(ChatColor.DARK_GREEN) + "1.0.8", String.valueOf(ChatColor.DARK_GREEN) + "Authors: " + String.valueOf(ChatColor.DARK_GREEN) + "thejames10,Hugo5000", String.valueOf(ChatColor.DARK_GREEN) + "Root Cmd: " + String.valueOf(ChatColor.DARK_GREEN) + "/TownyDiscordChat", String.valueOf(ChatColor.DARK_GREEN) + "Alias: " + String.valueOf(ChatColor.DARK_GREEN) + "/TDC", String.valueOf(ChatColor.DARK_GREEN) + "---------------------------------"});


            TDCMessages.sendMessageToPlayerGame(player, msg);

            return true;
        }


        if ("Check".equalsIgnoreCase(args[0])) {

            if (args.length >= 2 && "Role".equalsIgnoreCase(args[1])) {

                if (args.length >= 3 && "AllLinked".equalsIgnoreCase(args[2])) {


                    if (sender.hasPermission("TownyDiscordChat.Admin") || sender.hasPermission("TownyDiscordChat.Check.Role.AllLinked")) {
                        TDCMessages.sendMessageToPlayerGame(player, TDCMessages.getConfigMsgCommandsPleasewait());
                        TDCManager.discordUserRoleCheckAllLinked();
                    } else {
                        TDCMessages.sendMessageToPlayerGame(player, TDCMessages.getConfigMsgCommandsNopermission());
                    }
                    return true;
                }
                if (args.length >= 3 && "CreateAllTownsAndNations".equalsIgnoreCase(args[2])) {


                    if (sender.hasPermission("TownyDiscordChat.Admin") || sender.hasPermission("TownyDiscordChat.Check.Role.CreateAllTownsAndNations")) {
                        TDCMessages.sendMessageToPlayerGame(player, TDCMessages.getConfigMsgCommandsPleasewait());
                        TDCManager.discordRoleCheckAllTownsAllNations();
                    } else {
                        TDCMessages.sendMessageToPlayerGame(player, TDCMessages.getConfigMsgCommandsNopermission());
                    }
                    return true;
                }

                if (sender.hasPermission("TownyDiscordChat.Admin") || sender.hasPermission("TownyDiscordChat.Player") || sender.hasPermission("TownyDiscordChat.Check.Role")) {
                    UUID UUID = player.getUniqueId();
                    String discordId = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(UUID);

                    Preconditions.checkNotNull(UUID, "UUID null in onCommand()!");
                    Preconditions.checkNotNull(UUID, "discordId null in onCommand()!");

                    TDCMessages.sendMessageToPlayerGame(player, TDCMessages.getConfigMsgCommandsPleasewait());
                    TDCManager.discordUserRoleCheck(discordId, UUID);
                } else {
                    TDCMessages.sendMessageToPlayerGame(player, TDCMessages.getConfigMsgCommandsNopermission());
                }
                return true;
            }
            if (args.length >= 2 && "TextChannel".equalsIgnoreCase(args[1])) {

                if (args.length >= 3 && "AllTownsAndNations".equalsIgnoreCase(args[2])) {


                    if (sender.hasPermission("TownyDiscordChat.Admin") || sender.hasPermission("TownyDiscordChat.Check.TextChannel.AllTownsAndNations")) {
                        TDCMessages.sendMessageToPlayerGame(player, TDCMessages.getConfigMsgCommandsPleasewait());
                        TDCManager.discordTextChannelCheckAllTownsAllNations();
                    } else {
                        TDCMessages.sendMessageToPlayerGame(player, TDCMessages.getConfigMsgCommandsNopermission());
                    }
                    return true;
                }
                return true;
            }
            if (args.length >= 2 && "VoiceChannel".equalsIgnoreCase(args[1])) {

                if (args.length >= 3 && "AllTownsAndNations".equalsIgnoreCase(args[2])) {


                    if (sender.hasPermission("TownyDiscordChat.Admin") || sender.hasPermission("TownyDiscordChat.Check.VoiceChannel.AllTownsAndNations")) {
                        TDCMessages.sendMessageToPlayerGame(player, TDCMessages.getConfigMsgCommandsPleasewait());
                        TDCManager.discordVoiceChannelCheckAllTownsAllNations();
                    } else {
                        TDCMessages.sendMessageToPlayerGame(player, TDCMessages.getConfigMsgCommandsNopermission());
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


/* Location:              /home/sugaku/Development/Minecraft/Plugins/TownyDiscordChat/TownyDiscordChat-Build-1.0.7.jar!/com/TownyDiscordChat/TownyDiscordChat/TDCCommand.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */