package com.sdastest.projects.api.beeceptor.models;

public class Item {
    private String product_id;
    private String name;
    private int quantity;
    private double price;

    public Item() {}

    public Item(String product_id, String name, int quantity, double price) {
        this.product_id = product_id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters and Setters
    public String getProduct_id() { return product_id; }
    public void setProduct_id(String product_id) { this.product_id = product_id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    @Override
    public String toString() {
        return "Item{" +
                "product_id='" + product_id + '\'' +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }

    public boolean equals(Item other) {
        if (other == null) return false;
        return safeEquals(this.product_id, other.product_id) &&
               safeEquals(this.name, other.name) &&
               this.quantity == other.quantity &&
               Double.compare(this.price, other.price) == 0;
    }

    private boolean safeEquals(String str1, String str2) {
        if (str1 == null && str2 == null) return true;
        if (str1 == null || str2 == null) return false;
        return str1.equals(str2);
    }
}