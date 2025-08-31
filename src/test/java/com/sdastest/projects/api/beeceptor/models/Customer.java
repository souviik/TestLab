package com.sdastest.projects.api.beeceptor.models;

public class Customer {
    private String name;
    private String email;
    private String phone;
    private Address address;

    public Customer() {}

    public Customer(String name, String email, String phone, Address address) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    @Override
    public String toString() {
        return "Customer{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address=" + address +
                '}';
    }

    public boolean equals(Customer other) {
        if (other == null) return false;
        return safeEquals(this.name, other.name) &&
               safeEquals(this.email, other.email) &&
               safeEquals(this.phone, other.phone) &&
               (this.address != null ? this.address.equals(other.address) : other.address == null);
    }

    private boolean safeEquals(String str1, String str2) {
        if (str1 == null && str2 == null) return true;
        if (str1 == null || str2 == null) return false;
        return str1.equals(str2);
    }
}