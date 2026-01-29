package com.tallemalle.api.payment.model;

public class PaymentMethod {
    private Integer id;
    private String alias;

    public PaymentMethod(Integer id, String alias) {
        this.id = id;
        this.alias = alias;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
