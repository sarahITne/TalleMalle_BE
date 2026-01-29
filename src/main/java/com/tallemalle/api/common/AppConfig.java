package com.tallemalle.api.common;

import com.tallemalle.api.chat.ChatController;
import com.tallemalle.api.chat.ChatRepository;
import com.tallemalle.api.chat.ChatService;
import com.zaxxer.hikari.HikariDataSource;

import java.util.HashMap;
import java.util.Map;

public class AppConfig {
    private final Map<String, Controller> controllerMap = new HashMap<>();

    private final HikariDataSource ds = new HikariDataSource();

    ChatRepository chatRepository = new ChatRepository(ds);
    ChatService chatService = new ChatService(chatRepository);
    ChatController chatController = new ChatController(chatService);

    public AppConfig() {
        ds.setJdbcUrl("jdbc:mariadb://54.180.30.27:3306/test");
        ds.setUsername("pbs");
        ds.setPassword("qwer1234");

        controllerMap.put("/chat/rooms", chatController);
        controllerMap.put("/chat/message", chatController);
    }

    public Controller getController(String uri) {
        return controllerMap.get(uri);
    }
}

