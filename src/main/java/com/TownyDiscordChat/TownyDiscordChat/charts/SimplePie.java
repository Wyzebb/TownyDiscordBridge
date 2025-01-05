package com.TownyDiscordChat.TownyDiscordChat.charts;

import com.TownyDiscordChat.TownyDiscordChat.json.JsonObjectBuilder;

import java.util.concurrent.Callable;


public class SimplePie
        extends CustomChart {
    private final Callable<String> callable;

    public SimplePie(String chartId, Callable<String> callable) {
        super(chartId);
        this.callable = callable;
    }


    protected JsonObjectBuilder.JsonObject getChartData() throws Exception {
        String value = this.callable.call();
        if (value == null || value.isEmpty()) {
            return null;
        }
        return (new JsonObjectBuilder())
                .appendField("value", value)
                .build();
    }
}


/* Location:              /home/sugaku/Development/Minecraft/Plugins/TownyDiscordChat/TownyDiscordChat-Build-1.0.7.jar!/com/TownyDiscordChat/TownyDiscordChat/charts/SimplePie.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */