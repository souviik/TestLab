package com.sdastest.projects.api.beeceptor.models;

public class Shipping {
    private String method;
    private double cost;
    private String estimated_delivery;

    public Shipping() {}

    public Shipping(String method, double cost, String estimated_delivery) {
        this.method = method;
        this.cost = cost;
        this.estimated_delivery = estimated_delivery;
    }

    // Getters and Setters
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }

    public String getEstimated_delivery() { return estimated_delivery; }
    public void setEstimated_delivery(String estimated_delivery) { this.estimated_delivery = estimated_delivery; }

    @Override
    public String toString() {
        return "Shipping{" +
                "method='" + method + '\'' +
                ", cost=" + cost +
                ", estimated_delivery='" + estimated_delivery + '\'' +
                '}';
    }

    public boolean equals(Shipping other) {
        if (other == null) return false;
        return safeEquals(this.method, other.method) &&
               Double.compare(this.cost, other.cost) == 0 &&
               safeEquals(this.estimated_delivery, other.estimated_delivery);
    }

    private boolean safeEquals(String str1, String str2) {
        if (str1 == null && str2 == null) return true;
        if (str1 == null || str2 == null) return false;
        return str1.equals(str2);
    }
}