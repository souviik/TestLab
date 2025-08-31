package com.sdastest.projects.api.beeceptor.models;

public class Address {
    private String street;
    private String city;
    private String state;
    private String zipcode;
    private String country;

    public Address() {}

    public Address(String street, String city, String state, String zipcode, String country) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
        this.country = country;
    }

    // Getters and Setters
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getZipcode() { return zipcode; }
    public void setZipcode(String zipcode) { this.zipcode = zipcode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    @Override
    public String toString() {
        return "Address{" +
                "street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zipcode='" + zipcode + '\'' +
                ", country='" + country + '\'' +
                '}';
    }

    public boolean equals(Address other) {
        if (other == null) return false;
        return safeEquals(this.street, other.street) &&
               safeEquals(this.city, other.city) &&
               safeEquals(this.state, other.state) &&
               safeEquals(this.zipcode, other.zipcode) &&
               safeEquals(this.country, other.country);
    }

    private boolean safeEquals(String str1, String str2) {
        if (str1 == null && str2 == null) return true;
        if (str1 == null || str2 == null) return false;
        return str1.equals(str2);
    }
}