package com.tallemalle.api.payment.controller;

import com.tallemalle.api.common.DataSourceConfig;
import com.tallemalle.api.payment.model.entity.Payment;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class PaymentRepository {
    private final DataSource ds;

    public PaymentRepository(DataSource ds) {
        this.ds = ds;
    }

    public ResultSet list(Integer userId) {
        try (Connection conn = ds.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("SELECT * FROM payment WHERE user_id=?");
            pStmt.setInt(1, userId);
            return pStmt.executeQuery();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void enroll(Payment payment) {
        Session session = null;
        Transaction tx = null;
        try {
            session = DataSourceConfig.getSessionFactory().openSession();
            tx = session.beginTransaction();
            session.persist(payment);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException(e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public void setDefault(Integer userId, Integer paymentId) {
        try (Connection conn = ds.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("INSERT INTO defaultPayment(user_id, payment_id) VALUES (?, ?) ON DUPLICATE KEY UPDATE payment_id = VALUES(payment_id)");
            pStmt.setInt(1, userId);
            pStmt.setInt(2, paymentId);
            pStmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet fetchDefault(Integer userId) {
        try (Connection conn = ds.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("SELECT * FROM defaultPayment WHERE user_id=?");
            pStmt.setInt(1, userId);
            return pStmt.executeQuery();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
