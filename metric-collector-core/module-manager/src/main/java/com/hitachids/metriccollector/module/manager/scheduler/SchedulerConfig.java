package com.hitachids.metriccollector.module.manager.scheduler;

import com.hitachids.metriccollector.common.utils.ConfigurationUtil;

import lombok.Getter;

@Getter
public class SchedulerConfig {
    private final String baseDirectory;
    private final int intervalSeconds;

    public SchedulerConfig(String baseDirectory, int intervalSeconds) {
        this.baseDirectory = baseDirectory;
        this.intervalSeconds = intervalSeconds;
    }

    public static SchedulerConfig fromProperties() {
        String collectorBinariesDirectory = ConfigurationUtil.getCollectorBinariesDirectory();
        Integer collectorSchedulerInterval = ConfigurationUtil.getCollectorSchedulerInterval();

        return new SchedulerConfig(
                collectorBinariesDirectory != null ? collectorBinariesDirectory : "collector-binaries",
                collectorSchedulerInterval != null ? collectorSchedulerInterval : 60);
    }
}
