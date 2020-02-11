package io.github.complexcodegit.hidepluginsproject.managers;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import io.github.complexcodegit.hidepluginsproject.external.Metrics;

import java.util.concurrent.Callable;

public class MetricManager {
    public static void customMetrics(HidePluginsProject plugin){
        Metrics metrics = new Metrics(plugin, 5432);
        metrics.addCustomChart(new Metrics.SimplePie("used_language", new Callable<String>() {
            @Override
            public String call() throws Exception {
                return plugin.getConfig().getString("language");
            }
        }));
    }
}
