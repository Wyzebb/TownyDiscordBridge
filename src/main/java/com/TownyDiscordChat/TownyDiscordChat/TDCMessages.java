package com.TownyDiscordChat.TownyDiscordChat;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.google.common.base.Preconditions;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;


public class TDCMessages {
    public static void sendMessageToPlayerGameAndLog(UUID uUID, String message) {
        Preconditions.checkNotNull(uUID);
        Preconditions.checkNotNull(message);

        sendMessageToPlayerGame(Bukkit.getOfflinePlayer(uUID), message);
        sendMessageToDiscordLogChannel(uUID, message);
    }


    public static void sendMessageToPlayerGame(Player player, String message) {
        Preconditions.checkNotNull(player);
        Preconditions.checkNotNull(message);

        player.sendMessage(getPluginPrefix() + " " + getPluginPrefix());
    }


    public static void sendMessageToPlayerGame(OfflinePlayer offlinePlayer, String message) {
        Preconditions.checkNotNull(offlinePlayer);
        Preconditions.checkNotNull(message);

        if (offlinePlayer.getPlayer() != null) {
            offlinePlayer.getPlayer().sendMessage(getPluginPrefix() + " " + getPluginPrefix());
        }
    }


    public static void sendMessageToDiscordLogChannel(String message) {
        Preconditions.checkNotNull(message);

        String discordLogTextChannelId = (String) Preconditions.checkNotNull(Main.plugin.config.getString("messages.DiscordLogTextChannelId"));

        if (!"0".equals(discordLogTextChannelId)) {
            Guild guild = (Guild) Preconditions.checkNotNull(DiscordSRV.getPlugin().getMainGuild());
            TextChannel textChannel = (TextChannel) Preconditions.checkNotNull(guild.getTextChannelById(discordLogTextChannelId));

            String timeZone = getConfigTimeZone();
            String dateTimeFormat = getConfigDateTimeFormat();

            Instant instant = Instant.now();
            ZoneId zoneId = ZoneId.of(timeZone);
            ZonedDateTime zonedDateTime = instant.atZone(zoneId);
            String logTime = DateTimeFormatter.ofPattern(dateTimeFormat).format(zonedDateTime);

            String logMsg = String.join("\n", new CharSequence[]{logTime, "--------------------------------------------------", "Message: " +


                    getPluginPrefix() + " " + message, "--------------------------------------------------"});


            DiscordUtil.sendMessage(textChannel, ChatColor.stripColor(logMsg));
            Main.plugin.getLogger().info(ChatColor.stripColor(logMsg));
        }
    }


    public static void sendMessageToDiscordLogChannel(UUID uUID, String message) {
        Preconditions.checkNotNull(message);

        String discordLogTextChannelId = (String) Preconditions.checkNotNull(Main.plugin.config.getString("messages.DiscordLogTextChannelId"));

        if (!"0".equals(discordLogTextChannelId)) {
            OfflinePlayer offlinePlayer = (OfflinePlayer) Preconditions.checkNotNull(Bukkit.getOfflinePlayer(uUID));
            Guild guild = (Guild) Preconditions.checkNotNull(DiscordSRV.getPlugin().getMainGuild());
            TextChannel textChannel = (TextChannel) Preconditions.checkNotNull(guild.getTextChannelById(discordLogTextChannelId));
            String discordId = (String) Preconditions.checkNotNull(TDCManager.getLinkedId(offlinePlayer));
            Member member = (Member) Preconditions.checkNotNull(DiscordUtil.getMemberById(discordId));
            List<Role> roles = ((Member) Preconditions.checkNotNull(member)).getRoles();

            String timeZone = getConfigTimeZone();
            String dateTimeFormat = getConfigDateTimeFormat();

            Instant instant = Instant.now();
            ZoneId zoneId = ZoneId.of(timeZone);
            ZonedDateTime zonedDateTime = instant.atZone(zoneId);
            String logTime = DateTimeFormatter.ofPattern(dateTimeFormat).format(zonedDateTime);

            String logMsg = String.join("\n", new CharSequence[]{logTime, "--------------------------------------------------", "Minecraft Name: " + offlinePlayer


                    .getName(), "Minecraft UUID: " + String.valueOf(uUID), "Discord Name: " + member

                    .getUser().getAsMention(), "Discord ID: " +
                    TDCManager.getLinkedId(offlinePlayer), "Discord Roles: " + String.valueOf(roles), "Message: " +

                    getPluginPrefix() + " " + message, "--------------------------------------------------"});


            DiscordUtil.sendMessage(textChannel, ChatColor.stripColor(logMsg));
            Main.plugin.getLogger().info(ChatColor.stripColor(logMsg));
        }
    }


