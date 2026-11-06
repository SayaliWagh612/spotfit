package com.eklavya.spotfit.model;

/**
 * Lightweight space representation used by the User-side JavaFX screens
 * (view.user package, e.g. SpaceListing/SpaceDetails) while they run
 * against mock data.
 *
 * NOTE: This is intentionally kept separate from {@link Space}, which is
 * the richer Manager-side/Firestore-shaped model already used across the
 * owner and spaceManager packages. Renamed on merge (was "Space" in the
 * user-side project) purely to avoid a class-name collision — once
 * Firebase is wired up, the User screens should be updated to read
 * directly from {@link Space} (plus {@link Offering}) instead of this
 * mock card.
 */
public class UserSpaceCard {

    private String name;
    private String rating;
    private int reviews;

    private String location;
    private String distance;

    private int price;
    private String pricingUnit;

    private String amenities;
    private String imagePath;

    public UserSpaceCard(
            String name,
            String rating,
            int reviews,
            String location,
            String distance,
            int price,
            String pricingUnit,
            String amenities,
            String imagePath) {

        this.name = name;
        this.rating = rating;
        this.reviews = reviews;
        this.location = location;
        this.distance = distance;
        this.price = price;
        this.pricingUnit = pricingUnit;
        this.amenities = amenities;
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public String getRating() {
        return rating;
    }

    public int getReviews() {
        return reviews;
    }

    public String getLocation() {
        return location;
    }

    public String getDistance() {
        return distance;
    }

    public int getPrice() {
        return price;
    }

    public String getPricingUnit() {
        return pricingUnit;
    }

    public String getAmenities() {
        return amenities;
    }

    public String getImagePath() {
        return imagePath;
    }
}

