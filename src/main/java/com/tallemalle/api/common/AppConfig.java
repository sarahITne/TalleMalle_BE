package com.tallemalle.api.common;

import com.tallemalle.api.notification.NotificationController;
import com.tallemalle.api.notification.NotificationRepository;
import com.tallemalle.api.notification.NotificationService;
import com.zaxxer.hikari.HikariDataSource;

import java.util.HashMap;
import java.util.Map;

public class AppConfig {
    private final Map<String, Controller> controllerMap = new HashMap<>();

    private final HikariDataSource ds = new HikariDataSource();

    NotificationRepository notificationRepository = new NotificationRepository(ds);
    NotificationService notificationService = new NotificationService(notificationRepository);
    NotificationController notificationController = new NotificationController(notificationService);

    public AppConfig() {
        ds.setJdbcUrl("jdbc:mariadb://100.100.100.60:3306/test");
        ds.setUsername("root");
        ds.setPassword("qwer1234");

        controllerMap.put("/notification/list", notificationController);
        controllerMap.put("/notification/summary", notificationController);
        controllerMap.put("/notification/readall", notificationController);
    }

    public Controller getController(String uri) {
        return controllerMap.get(uri);
    }
}

