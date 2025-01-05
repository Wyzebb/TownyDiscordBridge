package com.TownyDiscordChat.TownyDiscordChat.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;


public class MetricsConfig {
    private final File file;
    private final boolean defaultEnabled;
    private String serverUUID;
    private boolean enabled;
    private boolean logErrors;
    private boolean logSentData;
    private boolean logResponseStatusText;
    private boolean didExistBefore = true;

    public MetricsConfig(File file, boolean defaultEnabled) throws IOException {
        this.file = file;
        this.defaultEnabled = defaultEnabled;

        setupConfig();
    }

    public String getServerUUID() {
        return this.serverUUID;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isLogErrorsEnabled() {
        return this.logErrors;
    }

    public boolean isLogSentDataEnabled() {
        return this.logSentData;
    }

    public boolean isLogResponseStatusTextEnabled() {
        return this.logResponseStatusText;
    }


    public boolean didExistBefore() {
        return this.didExistBefore;
    }


    private void setupConfig() throws IOException {
        if (!this.file.exists()) {
            this.didExistBefore = false;
            writeConfig();
        }
        readConfig();
        if (this.serverUUID == null) {

            writeConfig();
            readConfig();
        }
    }


    private void writeConfig() throws IOException {
        List<String> configContent = new ArrayList<>();
        configContent.add("# bStats (https://bStats.org) collects some basic information for plugin authors, like");
        configContent.add("# how many people use their plugin and their total player count. It's recommended to keep");
        configContent.add("# bStats enabled, but if you're not comfortable with this, you can turn this setting off.");
        configContent.add("# There is no performance penalty associated with having metrics enabled, and data sent to");
        configContent.add("# bStats is fully anonymous.");
        configContent.add("enabled=" + this.defaultEnabled);
        configContent.add("server-uuid=" + UUID.randomUUID().toString());
        configContent.add("log-errors=false");
        configContent.add("log-sent-data=false");
        configContent.add("log-response-status-text=false");
        writeFile(this.file, configContent);
    }


    private void readConfig() throws IOException {
        List<String> lines = readFile(this.file);
        if (lines == null) {
            throw new AssertionError("Content of newly created file is null");
        }

        this.enabled = ((Boolean) getConfigValue("enabled", lines).<Boolean>map("true"::equals).orElse(Boolean.valueOf(true))).booleanValue();
        this.serverUUID = getConfigValue("server-uuid", lines).orElse(null);
        this.logErrors = ((Boolean) getConfigValue("log-errors", lines).<Boolean>map("true"::equals).orElse(Boolean.valueOf(false))).booleanValue();
        this.logSentData = ((Boolean) getConfigValue("log-sent-data", lines).<Boolean>map("true"::equals).orElse(Boolean.valueOf(false))).booleanValue();
        this.logResponseStatusText = ((Boolean) getConfigValue("log-response-status-text", lines).<Boolean>map("true"::equals).orElse(Boolean.valueOf(false))).booleanValue();
    }


    private Optional<String> getConfigValue(String key, List<String> lines) {
        return lines.stream()
                .filter(line -> line.startsWith(key + "="))
                .map(line -> line.replaceFirst(Pattern.quote(key + "="), ""))
                .findFirst();
    }


    private List<String> readFile(File file) throws IOException {
        if (!file.exists()) {
            return null;
        }

        FileReader fileReader = new FileReader(file);
        try {
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            try {
                List<String> list = (List<String>) bufferedReader.lines().collect((Collector) Collectors.toList());
                bufferedReader.close();
                fileReader.close();
                return list;
            } catch (Throwable throwable) {
                try {
                    bufferedReader.close();
                } catch (Throwable throwable1) {
                    throwable.addSuppressed(throwable1);
                }
                throw throwable;
            }
        } catch (Throwable throwable) {
            try {
                fileReader.close();
            } catch (Throwable throwable1) {
                throwable.addSuppressed(throwable1);
            }
            throw throwable;
        }
    }

    private void writeFile(File file, List<String> lines) throws IOException {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }

        FileWriter fileWriter = new FileWriter(file);
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            try {
                for (String line : lines) {
                    bufferedWriter.write(line);
                    bufferedWriter.newLine();
                }
                bufferedWriter.close();
            } catch (Throwable throwable) {
                try {
                    bufferedWriter.close();
                } catch (Throwable throwable1) {
                    throwable.addSuppressed(throwable1);
                }
                throw throwable;
            }
            fileWriter.close();
        } catch (Throwable throwable) {
            try {
                fileWriter.close();
            } catch (Throwable throwable1) {
                throwable.addSuppressed(throwable1);
            }
            throw throwable;
        }
    }

}


/* Location:              /home/sugaku/Development/Minecraft/Plugins/TownyDiscordChat/TownyDiscordChat-Build-1.0.7.jar!/com/TownyDiscordChat/TownyDiscordChat/config/MetricsConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */