package com.TownyDiscordChat.TownyDiscordChat.charts;

import com.TownyDiscordChat.TownyDiscordChat.json.JsonObjectBuilder;

import java.util.Map;
import java.util.concurrent.Callable;


public class DrilldownPie
        extends CustomChart {
    private final Callable<Map<String, Map<String, Integer>>> callable;

    public DrilldownPie(String chartId, Callable<Map<String, Map<String, Integer>>> callable) {
        super(chartId);
        this.callable = callable;
    }


    public JsonObjectBuilder.JsonObject getChartData() throws Exception {
        JsonObjectBuilder valuesBuilder = new JsonObjectBuilder();

        Map<String, Map<String, Integer>> map = this.callable.call();
        if (map == null || map.isEmpty()) {
            return null;
        }
        boolean reallyAllSkipped = true;
        for (Map.Entry<String, Map<String, Integer>> entryValues : map.entrySet()) {
            JsonObjectBuilder valueBuilder = new JsonObjectBuilder();
            boolean allSkipped = true;
            for (Map.Entry<String, Integer> valueEntry : (Iterable<Map.Entry<String, Integer>>) ((Map) map.get(entryValues.getKey())).entrySet()) {
                valueBuilder.appendField(valueEntry.getKey(), ((Integer) valueEntry.getValue()).intValue());
                allSkipped = false;
            }
            if (!allSkipped) {
                reallyAllSkipped = false;
                valuesBuilder.appendField(entryValues.getKey(), valueBuilder.build());
            }
        }
        if (reallyAllSkipped) {
            return null;
        }

        return (new JsonObjectBuilder())
                .appendField("values", valuesBuilder.build())
                .build();
    }
}


/* Location:              /home/sugaku/Development/Minecraft/Plugins/TownyDiscordChat/TownyDiscordChat-Build-1.0.7.jar!/com/TownyDiscordChat/TownyDiscordChat/charts/DrilldownPie.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */