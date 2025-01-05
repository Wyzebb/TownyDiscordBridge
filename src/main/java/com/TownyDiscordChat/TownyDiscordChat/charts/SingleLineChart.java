package com.TownyDiscordChat.TownyDiscordChat.charts;

import com.TownyDiscordChat.TownyDiscordChat.json.JsonObjectBuilder;

import java.util.concurrent.Callable;


public class SingleLineChart
        extends CustomChart {
    private final Callable<Integer> callable;

    public SingleLineChart(String chartId, Callable<Integer> callable) {
        super(chartId);
        this.callable = callable;
    }


    protected JsonObjectBuilder.JsonObject getChartData() throws Exception {
        int value = ((Integer) this.callable.call()).intValue();
        if (value == 0) {
            return null;
        }
        return (new JsonObjectBuilder())
                .appendField("value", value)
                .build();
    }
}


/* Location:              /home/sugaku/Development/Minecraft/Plugins/TownyDiscordChat/TownyDiscordChat-Build-1.0.7.jar!/com/TownyDiscordChat/TownyDiscordChat/charts/SingleLineChart.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */