package com.eklavya.spotfit.controller.admin;

import com.eklavya.spotfit.model.*;
import java.util.*;
import java.util.stream.Collectors;

public class MockDataStore {

    private static MockDataStore instance;

    private Owner currentOwner;
    private String selectedSpaceId = "ALL"; // "ALL" or specific spaceId

    private final List<Space> spaces = new ArrayList<>();
    private final List<Manager> managers = new ArrayList<>();
    private final List<Offering> offerings = new ArrayList<>();
    private final List<Resource> resources = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();
    private final List<Customer> customers = new ArrayList<>();
    private final List<Review> reviews = new ArrayList<>();
    private final List<Complaint> complaints = new ArrayList<>();
    private final List<Payment> payments = new ArrayList<>();
    private final List<Notification> notifications = new ArrayList<>();
    private final List<ManagerInvitation> invitations = new ArrayList<>();
    private final List<ActivityLog> activityLogs = new ArrayList<>();
    private final List<ActivityType> activityTypes = new ArrayList<>();
    private final List<SpaceType> spaceTypes = new ArrayList<>();

    private MockDataStore() {
        seedData();
    }

    public static synchronized MockDataStore getInstance() {
        if (instance == null) {
            instance = new MockDataStore();
        }
        return instance;
    }

    private void seedData() {
        // Owner
        currentOwner = new Owner("owner-1", "Eklavya Sharma", "owner@spotfit.com", "+91 98765 00000",
                "Rhythm Creative Business");

        // Managers
        Manager mgr1 = new Manager("mgr-1", "space-1", "Rahul Sharma", "rahul@spotfit.com", "+91 98765 43210", "ACTIVE",
                "2026-01-15");
        Manager mgr2 = new Manager("mgr-2", "space-2", "Priya Nair", "priya@spotfit.com", "+91 98765 12345", "ACTIVE",
                "2026-02-01");
        managers.add(mgr1);
        managers.add(mgr2);

        // Spaces
        Space s1 = new Space(
                "space-1", "owner-1", "Rhythm Creator Studio", "Creativity", "Content Creation", "Creator Studio",
                "State-of-the-art photography and creator studio equipped with professional lighting, acoustics, and backdrops.",
                "102 Creative Hub, SG Highway", "Ahmedabad", 25, "pixel@spotfit.com", "+91 98765 43210",
                "Mon - Sat", "08:00 AM", "09:00 PM", "No smoking. Treat equipment with care.",
                "24h full refund cancellation policy.",
                Arrays.asList("High Speed Wi-Fi", "Air Conditioning", "Professional Lighting", "Soundproofing",
                        "Changing Room"),
                "ACTIVE", "mgr-1", 4.8, 142, 125000.0);

        Space s2 = new Space(
                "space-2", "owner-1", "Rhythm Podcast Studio", "Creativity", "Podcasts", "Podcast Studio",
                "Professional soundproof podcast rooms for recording, editing and creative workshops.",
                "405 Harmony Complex, Bodakdev", "Ahmedabad", 20, "podcast@spotfit.com", "+91 98765 12345",
                "Mon - Sun", "08:00 AM", "10:00 PM", "Handle microphones carefully. No smoking.",
                "12h cancellation policy.",
                Arrays.asList("Podcast Microphones", "Audio Mixer", "Soundproofing", "Editing Workstation",
                        "Air Conditioning"),
                "ACTIVE", "mgr-2", 4.9, 98, 88000.0);

        spaces.add(s1);
        spaces.add(s2);

        // Taxonomy
        activityTypes.add(new ActivityType("act-1", "Creativity", "Content Creation"));
        activityTypes.add(new ActivityType("act-2", "Creativity", "Podcasts"));
        activityTypes.add(new ActivityType("act-3", "Creativity", "Art"));
        activityTypes.add(new ActivityType("act-4", "Creativity", "Music"));

        activityTypes.add(new ActivityType("act-5", "Sports", "Indoor Sports"));
        activityTypes.add(new ActivityType("act-6", "Sports", "Outdoor Sports"));
        activityTypes.add(new ActivityType("act-7", "Sports", "Fitness Sports"));
        activityTypes.add(new ActivityType("act-8", "Sports", "Adventure Activities"));

        activityTypes.add(new ActivityType("act-9", "Wellness", "Yoga"));
        activityTypes.add(new ActivityType("act-10", "Wellness", "Meditation"));
        activityTypes.add(new ActivityType("act-11", "Wellness", "Fitness"));
        activityTypes.add(new ActivityType("act-12", "Wellness", "Therapy & Relaxation"));

        activityTypes.add(new ActivityType("act-13", "Study & Workspace", "Library"));
        activityTypes.add(new ActivityType("act-14", "Study & Workspace", "Study Room"));
        activityTypes.add(new ActivityType("act-15", "Study & Workspace", "Coworking Space"));
        activityTypes.add(new ActivityType("act-16", "Study & Workspace", "Meeting Room"));

        spaceTypes.add(new SpaceType("st-1", "act-1", "Creator Studio"));
        spaceTypes.add(new SpaceType("st-2", "act-2", "Podcast Studio"));
        spaceTypes.add(new SpaceType("st-3", "act-3", "Art Workshop"));
        spaceTypes.add(new SpaceType("st-4", "act-4", "Practice Room"));
        spaceTypes.add(new SpaceType("st-5", "act-5", "Badminton Court"));
        spaceTypes.add(new SpaceType("st-6", "act-9", "Yoga Studio"));
        spaceTypes.add(new SpaceType("st-7", "act-15", "Shared Workspace"));

        // Offerings
        offerings.add(new Offering("off-1", "space-1", "Creator Studio Pass", "HOURLY", "hour", 1500.0, 90, 5,
                "Access to main studio with RGB lighting.", true));
        offerings.add(new Offering("off-2", "space-1", "Podcast Recording", "HOURLY", "hour", 2000.0, 90, 4,
                "Acoustically isolated podcast suite.", true));
        offerings.add(new Offering("off-3", "space-1", "Art Workshop Space", "HOURLY", "hour", 1800.0, 120, 10,
                "Spacious art studio with natural sunlight.", true));

        offerings.add(new Offering("off-4", "space-2", "Podcast Recording", "SLOT", "hour", 1200.0, 60, 4,
                "Soundproof recording suite with microphones and mixer.", true));
        offerings.add(new Offering("off-5", "space-2", "Podcast Workshop", "WORKSHOP", "person", 800.0, 120, 15,
                "Practical podcast production workshop.", true));

        // Resources
        resources.add(new Resource("res-1", "space-1", "off-1", "Studio Room A", "Creator Room", "AVAILABLE"));
        resources.add(new Resource("res-2", "space-1", "off-2", "Podcast Room 1", "Audio Suite", "OCCUPIED"));
        resources.add(new Resource("res-3", "space-1", "off-3", "Art Studio B", "Workshop Room", "AVAILABLE"));
        resources.add(new Resource("res-4", "space-1", "off-1", "Editing Suite 1", "Post Production", "MAINTENANCE"));

        resources.add(new Resource("res-5", "space-2", "off-4", "Podcast Room A", "Audio Suite", "RESERVED"));
        resources.add(new Resource("res-6", "space-2", "off-5", "Workshop Room", "Creative Workshop", "AVAILABLE"));

        // Customers
        customers.add(new Customer("cust-1", "Neha Shah", "neha.s@gmail.com", "+91 98980 11111", 5, 7500.0));
        customers.add(new Customer("cust-2", "Aarav Mehta", "aarav.m@gmail.com", "+91 98980 22222", 3, 6000.0));
        customers.add(new Customer("cust-3", "Riya Kapoor", "riya.k@gmail.com", "+91 98980 33333", 2, 3600.0));
        customers.add(new Customer("cust-4", "Dev Malhotra", "dev.m@gmail.com", "+91 98980 44444", 4, 4800.0));

        // Shared Bookings
        bookings.add(new Booking(
                "SPF-2852", "space-1", "cust-1", "Neha Shah", "neha.s@gmail.com", "+91 98980 11111",
                "Creator Studio Pass", "res-1", "Studio Room A", "285201", "Today", "10:00 AM - 11:30 AM",
                1500.0, "SUCCESSFUL", "CONFIRMED", null, null));

        bookings.add(new Booking(
                "SPF-2841", "space-1", "cust-2", "Aarav Mehta", "aarav.m@gmail.com", "+91 98980 22222",
                "Podcast Recording", "res-2", "Podcast Room 1", "284102", "Today", "02:00 PM - 03:30 PM",
                2000.0, "SUCCESSFUL", "CHECKED_IN", "02:05 PM", null));

        bookings.add(new Booking(
                "SPF-2848", "space-1", "cust-3", "Riya Kapoor", "riya.k@gmail.com", "+91 98980 33333",
                "Art Workshop Space", "res-3", "Art Studio B", "284803", "Yesterday", "11:00 AM - 01:00 PM",
                1800.0, "SUCCESSFUL", "COMPLETED", "11:02 AM", "01:00 PM"));

        bookings.add(new Booking(
                "SPF-3901", "space-2", "cust-4", "Dev Malhotra", "dev.m@gmail.com", "+91 98980 44444",
                "Podcast Recording", "res-5", "Podcast Room A", "390104", "Today", "09:00 AM - 10:00 AM",
                1200.0, "SUCCESSFUL", "CONFIRMED", null, null));

        // Payments
        payments.add(new Payment("pay-1", "SPF-2852", "space-1", 1500.0, "UPI (Razorpay Mock)", "SUCCESSFUL",
                "Today 09:45 AM", "pay_MOCK285201"));
        payments.add(new Payment("pay-2", "SPF-2841", "space-1", 2000.0, "Credit Card (Razorpay Mock)", "SUCCESSFUL",
                "Today 01:30 PM", "pay_MOCK284102"));
        payments.add(new Payment("pay-3", "SPF-2848", "space-1", 1800.0, "Net Banking (Razorpay Mock)", "SUCCESSFUL",
                "Yesterday 10:15 AM", "pay_MOCK284803"));
        payments.add(new Payment("pay-4", "SPF-3901", "space-2", 1200.0, "UPI (Razorpay Mock)", "SUCCESSFUL",
                "Today 08:30 AM", "pay_MOCK390104"));

        // Complaints
        complaints.add(new Complaint(
                "CMP-101", "space-1", "SPF-2841", "Aarav Mehta", "OPERATIONAL_CLEANLINESS", "HIGH",
                "Microphone Stand Loose & Dust in Podcast Room",
                "The microphone stand was loose and dust on the acoustic panels.",
                "IN_PROGRESS", "Manager cleaned the room and replaced mic stand.", null, false, "Today 02:15 PM",
                null));

        complaints.add(new Complaint(
                "CMP-102", "space-1", "SPF-2848", "Riya Kapoor", "FINANCIAL_REFUND", "MEDIUM",
                "Overcharge Request for Art Session", "Customer requested partial refund due to 15 min delayed entry.",
                "ESCALATED", "Forwarded to Owner for refund authorization.",
                "Approved 10% refund credit for next booking.", true, "Yesterday 02:00 PM", null));

        // Reviews
        reviews.add(new Review(
                "REV-1", "space-1", "SPF-2848", "Riya Kapoor", 5,
                "Loved the natural light in Art Studio B! The staff was super helpful.",
                "Yesterday", "REPLIED", "Thank you Riya! Looking forward to having you back.",
                "Glad you enjoyed our art studio!", null));

        reviews.add(new Review(
                "REV-2", "space-1", "SPF-2841", "Aarav Mehta", 4,
                "Podcast gear is high quality. Room was quiet and well soundproofed.",
                "Today", "PUBLISHED", null, null, null));

        reviews.add(new Review(
                "REV-3", "space-2", "SPF-3901", "Dev Malhotra", 5,
                "Clear audio, helpful setup and a very comfortable recording room.",
                "Today", "PUBLISHED", null, null, null));

        // Activity Logs
        activityLogs.add(new ActivityLog("log-1", "space-1", "Space Manager", "Rahul Sharma", "CHECK_IN",
                "Checked in booking SPF-2841 for customer Aarav Mehta", "Today 02:05 PM"));
        activityLogs.add(new ActivityLog("log-2", "space-1", "Space Manager", "Rahul Sharma", "RESOURCE_UPDATE",
                "Marked Editing Suite 1 as MAINTENANCE", "Today 11:30 AM"));

        // Notifications
        notifications.add(new Notification("notif-1", "OWNER", "owner-1", "New Booking Confirmed",
                "Booking SPF-2852 created for Pixel Photography Studio", "Today 09:45 AM", false));
        notifications.add(new Notification("notif-2", "MANAGER", "mgr-1", "Customer Checked In",
                "Aarav Mehta checked in for Podcast Recording", "Today 02:05 PM", true));
    }

    // Getters & Scope Handlers
    public Owner getCurrentOwner() {
        return currentOwner;
    }

    public String getSelectedSpaceId() {
        return selectedSpaceId;
    }

    public void setSelectedSpaceId(String spaceId) {
        this.selectedSpaceId = spaceId;
    }

    public Space getSelectedSpace() {
        if ("ALL".equalsIgnoreCase(selectedSpaceId) || selectedSpaceId == null) {
            return null;
        }
        return spaces.stream().filter(s -> s.getSpaceId().equals(selectedSpaceId)).findFirst().orElse(null);
    }

    public List<Space> getSpaces() {
        return spaces;
    }

    public List<Space> getSpacesForOwner() {
        return spaces;
    }

    public List<Manager> getManagers() {
        return managers;
    }

    public Manager getManagerForSpace(String spaceId) {
        return managers.stream()
                .filter(m -> spaceId.equals(m.getSpaceId()) && "ACTIVE".equals(m.getStatus()))
                .findFirst()
                .orElse(null);
    }

    public List<Offering> getOfferingsForSpace(String spaceId) {
        if ("ALL".equalsIgnoreCase(spaceId) || spaceId == null) {
            return offerings;
        }
        return offerings.stream().filter(o -> spaceId.equals(o.getSpaceId())).collect(Collectors.toList());
    }

    public List<Resource> getResourcesForSpace(String spaceId) {
        if ("ALL".equalsIgnoreCase(spaceId) || spaceId == null) {
            return resources;
        }
        return resources.stream().filter(r -> spaceId.equals(r.getSpaceId())).collect(Collectors.toList());
    }

    public List<Booking> getBookingsForScope(String spaceId) {
        if ("ALL".equalsIgnoreCase(spaceId) || spaceId == null) {
            return bookings;
        }
        return bookings.stream().filter(b -> spaceId.equals(b.getSpaceId())).collect(Collectors.toList());
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public List<Review> getReviewsForScope(String spaceId) {
        if ("ALL".equalsIgnoreCase(spaceId) || spaceId == null) {
            return reviews;
        }
        return reviews.stream().filter(r -> spaceId.equals(r.getSpaceId())).collect(Collectors.toList());
    }

    public List<Complaint> getComplaintsForScope(String spaceId) {
        if ("ALL".equalsIgnoreCase(spaceId) || spaceId == null) {
            return complaints;
        }
        return complaints.stream().filter(c -> spaceId.equals(c.getSpaceId())).collect(Collectors.toList());
    }

    public List<Payment> getPaymentsForScope(String spaceId) {
        if ("ALL".equalsIgnoreCase(spaceId) || spaceId == null) {
            return payments;
        }
        return payments.stream().filter(p -> spaceId.equals(p.getSpaceId())).collect(Collectors.toList());
    }

    public List<ActivityLog> getActivityLogsForScope(String spaceId) {
        if ("ALL".equalsIgnoreCase(spaceId) || spaceId == null) {
            return activityLogs;
        }
        return activityLogs.stream().filter(l -> spaceId.equals(l.getSpaceId())).collect(Collectors.toList());
    }

    public List<ManagerInvitation> getInvitations() {
        return invitations;
    }

    public List<ActivityType> getActivityTypesForCategory(String category) {
        return activityTypes.stream().filter(a -> category.equalsIgnoreCase(a.getCategory()))
                .collect(Collectors.toList());
    }

    public List<SpaceType> getSpaceTypesForActivityType(String activityTypeId) {
        return spaceTypes.stream().filter(st -> activityTypeId.equals(st.getActivityTypeId()))
                .collect(Collectors.toList());
    }

    // Data Mutation Actions
    public void addBooking(Booking b) {
        bookings.add(b);
    }

    public void addSpace(Space space) {
        spaces.add(space);
    }

    public void addOffering(Offering offering) {
        offerings.add(offering);
    }

    public void addResource(Resource resource) {
        resources.add(resource);
    }

    public void addActivityLog(String spaceId, String actorRole, String actorName, String action, String details) {
        String logId = "log-" + (activityLogs.size() + 1);
        String timestamp = "Just now";
        activityLogs.add(0, new ActivityLog(logId, spaceId, actorRole, actorName, action, details, timestamp));
    }

    public void addInvitation(ManagerInvitation invite) {
        invitations.add(invite);
    }

    public void addManager(Manager mgr) {
        managers.add(mgr);
    }

    public Booking findBookingById(String bookingId) {
        return bookings.stream().filter(b -> b.getBookingId().equalsIgnoreCase(bookingId.trim())).findFirst()
                .orElse(null);
    }
}
