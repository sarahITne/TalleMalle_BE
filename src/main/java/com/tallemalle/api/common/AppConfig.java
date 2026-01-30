package com.tallemalle.api.common;

import com.tallemalle.api.payment.controller.PaymentController;
import com.tallemalle.api.payment.controller.PaymentRepository;
import com.tallemalle.api.payment.controller.PaymentService;
import com.zaxxer.hikari.HikariDataSource;

import java.util.HashMap;
import java.util.Map;

public class AppConfig {
    private final Map<String, Controller> controllerMap = new HashMap<>();

    private final HikariDataSource ds = new HikariDataSource();


    public AppConfig() {
        ds.setJdbcUrl(System.getenv("DB_URL"));
        ds.setUsername(System.getenv("DB_USERNAME"));
        ds.setPassword(System.getenv("DB_PASSWORD"));
        ds.setDriverClassName("org.mariadb.jdbc.Driver");
        controllerMap.put("/payment/enroll", new PaymentController(new PaymentService(new PaymentRepository(ds))));
    }

    public Controller getController(String uri) {
        return controllerMap.get(uri);
    }
}

