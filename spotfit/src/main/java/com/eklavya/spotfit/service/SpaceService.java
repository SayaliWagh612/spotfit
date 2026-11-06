// package com.eklavya.spotfit.service;

// import com.eklavya.spotfit.constants.*;
// import com.eklavya.spotfit.dao.BusinessDao;
// import com.eklavya.spotfit.dao.SpaceDao;
// import com.eklavya.spotfit.model.*;
// import com.eklavya.spotfit.model.bookingconfig.*;
// import com.eklavya.spotfit.util.ValidationUtil;

// import java.time.ZoneId;
// import java.util.*;
// import java.util.concurrent.CompletableFuture;
// import java.util.regex.Pattern;

// /**
//  * Service class enforcing domain validation and business rules for Space
//  * management across V1 and V2 schemas.
//  */
// public class SpaceService {

//     private static final Pattern TIME_FORMAT_PATTERN = Pattern.compile("^([0-1][0-9]|2[0-3]):[0-5][0-9]$");
//     private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("^\\d{6}$");

//     private final SpaceDao spaceDao;
//     private final BusinessDao businessDao;

//     public SpaceService() {
//         this.spaceDao = new SpaceDao();
//         this.businessDao = new BusinessDao();
//     }

//     public SpaceService(SpaceDao spaceDao) {
//         this.spaceDao = spaceDao;
//         this.businessDao = new BusinessDao();
//     }

//     public SpaceService(SpaceDao spaceDao, BusinessDao businessDao) {
//         this.spaceDao = spaceDao;
//         this.businessDao = businessDao;
//     }

//     // ==================== V1 Methods ====================

//     public CompletableFuture<OperationResult<Space>> createSpace(
//             String ownerId,
//             String businessId,
//             String spaceName,
//             String description,
//             String category,
//             String activityGroup,
//             String spaceType,
//             String addressLine,
//             String landmark,
//             String city,
//             String state,
//             String postalCode,
//             int capacity,
//             List<String> amenities,
//             List<String> suitableActivities,
//             List<String> rules,
//             String openingTime,
//             String closingTime,
//             List<String> operatingDays,
//             String idToken) {

//         if (ValidationUtil.isBlank(ownerId)) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Owner ID is required."));
//         }
//         if (ValidationUtil.isBlank(businessId)) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Business ID is required."));
//         }

//         OperationResult<Void> validation = validateSpaceInputs(
//                 spaceName, description, category, activityGroup, spaceType,
//                 addressLine, city, state, postalCode, capacity,
//                 openingTime, closingTime, operatingDays);

//         if (validation.isFailure()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(validation.getErrorCode(), validation.getMessage()));
//         }

//         String spaceId = UUID.randomUUID().toString();
//         long now = System.currentTimeMillis();

//         Space space = new Space(
//                 spaceId,
//                 businessId.trim(),
//                 ownerId.trim(),
//                 ValidationUtil.sanitizeText(spaceName),
//                 ValidationUtil.sanitizeText(description),
//                 category.trim(),
//                 activityGroup.trim(),
//                 ValidationUtil.sanitizeText(spaceType),
//                 ValidationUtil.sanitizeText(addressLine),
//                 ValidationUtil.sanitizeText(landmark),
//                 ValidationUtil.sanitizeText(city),
//                 ValidationUtil.sanitizeText(state),
//                 ValidationUtil.sanitizeText(postalCode),
//                 capacity,
//                 amenities,
//                 suitableActivities,
//                 rules,
//                 ValidationUtil.sanitizeText(openingTime),
//                 ValidationUtil.sanitizeText(closingTime),
//                 operatingDays,
//                 SpaceStatus.DRAFT,
//                 true,
//                 now,
//                 now);

//         return spaceDao.createSpace(space, idToken);
//     }

//     public CompletableFuture<OperationResult<Space>> getSpace(String spaceId, String idToken) {
//         return spaceDao.getSpace(spaceId, idToken);
//     }

//     public CompletableFuture<OperationResult<List<Space>>> getSpacesForOwner(String ownerId, String idToken) {
//         return spaceDao.getSpacesByOwner(ownerId, idToken);
//     }

//     public CompletableFuture<OperationResult<List<Space>>> getSpacesForBusiness(String businessId, String idToken) {
//         return spaceDao.getSpacesByBusiness(businessId, idToken);
//     }

//     public CompletableFuture<OperationResult<List<Space>>> getSpacesForOwnerAndBusiness(
//             String ownerId, String businessId, String idToken) {
//         return spaceDao.getSpacesByOwnerAndBusiness(ownerId, businessId, idToken);
//     }

//     public CompletableFuture<OperationResult<Space>> updateSpace(Space space, String idToken) {
//         if (space == null) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space object cannot be null."));
//         }

//         OperationResult<Void> validation = validateSpaceInputs(
//                 space.getSpaceName(),
//                 space.getDescription(),
//                 space.getCategory(),
//                 space.getActivityGroup(),
//                 space.getSpaceType(),
//                 space.getAddressLine(),
//                 space.getCity(),
//                 space.getState(),
//                 space.getPostalCode(),
//                 space.getCapacity(),
//                 space.getOpeningTime(),
//                 space.getClosingTime(),
//                 space.getOperatingDays());

//         if (validation.isFailure()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(validation.getErrorCode(), validation.getMessage()));
//         }

//         if (!SpaceStatus.DRAFT.equalsIgnoreCase(space.getStatus())) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.PERMISSION_DENIED,
//                             "Only DRAFT spaces can be edited."));
//         }

//         space.setSpaceName(ValidationUtil.sanitizeText(space.getSpaceName()));
//         space.setDescription(ValidationUtil.sanitizeText(space.getDescription()));
//         space.setSpaceType(ValidationUtil.sanitizeText(space.getSpaceType()));
//         space.setAddressLine(ValidationUtil.sanitizeText(space.getAddressLine()));
//         space.setLandmark(ValidationUtil.sanitizeText(space.getLandmark()));
//         space.setCity(ValidationUtil.sanitizeText(space.getCity()));
//         space.setState(ValidationUtil.sanitizeText(space.getState()));
//         space.setPostalCode(ValidationUtil.sanitizeText(space.getPostalCode()));
//         space.setOpeningTime(ValidationUtil.sanitizeText(space.getOpeningTime()));
//         space.setClosingTime(ValidationUtil.sanitizeText(space.getClosingTime()));
//         space.setUpdatedAt(System.currentTimeMillis());

//         return spaceDao.updateSpace(space, idToken);
//     }

//     public OperationResult<Void> validateSpaceInputs(
//             String spaceName,
//             String description,
//             String category,
//             String activityGroup,
//             String spaceType,
//             String addressLine,
//             String city,
//             String state,
//             String postalCode,
//             int capacity,
//             String openingTime,
//             String closingTime,
//             List<String> operatingDays) {

//         if (ValidationUtil.isBlank(spaceName)) {
//             return OperationResult.failure("SPACE_NAME_REQUIRED", "Space name is required.");
//         }
//         if (ValidationUtil.isBlank(description)) {
//             return OperationResult.failure("DESCRIPTION_REQUIRED", "Description is required.");
//         }
//         if (ValidationUtil.isBlank(category) || !SpaceCategory.isValid(category)) {
//             return OperationResult.failure("CATEGORY_INVALID", "Please select a valid Space category.");
//         }
//         if (SpaceCategory.CREATIVITY.equalsIgnoreCase(category)) {
//             if (ValidationUtil.isBlank(activityGroup) || !CreativityActivityGroup.isValid(activityGroup)) {
//                 return OperationResult.failure("ACTIVITY_GROUP_INVALID",
//                         "Please select a valid activity group for Creativity.");
//             }
//         }
//         if (ValidationUtil.isBlank(spaceType)) {
//             return OperationResult.failure("SPACE_TYPE_REQUIRED", "Space type is required.");
//         }
//         if (ValidationUtil.isBlank(addressLine)) {
//             return OperationResult.failure("ADDRESS_LINE_REQUIRED", "Address line is required.");
//         }
//         if (ValidationUtil.isBlank(city)) {
//             return OperationResult.failure("CITY_REQUIRED", "City is required.");
//         }
//         if (ValidationUtil.isBlank(state)) {
//             return OperationResult.failure("STATE_REQUIRED", "State is required.");
//         }
//         if (ValidationUtil.isBlank(postalCode)) {
//             return OperationResult.failure("POSTAL_CODE_REQUIRED", "Postal code is required.");
//         }
//         if (capacity <= 0) {
//             return OperationResult.failure("CAPACITY_INVALID", "Capacity must be greater than zero.");
//         }
//         if (ValidationUtil.isBlank(openingTime) || ValidationUtil.isBlank(closingTime)) {
//             return OperationResult.failure("OPERATING_HOURS_REQUIRED", "Opening and closing times are required.");
//         }
//         if (operatingDays == null || operatingDays.isEmpty()) {
//             return OperationResult.failure("OPERATING_DAYS_REQUIRED", "Select at least one operating day.");
//         }

//         return OperationResult.success(null, "Space inputs are valid.");
//     }

//     public CompletableFuture<OperationResult<List<Space>>> getLiveCreativitySpaces(String activityGroup,
//             String idToken) {
//         if (ValidationUtil.isBlank(activityGroup) || !CreativityActivityGroup.isValid(activityGroup)) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.INVALID_ARGUMENT,
//                             "Valid controlled Creativity activity group is required."));
//         }
//         if (ValidationUtil.isBlank(idToken)) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
//         }

//         return spaceDao.getLiveCreativitySpaces(activityGroup, idToken)
//                 .thenCompose(res -> {
//                     if (res.isFailure()) {
//                         return CompletableFuture.completedFuture(
//                                 OperationResult.failure(res.getErrorCode(), res.getMessage()));
//                     }

//                     List<Space> rawSpaces = res.getData();
//                     if (rawSpaces == null || rawSpaces.isEmpty()) {
//                         return CompletableFuture.completedFuture(OperationResult.success(new ArrayList<>()));
//                     }

//                     Map<String, CompletableFuture<OperationResult<Business>>> busFutures = new HashMap<>();
//                     for (Space space : rawSpaces) {
//                         String bId = space.getBusinessId();
//                         if (bId != null && !bId.isBlank() && !busFutures.containsKey(bId)) {
//                             busFutures.put(bId, businessDao.getBusiness(bId, idToken));
//                         }
//                     }

//                     CompletableFuture<?>[] allFuturesArray = busFutures.values().toArray(new CompletableFuture[0]);

//                     return CompletableFuture.allOf(allFuturesArray).thenApply(v -> {
//                         for (var entry : busFutures.entrySet()) {
//                             OperationResult<Business> busRes = entry.getValue().join();
//                             if (busRes.isFailure()) {
//                                 String errCode = busRes.getErrorCode();
//                                 if (!OperationCode.NOT_FOUND.equalsIgnoreCase(errCode)) {
//                                     return OperationResult.<List<Space>>failure(errCode, busRes.getMessage());
//                                 }
//                             }
//                         }

//                         List<Space> validatedSpaces = new ArrayList<>();
//                         for (Space space : rawSpaces) {
//                             String bId = space.getBusinessId();
//                             if (bId == null || bId.isBlank()) {
//                                 continue;
//                             }
//                             CompletableFuture<OperationResult<Business>> fut = busFutures.get(bId);
//                             if (fut == null)
//                                 continue;

//                             OperationResult<Business> busRes = fut.join();
//                             if (busRes.isSuccess() && busRes.getData() != null) {
//                                 Business bus = busRes.getData();
//                                 if (bus.isActive()
//                                         && BusinessStatus.APPROVED.equalsIgnoreCase(bus.getVerificationStatus())
//                                         && SpaceStatus.LIVE.equalsIgnoreCase(space.getStatus())
//                                         && space.isActive()
//                                         && SpaceCategory.CREATIVITY.equalsIgnoreCase(space.getCategory())
//                                         && activityGroup.equalsIgnoreCase(space.getActivityGroup())) {
//                                     validatedSpaces.add(space);
//                                 }
//                             }
//                         }

//                         return OperationResult.success(validatedSpaces);
//                     });
//                 });
//     }

//     public CompletableFuture<OperationResult<List<Space>>> getPublicLiveCreativitySpaces(String activityGroup) {
//         if (ValidationUtil.isBlank(activityGroup) || !CreativityActivityGroup.isValid(activityGroup)) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.INVALID_ARGUMENT,
//                             "Valid controlled Creativity activity group is required."));
//         }

//         return spaceDao.getPublicLiveCreativitySpaces(activityGroup)
//                 .thenCompose(res -> {
//                     if (res.isFailure()) {
//                         return CompletableFuture.completedFuture(
//                                 OperationResult.failure(res.getErrorCode(), res.getMessage()));
//                     }

//                     List<Space> rawSpaces = res.getData();
//                     if (rawSpaces == null || rawSpaces.isEmpty()) {
//                         return CompletableFuture.completedFuture(OperationResult.success(new ArrayList<>()));
//                     }

//                     Map<String, CompletableFuture<OperationResult<Business>>> busFutures = new HashMap<>();
//                     for (Space space : rawSpaces) {
//                         String bId = space.getBusinessId();
//                         if (bId != null && !bId.isBlank() && !busFutures.containsKey(bId)) {
//                             busFutures.put(bId, businessDao.getPublicBusiness(bId));
//                         }
//                     }

//                     CompletableFuture<?>[] allFuturesArray = busFutures.values().toArray(new CompletableFuture[0]);

