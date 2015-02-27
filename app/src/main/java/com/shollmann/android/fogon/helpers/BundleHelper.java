package com.shollmann.android.fogon.helpers;

import android.os.Bundle;

import java.io.Serializable;

public class BundleHelper {
    public static boolean bundleContains(Bundle bundle, Class<? extends Serializable> aClass) {
        return bundle != null && bundle.containsKey(aClass.getCanonicalName());
    }

    public static boolean bundleContains(Bundle bundle, String key) {
        return bundle != null && bundle.containsKey(key);
    }

    public static <T> T fromBundle(Bundle bundle, String key) {
        return fromBundle(bundle, key, (T) null);
    }

    @SuppressWarnings("unchecked")
    public static <T> T fromBundle(Bundle bundle, String key, T defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
        return bundleContains(bundle, key) ? (T) bundle.get(key) : defaultValue;
    }
}
