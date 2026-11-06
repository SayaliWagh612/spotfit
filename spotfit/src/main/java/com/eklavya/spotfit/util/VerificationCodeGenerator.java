package com.eklavya.spotfit.util;

import com.eklavya.spotfit.repository.BookingRepository;
import java.security.SecureRandom;

public class VerificationCodeGenerator {

    // Removed O, 0, I, 1 for visual clarity
    private static final String CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CODE_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateUniqueCode() {
        String code;
        do {
            code = "SPF-" + generateRandomString();
        } while (BookingRepository.findByVerificationCode(code) != null);

        return code;
    }

    private static String generateRandomString() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}
