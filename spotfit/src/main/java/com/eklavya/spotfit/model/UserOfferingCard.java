package com.eklavya.spotfit.model;

/**
 * Lightweight offering representation used by the User-side JavaFX screens
 * (view.user package) while they run against mock data.
 *
 * NOTE: This is intentionally kept separate from {@link Offering}, which is
 * the richer Manager-side/Firestore-shaped model already used across the
 * owner and spaceManager packages. Renamed on merge (was "Offering" in the
 * user-side project) purely to avoid a class-name collision — once Firebase
 * is wired up, the User screens should be updated to read directly from
 * {@link Offering} instead of this mock card.
 */
public class UserOfferingCard {

    private String id;
    private String name;
    private String description;
    private String bookingType;
    private double price;
    private String pricingUnit;

    public UserOfferingCard(
            String id,
            String name,
            String description,
            String bookingType,
            double price,
            String pricingUnit) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.bookingType = bookingType;
        this.price = price;
        this.pricingUnit = pricingUnit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPricingUnit() {
        return pricingUnit;
    }

    public void setPricingUnit(String pricingUnit) {
        this.pricingUnit = pricingUnit;
    }
}
