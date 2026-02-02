package com.tallemalle.api.payment.controller;

import com.tallemalle.api.payment.model.PaymentMethod;
import com.tallemalle.api.payment.model.PaymentMethodEnroll;
import com.tallemalle.api.payment.model.PaymentMethodList;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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

    public ResultSet enroll(Integer userId, String alias, String billingKey) {
        try (Connection conn = ds.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("INSERT INTO payment(user_id, alias, billing_key) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            pStmt.setInt(1, userId);
            pStmt.setString(2, alias);
            pStmt.setString(3, billingKey);
            pStmt.executeUpdate();
            return pStmt.getGeneratedKeys();
        } catch (Exception e) {
            throw new RuntimeException(e);
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
