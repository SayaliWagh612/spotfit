package com.eklavya.spotfit.model;

import java.util.Objects;

/**
 * Value model representing physical location details for V2 Spaces.
 */
public class SpaceLocation {

    private String addressLine;
    private String area;
    private String city;
    private String state;
    private String postalCode;
    private String landmark;
    private double latitude;
    private double longitude;

    public SpaceLocation() {
        this.addressLine = "";
        this.area = "";
        this.city = "";
        this.state = "";
        this.postalCode = "";
        this.landmark = "";
        this.latitude = 0.0;
        this.longitude = 0.0;
    }

    public SpaceLocation(String addressLine, String area, String city, String state,
                         String postalCode, String landmark, double latitude, double longitude) {
        this.addressLine = addressLine != null ? addressLine : "";
        this.area = area != null ? area : "";
        this.city = city != null ? city : "";
        this.state = state != null ? state : "";
        this.postalCode = postalCode != null ? postalCode : "";
        this.landmark = landmark != null ? landmark : "";
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public SpaceLocation copy() {
        return new SpaceLocation(addressLine, area, city, state, postalCode, landmark, latitude, longitude);
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine != null ? addressLine : "";
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area != null ? area : "";
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city != null ? city : "";
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state != null ? state : "";
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode != null ? postalCode : "";
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark != null ? landmark : "";
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpaceLocation that = (SpaceLocation) o;
        return Double.compare(that.latitude, latitude) == 0 &&
                Double.compare(that.longitude, longitude) == 0 &&
                Objects.equals(addressLine, that.addressLine) &&
                Objects.equals(area, that.area) &&
                Objects.equals(city, that.city) &&
                Objects.equals(state, that.state) &&
                Objects.equals(postalCode, that.postalCode) &&
                Objects.equals(landmark, that.landmark);
    }

    @Override
    public int hashCode() {
        return Objects.hash(addressLine, area, city, state, postalCode, landmark, latitude, longitude);
    }
}
