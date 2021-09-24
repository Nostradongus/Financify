package com.mobdeve.s13.group2.financify.helpers;

// Helper class for BCrypt
public class BCryptHelper {

    /**
     * Generates a hashed version of the user's PIN.
     *
     * @param PIN the user's PIN to be hashed
     * @return  the hashed PIN
     */
    public static String generateHash (String PIN) {
        return BCrypt.hashpw (PIN, BCrypt.gensalt ());
    }

    /**
     * Checks if a given PIN matches a hashed PIN.
     *
     * @param PIN       the PIN to be checked
     * @param hashedPIN the hashed PIN to be compared to
     * @return  true if matching; otherwise, false
     */
    public static boolean isCorrectPIN (String PIN, String hashedPIN) {
        return BCrypt.checkpw (PIN, hashedPIN);
    }
}
