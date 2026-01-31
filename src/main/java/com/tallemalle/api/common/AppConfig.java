package com.tallemalle.api.common;

import com.tallemalle.api.notice.NoticeController;
import com.tallemalle.api.notice.NoticeRepository;
import com.tallemalle.api.notice.NoticeService;
import com.tallemalle.api.notification.NotificationController;
import com.tallemalle.api.notification.NotificationRepository;
import com.tallemalle.api.notification.NotificationService;
import com.tallemalle.api.user.UserController;
import com.tallemalle.api.user.UserRepository;
import com.tallemalle.api.user.UserService;
import com.zaxxer.hikari.HikariDataSource;

import java.util.HashMap;
import java.util.Map;

public class AppConfig {
    private final Map<String, Controller> controllerMap = new HashMap<>();

    private final HikariDataSource ds = new HikariDataSource();

    // 유저
    private final UserRepository userRepository = new UserRepository(ds);
    private final UserService userService = new UserService(userRepository);
    private final UserController userController = new UserController(userService);

    // 알림
    private final NotificationRepository notificationRepository = new NotificationRepository(ds);
    private final NotificationService notificationService = new NotificationService(notificationRepository);
    private final NotificationController notificationController = new NotificationController(notificationService);

    // 공지사항
    private final NoticeRepository noticeRepository = new NoticeRepository(ds);
    private final NoticeService noticeService = new NoticeService(noticeRepository);
    private final NoticeController noticeController = new NoticeController(noticeService);

    public AppConfig() {
        ds.setJdbcUrl("jdbc:mariadb://127.0.0.1:3306/tallemalle");
        ds.setUsername("root");
        ds.setPassword("qwer1234");

        // User
        controllerMap.put("/user/login", userController);
        controllerMap.put("/user/signup", userController);

        // Notification
        controllerMap.put("/notification/list", notificationController);
        controllerMap.put("/notification/summary", notificationController);
        controllerMap.put("/notification/readall", notificationController);
        controllerMap.put("/notification/read", notificationController);

        // notice
        controllerMap.put("/notice", noticeController);
        controllerMap.put("/notice/read", noticeController);
    }

    public Controller getController(String uri) {
        return controllerMap.get(uri);
    }
}