//                     return CompletableFuture.allOf(allFuturesArray).thenApply(v -> {
//                         for (var entry : busFutures.entrySet()) {
//                             OperationResult<Business> busRes = entry.getValue().join();
//                             if (busRes.isFailure()) {
//                                 String errCode = busRes.getErrorCode();
//                                 if (!OperationCode.NOT_FOUND.equalsIgnoreCase(errCode)) {
//                                     return OperationResult.<List<Space>>failure(errCode, busRes.getMessage());
//                                 }
//                             }
//                         }

//                         List<Space> validatedSpaces = new ArrayList<>();
//                         for (Space space : rawSpaces) {
//                             String bId = space.getBusinessId();
//                             if (bId == null || bId.isBlank()) {
//                                 continue;
//                             }
//                             CompletableFuture<OperationResult<Business>> fut = busFutures.get(bId);
//                             if (fut == null)
//                                 continue;

//                             OperationResult<Business> busRes = fut.join();
//                             if (busRes.isSuccess() && busRes.getData() != null) {
//                                 Business bus = busRes.getData();
//                                 if (bus.isActive()
//                                         && BusinessStatus.APPROVED.equalsIgnoreCase(bus.getVerificationStatus())
//                                         && SpaceStatus.LIVE.equalsIgnoreCase(space.getStatus())
//                                         && space.isActive()
//                                         && SpaceCategory.CREATIVITY.equalsIgnoreCase(space.getCategory())
//                                         && activityGroup.equalsIgnoreCase(space.getActivityGroup())) {
//                                     validatedSpaces.add(space);
//                                 }
//                             }
//                         }

//                         return OperationResult.success(validatedSpaces);
//                     });
//                 });
//     }

//     public CompletableFuture<OperationResult<List<Space>>> getVisibleV2CreativitySpaces(String activityGroup,
//             String activityName, String idToken) {
//         if (ValidationUtil.isBlank(activityGroup) || !CreativityActivityGroup.isValid(activityGroup)) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.success(Collections.emptyList()));
//         }
//         if (ValidationUtil.isBlank(idToken)) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
//         }

//         return spaceDao.getVisibleV2CreativitySpaces(activityGroup, idToken)
//                 .thenCompose(res -> {
//                     if (res.isFailure()) {
//                         return CompletableFuture.completedFuture(
//                                 OperationResult.failure(res.getErrorCode(), res.getMessage()));
//                     }

//                     List<Space> rawSpaces = res.getData();
//                     if (rawSpaces == null || rawSpaces.isEmpty()) {
//                         return CompletableFuture.completedFuture(OperationResult.success(new ArrayList<>()));
//                     }

//                     Map<String, CompletableFuture<OperationResult<Business>>> busFutures = new HashMap<>();
//                     for (Space space : rawSpaces) {
//                         String bId = space.getBusinessId();
//                         if (bId != null && !bId.isBlank() && !busFutures.containsKey(bId)) {
//                             busFutures.put(bId, businessDao.getBusiness(bId, idToken));
//                         }
//                     }

//                     CompletableFuture<?>[] allFuturesArray = busFutures.values().toArray(new CompletableFuture[0]);

//                     return CompletableFuture.allOf(allFuturesArray).thenApply(v -> {
//                         for (var entry : busFutures.entrySet()) {
//                             OperationResult<Business> busRes = entry.getValue().join();
//                             if (busRes.isFailure()) {
//                                 String errCode = busRes.getErrorCode();
//                                 if (!OperationCode.NOT_FOUND.equalsIgnoreCase(errCode)) {
//                                     return OperationResult.<List<Space>>failure(errCode, busRes.getMessage());
//                                 }
//                             }
//                         }

//                         List<Space> validatedSpaces = new ArrayList<>();
//                         for (Space space : rawSpaces) {
//                             String bId = space.getBusinessId();
//                             if (bId == null || bId.isBlank()) {
//                                 continue;
//                             }
//                             CompletableFuture<OperationResult<Business>> fut = busFutures.get(bId);
//                             if (fut == null)
//                                 continue;

//                             OperationResult<Business> busRes = fut.join();
//                             if (busRes.isSuccess() && busRes.getData() != null) {
//                                 Business bus = busRes.getData();
//                                 if (bus.getSchemaVersion() == 2
//                                         && bus.isActive()
//                                         && BusinessStatus.APPROVED.equalsIgnoreCase(bus.getVerificationStatus())
//                                         && SpaceCategory.CREATIVITY.equalsIgnoreCase(bus.getCategory())
//                                         && bus.getActivityGroups() != null
//                                         && containsIgnoreCase(bus.getActivityGroups(), space.getActivityGroup())) {

//                                     boolean matches = false;
//                                     String type = space.getBookingType() != null ? space.getBookingType().toUpperCase()
//                                             : "";
//                                     String query = activityName != null ? activityName.trim().toLowerCase() : "";

//                                     if (query.isEmpty()) {
//                                         matches = true;
//                                     } else if (query.contains("workshop") && BookingType.WORKSHOP.equals(type)) {
//                                         matches = true;
//                                     } else if (query.contains("venue") && BookingType.VENUE.equals(type)) {
//                                         matches = true;
//                                     } else if (query.contains("practice") && BookingType.PRACTICE_ROOM.equals(type)) {
//                                         matches = true;
//                                     } else if (BookingType.SLOT.equals(type)
//                                             && (query.contains("studio") || query.contains("recording")
//                                                     || query.contains("shoot") || query.contains("creation"))) {
//                                         matches = true;
//                                     } else if (space.getActivityType() != null
//                                             && space.getActivityType().toLowerCase().contains(query)) {
//                                         matches = true;
//                                     } else if (query.contains("learning") || query.contains("art")
//                                             || query.contains("craft")) {
//                                         matches = true;
//                                     } else {
//                                         matches = space.getActivityType() != null
//                                                 && space.getActivityType().equalsIgnoreCase(activityName);
//                                     }

//                                     if (matches) {
//                                         validatedSpaces.add(space);
//                                     }
//                                 }
//                             }
//                         }

//                         return OperationResult.success(validatedSpaces);
//                     });
//                 });
//     }

//     public CompletableFuture<OperationResult<List<Space>>> getPublicVisibleV2CreativitySpaces(String activityGroup,
//             String activityName) {
//         if (ValidationUtil.isBlank(activityGroup) || !CreativityActivityGroup.isValid(activityGroup)) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.success(Collections.emptyList()));
//         }

//         return spaceDao.getPublicVisibleV2CreativitySpaces(activityGroup)
//                 .thenCompose(res -> {
//                     if (res.isFailure()) {
//                         return CompletableFuture.completedFuture(
//                                 OperationResult.failure(res.getErrorCode(), res.getMessage()));
//                     }

//                     List<Space> rawSpaces = res.getData();
//                     if (rawSpaces == null || rawSpaces.isEmpty()) {
//                         return CompletableFuture.completedFuture(OperationResult.success(new ArrayList<>()));
//                     }

//                     Map<String, CompletableFuture<OperationResult<Business>>> busFutures = new HashMap<>();
//                     for (Space space : rawSpaces) {
//                         String bId = space.getBusinessId();
//                         if (bId != null && !bId.isBlank() && !busFutures.containsKey(bId)) {
//                             busFutures.put(bId, businessDao.getPublicBusiness(bId));
//                         }
//                     }

//                     CompletableFuture<?>[] allFuturesArray = busFutures.values().toArray(new CompletableFuture[0]);

//                     return CompletableFuture.allOf(allFuturesArray).thenApply(v -> {
//                         for (var entry : busFutures.entrySet()) {
//                             OperationResult<Business> busRes = entry.getValue().join();
//                             if (busRes.isFailure()) {
//                                 String errCode = busRes.getErrorCode();
//                                 if (!OperationCode.NOT_FOUND.equalsIgnoreCase(errCode)) {
//                                     return OperationResult.<List<Space>>failure(errCode, busRes.getMessage());
//                                 }
//                             }
//                         }

//                         List<Space> validatedSpaces = new ArrayList<>();
//                         for (Space space : rawSpaces) {
//                             String bId = space.getBusinessId();
//                             if (bId == null || bId.isBlank()) {
//                                 continue;
//                             }
//                             CompletableFuture<OperationResult<Business>> fut = busFutures.get(bId);
//                             if (fut == null)
//                                 continue;

//                             OperationResult<Business> busRes = fut.join();
//                             if (busRes.isSuccess() && busRes.getData() != null) {
//                                 Business bus = busRes.getData();
//                                 if (bus.getSchemaVersion() == 2
//                                         && bus.isActive()
//                                         && BusinessStatus.APPROVED.equalsIgnoreCase(bus.getVerificationStatus())
//                                         && SpaceCategory.CREATIVITY.equalsIgnoreCase(bus.getCategory())
//                                         && bus.getActivityGroups() != null
//                                         && containsIgnoreCase(bus.getActivityGroups(), space.getActivityGroup())) {

//                                     boolean matches = false;
//                                     String type = space.getBookingType() != null ? space.getBookingType().toUpperCase()
//                                             : "";
//                                     String query = activityName != null ? activityName.trim().toLowerCase() : "";

//                                     if (query.isEmpty()) {
//                                         matches = true;
//                                     } else if (query.contains("workshop") && BookingType.WORKSHOP.equals(type)) {
//                                         matches = true;
//                                     } else if (query.contains("venue") && BookingType.VENUE.equals(type)) {
//                                         matches = true;
//                                     } else if (query.contains("practice") && BookingType.PRACTICE_ROOM.equals(type)) {
//                                         matches = true;
//                                     } else if (BookingType.SLOT.equals(type)
//                                             && (query.contains("studio") || query.contains("recording")
//                                                     || query.contains("shoot") || query.contains("creation"))) {
//                                         matches = true;
//                                     } else if (space.getActivityType() != null
//                                             && space.getActivityType().toLowerCase().contains(query)) {
//                                         matches = true;
//                                     } else if (query.contains("learning") || query.contains("art")
//                                             || query.contains("craft")) {
//                                         matches = true;
//                                     } else {
//                                         matches = space.getActivityType() != null
//                                                 && space.getActivityType().equalsIgnoreCase(activityName);
//                                     }

//                                     if (matches) {
//                                         validatedSpaces.add(space);
//                                     }
//                                 }
//                             }
//                         }

//                         return OperationResult.success(validatedSpaces);
//                     });
//                 });
//     }

//     public CompletableFuture<OperationResult<Space>> getVisibleV2SpaceDetails(String spaceId, String idToken) {
//         if (ValidationUtil.isBlank(spaceId)) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
//         }
//         if (ValidationUtil.isBlank(idToken)) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
//         }

//         return spaceDao.getSpace(spaceId, idToken)
//                 .thenCompose(spaceRes -> {
//                     if (spaceRes.isFailure()) {
//                         return CompletableFuture.completedFuture(
//                                 OperationResult.failure(spaceRes.getErrorCode(), spaceRes.getMessage()));
//                     }
//                     Space space = spaceRes.getData();
//                     if (space == null) {
//                         return CompletableFuture.completedFuture(
//                                 OperationResult.failure(OperationCode.NOT_FOUND, "Space not found."));
//                     }

//                     if (space.getSchemaVersion() != 2
//                             || !SpaceApprovalStatus.APPROVED.equalsIgnoreCase(space.getApprovalStatus())
//                             || !SpaceOperationalStatus.ACTIVE.equalsIgnoreCase(space.getOperationalStatus())
//                             || !space.isActive()
//                             || !SpaceCategory.CREATIVITY.equalsIgnoreCase(space.getCategory())) {
//                         return CompletableFuture.completedFuture(
//                                 OperationResult.failure(OperationCode.NOT_FOUND,
//                                         "Space is not currently visible in marketplace."));
//                     }

//                     String bId = space.getBusinessId();
//                     if (ValidationUtil.isBlank(bId)) {
//                         return CompletableFuture.completedFuture(
//                                 OperationResult.failure(OperationCode.NOT_FOUND, "Parent Business ID missing."));
//                     }

//                     return businessDao.getBusiness(bId, idToken)
//                             .thenApply(busRes -> {
//                                 if (busRes.isFailure()) {
//                                     return OperationResult.failure(busRes.getErrorCode(), busRes.getMessage());
//                                 }
//                                 Business bus = busRes.getData();
//                                 if (bus == null || bus.getSchemaVersion() != 2
//                                         || !bus.isActive()
//                                         || !BusinessStatus.APPROVED.equalsIgnoreCase(bus.getVerificationStatus())
//                                         || !SpaceCategory.CREATIVITY.equalsIgnoreCase(bus.getCategory())
//                                         || bus.getActivityGroups() == null
//                                         || !containsIgnoreCase(bus.getActivityGroups(), space.getActivityGroup())) {
//                                     return OperationResult.failure(OperationCode.NOT_FOUND,
//                                             "Space is not currently visible in marketplace.");
//                                 }
//                                 return OperationResult.success(space);
//                             });
//                 });
//     }

//     public CompletableFuture<OperationResult<Space>> getPublicVisibleV2SpaceDetails(String spaceId) {
//         if (ValidationUtil.isBlank(spaceId)) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
//         }

//         return spaceDao.getPublicSpace(spaceId)
//                 .thenCompose(spaceRes -> {
//                     if (spaceRes.isFailure()) {
//                         return CompletableFuture.completedFuture(
//                                 OperationResult.failure(spaceRes.getErrorCode(), spaceRes.getMessage()));
//                     }
//                     Space space = spaceRes.getData();
//                     if (space == null) {
//                         return CompletableFuture.completedFuture(
//                                 OperationResult.failure(OperationCode.NOT_FOUND, "Space not found."));
//                     }

