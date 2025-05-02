package com.hitachids.metriccollector.module.manager.scheduler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CollectorProcessManager {
    private static final Log LOG = LogFactory.getLog(CollectorProcessManager.class);
    private final Map<String, Process> runningCollectors = new ConcurrentHashMap<>();
    private final Map<String, Thread> outputReaders = new ConcurrentHashMap<>();

    public CollectorProcessManager() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOG.info("[Module Manager][CollectorProcessManager] Shutdown hook triggered, stopping all collectors...");
            stopAllCollectors();
        }));
    }

    public boolean isCollectorRunning(String metricType) {
        return runningCollectors.containsKey(metricType) && runningCollectors.get(metricType).isAlive();
    }

    public void startCollector(String metricType, String binaryPath) throws Exception {
        LOG.info("[Module Manager][CollectorProcessManager] startCollector, metricType: " + metricType
                + ", binaryPath: " + binaryPath);
        if (isCollectorRunning(metricType))
            return;

        ProcessBuilder pb = new ProcessBuilder(binaryPath);
        pb.redirectErrorStream(true); // Merge stderr into stdout
        Process process = pb.start();
        Thread readerThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    LOG.info("[" + metricType + " collector process] " + line);
                }
            } catch (Exception e) {
                if (!Thread.currentThread().isInterrupted()) {
                    LOG.error(
                            "[Module Manager][CollectorProcessManager] startColector, error reading process output of metric collector: "
                                    + metricType,
                            e);
                }
            }
        });
        readerThread.setDaemon(true);
        readerThread.start();

        runningCollectors.put(metricType, process);
        outputReaders.put(metricType, readerThread);
        LOG.info(String.format(
                "[Module Manager][CollectorProcessManager] startCollector, started collector process for metric type: %s",
                metricType));
    }

    public void stopCollector(String metricType) {
        LOG.info("[Module Manager][CollectorProcessManager] stopCollector, metricType: " + metricType);
        Process process = runningCollectors.get(metricType);
        if (process != null && process.isAlive()) {
            process.destroy();
            runningCollectors.remove(metricType);
            Thread readerThread = outputReaders.remove(metricType);
            if (readerThread != null) {
                readerThread.interrupt();
            }

            LOG.info(String.format(
                    "[Module Manager][CollectorProcessManager] stopCollector, stopped collector process for metric type: %s",
                    metricType));
        }
    }

    public void stopCollectorsForDisabled(Iterable<String> disabledMetricTypes) {
        for (String metricType : disabledMetricTypes) {
            stopCollector(metricType);
        }
    }

    public void cleanupDeadProcesses() {
        runningCollectors.entrySet().removeIf(entry -> !entry.getValue().isAlive());
    }

    protected void stopAllCollectors() {
        // Create a copy of keys to avoid ConcurrentModificationException
        for (String metricType : runningCollectors.keySet().toArray(new String[0])) {
            try {
                stopCollector(metricType);
            } catch (Exception e) {
                LOG.error("[Module Manager][CollectorProcessManager] Error stopping collector: " + metricType, e);
            }
        }
    }

    public Map<String, Process> getRunningCollectors() {
        return runningCollectors;
    }
}
