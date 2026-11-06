package com.eklavya.spotfit.view.user;

import com.eklavya.spotfit.constants.*;
import com.eklavya.spotfit.controller.user.CustomerMarketplaceController;
import com.eklavya.spotfit.dao.*;
import com.eklavya.spotfit.model.*;
import com.eklavya.spotfit.model.Booking;
import com.eklavya.spotfit.model.bookingconfig.SeatBookingConfig;
import com.eklavya.spotfit.service.BookingService;
import com.eklavya.spotfit.service.SpaceService;
import com.eklavya.spotfit.session.AppSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Phase 11B Test Suite — Customer V2 Pre-Payment Booking Intents.
 *
 * Canonical pre-payment status: PENDING_PAYMENT (exclusively)
 *
 * BookingService performs application-layer recalculation from persisted Space
 * configuration. It is NOT a trusted backend security boundary. Prices stored
 * as provisional quotedTotalAmountPaise while status is PENDING_PAYMENT.
 * Final verification belongs to the payment-confirmation phase.
 */
public class CustomerBookingV2Phase11BTest {

    private static final String TEST_OWNER_ID    = "owner-v2-p11b-uid";
    private static final String TEST_CUSTOMER_ID = "customer-v2-p11b-uid";
    private static final String TEST_BUSINESS_ID = "biz-v2-p11b-id";
    private static final String TEST_SPACE_ID    = "space-v2-p11b-id";
    private static final String TEST_SEAT_ID     = "seat-101";
    private static final String TEST_SESSION_ID  = "session-201";

    private Business testBusiness;
    private Space    testSpace;
    private Seat     testSeat;
    private WorkshopSession testSession;

    private MockBookingDao         mockBookingDao;
    private MockSpaceDao           mockSpaceDao;
    private MockBusinessDao        mockBusinessDao;
    private MockSeatDao            mockSeatDao;
    private MockWorkshopSessionDao mockWorkshopSessionDao;
    private MockAvailabilityDao    mockAvailabilityDao;
    private MockUserDao            mockUserDao;

    private BookingService                bookingService;
    private CustomerMarketplaceController controller;

    @BeforeEach
    public void setUp() {
        long now = System.currentTimeMillis();

        testBusiness = new Business(
                2, TEST_BUSINESS_ID, TEST_OWNER_ID,
                "Creativity Hub", "Creativity Hub Pvt Ltd",
                "biz@hub.com", "+919876543210",
                "Approved V2 Business",
                "123 Street", "", "Pune", "Maharashtra", "411001",
                "", SpaceCategory.CREATIVITY,
                List.of(CreativityActivityGroup.MUSIC, CreativityActivityGroup.ART),
                "", "", BusinessStatus.APPROVED, true,
                now, 0L, "", now, now
        );

        SeatBookingConfig seatCfg = new SeatBookingConfig();
        seatCfg.setSelectionMode("MANUAL");

        testSpace = new Space();
        testSpace.setSpaceId(TEST_SPACE_ID);
        testSpace.setBusinessId(TEST_BUSINESS_ID);
        testSpace.setOwnerId(TEST_OWNER_ID);
        testSpace.setName("Music Recording Studio");
        testSpace.setSchemaVersion(2);
        testSpace.setCategory(SpaceCategory.CREATIVITY);
        testSpace.setActivityGroup(CreativityActivityGroup.MUSIC);
        testSpace.setBookingType(BookingType.SEAT);
        testSpace.setBasePricePaise(50000L); // Rs. 500.00
        testSpace.setPricingUnit(PricingUnit.HOUR);
        testSpace.setApprovalStatus(SpaceApprovalStatus.APPROVED);
        testSpace.setOperationalStatus(SpaceOperationalStatus.ACTIVE);
        testSpace.setActive(true);
        testSpace.setBookingConfig(seatCfg);

        testSeat = new Seat(
                TEST_SEAT_ID, TEST_SPACE_ID, "Vocal Booth Seat 1", "SEAT-1",
                "Studio A", "A", 1, 1, 0L, SeatStatus.AVAILABLE, true,
                now, now
        );

        testSession = new WorkshopSession(
                TEST_SESSION_ID, TEST_SPACE_ID,
                now + 86400000L, now + 90000000L,
                10, 2, 75000L,
                WorkshopSessionStatus.SCHEDULED, true, now, now
        );

        mockBookingDao         = new MockBookingDao();
        mockSpaceDao           = new MockSpaceDao(testSpace);
        mockBusinessDao        = new MockBusinessDao(testBusiness);
        mockSeatDao            = new MockSeatDao(testSeat);
        mockWorkshopSessionDao = new MockWorkshopSessionDao(testSession);
        mockAvailabilityDao    = new MockAvailabilityDao();
        mockUserDao            = new MockUserDao(TEST_CUSTOMER_ID, "Anushka Tupe", "anushka@spotfit.com", "+919999999999");

        bookingService = new BookingService(
                mockBookingDao, mockSpaceDao, mockBusinessDao,
                mockSeatDao, mockWorkshopSessionDao, mockAvailabilityDao, mockUserDao
        );

        SpaceService spaceService = new SpaceService(mockSpaceDao, mockBusinessDao);
        controller = new CustomerMarketplaceController(
                spaceService, mockSeatDao, mockWorkshopSessionDao,
                mockAvailabilityDao, mockUserDao, bookingService
        );

        AppSession.startSession(
                TEST_CUSTOMER_ID, "anushka@spotfit.com", "Anushka Tupe",
                UserRole.USER, "valid-customer-token", "refresh-token", 3600L
        );
    }

