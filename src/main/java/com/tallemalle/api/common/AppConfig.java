package com.tallemalle.api.common;

import com.tallemalle.api.notification.NotificationController;
import com.tallemalle.api.notification.NotificationRepository;
import com.tallemalle.api.notification.NotificationService;
import com.tallemalle.api.recruit.RecruitController;
import com.tallemalle.api.recruit.RecruitRepositoryImpl;
import com.tallemalle.api.recruit.RecruitRepository;
import com.tallemalle.api.recruit.RecruitService;
import com.zaxxer.hikari.HikariDataSource;

import java.util.HashMap;
import java.util.Map;

public class AppConfig {
    private final Map<String, Controller> controllerMap = new HashMap<>();

    private final HikariDataSource ds = new HikariDataSource();

    NotificationRepository notificationRepository = new NotificationRepository(ds);
    NotificationService notificationService = new NotificationService(notificationRepository);
    NotificationController notificationController = new NotificationController(notificationService);

    // Recruit
    RecruitRepository recruitRepository = new RecruitRepositoryImpl(ds);
    RecruitService recruitService = new RecruitService(recruitRepository);
    RecruitController recruitController = new RecruitController(recruitService);

    public AppConfig() {
        ds.setDriverClassName("org.mariadb.jdbc.Driver");
        ds.setJdbcUrl("jdbc:mariadb://10.10.10.30:3306/test");
        ds.setUsername("root");
        ds.setPassword("qwer1234");

        // Notification
        controllerMap.put("/notification/list", notificationController);
        controllerMap.put("/notification/summary", notificationController);
        controllerMap.put("/notification/readall", notificationController);
        controllerMap.put("/notification/read", notificationController);

        // Recruit
        controllerMap.put("/recruits", recruitController);
    }

    public Controller getController(String uri) {
        return controllerMap.get(uri);
    }
}