//                     if (space.getSchemaVersion() != 2
//                             || !SpaceApprovalStatus.APPROVED.equalsIgnoreCase(space.getApprovalStatus())
//                             || !SpaceOperationalStatus.ACTIVE.equalsIgnoreCase(space.getOperationalStatus())
//                             || !space.isActive()
//                             || !SpaceCategory.CREATIVITY.equalsIgnoreCase(space.getCategory())) {
//                         return CompletableFuture.completedFuture(
//                                 OperationResult.failure(OperationCode.NOT_FOUND,
//                                         "Space is not currently visible in marketplace."));
//                     }

//                     String bId = space.getBusinessId();
//                     if (ValidationUtil.isBlank(bId)) {
//                         return CompletableFuture.completedFuture(
//                                 OperationResult.failure(OperationCode.NOT_FOUND, "Parent Business ID missing."));
//                     }

//                     return businessDao.getPublicBusiness(bId)
//                             .thenApply(busRes -> {
//                                 if (busRes.isFailure()) {
//                                     return OperationResult.failure(busRes.getErrorCode(), busRes.getMessage());
//                                 }
//                                 Business bus = busRes.getData();
//                                 if (bus == null || bus.getSchemaVersion() != 2
//                                         || !bus.isActive()
//                                         || !BusinessStatus.APPROVED.equalsIgnoreCase(bus.getVerificationStatus())
//                                         || !SpaceCategory.CREATIVITY.equalsIgnoreCase(bus.getCategory())
//                                         || bus.getActivityGroups() == null
//                                         || !containsIgnoreCase(bus.getActivityGroups(), space.getActivityGroup())) {
//                                     return OperationResult.failure(OperationCode.NOT_FOUND,
//                                             "Space is not currently visible in marketplace.");
//                                 }
//                                 return OperationResult.success(space);
//                             });
//                 });
//     }

//     private boolean containsIgnoreCase(List<String> list, String value) {
//         if (list == null || value == null)
//             return false;
//         for (String item : list) {
//             if (item != null && item.equalsIgnoreCase(value)) {
//                 return true;
//             }
//         }
//         return false;
//     }

//     // ==================== V2 Methods ====================

//     public SubmissionReadinessResult validateV2Space(Business business, Space space, String authenticatedOwnerId) {
//         SubmissionReadinessResult result = new SubmissionReadinessResult();
//         List<String> missing = new ArrayList<>();

//         if (space == null) {
//             missing.add("Space object cannot be null");
//             result.setReady(false);
//             result.setMissingRequirements(missing);
//             return result;
//         }

//         if (space.getSchemaVersion() != 2) {
//             missing.add("Space schemaVersion must be 2");
//         }

//         if (ValidationUtil.isBlank(authenticatedOwnerId)) {
//             missing.add("Authenticated owner ID is required");
//         } else {
//             if (!authenticatedOwnerId.equals(space.getOwnerId())) {
//                 missing.add("Space owner ID must match authenticated owner");
//             }
//         }

//         if (business == null) {
//             missing.add("Parent Business is required");
//         } else {
//             if (business.getSchemaVersion() != 2) {
//                 missing.add("Parent Business must be V2");
//             }
//             if (!business.isActive()) {
//                 missing.add("Parent Business must be active");
//             }
//             if (!BusinessStatus.APPROVED.equalsIgnoreCase(business.getVerificationStatus())) {
//                 missing.add("Parent Business must be APPROVED");
//             }
//             if (authenticatedOwnerId != null && !authenticatedOwnerId.equals(business.getOwnerId())) {
//                 missing.add("Parent Business owner must match authenticated owner");
//             }
//             if (!SpaceCategory.CREATIVITY.equalsIgnoreCase(business.getCategory())) {
//                 missing.add("Parent Business category must be CREATIVITY");
//             }
//             if (space.getActivityGroup() != null
//                     && (business.getActivityGroups() == null
//                             || !business.getActivityGroups().contains(space.getActivityGroup()))) {
//                 missing.add("Parent Business activity groups must contain Space activity group");
//             }
//         }

//         if (ValidationUtil.isBlank(space.getSpaceName())) {
//             missing.add("Space name is required");
//         }
//         if (ValidationUtil.isBlank(space.getDescription())) {
//             missing.add("Space description is required");
//         }

//         if (!SpaceCategory.CREATIVITY.equalsIgnoreCase(space.getCategory())) {
//             missing.add("Space category must be CREATIVITY");
//         }
//         if (ValidationUtil.isBlank(space.getActivityGroup())
//                 || !CreativityActivityGroup.isValid(space.getActivityGroup())) {
//             missing.add("Valid Creativity activity group is required");
//         }
//         if (ValidationUtil.isBlank(space.getActivityType())) {
//             missing.add("Canonical activity type is required");
//         }

//         // Location
//         SpaceLocation loc = space.getSpaceLocation();
//         if (loc == null) {
//             missing.add("Space location is required");
//         } else {
//             if (ValidationUtil.isBlank(loc.getAddressLine()))
//                 missing.add("Address line is required");
//             if (ValidationUtil.isBlank(loc.getCity()))
//                 missing.add("City is required");
//             if (ValidationUtil.isBlank(loc.getState()))
//                 missing.add("State is required");
//             if (ValidationUtil.isBlank(loc.getPostalCode())
//                     || !POSTAL_CODE_PATTERN.matcher(loc.getPostalCode()).matches()) {
//                 missing.add("Valid 6-digit postal code is required");
//             }
//         }

//         if (space.getCapacity() <= 0) {
//             missing.add("Capacity must be greater than zero");
//         }

//         // TimeZone
//         if (ValidationUtil.isBlank(space.getTimeZoneId())) {
//             missing.add("Timezone ID is required");
//         } else {
//             try {
//                 ZoneId.of(space.getTimeZoneId());
//             } catch (Exception e) {
//                 missing.add("Invalid timezone ID: " + space.getTimeZoneId());
//             }
//         }

//         // BookingType & PricingUnit
//         String bType = space.getBookingType();
//         if (ValidationUtil.isBlank(bType) || !BookingType.isValid(bType)
//                 || BookingType.RESOURCE.equalsIgnoreCase(bType)) {
//             missing.add("Valid V2 booking type is required");
//         }
//         String pUnit = space.getPricingUnit();
//         if (ValidationUtil.isBlank(pUnit) || !PricingUnit.isValid(pUnit)) {
//             missing.add("Valid pricing unit is required");
//         }
//         if (space.getBasePricePaise() <= 0L) {
//             missing.add("Base price in paise must be greater than zero");
//         }

//         // BookingConfig matching
//         BookingConfig cfg = space.getBookingConfig();
//         if (cfg == null || !space.hasMatchingBookingConfig()) {
//             missing.add("Valid matching booking configuration is required");
//         } else {
//             validateBookingConfigMatrix(bType, pUnit, space.getBasePricePaise(), cfg, missing);
//         }

//         // OperatingHours
//         Map<String, OperatingDayConfig> oh = space.getOperatingHours();
//         if (oh == null || oh.isEmpty()) {
//             missing.add("Operating hours map is required");
//         } else {
//             int activeDays = 0;
//             for (Map.Entry<String, OperatingDayConfig> entry : oh.entrySet()) {
//                 OperatingDayConfig dayCfg = entry.getValue();
//                 if (dayCfg != null && dayCfg.isActive()) {
//                     activeDays++;
//                     String open = dayCfg.getOpeningTime();
//                     String close = dayCfg.getClosingTime();
//                     if (open == null || !TIME_FORMAT_PATTERN.matcher(open).matches()) {
//                         missing.add("Opening time for " + entry.getKey() + " must be HH:mm 24h format");
//                     }
//                     if (close == null || !TIME_FORMAT_PATTERN.matcher(close).matches()) {
//                         missing.add("Closing time for " + entry.getKey() + " must be HH:mm 24h format");
//                     }
//                     if (open != null && close != null && open.compareTo(close) >= 0) {
//                         missing.add("Opening time must precede closing time for " + entry.getKey());
//                     }
//                 }
//             }
//             if (activeDays == 0) {
//                 missing.add("At least one operating day must be active");
//             }
//         }

//         boolean isReady = missing.isEmpty();
//         result.setReady(isReady);
//         result.setMissingRequirements(missing);
//         return result;
//     }

//     private void validateBookingConfigMatrix(String bookingType, String pricingUnit, long basePricePaise,
//             BookingConfig cfg, List<String> missing) {
//         if (bookingType == null)
//             return;
//         switch (bookingType.trim().toUpperCase()) {
//             case BookingType.SEAT:
//                 if (!(cfg instanceof SeatBookingConfig)) {
//                     missing.add("Seat booking config required");
//                     break;
//                 }
//                 SeatBookingConfig seatCfg = (SeatBookingConfig) cfg;
//                 if (seatCfg.getSelectionMode() == null || seatCfg.getSelectionMode().isBlank()) {
//                     missing.add("Seat selection mode is required");
//                 }
//                 if (seatCfg.getSeatPriceMode() == null || seatCfg.getSeatPriceMode().isBlank()) {
//                     missing.add("Seat price mode is required");
//                 }
//                 if (seatCfg.getMinimumDurationMinutes() <= 0)
//                     missing.add("Minimum duration must be > 0");
//                 if (seatCfg.getMaximumDurationMinutes() < seatCfg.getMinimumDurationMinutes())
//                     missing.add("Maximum duration must be >= minimum duration");
//                 if (seatCfg.getDurationStepMinutes() <= 0)
//                     missing.add("Duration step must be > 0");
//                 if (seatCfg.getMaxGuestsPerBooking() <= 0)
//                     missing.add("Max guests per booking must be > 0");
//                 if (seatCfg.getAdvanceBookingDays() < 0)
//                     missing.add("Advance booking days must be >= 0");
//                 if (seatCfg.getMinimumAdvanceMinutes() < 0)
//                     missing.add("Minimum advance minutes must be >= 0");
//                 if (!Set.of(PricingUnit.SEAT, PricingUnit.PERSON, PricingUnit.HOUR, PricingUnit.SESSION,
//                         PricingUnit.FLAT).contains(pricingUnit)) {
//                     missing.add("Pricing unit for SEAT must be SEAT, PERSON, HOUR, SESSION, or FLAT");
//                 }
//                 break;
//             case BookingType.SLOT:
//                 if (!(cfg instanceof SlotBookingConfig)) {
//                     missing.add("Slot booking config required");
//                     break;
//                 }
//                 SlotBookingConfig slotCfg = (SlotBookingConfig) cfg;
//                 if (slotCfg.getMinimumDurationMinutes() <= 0)
//                     missing.add("Minimum duration must be > 0");
//                 if (slotCfg.getMaximumDurationMinutes() < slotCfg.getMinimumDurationMinutes())
//                     missing.add("Maximum duration must be >= minimum duration");
//                 if (slotCfg.getDurationStepMinutes() <= 0)
//                     missing.add("Duration step must be > 0");
//                 if (slotCfg.getSlotIntervalMinutes() <= 0)
//                     missing.add("Slot interval must be > 0");
//                 if (slotCfg.getBufferBeforeMinutes() < 0)
//                     missing.add("Buffer before must be >= 0");
//                 if (slotCfg.getBufferAfterMinutes() < 0)
//                     missing.add("Buffer after must be >= 0");
//                 if (slotCfg.getMaxPeople() <= 0)
//                     missing.add("Max people must be > 0");
//                 if (slotCfg.getAdvanceBookingDays() < 0)
//                     missing.add("Advance booking days must be >= 0");
//                 if (slotCfg.getMinimumAdvanceMinutes() < 0)
//                     missing.add("Minimum advance minutes must be >= 0");
//                 if (!Set.of(PricingUnit.HOUR, PricingUnit.SESSION, PricingUnit.FLAT).contains(pricingUnit)) {
//                     missing.add("Pricing unit for SLOT must be HOUR, SESSION, or FLAT");
//                 }
//                 break;
//             case BookingType.WORKSHOP:
//                 if (!(cfg instanceof WorkshopBookingConfig)) {
//                     missing.add("Workshop booking config required");
//                     break;
//                 }
//                 WorkshopBookingConfig wsCfg = (WorkshopBookingConfig) cfg;
//                 if (wsCfg.getPricePerPersonPaise() <= 0L)
//                     missing.add("Price per person in paise must be > 0");
//                 if (wsCfg.getMinimumParticipantsPerBooking() <= 0)
//                     missing.add("Minimum participants must be > 0");
//                 if (wsCfg.getMaximumParticipantsPerBooking() < wsCfg.getMinimumParticipantsPerBooking())
//                     missing.add("Maximum participants must be >= minimum participants");
//                 if (wsCfg.getBookingCutoffHours() < 0)
//                     missing.add("Booking cutoff hours must be >= 0");
//                 if (wsCfg.getCancellationCutoffHours() < 0)
//                     missing.add("Cancellation cutoff hours must be >= 0");
//                 if (!PricingUnit.PERSON.equalsIgnoreCase(pricingUnit)) {
//                     missing.add("Pricing unit for WORKSHOP must be PERSON");
//                 }
//                 if (wsCfg.getPricePerPersonPaise() != basePricePaise) {
//                     missing.add("Workshop pricePerPersonPaise must equal basePricePaise");
//                 }
//                 break;
//             case BookingType.RENTAL:
//                 if (!(cfg instanceof RentalBookingConfig)) {
//                     missing.add("Rental booking config required");
//                     break;
//                 }
//                 RentalBookingConfig rentalCfg = (RentalBookingConfig) cfg;
//                 if (rentalCfg.getAvailableQuantity() <= 0)
//                     missing.add("Available quantity must be > 0");
//                 if (rentalCfg.getMinimumRentalDurationMinutes() <= 0)
//                     missing.add("Minimum rental duration must be > 0");
//                 if (rentalCfg.getMaximumRentalDurationMinutes() < rentalCfg.getMinimumRentalDurationMinutes())
//                     missing.add("Maximum rental duration must be >= minimum rental duration");
//                 if (rentalCfg.getSecurityDepositPaise() < 0L)
//                     missing.add("Security deposit must be >= 0");
//                 if (rentalCfg.getDeliveryRadiusKm() < 0.0)
//                     missing.add("Delivery radius must be >= 0");
//                 if (rentalCfg.getDeliveryChargePaise() < 0L)
//                     missing.add("Delivery charge must be >= 0");
//                 if (rentalCfg.getLateFeePaise() < 0L)
//                     missing.add("Late fee must be >= 0");
//                 if (rentalCfg.getDeliveryChargePaise() > 0L && !rentalCfg.isDeliveryAvailable()) {
//                     missing.add("Delivery charge requires delivery to be enabled");
//                 }
//                 if (!rentalCfg.isPickupAvailable() && !rentalCfg.isDeliveryAvailable()) {
//                     missing.add("At least pickup or delivery must be available");
//                 }
//                 if (!Set.of(PricingUnit.HOUR, PricingUnit.DAY, PricingUnit.FLAT).contains(pricingUnit)) {
//                     missing.add("Pricing unit for RENTAL must be HOUR, DAY, or FLAT");
//                 }
//                 break;
//             case BookingType.VENUE:
//                 if (!(cfg instanceof VenueBookingConfig)) {
//                     missing.add("Venue booking config required");
//                     break;
//                 }
//                 VenueBookingConfig venueCfg = (VenueBookingConfig) cfg;
//                 if (venueCfg.getMinimumBookingHours() <= 0)
//                     missing.add("Minimum booking hours must be > 0");
//                 if (venueCfg.getMaximumBookingHours() < venueCfg.getMinimumBookingHours())
//                     missing.add("Maximum booking hours must be >= minimum booking hours");
//                 if (venueCfg.getMinimumAdvanceDays() < 0)
//                     missing.add("Minimum advance days must be >= 0");
//                 if (venueCfg.getSecurityDepositPaise() < 0L)
//                     missing.add("Security deposit must be >= 0");
//                 if (venueCfg.getOvertimeRatePaise() < 0L)
//                     missing.add("Overtime rate must be >= 0");
//                 if (!Set.of(PricingUnit.HOUR, PricingUnit.EVENT, PricingUnit.SESSION, PricingUnit.FLAT)
//                         .contains(pricingUnit)) {
//                     missing.add("Pricing unit for VENUE must be HOUR, EVENT, SESSION, or FLAT");
//                 }
//                 break;
//             case BookingType.PRACTICE_ROOM:
//                 if (!(cfg instanceof PracticeRoomBookingConfig)) {
//                     missing.add("Practice room booking config required");
//                     break;
//                 }
//                 PracticeRoomBookingConfig prCfg = (PracticeRoomBookingConfig) cfg;
//                 if (prCfg.getMinimumDurationMinutes() <= 0)
//                     missing.add("Minimum duration must be > 0");
//                 if (prCfg.getMaximumDurationMinutes() < prCfg.getMinimumDurationMinutes())
//                     missing.add("Maximum duration must be >= minimum duration");
//                 if (prCfg.getDurationStepMinutes() <= 0)
//                     missing.add("Duration step must be > 0");
//                 if (!Set.of(PricingUnit.HOUR, PricingUnit.SESSION, PricingUnit.FLAT).contains(pricingUnit)) {
//                     missing.add("Pricing unit for PRACTICE_ROOM must be HOUR, SESSION, or FLAT");
//                 }
//                 break;
//             default:
//                 missing.add("Unsupported booking type: " + bookingType);
//                 break;
//         }
//     }