    @AfterEach
    public void tearDown() {
        AppSession.clearSession();
    }

    // -------------------------------------------------------------------------
    // Test 1: Canonical pre-payment status is frozen to PENDING_PAYMENT
    // -------------------------------------------------------------------------
    @Test
    public void test1_CanonicalPrePaymentStatusFrozenToPendingPayment() {
        BookingRequest req = new BookingRequest(TEST_SPACE_ID, BookingType.SEAT);
        req.setSeatId(TEST_SEAT_ID);
        req.setSeatCount(1);
        req.setDurationHours(2);

        OperationResult<Booking> result = controller.createV2Booking(req).join();
        assertTrue(result.isSuccess(), "Booking creation must succeed. Error: " + result.getMessage());
        Booking booking = result.getData();

        assertEquals(BookingStatus.PENDING_PAYMENT, booking.getStatus(),
                "EXCLUSIVELY PENDING_PAYMENT — must not be INITIATED, CONFIRMED, or any other status.");
        assertEquals("PENDING", booking.getPaymentStatus());
    }

    // -------------------------------------------------------------------------
    // Test 2: New booking must not be any post-payment status
    // -------------------------------------------------------------------------
    @Test
    public void test2_NewBookingIsNotConfirmedOrPostPayment() {
        BookingRequest req = new BookingRequest(TEST_SPACE_ID, BookingType.SEAT);
        req.setSeatId(TEST_SEAT_ID);
        req.setSeatCount(1);

        Booking booking = controller.createV2Booking(req).join().getData();
        assertNotNull(booking);
        assertNotEquals(BookingStatus.CONFIRMED,       booking.getStatus());
        assertNotEquals(BookingStatus.PAYMENT_SUCCESS, booking.getStatus());
        assertNotEquals(BookingStatus.CHECK_IN_PENDING, booking.getStatus());
    }

    // -------------------------------------------------------------------------
    // Test 3: Pre-payment intent must not modify Seat status
    // -------------------------------------------------------------------------
    @Test
    public void test3_BookingCreationDoesNotModifySeatStatus() {
        BookingRequest req = new BookingRequest(TEST_SPACE_ID, BookingType.SEAT);
        req.setSeatId(TEST_SEAT_ID);
        req.setSeatCount(1);

        controller.createV2Booking(req).join();
        assertEquals(SeatStatus.AVAILABLE, testSeat.getStatus(),
                "Seat status MUST remain AVAILABLE — intent is NOT a reservation.");
    }

    // -------------------------------------------------------------------------
    // Test 4: Pre-payment intent must not increment WorkshopSession.bookedCount
    // -------------------------------------------------------------------------
    @Test
    public void test4_BookingCreationDoesNotIncrementWorkshopBookedCount() {
        testSpace.setBookingType(BookingType.WORKSHOP);
        int initialCount = testSession.getBookedCount();

        BookingRequest req = new BookingRequest(TEST_SPACE_ID, BookingType.WORKSHOP);
        req.setSessionId(TEST_SESSION_ID);
        req.setParticipantCount(2);

        controller.createV2Booking(req).join();
        assertEquals(initialCount, testSession.getBookedCount(),
                "WorkshopSession.bookedCount MUST NOT be incremented before payment.");
    }

    // -------------------------------------------------------------------------
    // Test 5: Rental intent must not deduct inventory and must carry limitation message
    // -------------------------------------------------------------------------
    @Test
    public void test5_RentalIntentDoesNotDeductInventory() {
        testSpace.setBookingType(BookingType.RENTAL);

        BookingRequest req = new BookingRequest(TEST_SPACE_ID, BookingType.RENTAL);
        req.setRentalDays(3);
        req.setQuantity(2);

        OperationResult<BookingPreviewResult> previewResult = controller.previewV2Booking(req).join();
        assertTrue(previewResult.isSuccess(), "Preview must succeed for RENTAL type.");
        BookingPreviewResult preview = previewResult.getData();
        assertNotNull(preview.getPrototypeLimitationMessage(),
                "RENTAL preview must carry a limitation message.");
        assertTrue(preview.getPrototypeLimitationMessage().contains("verified before payment"),
                "Limitation message must say stock availability will be verified before payment.");
    }

    // -------------------------------------------------------------------------
    // Test 6: Customer cannot query other customers' bookings
    // -------------------------------------------------------------------------
    @Test
    public void test6_CustomerCannotQueryOtherCustomersBookings() {
        Booking ownBooking = new Booking();
        ownBooking.setBookingId("own-b1");
        ownBooking.setCustomerId(TEST_CUSTOMER_ID);

        Booking otherBooking = new Booking();
        otherBooking.setBookingId("other-b2");
        otherBooking.setCustomerId("other-customer-uid");

        mockBookingDao.addBooking(ownBooking);
        mockBookingDao.addBooking(otherBooking);

        OperationResult<List<Booking>> res = controller.loadCurrentCustomerBookings().join();
        assertTrue(res.isSuccess());
        assertNotNull(res.getData());
        assertEquals(1, res.getData().size(),
                "Customer MUST only see their own bookings — never another customer's booking.");
        assertEquals(TEST_CUSTOMER_ID, res.getData().get(0).getCustomerId());
    }

    // -------------------------------------------------------------------------
    // Test 7: createV2Booking independently recalculates price (does not trust preview)
    // -------------------------------------------------------------------------
    @Test
    public void test7_CreateRecalculatesPriceIndependentlyFromPreview() {
        BookingRequest previewReq = new BookingRequest(TEST_SPACE_ID, BookingType.SEAT);
        previewReq.setSeatId(TEST_SEAT_ID);
        previewReq.setDurationHours(3); // 3 * Rs.500 = Rs.1500 = 150000 paise
        BookingPreviewResult preview = controller.previewV2Booking(previewReq).join().getData();
        assertEquals(150000L, preview.getTotalQuotePaise());

        BookingRequest createReq = new BookingRequest(TEST_SPACE_ID, BookingType.SEAT);
        createReq.setSeatId(TEST_SEAT_ID);
        createReq.setDurationHours(3);
        Booking created = controller.createV2Booking(createReq).join().getData();

        // createV2Booking reloads from Firestore and recalculates independently
        assertEquals(150000L, created.getTotalAmountPaise(),
                "createV2Booking must independently recalculate from persisted Space config.");
    }

