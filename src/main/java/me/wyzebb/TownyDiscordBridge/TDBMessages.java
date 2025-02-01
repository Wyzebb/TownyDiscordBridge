package me.wyzebb.TownyDiscordBridge;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.google.common.base.Preconditions;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
import me.wyzebb.TownyDiscordBridge.util.SimpleGetters;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static me.wyzebb.TownyDiscordBridge.TownyDiscordBridge.plugin;


public class TDBMessages {
    public static void sendMessageToPlayerGameAndLog(UUID uuid, String msg) {
        if (uuid == null || msg == null) {
            throw new NullPointerException();
        }

        sendMessageToPlayerGame(Bukkit.getOfflinePlayer(uuid), msg);
        sendMessageToDiscordLogChannel(uuid, msg);
    }


    public static void sendMessageToPlayerGame(Player player, String msg) {
        if (player == null || msg == null) {
            throw new NullPointerException();
        }

        player.sendMessage(getPluginPrefix() + " " + msg);
    }


    public static void sendMessageToPlayerGame(OfflinePlayer offlinePlayer, String msg) {
        if (offlinePlayer == null || msg == null) {
            throw new NullPointerException();
        }

        if (offlinePlayer.getPlayer() != null) {
            offlinePlayer.getPlayer().sendMessage(getPluginPrefix() + " " + msg);
        }
    }


    public static void sendMessageToDiscordLogChannel(String msg) {
        if (msg == null || plugin.config.getString("messages.DiscordLogChannel") == null) {
            throw new NullPointerException();
        }

        String discordLogChannel = plugin.config.getString("messages.DiscordLogChannel");

        if (!("0".equals(discordLogChannel))) {
            Guild guild = DiscordSRV.getPlugin().getMainGuild();

            assert discordLogChannel != null;
            TextChannel textChannel = guild.getTextChannelById(discordLogChannel);

            String timezone = getConfigTimeZone();
            String dateTimeFormat = getConfigDateTimeFormat();

            ZoneId zoneId = ZoneId.of(timezone);
            ZonedDateTime zonedDateTime = Instant.now().atZone(zoneId);
            String logTime = DateTimeFormatter.ofPattern(dateTimeFormat).format(zonedDateTime);

            String logMsg = String.join("\n", new CharSequence[]{logTime, "--------------------------------------------------", "Message: " +


                    getPluginPrefix() + " " + msg, "--------------------------------------------------"});

            DiscordUtil.sendMessage(textChannel, ChatColor.stripColor(logMsg));
            plugin.getLogger().info(ChatColor.stripColor(logMsg));
        }
    }


    public static void sendMessageToDiscordLogChannel(UUID uuid, String msg) {
        if (msg == null || uuid == null || plugin.config.getString("messages.DiscordLogChannel") == null) {
            throw new NullPointerException();
        }

        String discordLogChannel = plugin.config.getString("messages.DiscordLogChannel");

        if (!("0".equals(discordLogChannel))) {
            OfflinePlayer offlinePlayer = Preconditions.checkNotNull(Bukkit.getOfflinePlayer(uuid));
            Guild guild = Preconditions.checkNotNull(DiscordSRV.getPlugin().getMainGuild());

            assert discordLogChannel != null;
            TextChannel textChannel = guild.getTextChannelById(discordLogChannel);
            String discordId = Preconditions.checkNotNull(SimpleGetters.getLinkedId(offlinePlayer));

            Member member = Preconditions.checkNotNull(DiscordUtil.getMemberById(discordId));
            List<Role> roles = Preconditions.checkNotNull(member).getRoles();

            String timezone = getConfigTimeZone();
            String dateTimeFormat = getConfigDateTimeFormat();

            ZoneId zoneId = ZoneId.of(timezone);
            ZonedDateTime zonedDateTime = Instant.now().atZone(zoneId);
            String logTime = DateTimeFormatter.ofPattern(dateTimeFormat).format(zonedDateTime);

            String logMsg = String.join("\n", new CharSequence[]{logTime, "--------------------------------------------------", "Minecraft Name: " + offlinePlayer


                    .getName(), "Minecraft UUID: " + String.valueOf(uuid), "Discord Name: " + member

                    .getUser().getAsMention(), "Discord ID: " +
                    SimpleGetters.getLinkedId(offlinePlayer), "Discord Roles: " + String.valueOf(roles), "Message: " +

                    getPluginPrefix() + " " + msg, "--------------------------------------------------"});


            DiscordUtil.sendMessage(textChannel, ChatColor.stripColor(logMsg));
            plugin.getLogger().info(ChatColor.stripColor(logMsg));
        }
    }


    public static String getPluginPrefix() {
        String prefix = Preconditions.checkNotNull(plugin.config.getString("messages.Prefix"));
        return ChatColor.translateAlternateColorCodes('&', prefix);
    }


    public static String getConfigMsgCommandWait() {
        return getConfigMsg("messages.commands.Wait");
    }


    public static String getConfigMsgCommandNoPerms() {
        return getConfigMsg("messages.commands.NoPermission");
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
        String plainText = Preconditions.checkNotNull(plugin.config.getString(ymlPath));

        return ChatColor.translateAlternateColorCodes('&', plainText);
    }


    private static String getConfigTimeZone() {
        String timezone = plugin.config.getString("messages.TimeZone");
        return Preconditions.checkNotNull(timezone);
    }


    private static String getConfigDateTimeFormat() {
        String dateTimeFormat = plugin.config.getString("messages.DateFormat");
        return Preconditions.checkNotNull(dateTimeFormat);
    }
}