//     public CompletableFuture<OperationResult<Space>> createV2Space(
//             String authenticatedOwnerId,
//             String businessId,
//             Space submittedSpace,
//             String idToken) {

//         if (ValidationUtil.isBlank(authenticatedOwnerId)) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED,
//                             "Authenticated owner ID is required."));
//         }
//         if (ValidationUtil.isBlank(businessId)) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Business ID is required."));
//         }
//         if (ValidationUtil.isBlank(idToken)) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
//         }
//         if (submittedSpace == null) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Submitted Space cannot be null."));
//         }

//         // Authoritative parent Business fetch
//         return businessDao.getBusiness(businessId, idToken)
//                 .thenCompose(bRes -> {
//                     if (bRes.isFailure() || bRes.getData() == null) {
//                         return CompletableFuture.completedFuture(
//                                 OperationResult.<Space>failure(
//                                         OperationCode.NOT_FOUND.equalsIgnoreCase(bRes.getErrorCode())
//                                                 ? OperationCode.NOT_FOUND
//                                                 : bRes.getErrorCode(),
//                                         "Parent Business not found: " + bRes.getMessage()));
//                     }

//                     Business business = bRes.getData();
//                     if (business.getSchemaVersion() != 2) {
//                         return CompletableFuture.completedFuture(
//                                 OperationResult.<Space>failure(OperationCode.INVALID_ARGUMENT,
//                                         "Parent Business must be schemaVersion == 2."));
//                     }
//                     if (!authenticatedOwnerId.equals(business.getOwnerId())) {
//                         return CompletableFuture.completedFuture(
//                                 OperationResult.<Space>failure(OperationCode.PERMISSION_DENIED,
//                                         "Access denied. Parent Business is not owned by authenticated Owner."));
//                     }
//                     if (!business.isActive()) {
//                         return CompletableFuture.completedFuture(
//                                 OperationResult.<Space>failure(OperationCode.PERMISSION_DENIED,
//                                         "Cannot create Space for inactive Business."));
//                     }
//                     if (!BusinessStatus.APPROVED.equalsIgnoreCase(business.getVerificationStatus())) {
//                         return CompletableFuture.completedFuture(
//                                 OperationResult.<Space>failure(OperationCode.PERMISSION_DENIED,
//                                         "Cannot create Space for unapproved Business."));
//                     }
//                     if (!SpaceCategory.CREATIVITY.equalsIgnoreCase(business.getCategory())) {
//                         return CompletableFuture.completedFuture(
//                                 OperationResult.<Space>failure(OperationCode.INVALID_ARGUMENT,
//                                         "Business category must be CREATIVITY."));
//                     }
//                     if (business.getActivityGroups() == null
//                             || !business.getActivityGroups().contains(submittedSpace.getActivityGroup())) {
//                         return CompletableFuture.completedFuture(
//                                 OperationResult.<Space>failure(OperationCode.INVALID_ARGUMENT,
//                                         "Business does not support Space activity group: "
//                                                 + submittedSpace.getActivityGroup()));
//                     }

//                     // Perform Domain Validation
//                     SubmissionReadinessResult readiness = validateV2Space(business, submittedSpace,
//                             authenticatedOwnerId);
//                     if (!readiness.isReady()) {
//                         return CompletableFuture.completedFuture(
//                                 OperationResult.<Space>failure(OperationCode.INVALID_ARGUMENT,
//                                         "V2 Space validation failed: "
//                                                 + String.join("; ", readiness.getMissingRequirements())));
//                     }

//                     // Application-assigned single captured timestamp & UUID
//                     String spaceId = UUID.randomUUID().toString();
//                     long now = System.currentTimeMillis();

//                     Space v2Space = Space.createV2(
//                             spaceId,
//                             business.getBusinessId(),
//                             authenticatedOwnerId,
//                             submittedSpace.getSpaceName(),
//                             submittedSpace.getDescription(),
//                             SpaceCategory.CREATIVITY,
//                             submittedSpace.getActivityGroup(),
//                             submittedSpace.getActivityType(),
//                             submittedSpace.getSpaceLocation(),
//                             submittedSpace.getCoverPhotoUrl(),
//                             submittedSpace.getPhotoUrls(),
//                             submittedSpace.getAmenities(),
//                             submittedSpace.getFacilities(),
//                             submittedSpace.getCapacity(),
//                             submittedSpace.getBookingType(),
//                             submittedSpace.getBasePricePaise(),
//                             submittedSpace.getPricingUnit(),
//                             submittedSpace.getBookingConfig(),
//                             submittedSpace.getOperatingHours(),
//                             submittedSpace.getRules(),
//                             submittedSpace.getCancellationPolicy());

//                     v2Space.setTimeZoneId(submittedSpace.getTimeZoneId());
//                     v2Space.setApprovalStatus(SpaceApprovalStatus.DRAFT);
//                     v2Space.setOperationalStatus(SpaceOperationalStatus.INACTIVE);
//                     v2Space.setActive(true);
//                     v2Space.setSubmittedAt(0L);
//                     v2Space.setReviewedAt(0L);
//                     v2Space.setRejectionReason("");
//                     v2Space.setApprovedAt(0L);
//                     v2Space.setPublishedAt(0L);
//                     v2Space.setCreatedAt(now);
//                     v2Space.setUpdatedAt(now);

//                     return spaceDao.createV2Space(v2Space, idToken);
//                 });
//     }

//     public CompletableFuture<OperationResult<Space>> updateV2Space(
//             String authenticatedOwnerId,
//             Space existingSpace,
//             Space submittedSpace,
//             String idToken) {

//         if (ValidationUtil.isBlank(authenticatedOwnerId)) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED,
//                             "Authenticated owner ID is required."));
//         }
//         if (existingSpace == null || existingSpace.getSchemaVersion() != 2) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.INVALID_ARGUMENT,
//                             "Existing space must have schemaVersion == 2."));
//         }
//         if (submittedSpace == null) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Submitted space cannot be null."));
//         }
//         if (ValidationUtil.isBlank(idToken)) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
//         }

//         // Confirm ownership
//         if (!authenticatedOwnerId.equals(existingSpace.getOwnerId())) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.PERMISSION_DENIED,
//                             "Access denied. Space is not owned by authenticated Owner."));
//         }

//         // Authoritative parent Business fetch
//         return businessDao.getBusiness(existingSpace.getBusinessId(), idToken)
//                 .thenCompose(bRes -> {
//                     if (bRes.isFailure() || bRes.getData() == null) {
//                         return CompletableFuture.completedFuture(
//                                 OperationResult.<Space>failure(OperationCode.NOT_FOUND,
//                                         "Parent Business not found for space."));
//                     }

//                     Business business = bRes.getData();
//                     if (!authenticatedOwnerId.equals(business.getOwnerId()) || !business.isActive()) {
//                         return CompletableFuture.completedFuture(
//                                 OperationResult.<Space>failure(OperationCode.PERMISSION_DENIED,
//                                         "Parent Business is invalid or inactive."));
//                     }
//                     if (!BusinessStatus.APPROVED.equalsIgnoreCase(business.getVerificationStatus())) {
//                         return CompletableFuture.completedFuture(
//                                 OperationResult.<Space>failure(OperationCode.PERMISSION_DENIED,
//                                         "Parent Business is not approved."));
//                     }

//                     // Check submission state
//                     String currentApproval = existingSpace.getApprovalStatus();
//                     if (SpaceApprovalStatus.SUBMITTED.equalsIgnoreCase(currentApproval)
//                             || SpaceApprovalStatus.UNDER_REVIEW.equalsIgnoreCase(currentApproval)) {
//                         return CompletableFuture.completedFuture(
//                                 OperationResult.<Space>failure(OperationCode.PERMISSION_DENIED,
//                                         "Cannot edit Space while SUBMITTED or UNDER_REVIEW."));
//                     }

//                     // Validate submittedSpace against domain
//                     SubmissionReadinessResult readiness = validateV2Space(business, submittedSpace,
//                             authenticatedOwnerId);
//                     if (!readiness.isReady()) {
//                         return CompletableFuture.completedFuture(
//                                 OperationResult.<Space>failure(OperationCode.INVALID_ARGUMENT,
//                                         "Validation failed for updated space: "
//                                                 + String.join("; ", readiness.getMissingRequirements())));
//                     }

//                     boolean materialChanged = hasMaterialChanges(existingSpace, submittedSpace);

//                     // Prepare target updated Space
//                     existingSpace.setSpaceName(submittedSpace.getSpaceName());
//                     existingSpace.setDescription(submittedSpace.getDescription());
//                     existingSpace.setCategory(SpaceCategory.CREATIVITY);
//                     existingSpace.setActivityGroup(submittedSpace.getActivityGroup());
//                     existingSpace.setActivityType(submittedSpace.getActivityType());
//                     existingSpace.setSpaceLocation(submittedSpace.getSpaceLocation());
//                     existingSpace.setCoverPhotoUrl(submittedSpace.getCoverPhotoUrl());
//                     existingSpace.setPhotoUrls(submittedSpace.getPhotoUrls());
//                     existingSpace.setAmenities(submittedSpace.getAmenities());
//                     existingSpace.setFacilities(submittedSpace.getFacilities());
//                     existingSpace.setCapacity(submittedSpace.getCapacity());
//                     existingSpace.setBookingType(submittedSpace.getBookingType());
//                     existingSpace.setBasePricePaise(submittedSpace.getBasePricePaise());
//                     existingSpace.setPricingUnit(submittedSpace.getPricingUnit());
//                     existingSpace.setBookingConfig(submittedSpace.getBookingConfig());
//                     existingSpace.setOperatingHours(submittedSpace.getOperatingHours());
//                     existingSpace.setRules(submittedSpace.getRules());
//                     existingSpace.setCancellationPolicy(submittedSpace.getCancellationPolicy());
//                     existingSpace.setTimeZoneId(submittedSpace.getTimeZoneId());