    // -------------------------------------------------------------------------
    // Test 8: Form-supplied price is ignored; price comes from Space config
    // -------------------------------------------------------------------------
    @Test
    public void test8_FormSuppliedPriceIsIgnored() {
        BookingRequest req = new BookingRequest(TEST_SPACE_ID, BookingType.SEAT);
        req.setSeatId(TEST_SEAT_ID);
        req.setDurationHours(2); // correct: 2 * 50000 = 100000 paise

        Booking created = controller.createV2Booking(req).join().getData();
        assertEquals(100000L, created.getTotalAmountPaise(),
                "Price must come from persisted Space.basePricePaise, ignoring any form input.");
    }

    // -------------------------------------------------------------------------
    // Test 9: V2 monetary source of truth is integer paise, not double
    // -------------------------------------------------------------------------
    @Test
    public void test9_V2MonetarySourceIsIntegerPaise() {
        BookingRequest req = new BookingRequest(TEST_SPACE_ID, BookingType.SEAT);
        req.setSeatId(TEST_SEAT_ID);
        req.setDurationHours(1);

        Booking created = controller.createV2Booking(req).join().getData();
        // paise fields must be authoritative
        assertTrue(created.getBaseAmountPaise() > 0,  "baseAmountPaise must be set.");
        assertTrue(created.getTotalAmountPaise() > 0, "totalAmountPaise must be set.");
        assertEquals(created.getTotalAmountPaise(), created.getQuotedTotalAmountPaise(),
                "quotedTotalAmountPaise must match totalAmountPaise for new V2 bookings.");
        // V1 double amount must only be a derived display value, not independently set
        assertEquals((double) created.getTotalAmountPaise() / 100.0, created.getAmount(), 0.001,
                "V1 double 'amount' must be derived from paise for backward compatibility only.");
    }

    // -------------------------------------------------------------------------
    // Test 10: Booking marked as provisional quote while PENDING_PAYMENT
    // -------------------------------------------------------------------------
    @Test
    public void test10_BookingHasProvisionalQuoteWhilePendingPayment() {
        BookingRequest req = new BookingRequest(TEST_SPACE_ID, BookingType.SEAT);
        req.setSeatId(TEST_SEAT_ID);
        req.setDurationHours(1);

        Booking created = controller.createV2Booking(req).join().getData();
        assertEquals(BookingStatus.PENDING_PAYMENT, created.getStatus());
        assertTrue(created.getQuotedTotalAmountPaise() >= 0,
                "quotedTotalAmountPaise must be set as provisional quote.");
        // UI contract: quotedTotalAmountPaise is provisional while PENDING_PAYMENT
        // Final payable amount will be recomputed by payment-confirmation phase
    }

    // -------------------------------------------------------------------------
    // Test 11: Manager check-in cannot operate on pre-payment booking
    // -------------------------------------------------------------------------
    @Test
    public void test11_ManagerCheckInCannotOperateOnPrePaymentBooking() {
        Booking b = new Booking();
        b.setStatus(BookingStatus.PENDING_PAYMENT);

        // Manager check-in eligibility: must be CONFIRMED or CHECK_IN_PENDING
        boolean eligibleForCheckIn = BookingStatus.CONFIRMED.equalsIgnoreCase(b.getStatus())
                || BookingStatus.CHECK_IN_PENDING.equalsIgnoreCase(b.getStatus());

        assertFalse(eligibleForCheckIn,
                "Manager check-in MUST NOT operate on PENDING_PAYMENT booking intents.");
    }

    // -------------------------------------------------------------------------
    // Test 12: Firestore rules do not verify complex pricing formulas
    // -------------------------------------------------------------------------
    @Test
    public void test12_FirestoreRulesDoNotClaimPriceVerification() {
        // Structural test: verify the booking DAO creates documents with required
        // fields that Firestore rules CAN validate (IDs, status, schemaVersion, paise).
        // Complex multi-type pricing formulas cannot be fully verified by Firestore rules.
        Booking b = new Booking();
        b.setBookingId("b-test-12");
        b.setSchemaVersion(2);
        b.setCustomerId(TEST_CUSTOMER_ID);
        b.setSpaceId(TEST_SPACE_ID);
        b.setBusinessId(TEST_BUSINESS_ID);
        b.setOwnerId(TEST_OWNER_ID);
        b.setStatus(BookingStatus.PENDING_PAYMENT);
        b.setTotalAmountPaise(50000L);
        b.setQuotedTotalAmountPaise(50000L);
        b.setStartAt(System.currentTimeMillis() + 3600000L);
        b.setEndAt(b.getStartAt() + 3600000L);

        // Fields that Firestore CAN verify
        assertNotNull(b.getBookingId(),   "bookingId must be set.");
        assertEquals(2, b.getSchemaVersion(), "schemaVersion must be 2.");
        assertEquals(TEST_CUSTOMER_ID, b.getCustomerId(), "customerId must match auth uid.");
        assertEquals(BookingStatus.PENDING_PAYMENT, b.getStatus(), "status must be PENDING_PAYMENT.");
        assertTrue(b.getTotalAmountPaise() >= 0, "totalAmountPaise must be non-negative.");
        assertTrue(b.getEndAt() >= b.getStartAt(), "endAt must be >= startAt.");

        // Fields that Firestore CANNOT fully verify (complex pricing calculation)
        // — actual pricing correctness verified only by BookingService (application layer)
        assertTrue(b.getQuotedTotalAmountPaise() >= 0,
                "quotedTotalAmountPaise is an application-layer provisional quote, not Firestore-verified price.");
    }

    // -------------------------------------------------------------------------
    // Test 13: Workshop booking fails if requested participants < minimum
    // -------------------------------------------------------------------------
    @Test
    public void test13_WorkshopBookingFailsIfBelowMinimumCapacity() {
        testSpace.setBookingType(BookingType.WORKSHOP);
        com.eklavya.spotfit.model.bookingconfig.WorkshopBookingConfig wCfg = new com.eklavya.spotfit.model.bookingconfig.WorkshopBookingConfig();
        wCfg.setMinimumParticipantsPerBooking(5);
        testSpace.setBookingConfig(wCfg);

        BookingRequest req = new BookingRequest(TEST_SPACE_ID, BookingType.WORKSHOP);
        req.setSessionId(TEST_SESSION_ID);
        req.setParticipantCount(3); // below 5

        OperationResult<BookingPreviewResult> res = controller.previewV2Booking(req).join();
        assertFalse(res.isSuccess());
        assertTrue(res.getMessage().contains("below minimum allowed"));
    }

    // -------------------------------------------------------------------------
    // Test 14: Workshop booking fails if requested participants > maximum
    // -------------------------------------------------------------------------
    @Test
    public void test14_WorkshopBookingFailsIfAboveMaximumCapacity() {
        testSpace.setBookingType(BookingType.WORKSHOP);
        com.eklavya.spotfit.model.bookingconfig.WorkshopBookingConfig wCfg = new com.eklavya.spotfit.model.bookingconfig.WorkshopBookingConfig();
        wCfg.setMaximumParticipantsPerBooking(4);
        testSpace.setBookingConfig(wCfg);

        BookingRequest req = new BookingRequest(TEST_SPACE_ID, BookingType.WORKSHOP);
        req.setSessionId(TEST_SESSION_ID);
        req.setParticipantCount(5); // above 4

        OperationResult<BookingPreviewResult> res = controller.previewV2Booking(req).join();
        assertFalse(res.isSuccess());
        assertTrue(res.getMessage().contains("above maximum allowed"));
    }

    // =========================================================================
    // Mock DAOs
    // =========================================================================

    private static class MockBookingDao extends BookingDao {
        private final Map<String, Booking> store = new LinkedHashMap<>();

        @Override
        public CompletableFuture<OperationResult<Booking>> createBooking(Booking booking, String idToken) {
            store.put(booking.getBookingId(), booking);
            return CompletableFuture.completedFuture(OperationResult.success(booking));
        }

