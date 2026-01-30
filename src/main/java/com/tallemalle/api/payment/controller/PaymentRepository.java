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

    public PaymentMethodList.Response list(PaymentMethodList.Request req) {
        return new PaymentMethodList.Response(0, null);
    }

    public PaymentMethodEnroll.Response enroll(PaymentMethodEnroll.Request req) {
        Integer defaultMethodId = -1;
        List<PaymentMethod> paymentMethods = new ArrayList<>();

        Integer userId = Integer.parseInt(req.getUserId());
        try (ResultSet enrollRs = enrollInternal(req.getAlias(), req.getBillingKey(), userId)) {
            if (enrollRs.next() && req.getAsDefault()) {
                Integer paymentId = enrollRs.getInt("insert_id");
                setDefaultInternal(userId, paymentId);
            }
            try (ResultSet defaultRs = fetchDefaultMethod(userId)) {
                if (defaultRs.next()) {
                    defaultMethodId = defaultRs.getInt("payment_id");
                }
            }
            try (ResultSet listRs = listInternal(userId)) {
                while (listRs.next()) {
                    paymentMethods.add(new PaymentMethod(listRs.getInt("id"), listRs.getString("alias")));
                }
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new PaymentMethodEnroll.Response(defaultMethodId, paymentMethods);
    }

    private ResultSet listInternal(Integer userId) {
        try (Connection conn = ds.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("SELECT * FROM payment WHERE user_id=?");
            pStmt.setInt(1, userId);
            return pStmt.executeQuery();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ResultSet enrollInternal(String alias, String billingKey, Integer userId) {
        try (Connection conn = ds.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("INSERT INTO payment(alias, billing_key, user_id) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            pStmt.setString(1, alias);
            pStmt.setString(2, billingKey);
            pStmt.setInt(3, userId);
            pStmt.executeUpdate();
            return pStmt.getGeneratedKeys();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setDefaultInternal(Integer userId, Integer paymentId) {
        try (Connection conn = ds.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("INSERT INTO defaultPayment(user_id, payment_id) VALUES (?, ?) ON DUPLICATE KEY UPDATE payment_id = VALUES(payment_id)");
            pStmt.setInt(1, userId);
            pStmt.setInt(2, paymentId);
            pStmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private ResultSet fetchDefaultMethod(Integer userId) {
        try (Connection conn = ds.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("SELECT * FROM defaultPayment WHERE user_id=?");
            pStmt.setInt(1, userId);
            return pStmt.executeQuery();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
