package com.tallemalle.api.common;

import com.tallemalle.api.payment.model.entity.Payment;
import com.tallemalle.api.user.model.entity.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import java.util.Properties;

public class DataSourceConfig {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();

            Properties settings = new Properties();
            settings.put(Environment.DRIVER, "org.mariadb.jdbc.Driver");
            settings.put(Environment.URL, System.getenv("DB_URL"));
            settings.put(Environment.USER, System.getenv("DB_USERNAME"));
            settings.put(Environment.PASS, System.getenv("DB_PASSWORD"));

            settings.put(Environment.CONNECTION_PROVIDER, "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");

            settings.put("hibernate.hikari.minimumIdle", "5");
            settings.put("hibernate.hikari.maximumPoolSize", "20");
            settings.put("hibernate.hikari.idleTimeout", "30000");
            settings.put("hibernate.hikari.poolName", "TalleMalleHikariPool");

            settings.put(Environment.SHOW_SQL, "true");
            settings.put(Environment.HBM2DDL_AUTO, "update");

            configuration.setProperties(settings);

            configuration.addAnnotatedClass(User.class);
            configuration.addAnnotatedClass(Payment.class);

            return configuration.buildSessionFactory();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("SessionFactory 초기화 실패: " + e.getMessage());
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        sessionFactory.close();
    }
}