//                     long now = System.currentTimeMillis();
//                     existingSpace.setUpdatedAt(now);

//                     if (SpaceApprovalStatus.APPROVED.equalsIgnoreCase(currentApproval)) {
//                         if (materialChanged) {
//                             // Reset approval status on material edit
//                             existingSpace.setApprovalStatus(SpaceApprovalStatus.DRAFT);
//                             existingSpace.setOperationalStatus(SpaceOperationalStatus.INACTIVE);
//                             existingSpace.setStatus(SpaceStatus.DRAFT);
//                             existingSpace.setSubmittedAt(0L);
//                             existingSpace.setReviewedAt(0L);
//                             existingSpace.setRejectionReason("");
//                             existingSpace.setApprovedAt(0L);
//                             existingSpace.setPublishedAt(0L);
//                         }
//                     } else if (SpaceApprovalStatus.REJECTED.equalsIgnoreCase(currentApproval)) {
//                         existingSpace.setApprovalStatus(SpaceApprovalStatus.DRAFT);
//                         existingSpace.setOperationalStatus(SpaceOperationalStatus.INACTIVE);
//                         existingSpace.setStatus(SpaceStatus.DRAFT);
//                         existingSpace.setRejectionReason("");
//                         existingSpace.setReviewedAt(0L);
//                     } else {
//                         existingSpace.setApprovalStatus(SpaceApprovalStatus.DRAFT);
//                         existingSpace.setOperationalStatus(SpaceOperationalStatus.INACTIVE);
//                         existingSpace.setStatus(SpaceStatus.DRAFT);
//                     }

//                     // Construct fixed update mask from allowlist
//                     List<String> updateMask = List.of(
//                             "spaceName", "description", "category", "activityGroup", "activityType",
//                             "location", "coverPhotoUrl", "photoUrls", "amenities", "facilities",
//                             "capacity", "bookingType", "basePricePaise", "pricingUnit", "bookingConfig",
//                             "operatingHours", "rules", "cancellationPolicy", "approvalStatus", "operationalStatus",
//                             "submittedAt", "rejectionReason", "publishedAt",
//                             "updatedAt", "timeZoneId");

//                     return spaceDao.updateV2Space(existingSpace, updateMask, idToken);
//                 });
//     }

//     private boolean hasMaterialChanges(Space existing, Space submitted) {
//         if (!Objects.equals(existing.getSpaceName(), submitted.getSpaceName()))
//             return true;
//         if (!Objects.equals(existing.getDescription(), submitted.getDescription()))
//             return true;
//         if (!Objects.equals(existing.getCategory(), submitted.getCategory()))
//             return true;
//         if (!Objects.equals(existing.getActivityGroup(), submitted.getActivityGroup()))
//             return true;
//         if (!Objects.equals(existing.getActivityType(), submitted.getActivityType()))
//             return true;
//         if (!Objects.equals(existing.getCoverPhotoUrl(), submitted.getCoverPhotoUrl()))
//             return true;
//         if (existing.getCapacity() != submitted.getCapacity())
//             return true;
//         if (!Objects.equals(existing.getBookingType(), submitted.getBookingType()))
//             return true;
//         if (existing.getBasePricePaise() != submitted.getBasePricePaise())
//             return true;
//         if (!Objects.equals(existing.getPricingUnit(), submitted.getPricingUnit()))
//             return true;
//         if (!Objects.equals(existing.getCancellationPolicy(), submitted.getCancellationPolicy()))
//             return true;
//         if (!Objects.equals(existing.getTimeZoneId(), submitted.getTimeZoneId()))
//             return true;

//         if (!Objects.equals(existing.getPhotoUrls(), submitted.getPhotoUrls()))
//             return true;
//         if (!Objects.equals(existing.getAmenities(), submitted.getAmenities()))
//             return true;
//         if (!Objects.equals(existing.getFacilities(), submitted.getFacilities()))
//             return true;
//         if (!Objects.equals(existing.getRules(), submitted.getRules()))
//             return true;

//         if (!Objects.equals(existing.getSpaceLocation(), submitted.getSpaceLocation()))
//             return true;
//         if (!Objects.equals(existing.getBookingConfig(), submitted.getBookingConfig()))
//             return true;
//         if (!Objects.equals(existing.getOperatingHours(), submitted.getOperatingHours()))
//             return true;

//         return false;
//     }
// }
package com.eklavya.spotfit.service;

import com.eklavya.spotfit.constants.*;
import com.eklavya.spotfit.dao.BusinessDao;
import com.eklavya.spotfit.dao.SpaceDao;
import com.eklavya.spotfit.model.*;
import com.eklavya.spotfit.model.bookingconfig.*;
import com.eklavya.spotfit.util.ValidationUtil;

import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

/**
 * Service class enforcing domain validation and business rules for Space
 * management across V1 and V2 schemas.
 */
public class SpaceService {

    private static final Pattern TIME_FORMAT_PATTERN = Pattern.compile("^([0-1][0-9]|2[0-3]):[0-5][0-9]$");
    private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("^\\d{6}$");

    private final SpaceDao spaceDao;
    private final BusinessDao businessDao;

    public SpaceService() {
        this.spaceDao = new SpaceDao();
        this.businessDao = new BusinessDao();
    }

    public SpaceService(SpaceDao spaceDao) {
        this.spaceDao = spaceDao;
        this.businessDao = new BusinessDao();
    }

    public SpaceService(SpaceDao spaceDao, BusinessDao businessDao) {
        this.spaceDao = spaceDao;
        this.businessDao = businessDao;
    }

    // ==================== V1 Methods ====================

    public CompletableFuture<OperationResult<Space>> createSpace(
            String ownerId,
            String businessId,
            String spaceName,
            String description,
            String category,
            String activityGroup,
            String spaceType,
            String addressLine,
            String landmark,
            String city,
            String state,
            String postalCode,
            int capacity,
            List<String> amenities,
            List<String> suitableActivities,
            List<String> rules,
            String openingTime,
            String closingTime,
            List<String> operatingDays,
            String idToken) {

        if (ValidationUtil.isBlank(ownerId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Owner ID is required."));
        }
        if (ValidationUtil.isBlank(businessId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Business ID is required."));
        }

        OperationResult<Void> validation = validateSpaceInputs(
                spaceName, description, category, activityGroup, spaceType,
                addressLine, city, state, postalCode, capacity,
                openingTime, closingTime, operatingDays);

        if (validation.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(validation.getErrorCode(), validation.getMessage()));
        }

        String spaceId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();

        Space space = new Space(
                spaceId,
                businessId.trim(),
                ownerId.trim(),
                ValidationUtil.sanitizeText(spaceName),
                ValidationUtil.sanitizeText(description),
                category.trim(),
                activityGroup.trim(),
                ValidationUtil.sanitizeText(spaceType),
                ValidationUtil.sanitizeText(addressLine),
                ValidationUtil.sanitizeText(landmark),
                ValidationUtil.sanitizeText(city),
                ValidationUtil.sanitizeText(state),
                ValidationUtil.sanitizeText(postalCode),
                capacity,
                amenities,
                suitableActivities,
                rules,
                ValidationUtil.sanitizeText(openingTime),
                ValidationUtil.sanitizeText(closingTime),
                operatingDays,
                SpaceStatus.DRAFT,
                true,
                now,
                now);

        return spaceDao.createSpace(space, idToken);
    }

    public CompletableFuture<OperationResult<Space>> getSpace(String spaceId, String idToken) {
        return spaceDao.getSpace(spaceId, idToken);
    }

    public CompletableFuture<OperationResult<List<Space>>> getSpacesForOwner(String ownerId, String idToken) {
        return spaceDao.getSpacesByOwner(ownerId, idToken);
    }

    public CompletableFuture<OperationResult<List<Space>>> getSpacesForBusiness(String businessId, String idToken) {
        return spaceDao.getSpacesByBusiness(businessId, idToken);
    }

    public CompletableFuture<OperationResult<List<Space>>> getSpacesForOwnerAndBusiness(
            String ownerId, String businessId, String idToken) {
        return spaceDao.getSpacesByOwnerAndBusiness(ownerId, businessId, idToken);
    }

    public CompletableFuture<OperationResult<Space>> updateSpace(Space space, String idToken) {
        if (space == null) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space object cannot be null."));
        }

        OperationResult<Void> validation = validateSpaceInputs(
                space.getSpaceName(),
                space.getDescription(),
                space.getCategory(),
                space.getActivityGroup(),
                space.getSpaceType(),
                space.getAddressLine(),
                space.getCity(),
                space.getState(),
                space.getPostalCode(),
                space.getCapacity(),
                space.getOpeningTime(),
                space.getClosingTime(),
                space.getOperatingDays());

        if (validation.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(validation.getErrorCode(), validation.getMessage()));
        }

