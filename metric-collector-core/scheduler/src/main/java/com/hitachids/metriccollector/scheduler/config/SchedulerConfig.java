package com.hitachids.metriccollector.scheduler.config;

public class SchedulerConfig {
    private int intervalSeconds;
    private String metricType;

    public SchedulerConfig(int intervalSeconds, String metricType) {
           this.intervalSeconds = intervalSeconds;
           this.metricType = metricType;
    }
    
    public int getIntervalSeconds() {
        return intervalSeconds;
    }

    public void setIntervalSeconds(int intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
    }

    public String getMetricType() {
        return metricType;
    }

    public void setMetricType(String metricType) {
        this.metricType = metricType;
    }

}
