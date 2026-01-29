package com.tallemalle.api.common;

import com.zaxxer.hikari.HikariDataSource;

import java.util.HashMap;
import java.util.Map;

public class AppConfig {
    private final Map<String, Controller> controllerMap = new HashMap<>();

    private final HikariDataSource ds = new HikariDataSource();


    public AppConfig() {

    }

    public Controller getController(String uri) {
        return controllerMap.get(uri);
    }
}

