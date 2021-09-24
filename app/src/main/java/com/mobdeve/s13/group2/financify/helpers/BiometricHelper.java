package com.mobdeve.s13.group2.financify.helpers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.biometric.BiometricManager;
import androidx.core.app.ActivityCompat;

/**
 * Helper class for verifying if a device can use biometrics for authentication.
 */
public class BiometricHelper {

    /**
     * Checks if a device can use biometric authentication using the following conditions:
     * (1) Device has an API version of at least 23
     * (2) Device has biometrics hardware
     * (3) Permissions for biometrics are granted by the owner
     *
     * @param context   activity context
     * @return  true if device supports biometrics; otherwise false
     */
    public static boolean canUseBiometricAuth (Context context) {
        return isSDKVersionSupported() &&
               isHardwareSupported (context) &&
               isPermissionGranted (context);
    }

    /**
     * Checks if a device has an API level of at least 23.
     *
     * @return true if API level is at least 23; otherwise false
     */
    private static boolean isSDKVersionSupported () {
        boolean isSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
        System.out.println ("[BiometricHelper] Device at least API 23?: " + isSupported);
        return isSupported;
    }

    /**
     * Checks if a device has the necessary hardware for biometric authentication.
     *
     * @param context   activity context
     * @return  true if the device has the needed hardware; otherwise false
     */
    private static boolean isHardwareSupported (Context context) {
        BiometricManager bm = BiometricManager.from (context);
        int authCode = bm.canAuthenticate (BiometricManager.Authenticators.BIOMETRIC_WEAK);
        boolean canAuthenticate = authCode == BiometricManager.BIOMETRIC_SUCCESS;
        System.out.println ("[BiometricHelper] canAuthenticate code: " + authCode);
        System.out.println ("[BiometricHelper] Hardware is supported?: " + (canAuthenticate));
        return canAuthenticate;
    }

    /**
     * Checks if the necessary permissions are granted by the device's owner.
     *
     * @param context   activity context
     * @return  true if permissions are granted; otherwise false
     */
    private static boolean isPermissionGranted(Context context) {
        boolean isPermissionGranted = ActivityCompat.checkSelfPermission (
                context,
                Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED;
        System.out.println ("[BiometricHelper] Permission for biometrics granted?: " + isPermissionGranted);
        return isPermissionGranted;
    }
}