        if (!SpaceStatus.DRAFT.equalsIgnoreCase(space.getStatus())) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.PERMISSION_DENIED,
                            "Only DRAFT spaces can be edited."));
        }

        space.setSpaceName(ValidationUtil.sanitizeText(space.getSpaceName()));
        space.setDescription(ValidationUtil.sanitizeText(space.getDescription()));
        space.setSpaceType(ValidationUtil.sanitizeText(space.getSpaceType()));
        space.setAddressLine(ValidationUtil.sanitizeText(space.getAddressLine()));
        space.setLandmark(ValidationUtil.sanitizeText(space.getLandmark()));
        space.setCity(ValidationUtil.sanitizeText(space.getCity()));
        space.setState(ValidationUtil.sanitizeText(space.getState()));
        space.setPostalCode(ValidationUtil.sanitizeText(space.getPostalCode()));
        space.setOpeningTime(ValidationUtil.sanitizeText(space.getOpeningTime()));
        space.setClosingTime(ValidationUtil.sanitizeText(space.getClosingTime()));
        space.setUpdatedAt(System.currentTimeMillis());

        return spaceDao.updateSpace(space, idToken);
    }

    public OperationResult<Void> validateSpaceInputs(
            String spaceName,
            String description,
            String category,
            String activityGroup,
            String spaceType,
            String addressLine,
            String city,
            String state,
            String postalCode,
            int capacity,
            String openingTime,
            String closingTime,
            List<String> operatingDays) {

        if (ValidationUtil.isBlank(spaceName)) {
            return OperationResult.failure("SPACE_NAME_REQUIRED", "Space name is required.");
        }
        if (ValidationUtil.isBlank(description)) {
            return OperationResult.failure("DESCRIPTION_REQUIRED", "Description is required.");
        }
        if (ValidationUtil.isBlank(category) || !SpaceCategory.isValid(category)) {
            return OperationResult.failure("CATEGORY_INVALID", "Please select a valid Space category.");
        }
        if (SpaceCategory.CREATIVITY.equalsIgnoreCase(category)) {
            if (ValidationUtil.isBlank(activityGroup) || !CreativityActivityGroup.isValid(activityGroup)) {
                return OperationResult.failure("ACTIVITY_GROUP_INVALID",
                        "Please select a valid activity group for Creativity.");
            }
        }
        if (ValidationUtil.isBlank(spaceType)) {
            return OperationResult.failure("SPACE_TYPE_REQUIRED", "Space type is required.");
        }
        if (ValidationUtil.isBlank(addressLine)) {
            return OperationResult.failure("ADDRESS_LINE_REQUIRED", "Address line is required.");
        }
        if (ValidationUtil.isBlank(city)) {
            return OperationResult.failure("CITY_REQUIRED", "City is required.");
        }
        if (ValidationUtil.isBlank(state)) {
            return OperationResult.failure("STATE_REQUIRED", "State is required.");
        }
        if (ValidationUtil.isBlank(postalCode)) {
            return OperationResult.failure("POSTAL_CODE_REQUIRED", "Postal code is required.");
        }
        if (capacity <= 0) {
            return OperationResult.failure("CAPACITY_INVALID", "Capacity must be greater than zero.");
        }
        if (ValidationUtil.isBlank(openingTime) || ValidationUtil.isBlank(closingTime)) {
            return OperationResult.failure("OPERATING_HOURS_REQUIRED", "Opening and closing times are required.");
        }
        if (operatingDays == null || operatingDays.isEmpty()) {
            return OperationResult.failure("OPERATING_DAYS_REQUIRED", "Select at least one operating day.");
        }

        return OperationResult.success(null, "Space inputs are valid.");
    }

    public CompletableFuture<OperationResult<List<Space>>> getLiveCreativitySpaces(String activityGroup,
            String idToken) {
        if (ValidationUtil.isBlank(activityGroup) || !CreativityActivityGroup.isValid(activityGroup)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT,
                            "Valid controlled Creativity activity group is required."));
        }
        if (ValidationUtil.isBlank(idToken)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        return spaceDao.getLiveCreativitySpaces(activityGroup, idToken)
                .thenCompose(res -> {
                    if (res.isFailure()) {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(res.getErrorCode(), res.getMessage()));
                    }

                    List<Space> rawSpaces = res.getData();
                    if (rawSpaces == null || rawSpaces.isEmpty()) {
                        return CompletableFuture.completedFuture(OperationResult.success(new ArrayList<>()));
                    }

                    Map<String, CompletableFuture<OperationResult<Business>>> busFutures = new HashMap<>();
                    for (Space space : rawSpaces) {
                        String bId = space.getBusinessId();
                        if (bId != null && !bId.isBlank() && !busFutures.containsKey(bId)) {
                            busFutures.put(bId, businessDao.getBusiness(bId, idToken));
                        }
                    }

                    CompletableFuture<?>[] allFuturesArray = busFutures.values().toArray(new CompletableFuture[0]);

                    return CompletableFuture.allOf(allFuturesArray).thenApply(v -> {
                        for (var entry : busFutures.entrySet()) {
                            OperationResult<Business> busRes = entry.getValue().join();
                            if (busRes.isFailure()) {
                                String errCode = busRes.getErrorCode();
                                if (!OperationCode.NOT_FOUND.equalsIgnoreCase(errCode)) {
                                    return OperationResult.<List<Space>>failure(errCode, busRes.getMessage());
                                }
                            }
                        }

                        List<Space> validatedSpaces = new ArrayList<>();
                        for (Space space : rawSpaces) {
                            String bId = space.getBusinessId();
                            if (bId == null || bId.isBlank()) {
                                continue;
                            }
                            CompletableFuture<OperationResult<Business>> fut = busFutures.get(bId);
                            if (fut == null)
                                continue;

                            OperationResult<Business> busRes = fut.join();
                            if (busRes.isSuccess() && busRes.getData() != null) {
                                Business bus = busRes.getData();
                                if (bus.isActive()
                                        && BusinessStatus.APPROVED.equalsIgnoreCase(bus.getVerificationStatus())
                                        && SpaceStatus.LIVE.equalsIgnoreCase(space.getStatus())
                                        && space.isActive()
                                        && SpaceCategory.CREATIVITY.equalsIgnoreCase(space.getCategory())
                                        && activityGroup.equalsIgnoreCase(space.getActivityGroup())) {
                                    validatedSpaces.add(space);
                                }
                            }
                        }

                        return OperationResult.success(validatedSpaces);
                    });
                });
    }

    public CompletableFuture<OperationResult<List<Space>>> getPublicLiveCreativitySpaces(String activityGroup) {
        if (ValidationUtil.isBlank(activityGroup) || !CreativityActivityGroup.isValid(activityGroup)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT,
                            "Valid controlled Creativity activity group is required."));
        }

        return spaceDao.getPublicLiveCreativitySpaces(activityGroup)
                .thenApply(res -> {
                    if (res.isFailure()) {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }

                    List<Space> rawSpaces = res.getData();
                    if (rawSpaces == null || rawSpaces.isEmpty()) {
                        return OperationResult.success(new ArrayList<>());
                    }

                    List<Space> validatedSpaces = new ArrayList<>();
                    for (Space space : rawSpaces) {
                        if (isPublicMarketplaceSpace(space)
                                && activityGroup.equalsIgnoreCase(space.getActivityGroup())) {
                            validatedSpaces.add(space);
                        }
                    }

                    return OperationResult.success(validatedSpaces);
                });
    }

    public CompletableFuture<OperationResult<List<Space>>> getVisibleV2CreativitySpaces(String activityGroup,
            String activityName, String idToken) {
        if (ValidationUtil.isBlank(activityGroup) || !CreativityActivityGroup.isValid(activityGroup)) {
            return CompletableFuture.completedFuture(
                    OperationResult.success(Collections.emptyList()));
        }
        if (ValidationUtil.isBlank(idToken)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        return spaceDao.getVisibleV2CreativitySpaces(activityGroup, idToken)
                .thenCompose(res -> {
                    if (res.isFailure()) {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(res.getErrorCode(), res.getMessage()));
                    }

                    List<Space> rawSpaces = res.getData();
                    if (rawSpaces == null || rawSpaces.isEmpty()) {
                        return CompletableFuture.completedFuture(OperationResult.success(new ArrayList<>()));
                    }

                    Map<String, CompletableFuture<OperationResult<Business>>> busFutures = new HashMap<>();
                    for (Space space : rawSpaces) {
                        String bId = space.getBusinessId();
                        if (bId != null && !bId.isBlank() && !busFutures.containsKey(bId)) {
                            busFutures.put(bId, businessDao.getBusiness(bId, idToken));
                        }
                    }

                    CompletableFuture<?>[] allFuturesArray = busFutures.values().toArray(new CompletableFuture[0]);

                    return CompletableFuture.allOf(allFuturesArray).thenApply(v -> {
                        for (var entry : busFutures.entrySet()) {
                            OperationResult<Business> busRes = entry.getValue().join();
                            if (busRes.isFailure()) {
                                String errCode = busRes.getErrorCode();
                                if (!OperationCode.NOT_FOUND.equalsIgnoreCase(errCode)) {
                                    return OperationResult.<List<Space>>failure(errCode, busRes.getMessage());
                                }
                            }
                        }

                        List<Space> validatedSpaces = new ArrayList<>();
                        for (Space space : rawSpaces) {
                            String bId = space.getBusinessId();
                            if (bId == null || bId.isBlank()) {
                                continue;
                            }
                            CompletableFuture<OperationResult<Business>> fut = busFutures.get(bId);
                            if (fut == null)
                                continue;

                            OperationResult<Business> busRes = fut.join();
                            if (busRes.isSuccess() && busRes.getData() != null) {
                                Business bus = busRes.getData();
                                if (bus.getSchemaVersion() == 2
                                        && bus.isActive()
                                        && BusinessStatus.APPROVED.equalsIgnoreCase(bus.getVerificationStatus())
                                        && SpaceCategory.CREATIVITY.equalsIgnoreCase(bus.getCategory())
                                        && bus.getActivityGroups() != null
                                        && containsIgnoreCase(bus.getActivityGroups(), space.getActivityGroup())) {

                                    boolean matches = false;
                                    String type = space.getBookingType() != null ? space.getBookingType().toUpperCase()
                                            : "";
                                    String query = activityName != null ? activityName.trim().toLowerCase() : "";

                                    if (query.isEmpty()) {
                                        matches = true;
                                    } else if (query.contains("workshop") && BookingType.WORKSHOP.equals(type)) {
                                        matches = true;
                                    } else if (query.contains("venue") && BookingType.VENUE.equals(type)) {
                                        matches = true;
                                    } else if (query.contains("practice") && BookingType.PRACTICE_ROOM.equals(type)) {
                                        matches = true;
                                    } else if (BookingType.SLOT.equals(type)
                                            && (query.contains("studio") || query.contains("recording")
                                                    || query.contains("shoot") || query.contains("creation"))) {
                                        matches = true;
                                    } else if (space.getActivityType() != null
                                            && space.getActivityType().toLowerCase().contains(query)) {
                                        matches = true;
                                    } else if (query.contains("learning") || query.contains("art")
                                            || query.contains("craft")) {
                                        matches = true;
                                    } else {
                                        matches = space.getActivityType() != null
                                                && space.getActivityType().equalsIgnoreCase(activityName);
                                    }

                                    if (matches) {
                                        validatedSpaces.add(space);
                                    }
                                }
                            }
                        }

                        return OperationResult.success(validatedSpaces);
                    });
                });
    }

    public CompletableFuture<OperationResult<List<Space>>> getPublicVisibleV2CreativitySpaces(String activityGroup,
            String activityName) {
        if (ValidationUtil.isBlank(activityGroup) || !CreativityActivityGroup.isValid(activityGroup)) {
            return CompletableFuture.completedFuture(
                    OperationResult.success(Collections.emptyList()));
        }

        return spaceDao.getPublicVisibleV2CreativitySpaces(activityGroup)
                .thenApply(res -> {
                    if (res.isFailure()) {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }

                    List<Space> rawSpaces = res.getData();
                    if (rawSpaces == null || rawSpaces.isEmpty()) {
                        return OperationResult.success(new ArrayList<>());
                    }

                    List<Space> validatedSpaces = new ArrayList<>();
                    for (Space space : rawSpaces) {
                        if (!isPublicMarketplaceSpace(space)) {
                            continue;
                        }

                        String type = space.getBookingType() != null
                                ? space.getBookingType().toUpperCase()
                                : "";
                        String query = activityName != null ? activityName.trim().toLowerCase() : "";

                        boolean matches;
                        if (query.isEmpty()) {
                            matches = true;
                        } else if (query.contains("workshop") && BookingType.WORKSHOP.equals(type)) {
                            matches = true;
                        } else if (query.contains("venue") && BookingType.VENUE.equals(type)) {
                            matches = true;
                        } else if (query.contains("practice") && BookingType.PRACTICE_ROOM.equals(type)) {
                            matches = true;
                        } else if (BookingType.SLOT.equals(type)
                                && (query.contains("studio") || query.contains("recording")
                                        || query.contains("shoot") || query.contains("creation"))) {
                            matches = true;
                        } else if (space.getActivityType() != null
                                && space.getActivityType().toLowerCase().contains(query)) {
                            matches = true;
                        } else if (query.contains("learning") || query.contains("art") || query.contains("craft")) {
                            matches = true;
                        } else {
                            matches = space.getActivityType() != null
                                    && space.getActivityType().equalsIgnoreCase(activityName);
                        }

                        if (matches) {
                            validatedSpaces.add(space);
                        }
                    }

                    return OperationResult.success(validatedSpaces);
                });
    }

    public CompletableFuture<OperationResult<Space>> getVisibleV2SpaceDetails(String spaceId, String idToken) {
        if (ValidationUtil.isBlank(spaceId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }
        if (ValidationUtil.isBlank(idToken)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        return spaceDao.getSpace(spaceId, idToken)
                .thenCompose(spaceRes -> {
                    if (spaceRes.isFailure()) {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(spaceRes.getErrorCode(), spaceRes.getMessage()));
                    }
                    Space space = spaceRes.getData();
                    if (space == null) {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(OperationCode.NOT_FOUND, "Space not found."));
                    }

                    if (space.getSchemaVersion() != 2
                            || !SpaceApprovalStatus.APPROVED.equalsIgnoreCase(space.getApprovalStatus())
                            || !SpaceOperationalStatus.ACTIVE.equalsIgnoreCase(space.getOperationalStatus())
                            || !space.isActive()
                            || !SpaceCategory.CREATIVITY.equalsIgnoreCase(space.getCategory())) {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(OperationCode.NOT_FOUND,
                                        "Space is not currently visible in marketplace."));
                    }

                    String bId = space.getBusinessId();
                    if (ValidationUtil.isBlank(bId)) {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(OperationCode.NOT_FOUND, "Parent Business ID missing."));
                    }

                    return businessDao.getBusiness(bId, idToken)
                            .thenApply(busRes -> {
                                if (busRes.isFailure()) {
                                    return OperationResult.failure(busRes.getErrorCode(), busRes.getMessage());
                                }
                                Business bus = busRes.getData();
                                if (bus == null || bus.getSchemaVersion() != 2
                                        || !bus.isActive()
                                        || !BusinessStatus.APPROVED.equalsIgnoreCase(bus.getVerificationStatus())
                                        || !SpaceCategory.CREATIVITY.equalsIgnoreCase(bus.getCategory())
                                        || bus.getActivityGroups() == null
                                        || !containsIgnoreCase(bus.getActivityGroups(), space.getActivityGroup())) {
                                    return OperationResult.failure(OperationCode.NOT_FOUND,
                                            "Space is not currently visible in marketplace.");
                                }
                                return OperationResult.success(space);
                            });
                });
    }

    public CompletableFuture<OperationResult<Space>> getPublicVisibleV2SpaceDetails(String spaceId) {
        if (ValidationUtil.isBlank(spaceId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }

        return spaceDao.getPublicSpace(spaceId)
                .thenApply(spaceRes -> {
                    if (spaceRes.isFailure()) {
                        return OperationResult.failure(spaceRes.getErrorCode(), spaceRes.getMessage());
                    }
                    Space space = spaceRes.getData();
                    if (space == null) {
                        return OperationResult.failure(OperationCode.NOT_FOUND, "Space not found.");
                    }

                    if (!isPublicMarketplaceSpace(space)) {
                        return OperationResult.failure(OperationCode.NOT_FOUND,
                                "Space is not currently visible in marketplace.");
                    }

                    if (ValidationUtil.isBlank(space.getBusinessId())) {
                        return OperationResult.failure(OperationCode.NOT_FOUND, "Parent Business ID missing.");
                    }

                    return OperationResult.success(space);
                });
    }

    private boolean isPublicMarketplaceSpace(Space space) {
        return space != null
                && space.getSchemaVersion() == 2
                && SpaceApprovalStatus.APPROVED.equalsIgnoreCase(space.getApprovalStatus())
                && SpaceOperationalStatus.ACTIVE.equalsIgnoreCase(space.getOperationalStatus())
                && space.isActive()
                && SpaceCategory.CREATIVITY.equalsIgnoreCase(space.getCategory())
                && space.isParentBusinessMarketplaceEligible();
    }

    private boolean containsIgnoreCase(List<String> list, String value) {
        if (list == null || value == null)
            return false;
        for (String item : list) {
            if (item != null && item.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    // ==================== V2 Methods ====================

    public SubmissionReadinessResult validateV2Space(Business business, Space space, String authenticatedOwnerId) {
        SubmissionReadinessResult result = new SubmissionReadinessResult();
        List<String> missing = new ArrayList<>();

        if (space == null) {
            missing.add("Space object cannot be null");
            result.setReady(false);
            result.setMissingRequirements(missing);
            return result;
        }

        if (space.getSchemaVersion() != 2) {
            missing.add("Space schemaVersion must be 2");
        }

        if (ValidationUtil.isBlank(authenticatedOwnerId)) {
            missing.add("Authenticated owner ID is required");
        } else {
            if (!authenticatedOwnerId.equals(space.getOwnerId())) {
                missing.add("Space owner ID must match authenticated owner");
            }
        }

        if (business == null) {
            missing.add("Parent Business is required");
        } else {
            if (business.getSchemaVersion() != 2) {
                missing.add("Parent Business must be V2");
            }
            if (!business.isActive()) {
                missing.add("Parent Business must be active");
            }
            if (!BusinessStatus.APPROVED.equalsIgnoreCase(business.getVerificationStatus())) {
                missing.add("Parent Business must be APPROVED");
            }
            if (authenticatedOwnerId != null && !authenticatedOwnerId.equals(business.getOwnerId())) {
                missing.add("Parent Business owner must match authenticated owner");
            }
            if (!SpaceCategory.CREATIVITY.equalsIgnoreCase(business.getCategory())) {
                missing.add("Parent Business category must be CREATIVITY");
            }
            if (space.getActivityGroup() != null
                    && (business.getActivityGroups() == null
                            || !business.getActivityGroups().contains(space.getActivityGroup()))) {
                missing.add("Parent Business activity groups must contain Space activity group");
            }
        }

        if (ValidationUtil.isBlank(space.getSpaceName())) {
            missing.add("Space name is required");
        }
        if (ValidationUtil.isBlank(space.getDescription())) {
            missing.add("Space description is required");
        }

        if (!SpaceCategory.CREATIVITY.equalsIgnoreCase(space.getCategory())) {
            missing.add("Space category must be CREATIVITY");
        }
        if (ValidationUtil.isBlank(space.getActivityGroup())
                || !CreativityActivityGroup.isValid(space.getActivityGroup())) {
            missing.add("Valid Creativity activity group is required");
        }
        if (ValidationUtil.isBlank(space.getActivityType())) {
            missing.add("Canonical activity type is required");
        }

        // Location
        SpaceLocation loc = space.getSpaceLocation();
        if (loc == null) {
            missing.add("Space location is required");
        } else {
            if (ValidationUtil.isBlank(loc.getAddressLine()))
                missing.add("Address line is required");
            if (ValidationUtil.isBlank(loc.getCity()))
                missing.add("City is required");
            if (ValidationUtil.isBlank(loc.getState()))
                missing.add("State is required");
            if (ValidationUtil.isBlank(loc.getPostalCode())
                    || !POSTAL_CODE_PATTERN.matcher(loc.getPostalCode()).matches()) {
                missing.add("Valid 6-digit postal code is required");
            }
        }

        if (space.getCapacity() <= 0) {
            missing.add("Capacity must be greater than zero");
        }

        // TimeZone
        if (ValidationUtil.isBlank(space.getTimeZoneId())) {
            missing.add("Timezone ID is required");
        } else {
            try {
                ZoneId.of(space.getTimeZoneId());
            } catch (Exception e) {
                missing.add("Invalid timezone ID: " + space.getTimeZoneId());
            }
        }

        // BookingType & PricingUnit
        String bType = space.getBookingType();
        if (ValidationUtil.isBlank(bType) || !BookingType.isValid(bType)
                || BookingType.RESOURCE.equalsIgnoreCase(bType)) {
            missing.add("Valid V2 booking type is required");
        }
        String pUnit = space.getPricingUnit();
        if (ValidationUtil.isBlank(pUnit) || !PricingUnit.isValid(pUnit)) {
            missing.add("Valid pricing unit is required");
        }
        if (space.getBasePricePaise() <= 0L) {
            missing.add("Base price in paise must be greater than zero");
        }

        // BookingConfig matching
        BookingConfig cfg = space.getBookingConfig();
        if (cfg == null || !space.hasMatchingBookingConfig()) {
            missing.add("Valid matching booking configuration is required");
        } else {
            validateBookingConfigMatrix(bType, pUnit, space.getBasePricePaise(), cfg, missing);
        }

        // OperatingHours
        Map<String, OperatingDayConfig> oh = space.getOperatingHours();
        if (oh == null || oh.isEmpty()) {
            missing.add("Operating hours map is required");
        } else {
            int activeDays = 0;
            for (Map.Entry<String, OperatingDayConfig> entry : oh.entrySet()) {
                OperatingDayConfig dayCfg = entry.getValue();
                if (dayCfg != null && dayCfg.isActive()) {
                    activeDays++;
                    String open = dayCfg.getOpeningTime();
                    String close = dayCfg.getClosingTime();
                    if (open == null || !TIME_FORMAT_PATTERN.matcher(open).matches()) {
                        missing.add("Opening time for " + entry.getKey() + " must be HH:mm 24h format");
                    }
                    if (close == null || !TIME_FORMAT_PATTERN.matcher(close).matches()) {
                        missing.add("Closing time for " + entry.getKey() + " must be HH:mm 24h format");
                    }
                    if (open != null && close != null && open.compareTo(close) >= 0) {
                        missing.add("Opening time must precede closing time for " + entry.getKey());
                    }
                }
            }
            if (activeDays == 0) {
                missing.add("At least one operating day must be active");
            }
        }

        boolean isReady = missing.isEmpty();
        result.setReady(isReady);
        result.setMissingRequirements(missing);
        return result;
    }

    private void validateBookingConfigMatrix(String bookingType, String pricingUnit, long basePricePaise,
            BookingConfig cfg, List<String> missing) {
        if (bookingType == null)
            return;
        switch (bookingType.trim().toUpperCase()) {
            case BookingType.SEAT:
                if (!(cfg instanceof SeatBookingConfig)) {
                    missing.add("Seat booking config required");
                    break;
                }
                SeatBookingConfig seatCfg = (SeatBookingConfig) cfg;
                if (seatCfg.getSelectionMode() == null || seatCfg.getSelectionMode().isBlank()) {
                    missing.add("Seat selection mode is required");
                }
                if (seatCfg.getSeatPriceMode() == null || seatCfg.getSeatPriceMode().isBlank()) {
                    missing.add("Seat price mode is required");
                }
                if (seatCfg.getMinimumDurationMinutes() <= 0)
                    missing.add("Minimum duration must be > 0");
                if (seatCfg.getMaximumDurationMinutes() < seatCfg.getMinimumDurationMinutes())
                    missing.add("Maximum duration must be >= minimum duration");
                if (seatCfg.getDurationStepMinutes() <= 0)
                    missing.add("Duration step must be > 0");
                if (seatCfg.getMaxGuestsPerBooking() <= 0)
                    missing.add("Max guests per booking must be > 0");
                if (seatCfg.getAdvanceBookingDays() < 0)
                    missing.add("Advance booking days must be >= 0");
                if (seatCfg.getMinimumAdvanceMinutes() < 0)
                    missing.add("Minimum advance minutes must be >= 0");
                if (!Set.of(PricingUnit.SEAT, PricingUnit.PERSON, PricingUnit.HOUR, PricingUnit.SESSION,
                        PricingUnit.FLAT).contains(pricingUnit)) {
                    missing.add("Pricing unit for SEAT must be SEAT, PERSON, HOUR, SESSION, or FLAT");
                }
                break;
            case BookingType.SLOT:
                if (!(cfg instanceof SlotBookingConfig)) {
                    missing.add("Slot booking config required");
                    break;
                }
                SlotBookingConfig slotCfg = (SlotBookingConfig) cfg;
                if (slotCfg.getMinimumDurationMinutes() <= 0)
                    missing.add("Minimum duration must be > 0");
                if (slotCfg.getMaximumDurationMinutes() < slotCfg.getMinimumDurationMinutes())
                    missing.add("Maximum duration must be >= minimum duration");
                if (slotCfg.getDurationStepMinutes() <= 0)
                    missing.add("Duration step must be > 0");
                if (slotCfg.getSlotIntervalMinutes() <= 0)
                    missing.add("Slot interval must be > 0");
                if (slotCfg.getBufferBeforeMinutes() < 0)
                    missing.add("Buffer before must be >= 0");
                if (slotCfg.getBufferAfterMinutes() < 0)
                    missing.add("Buffer after must be >= 0");
                if (slotCfg.getMaxPeople() <= 0)
                    missing.add("Max people must be > 0");
                if (slotCfg.getAdvanceBookingDays() < 0)
                    missing.add("Advance booking days must be >= 0");
                if (slotCfg.getMinimumAdvanceMinutes() < 0)
                    missing.add("Minimum advance minutes must be >= 0");
                if (!Set.of(PricingUnit.HOUR, PricingUnit.SESSION, PricingUnit.FLAT).contains(pricingUnit)) {
                    missing.add("Pricing unit for SLOT must be HOUR, SESSION, or FLAT");
                }
                break;
            case BookingType.WORKSHOP:
                if (!(cfg instanceof WorkshopBookingConfig)) {
                    missing.add("Workshop booking config required");
                    break;
                }
                WorkshopBookingConfig wsCfg = (WorkshopBookingConfig) cfg;
                if (wsCfg.getPricePerPersonPaise() <= 0L)
                    missing.add("Price per person in paise must be > 0");
                if (wsCfg.getMinimumParticipantsPerBooking() <= 0)
                    missing.add("Minimum participants must be > 0");
                if (wsCfg.getMaximumParticipantsPerBooking() < wsCfg.getMinimumParticipantsPerBooking())
                    missing.add("Maximum participants must be >= minimum participants");
                if (wsCfg.getBookingCutoffHours() < 0)
                    missing.add("Booking cutoff hours must be >= 0");
                if (wsCfg.getCancellationCutoffHours() < 0)
                    missing.add("Cancellation cutoff hours must be >= 0");
                if (!PricingUnit.PERSON.equalsIgnoreCase(pricingUnit)) {
                    missing.add("Pricing unit for WORKSHOP must be PERSON");
                }
                if (wsCfg.getPricePerPersonPaise() != basePricePaise) {
                    missing.add("Workshop pricePerPersonPaise must equal basePricePaise");
                }
                break;
            case BookingType.RENTAL:
                if (!(cfg instanceof RentalBookingConfig)) {
                    missing.add("Rental booking config required");
                    break;
                }
                RentalBookingConfig rentalCfg = (RentalBookingConfig) cfg;
                if (rentalCfg.getAvailableQuantity() <= 0)
                    missing.add("Available quantity must be > 0");
                if (rentalCfg.getMinimumRentalDurationMinutes() <= 0)
                    missing.add("Minimum rental duration must be > 0");
                if (rentalCfg.getMaximumRentalDurationMinutes() < rentalCfg.getMinimumRentalDurationMinutes())
                    missing.add("Maximum rental duration must be >= minimum rental duration");
                if (rentalCfg.getSecurityDepositPaise() < 0L)
                    missing.add("Security deposit must be >= 0");
                if (rentalCfg.getDeliveryRadiusKm() < 0.0)
                    missing.add("Delivery radius must be >= 0");
                if (rentalCfg.getDeliveryChargePaise() < 0L)
                    missing.add("Delivery charge must be >= 0");
                if (rentalCfg.getLateFeePaise() < 0L)
                    missing.add("Late fee must be >= 0");
                if (rentalCfg.getDeliveryChargePaise() > 0L && !rentalCfg.isDeliveryAvailable()) {
                    missing.add("Delivery charge requires delivery to be enabled");
                }
                if (!rentalCfg.isPickupAvailable() && !rentalCfg.isDeliveryAvailable()) {
                    missing.add("At least pickup or delivery must be available");
                }
                if (!Set.of(PricingUnit.HOUR, PricingUnit.DAY, PricingUnit.FLAT).contains(pricingUnit)) {
                    missing.add("Pricing unit for RENTAL must be HOUR, DAY, or FLAT");
                }
                break;
            case BookingType.VENUE:
                if (!(cfg instanceof VenueBookingConfig)) {
                    missing.add("Venue booking config required");
                    break;
                }
                VenueBookingConfig venueCfg = (VenueBookingConfig) cfg;
                if (venueCfg.getMinimumBookingHours() <= 0)
                    missing.add("Minimum booking hours must be > 0");
                if (venueCfg.getMaximumBookingHours() < venueCfg.getMinimumBookingHours())
                    missing.add("Maximum booking hours must be >= minimum booking hours");
                if (venueCfg.getMinimumAdvanceDays() < 0)
                    missing.add("Minimum advance days must be >= 0");
                if (venueCfg.getSecurityDepositPaise() < 0L)
                    missing.add("Security deposit must be >= 0");
                if (venueCfg.getOvertimeRatePaise() < 0L)
                    missing.add("Overtime rate must be >= 0");
                if (!Set.of(PricingUnit.HOUR, PricingUnit.EVENT, PricingUnit.SESSION, PricingUnit.FLAT)
                        .contains(pricingUnit)) {
                    missing.add("Pricing unit for VENUE must be HOUR, EVENT, SESSION, or FLAT");
                }
                break;
            case BookingType.PRACTICE_ROOM:
                if (!(cfg instanceof PracticeRoomBookingConfig)) {
                    missing.add("Practice room booking config required");
                    break;
                }
                PracticeRoomBookingConfig prCfg = (PracticeRoomBookingConfig) cfg;
                if (prCfg.getMinimumDurationMinutes() <= 0)
                    missing.add("Minimum duration must be > 0");
                if (prCfg.getMaximumDurationMinutes() < prCfg.getMinimumDurationMinutes())
                    missing.add("Maximum duration must be >= minimum duration");
                if (prCfg.getDurationStepMinutes() <= 0)
                    missing.add("Duration step must be > 0");
                if (!Set.of(PricingUnit.HOUR, PricingUnit.SESSION, PricingUnit.FLAT).contains(pricingUnit)) {
                    missing.add("Pricing unit for PRACTICE_ROOM must be HOUR, SESSION, or FLAT");
                }
                break;
            default:
                missing.add("Unsupported booking type: " + bookingType);
                break;
        }
    }

    public CompletableFuture<OperationResult<Space>> createV2Space(
            String authenticatedOwnerId,
            String businessId,
            Space submittedSpace,
            String idToken) {

        if (ValidationUtil.isBlank(authenticatedOwnerId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED,
                            "Authenticated owner ID is required."));
        }
        if (ValidationUtil.isBlank(businessId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Business ID is required."));
        }
        if (ValidationUtil.isBlank(idToken)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }
        if (submittedSpace == null) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Submitted Space cannot be null."));
        }

        // Authoritative parent Business fetch
        return businessDao.getBusiness(businessId, idToken)
                .thenCompose(bRes -> {
                    if (bRes.isFailure() || bRes.getData() == null) {
                        return CompletableFuture.completedFuture(
                                OperationResult.<Space>failure(
                                        OperationCode.NOT_FOUND.equalsIgnoreCase(bRes.getErrorCode())
                                                ? OperationCode.NOT_FOUND
                                                : bRes.getErrorCode(),
                                        "Parent Business not found: " + bRes.getMessage()));
                    }

                    Business business = bRes.getData();
                    if (business.getSchemaVersion() != 2) {
                        return CompletableFuture.completedFuture(
                                OperationResult.<Space>failure(OperationCode.INVALID_ARGUMENT,
                                        "Parent Business must be schemaVersion == 2."));
                    }
                    if (!authenticatedOwnerId.equals(business.getOwnerId())) {
                        return CompletableFuture.completedFuture(
                                OperationResult.<Space>failure(OperationCode.PERMISSION_DENIED,
                                        "Access denied. Parent Business is not owned by authenticated Owner."));
                    }
                    if (!business.isActive()) {
                        return CompletableFuture.completedFuture(
                                OperationResult.<Space>failure(OperationCode.PERMISSION_DENIED,
                                        "Cannot create Space for inactive Business."));
                    }
                    if (!BusinessStatus.APPROVED.equalsIgnoreCase(business.getVerificationStatus())) {
                        return CompletableFuture.completedFuture(
                                OperationResult.<Space>failure(OperationCode.PERMISSION_DENIED,
                                        "Cannot create Space for unapproved Business."));
                    }
                    if (!SpaceCategory.CREATIVITY.equalsIgnoreCase(business.getCategory())) {
                        return CompletableFuture.completedFuture(
                                OperationResult.<Space>failure(OperationCode.INVALID_ARGUMENT,
                                        "Business category must be CREATIVITY."));
                    }
                    if (business.getActivityGroups() == null
                            || !business.getActivityGroups().contains(submittedSpace.getActivityGroup())) {
                        return CompletableFuture.completedFuture(
                                OperationResult.<Space>failure(OperationCode.INVALID_ARGUMENT,
                                        "Business does not support Space activity group: "
                                                + submittedSpace.getActivityGroup()));
                    }

                    // Perform Domain Validation
                    SubmissionReadinessResult readiness = validateV2Space(business, submittedSpace,
                            authenticatedOwnerId);
                    if (!readiness.isReady()) {
                        return CompletableFuture.completedFuture(
                                OperationResult.<Space>failure(OperationCode.INVALID_ARGUMENT,
                                        "V2 Space validation failed: "
                                                + String.join("; ", readiness.getMissingRequirements())));
                    }

                    // Application-assigned single captured timestamp & UUID
                    String spaceId = UUID.randomUUID().toString();
                    long now = System.currentTimeMillis();

                    Space v2Space = Space.createV2(
                            spaceId,
                            business.getBusinessId(),
                            authenticatedOwnerId,
                            submittedSpace.getSpaceName(),
                            submittedSpace.getDescription(),
                            SpaceCategory.CREATIVITY,
                            submittedSpace.getActivityGroup(),
                            submittedSpace.getActivityType(),
                            submittedSpace.getSpaceLocation(),
                            submittedSpace.getCoverPhotoUrl(),
                            submittedSpace.getPhotoUrls(),
                            submittedSpace.getAmenities(),
                            submittedSpace.getFacilities(),
                            submittedSpace.getCapacity(),
                            submittedSpace.getBookingType(),
                            submittedSpace.getBasePricePaise(),
                            submittedSpace.getPricingUnit(),
                            submittedSpace.getBookingConfig(),
                            submittedSpace.getOperatingHours(),
                            submittedSpace.getRules(),
                            submittedSpace.getCancellationPolicy());

                    v2Space.setTimeZoneId(submittedSpace.getTimeZoneId());
                    v2Space.setApprovalStatus(SpaceApprovalStatus.DRAFT);
                    v2Space.setOperationalStatus(SpaceOperationalStatus.INACTIVE);
                    v2Space.setActive(true);
                    v2Space.setSubmittedAt(0L);
                    v2Space.setReviewedAt(0L);
                    v2Space.setRejectionReason("");
                    v2Space.setApprovedAt(0L);
                    v2Space.setPublishedAt(0L);
                    v2Space.setCreatedAt(now);
                    v2Space.setUpdatedAt(now);

                    return spaceDao.createV2Space(v2Space, idToken);
                });
    }

    public CompletableFuture<OperationResult<Space>> updateV2Space(
            String authenticatedOwnerId,
            Space existingSpace,
            Space submittedSpace,
            String idToken) {

        if (ValidationUtil.isBlank(authenticatedOwnerId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED,
                            "Authenticated owner ID is required."));
        }
        if (existingSpace == null || existingSpace.getSchemaVersion() != 2) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT,
                            "Existing space must have schemaVersion == 2."));
        }
        if (submittedSpace == null) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Submitted space cannot be null."));
        }
        if (ValidationUtil.isBlank(idToken)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        // Confirm ownership
        if (!authenticatedOwnerId.equals(existingSpace.getOwnerId())) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.PERMISSION_DENIED,
                            "Access denied. Space is not owned by authenticated Owner."));
        }

        // Authoritative parent Business fetch
        return businessDao.getBusiness(existingSpace.getBusinessId(), idToken)
                .thenCompose(bRes -> {
                    if (bRes.isFailure() || bRes.getData() == null) {
                        return CompletableFuture.completedFuture(
                                OperationResult.<Space>failure(OperationCode.NOT_FOUND,
                                        "Parent Business not found for space."));
                    }

                    Business business = bRes.getData();
                    if (!authenticatedOwnerId.equals(business.getOwnerId()) || !business.isActive()) {
                        return CompletableFuture.completedFuture(
                                OperationResult.<Space>failure(OperationCode.PERMISSION_DENIED,
                                        "Parent Business is invalid or inactive."));
                    }
                    if (!BusinessStatus.APPROVED.equalsIgnoreCase(business.getVerificationStatus())) {
                        return CompletableFuture.completedFuture(
                                OperationResult.<Space>failure(OperationCode.PERMISSION_DENIED,
                                        "Parent Business is not approved."));
                    }

                    // Check submission state
                    String currentApproval = existingSpace.getApprovalStatus();
                    if (SpaceApprovalStatus.SUBMITTED.equalsIgnoreCase(currentApproval)
                            || SpaceApprovalStatus.UNDER_REVIEW.equalsIgnoreCase(currentApproval)) {
                        return CompletableFuture.completedFuture(
                                OperationResult.<Space>failure(OperationCode.PERMISSION_DENIED,
                                        "Cannot edit Space while SUBMITTED or UNDER_REVIEW."));
                    }

                    // Validate submittedSpace against domain
                    SubmissionReadinessResult readiness = validateV2Space(business, submittedSpace,
                            authenticatedOwnerId);
                    if (!readiness.isReady()) {
                        return CompletableFuture.completedFuture(
                                OperationResult.<Space>failure(OperationCode.INVALID_ARGUMENT,
                                        "Validation failed for updated space: "
                                                + String.join("; ", readiness.getMissingRequirements())));
                    }

                    boolean materialChanged = hasMaterialChanges(existingSpace, submittedSpace);

                    // Prepare target updated Space
                    existingSpace.setSpaceName(submittedSpace.getSpaceName());
                    existingSpace.setDescription(submittedSpace.getDescription());
                    existingSpace.setCategory(SpaceCategory.CREATIVITY);
                    existingSpace.setActivityGroup(submittedSpace.getActivityGroup());
                    existingSpace.setActivityType(submittedSpace.getActivityType());
                    existingSpace.setSpaceLocation(submittedSpace.getSpaceLocation());
                    existingSpace.setCoverPhotoUrl(submittedSpace.getCoverPhotoUrl());
                    existingSpace.setPhotoUrls(submittedSpace.getPhotoUrls());
                    existingSpace.setAmenities(submittedSpace.getAmenities());
                    existingSpace.setFacilities(submittedSpace.getFacilities());
                    existingSpace.setCapacity(submittedSpace.getCapacity());
                    existingSpace.setBookingType(submittedSpace.getBookingType());
                    existingSpace.setBasePricePaise(submittedSpace.getBasePricePaise());
                    existingSpace.setPricingUnit(submittedSpace.getPricingUnit());
                    existingSpace.setBookingConfig(submittedSpace.getBookingConfig());
                    existingSpace.setOperatingHours(submittedSpace.getOperatingHours());
                    existingSpace.setRules(submittedSpace.getRules());
                    existingSpace.setCancellationPolicy(submittedSpace.getCancellationPolicy());
                    existingSpace.setTimeZoneId(submittedSpace.getTimeZoneId());

                    long now = System.currentTimeMillis();
                    existingSpace.setUpdatedAt(now);

                    if (SpaceApprovalStatus.APPROVED.equalsIgnoreCase(currentApproval)) {
                        if (materialChanged) {
                            // Reset approval status on material edit
                            existingSpace.setApprovalStatus(SpaceApprovalStatus.DRAFT);
                            existingSpace.setOperationalStatus(SpaceOperationalStatus.INACTIVE);
                            existingSpace.setStatus(SpaceStatus.DRAFT);
                            existingSpace.setSubmittedAt(0L);
                            existingSpace.setReviewedAt(0L);
                            existingSpace.setRejectionReason("");
                            existingSpace.setApprovedAt(0L);
                            existingSpace.setPublishedAt(0L);
                        }
                    } else if (SpaceApprovalStatus.REJECTED.equalsIgnoreCase(currentApproval)) {
                        existingSpace.setApprovalStatus(SpaceApprovalStatus.DRAFT);
                        existingSpace.setOperationalStatus(SpaceOperationalStatus.INACTIVE);
                        existingSpace.setStatus(SpaceStatus.DRAFT);
                        existingSpace.setRejectionReason("");
                        existingSpace.setReviewedAt(0L);
                    } else {
                        existingSpace.setApprovalStatus(SpaceApprovalStatus.DRAFT);
                        existingSpace.setOperationalStatus(SpaceOperationalStatus.INACTIVE);
                        existingSpace.setStatus(SpaceStatus.DRAFT);
                    }

                    // Construct fixed update mask from allowlist
                    List<String> updateMask = List.of(
                            "spaceName", "description", "category", "activityGroup", "activityType",
                            "location", "coverPhotoUrl", "photoUrls", "amenities", "facilities",
                            "capacity", "bookingType", "basePricePaise", "pricingUnit", "bookingConfig",
                            "operatingHours", "rules", "cancellationPolicy", "approvalStatus", "operationalStatus",
                            "submittedAt", "rejectionReason", "publishedAt",
                            "updatedAt", "timeZoneId");

                    return spaceDao.updateV2Space(existingSpace, updateMask, idToken);
                });
    }

    private boolean hasMaterialChanges(Space existing, Space submitted) {
        if (!Objects.equals(existing.getSpaceName(), submitted.getSpaceName()))
            return true;
        if (!Objects.equals(existing.getDescription(), submitted.getDescription()))
            return true;
        if (!Objects.equals(existing.getCategory(), submitted.getCategory()))
            return true;
        if (!Objects.equals(existing.getActivityGroup(), submitted.getActivityGroup()))
            return true;
        if (!Objects.equals(existing.getActivityType(), submitted.getActivityType()))
            return true;
        if (!Objects.equals(existing.getCoverPhotoUrl(), submitted.getCoverPhotoUrl()))
            return true;
        if (existing.getCapacity() != submitted.getCapacity())
            return true;
        if (!Objects.equals(existing.getBookingType(), submitted.getBookingType()))
            return true;
        if (existing.getBasePricePaise() != submitted.getBasePricePaise())
            return true;
        if (!Objects.equals(existing.getPricingUnit(), submitted.getPricingUnit()))
            return true;
        if (!Objects.equals(existing.getCancellationPolicy(), submitted.getCancellationPolicy()))
            return true;
        if (!Objects.equals(existing.getTimeZoneId(), submitted.getTimeZoneId()))
            return true;

        if (!Objects.equals(existing.getPhotoUrls(), submitted.getPhotoUrls()))
            return true;
        if (!Objects.equals(existing.getAmenities(), submitted.getAmenities()))
            return true;
        if (!Objects.equals(existing.getFacilities(), submitted.getFacilities()))
            return true;
        if (!Objects.equals(existing.getRules(), submitted.getRules()))
            return true;

        if (!Objects.equals(existing.getSpaceLocation(), submitted.getSpaceLocation()))
            return true;
        if (!Objects.equals(existing.getBookingConfig(), submitted.getBookingConfig()))
            return true;
        if (!Objects.equals(existing.getOperatingHours(), submitted.getOperatingHours()))
            return true;

        return false;
    }
}