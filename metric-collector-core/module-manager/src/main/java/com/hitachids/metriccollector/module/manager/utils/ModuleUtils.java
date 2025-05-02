package com.hitachids.metriccollector.module.manager.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ModuleUtils {
    private static final Log LOG = LogFactory.getLog(ModuleUtils.class);

    public static Optional<String> findFileByName(String baseDirectory, String fileName) {
        LOG.info("[Module Manager][ModuleUtils] findFileByName, baseDirectory: " + baseDirectory + ", fileName: " + fileName);
        File externalFile = new File(new File("").getAbsolutePath().concat("/" + baseDirectory + fileName));

        LOG.info("[Module Manager][ModuleUtils] findFileByName, checking file: " + externalFile.getAbsolutePath());

        if (externalFile.exists() && externalFile.canRead()) {
            LOG.info("[Module Manager][ModuleUtils] findFileByName, found the file: " + externalFile.getAbsolutePath());
            return Optional.of(externalFile.getAbsolutePath());
        }
        String fallbackPath = "/metric-collector-core/" + baseDirectory + fileName;

        File fallbackExternalFile = new File(new File("").getAbsolutePath().concat(fallbackPath));

        LOG.info("[Module Manager][ModuleUtils] findFileByName, checking fallback file: " + fallbackExternalFile.getAbsolutePath());

        if (fallbackExternalFile.exists() && fallbackExternalFile.canRead()) {
            LOG.info("[Module Manager][ModuleUtils] findFileByName, found the fallback file: " + fallbackExternalFile.getAbsolutePath());
            return Optional.of(fallbackExternalFile.getAbsolutePath());
        }
        return Optional.empty();
    }

    private static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win");
    }

    public static String getCollectorFileName(String metricType) {
        return metricType + "-metric-collector" + (isWindows() ? ".exe" : "");
    }
}
