package com.TownyDiscordChat.TownyDiscordChat.charts;

import com.TownyDiscordChat.TownyDiscordChat.json.JsonObjectBuilder;

import java.util.Map;
import java.util.concurrent.Callable;


public class MultiLineChart
        extends CustomChart {
    private final Callable<Map<String, Integer>> callable;

    public MultiLineChart(String chartId, Callable<Map<String, Integer>> callable) {
        super(chartId);
        this.callable = callable;
    }


    protected JsonObjectBuilder.JsonObject getChartData() throws Exception {
        JsonObjectBuilder valuesBuilder = new JsonObjectBuilder();

        Map<String, Integer> map = this.callable.call();
        if (map == null || map.isEmpty()) {
            return null;
        }
        boolean allSkipped = true;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (((Integer) entry.getValue()).intValue() == 0) {
                continue;
            }
            allSkipped = false;
            valuesBuilder.appendField(entry.getKey(), ((Integer) entry.getValue()).intValue());
        }
        if (allSkipped) {
            return null;
        }

        return (new JsonObjectBuilder())
                .appendField("values", valuesBuilder.build())
                .build();
    }
}


/* Location:              /home/sugaku/Development/Minecraft/Plugins/TownyDiscordChat/TownyDiscordChat-Build-1.0.7.jar!/com/TownyDiscordChat/TownyDiscordChat/charts/MultiLineChart.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */