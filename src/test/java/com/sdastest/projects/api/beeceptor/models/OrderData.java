package com.sdastest.projects.api.beeceptor.models;

import java.util.Arrays;
import java.util.List;

public class OrderData {
    private String order_id;
    private Customer customer;
    private List<Item> items;
    private Payment payment;
    private Shipping shipping;
    private String order_status;
    private String created_at;

    public OrderData() {}

    // Getters and Setters
    public String getOrder_id() { return order_id; }
    public void setOrder_id(String order_id) { this.order_id = order_id; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }

    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }

    public Shipping getShipping() { return shipping; }
    public void setShipping(Shipping shipping) { this.shipping = shipping; }

    public String getOrder_status() { return order_status; }
    public void setOrder_status(String order_status) { this.order_status = order_status; }

    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }

    @Override
    public String toString() {
        return "OrderData{" +
                "order_id='" + order_id + '\'' +
                ", customer=" + customer +
                ", items=" + items +
                ", payment=" + payment +
                ", shipping=" + shipping +
                ", order_status='" + order_status + '\'' +
                ", created_at='" + created_at + '\'' +
                '}';
    }

    // Factory method to create sample order data as per requirements
    public static OrderData createSampleOrder() {
        OrderData order = new OrderData();
        order.setOrder_id("12345");
        order.setOrder_status("processing");
        order.setCreated_at("2024-11-07T12:00:00Z");

        // Create customer
        Address address = new Address("456 Oak Street", "Metropolis", "NY", "10001", "USA");
        Customer customer = new Customer("Jane Smith", "janesmith@example.com", "1-987-654-3210", address);
        order.setCustomer(customer);

        // Create items
        Item item1 = new Item("A101", "Wireless Headphones", 1, 79.99);
        Item item2 = new Item("B202", "Smartphone Case", 2, 15.99);
        order.setItems(Arrays.asList(item1, item2));

        // Create payment
        Payment payment = new Payment("credit_card", "txn_67890", 111.97, "USD");
        order.setPayment(payment);

        // Create shipping
        Shipping shipping = new Shipping("standard", 5.99, "2024-11-15");
        order.setShipping(shipping);

        return order;
    }

    public boolean equals(OrderData other) {
        if (other == null) return false;
        return safeEquals(this.order_id, other.order_id) &&
               (this.customer != null ? this.customer.equals(other.customer) : other.customer == null) &&
               compareItems(this.items, other.items) &&
               (this.payment != null ? this.payment.equals(other.payment) : other.payment == null) &&
               (this.shipping != null ? this.shipping.equals(other.shipping) : other.shipping == null) &&
               safeEquals(this.order_status, other.order_status) &&
               safeEquals(this.created_at, other.created_at);
    }

    private boolean compareItems(List<Item> items1, List<Item> items2) {
        if (items1 == null && items2 == null) return true;
        if (items1 == null || items2 == null) return false;
        if (items1.size() != items2.size()) return false;
        
        for (int i = 0; i < items1.size(); i++) {
            if (!items1.get(i).equals(items2.get(i))) {
                return false;
            }
        }
        return true;
    }

    private boolean safeEquals(String str1, String str2) {
        if (str1 == null && str2 == null) return true;
        if (str1 == null || str2 == null) return false;
        return str1.equals(str2);
    }
}