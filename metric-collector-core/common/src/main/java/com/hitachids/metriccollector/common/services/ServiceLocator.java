package com.hitachids.metriccollector.common.services;

import java.util.ServiceLoader;

public class ServiceLocator {

    public static <T> T getService(Class<T> serviceClass) {
        return ServiceLoader.load(serviceClass).findFirst().orElse(null);
    }

}