    public static String getPluginPrefix() {
        String prefix = (String) Preconditions.checkNotNull(Main.plugin.config.getString("messages.Prefix"));
        return ChatColor.translateAlternateColorCodes('&', prefix);
    }


    public static String getConfigMsgCommandsPleasewait() {
        return getConfigMsg("messages.Commands.PleaseWait");
    }


    public static String getConfigMsgCommandsNopermission() {
        return getConfigMsg("messages.Commands.NoPermission");
    }


    public static String getConfigMsgRoleDoNothingSuccess() {
        return getConfigMsg("messages.Role.DoNothing.Success");
    }


    public static String getConfigMsgRoleAddSuccess() {
        return getConfigMsg("messages.Role.Add.Success");
    }


    public static String getConfigMsgRoleAddFailure() {
        return getConfigMsg("messages.Role.Add.Failure");
    }


    public static String getConfigMsgRoleCreateSuccess() {
        return getConfigMsg("messages.Role.Create.Success");
    }


    public static String getConfigMsgRoleCreateFailure() {
        return getConfigMsg("messages.Role.Create.Failure");
    }


    public static String getConfigMsgRoleDeleteSuccess() {
        return getConfigMsg("messages.Role.Delete.Success");
    }


    public static String getConfigMsgRoleDeleteFailure() {
        return getConfigMsg("messages.Role.Delete.Failure");
    }


    public static String getConfigMsgRoleRenameSuccess() {
        return getConfigMsg("messages.Role.Rename.Success");
    }


    public static String getConfigMsgRoleRenameFailure() {
        return getConfigMsg("messages.Role.Rename.Failure");
    }


    public static String getConfigMsgTextChannelCreateSuccess() {
        return getConfigMsg("messages.TextChannel.Create.Success");
    }


    public static String getConfigMsgTextChannelCreateFailure() {
        return getConfigMsg("messages.TextChannel.Create.Failure");
    }


    public static String getConfigMsgTextChannelDeleteSuccess() {
        return getConfigMsg("messages.TextChannel.Delete.Success");
    }


    public static String getConfigMsgTextChannelDeleteFailure() {
        return getConfigMsg("messages.TextChannel.Delete.Failure");
    }


    public static String getConfigMsgTextChannelRenameSuccess() {
        return getConfigMsg("messages.TextChannel.Rename.Success");
    }


    public static String getConfigMsgTextChannelRenameFailure() {
        return getConfigMsg("messages.TextChannel.Rename.Failure");
    }


    public static String getConfigMsgVoiceChannelCreateSuccess() {
        return getConfigMsg("messages.VoiceChannel.Create.Success");
    }


    public static String getConfigMsgVoiceChannelCreateFailure() {
        return getConfigMsg("messages.VoiceChannel.Create.Failure");
    }


    public static String getConfigMsgVoiceChannelDeleteSuccess() {
        return getConfigMsg("messages.VoiceChannel.Delete.Success");
    }


    public static String getConfigMsgVoiceChannelDeleteFailure() {
        return getConfigMsg("messages.VoiceChannel.Delete.Failure");
    }


    public static String getConfigMsgVoiceChannelRenameSuccess() {
        return getConfigMsg("messages.VoiceChannel.Rename.Success");
    }


    public static String getConfigMsgVoiceChannelRenameFailure() {
        return getConfigMsg("messages.VoiceChannel.Rename.Failure");
    }


    private static String getConfigMsg(String ymlPath) {
        String plainText = (String) Preconditions.checkNotNull(Main.plugin.config.getString(ymlPath));

        return ChatColor.translateAlternateColorCodes('&', plainText);
    }


    private static String getConfigTimeZone() {
        String timeZone = Main.plugin.config.getString("messages.TimeZone");
        return (String) Preconditions.checkNotNull(timeZone);
    }


    private static String getConfigDateTimeFormat() {
        String dateTimeFormat = Main.plugin.config.getString("messages.DateFormat");
        return (String) Preconditions.checkNotNull(dateTimeFormat);
    }
}


/* Location:              /home/sugaku/Development/Minecraft/Plugins/TownyDiscordChat/TownyDiscordChat-Build-1.0.7.jar!/com/TownyDiscordChat/TownyDiscordChat/TDCMessages.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */