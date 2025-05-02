package com.hitachids.metriccollector.scheduler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hitachids.metriccollector.scheduler.config.SchedulerConfig;

public class MetricScheduler implements AutoCloseable {
    private static final Log LOG = LogFactory.getLog(MetricScheduler.class);
    private final SchedulerConfig config;
    private volatile boolean running = true;
    
    public MetricScheduler(SchedulerConfig config) {
        this.config = config;
    }

    public void start() {
        LOG.info("Starting metric scheduler with interval " + config.getIntervalSeconds());
        
        // Start collection loop in a separate thread
        Thread collectionThread = new Thread(() -> {
            int count = 0;
            while (running) {
                try {
                    // Collect metrics here
                    LOG.info("[Scheduler][MetricScheduler] start, Collecting metrics " + config.getMetricType() + ", number= " + count++);
                    Thread.sleep(config.getIntervalSeconds() * 1000L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        collectionThread.start();
    }

    @Override
    public void close() {
        running = false;
    }
}
