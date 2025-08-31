package com.sdastest.projects.api.beeceptor.models;

public class Payment {
    private String method;
    private String transaction_id;
    private double amount;
    private String currency;

    public Payment() {}

    public Payment(String method, String transaction_id, double amount, String currency) {
        this.method = method;
        this.transaction_id = transaction_id;
        this.amount = amount;
        this.currency = currency;
    }

    // Getters and Setters
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getTransaction_id() { return transaction_id; }
    public void setTransaction_id(String transaction_id) { this.transaction_id = transaction_id; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    @Override
    public String toString() {
        return "Payment{" +
                "method='" + method + '\'' +
                ", transaction_id='" + transaction_id + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                '}';
    }

    public boolean equals(Payment other) {
        if (other == null) return false;
        return safeEquals(this.method, other.method) &&
               safeEquals(this.transaction_id, other.transaction_id) &&
               Double.compare(this.amount, other.amount) == 0 &&
               safeEquals(this.currency, other.currency);
    }

    private boolean safeEquals(String str1, String str2) {
        if (str1 == null && str2 == null) return true;
        if (str1 == null || str2 == null) return false;
        return str1.equals(str2);
    }
}