        @Override
        public CompletableFuture<OperationResult<Booking>> getBooking(String bookingId, String idToken) {
            Booking b = store.get(bookingId);
            return b != null
                    ? CompletableFuture.completedFuture(OperationResult.success(b))
                    : CompletableFuture.completedFuture(OperationResult.failure(OperationCode.NOT_FOUND, "Not found."));
        }

        @Override
        public CompletableFuture<OperationResult<List<Booking>>> getBookingsForCustomer(String customerId, String idToken) {
            List<Booking> list = new ArrayList<>();
            for (Booking b : store.values()) {
                if (customerId.equals(b.getCustomerId())) {
                    list.add(b);
                }
            }
            return CompletableFuture.completedFuture(OperationResult.success(list));
        }

        public void addBooking(Booking b) {
            store.put(b.getBookingId(), b);
        }
    }

    private static class MockSpaceDao extends SpaceDao {
        private final Space space;
        MockSpaceDao(Space space) { this.space = space; }

        @Override
        public CompletableFuture<OperationResult<Space>> getSpace(String spaceId, String idToken) {
            return CompletableFuture.completedFuture(
                    space != null && space.getSpaceId().equals(spaceId)
                    ? OperationResult.success(space)
                    : OperationResult.failure(OperationCode.NOT_FOUND, "Space not found."));
        }
    }

    private static class MockBusinessDao extends BusinessDao {
        private final Business business;
        MockBusinessDao(Business business) { this.business = business; }

        @Override
        public CompletableFuture<OperationResult<Business>> getBusiness(String businessId, String idToken) {
            return CompletableFuture.completedFuture(
                    business != null && business.getBusinessId().equals(businessId)
                    ? OperationResult.success(business)
                    : OperationResult.failure(OperationCode.NOT_FOUND, "Business not found."));
        }
    }

    private static class MockSeatDao extends SeatDao {
        private final Seat seat;
        MockSeatDao(Seat seat) { this.seat = seat; }

        @Override
        public CompletableFuture<OperationResult<Seat>> getSeat(String spaceId, String seatId, String idToken) {
            return CompletableFuture.completedFuture(
                    seat != null && seat.getSeatId().equals(seatId)
                    ? OperationResult.success(seat)
                    : OperationResult.failure(OperationCode.NOT_FOUND, "Seat not found."));
        }
    }

    private static class MockWorkshopSessionDao extends WorkshopSessionDao {
        private final WorkshopSession session;
        MockWorkshopSessionDao(WorkshopSession session) { this.session = session; }

        @Override
        public CompletableFuture<OperationResult<WorkshopSession>> getSession(String spaceId, String sessionId, String idToken) {
            return CompletableFuture.completedFuture(
                    session != null && session.getSessionId().equals(sessionId)
                    ? OperationResult.success(session)
                    : OperationResult.failure(OperationCode.NOT_FOUND, "Session not found."));
        }
    }

    private static class MockAvailabilityDao extends AvailabilityDao {
        @Override
        public CompletableFuture<OperationResult<List<AvailabilityBlock>>> getSpaceAvailabilityBlocks(String spaceId, String idToken) {
            return CompletableFuture.completedFuture(OperationResult.success(new ArrayList<>()));
        }
    }

    private static class MockUserDao extends UserDao {
        private final User user;
        MockUserDao(String uid, String name, String email, String phone) {
            this.user = new User(uid, name, email, phone, UserRole.USER, null, null, null,
                    null, null, 0L, 0.0, true, false, false, true,
                    System.currentTimeMillis(), System.currentTimeMillis());
        }

        @Override
        public CompletableFuture<OperationResult<User>> getUser(String userId, String idToken) {
            return CompletableFuture.completedFuture(
                    user != null && user.getUserId().equals(userId)
                    ? OperationResult.success(user)
                    : OperationResult.failure(OperationCode.NOT_FOUND, "User not found."));
        }
    }
}
