package com.example.myapplication;

public class Property {
    private final int propertyId; // Changed to camelCase
    private final String eircode;
    private final String link;

    // Constructor
    public Property(int propertyId, String eircode, String link) {
        this.propertyId = propertyId; // Changed to camelCase
        this.eircode = eircode;
        this.link = link;
    }

    // Getter methods for propertyId, eircode, link
    public int getPropertyId() {
        return propertyId; // Changed to camelCase
    }

    public String getEircode() {
        return eircode;
    }

    public String getLink() {
        return link;
    }

    // Optional: toString method for easier debugging
    @Override
    public String toString() {
        return "Property{" +
                "propertyId=" + propertyId +
                ", eircode='" + eircode + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}
