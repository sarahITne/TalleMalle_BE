package com.tallemalle.api.common;

import com.tallemalle.api.notice.NoticeController;
import com.tallemalle.api.notice.NoticeRepository;
import com.tallemalle.api.notice.NoticeService;
import com.tallemalle.api.notification.NotificationController;
import com.tallemalle.api.notification.NotificationRepository;
import com.tallemalle.api.notification.NotificationService;
import com.tallemalle.api.payment.controller.PaymentController;
import com.tallemalle.api.payment.controller.PaymentRepository;
import com.tallemalle.api.payment.controller.PaymentService;
import com.tallemalle.api.user.UserController;
import com.tallemalle.api.user.UserRepository;
import com.tallemalle.api.user.UserService;
import com.tallemalle.api.chat.ChatController;
import com.tallemalle.api.chat.ChatRepository;
import com.tallemalle.api.chat.ChatService;
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
    // 결제
    private final PaymentRepository paymentRepository = new PaymentRepository(ds);
    private final PaymentService paymentService = new PaymentService(paymentRepository);
    private final PaymentController paymentController = new PaymentController(paymentService);
    // 채팅
    private final ChatRepository chatRepository = new ChatRepository(ds);
    private final ChatService chatService = new ChatService(chatRepository);
    private final ChatController chatController = new ChatController(chatService);

    public AppConfig() {
        ds.setDriverClassName("org.mariadb.jdbc.Driver");
        ds.setJdbcUrl(System.getenv("DB_URL"));
        ds.setUsername(System.getenv("DB_USERNAME"));
        ds.setPassword(System.getenv("DB_PASSWORD"));

        // User
        controllerMap.put("/user/login", userController);
        controllerMap.put("/user/signup", userController);

        // Chat
        controllerMap.put("/chat/rooms", chatController);
        controllerMap.put("/chat/message", chatController);

        // Notification
        controllerMap.put("/notification/list", notificationController);
        controllerMap.put("/notification/summary", notificationController);
        controllerMap.put("/notification/readall", notificationController);
        controllerMap.put("/notification/readonly", notificationController);

        // notice
        controllerMap.put("/notice", noticeController);
        controllerMap.put("/notice/read", noticeController);

        // payment
        controllerMap.put("/payment/list", paymentController);
        controllerMap.put("/payment/enroll", paymentController);
    }

    public Controller getController(String uri) {
        return controllerMap.get(uri);
    }
}

