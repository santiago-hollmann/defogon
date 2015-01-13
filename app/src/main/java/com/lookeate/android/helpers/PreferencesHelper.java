package com.lookeate.android.helpers;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.olx.olx.Constants;
import com.olx.olx.LeChuckApplication;
import com.olx.olx.R;
import com.olx.olx.model.ResolvedLocation;
import com.olx.olx.util.ListingModes;
import com.olx.smaug.api.model.City;
import com.olx.smaug.api.model.User;

public class PreferencesHelper {

    private final static String CATEGORIES_COUNTER = "categoriesCounter";
    private final static String RESOLVED_LOCATION = "resolvedLocation";
    private final static String PUBLISH_LOCATION = "publishLocation";
    private final static String AROUND_ME_LOCATION = "aroundMeLocation";
    private final static String API_ENDPOINT = "apiEndpoint";
    private static final String USER = "user";
    private static final String CONTACT_NAME = "name";
    private static final String CONTACT_PHONE = "phone";
    private static final String CONTACT_EMAIL = "email";
    private static final String FIRST_START = "firstStart";
    private static final String SHOULD_UPDATE_COUNTRY_TAG = "shouldUpdateCountryTag";
    private static final String REPLIER_TAG = "isReplierTag";
    private static final String LISTER_TAG = "isListerTag";
    private static final String ACTIVITIES_VIEWS = "activitiesViews";
    private static final String LAST_ASK_FOR_FEEDBACK = "lastAskForFeeback";
    private static final String GAVE_FEEDBACK = "gaveFeedback";
    private static final String APP_VERSION = "appVersion";
    private static final String USER_LEVEL = "userLevel";
    private static final String SEND_USER_INFORMATION = "sendUserInformation";
    private static final String LISTING_MODE = "listingMode";
    private static final String CATEGORIES_ALREADY_CACHED = "categoriesAlreadyCached";
    private static final String AUTOLOCATION_CITY = "autolocationCity";
    private static final String USE_AUTOLOCATION = "useAutolocation";
    private static final String TUTORIAL_SEEN = "tutorialSeen";
    private static final String DRAWER_SHOWCASE = "drawerShowCase";
    private static final String LISTING_TOOLTIP = "listingTooltip";
    private static final String OEM_ACTIVATION = "oemActivation";
    private static final String URBAN_AIRSHIP_DATA_SENT = "urbanAirshipDataSent";
    private static final String IS_NEW_VERSION = "isNewVersion";
    private static final String GOOGLE_PLAY_SERVICES_SHOWN = "googlePlayServicesShown";
    private static final String SWAP_INSTRUCTIONS_SEEN = "swapInstructionSeen";

    private final static Gson gson = new Gson();

    static {
        prefs = PreferenceManager.getDefaultSharedPreferences(LeChuckApplication.getApplication());
    }

    private static SharedPreferences prefs;

    public static void clear() {
        prefs.edit().clear().commit();
    }

    public static int getListingMode() {
        return get(LISTING_MODE, ListingModes.LIST);
    }

    public static void setListingMode(int value) {
        set(LISTING_MODE, value);
    }

    public static int getUserLevel() {
        return get(USER_LEVEL, 0);
    }

    public static void incrementUserLevel() {
        set(USER_LEVEL, getUserLevel() + 1);
    }

    public static String getCurrentVersion() {
        return get(APP_VERSION, null);
    }

    public static void setCurrentVersion(String value) {
        set(APP_VERSION, value);
    }

    public static boolean showGooglePlayService() {
        return get(GOOGLE_PLAY_SERVICES_SHOWN, true);
    }

    public static void setShowGooglePlayService(boolean value) {
        set(GOOGLE_PLAY_SERVICES_SHOWN, value);
    }

    public static boolean showDrawerShowCase() {
        return get(DRAWER_SHOWCASE, true);
    }

    public static void setShowDrawerShowCase(boolean value) {
        set(DRAWER_SHOWCASE, value);
    }

    public static boolean showListingTooltip() {
        return get(LISTING_TOOLTIP, true);
    }

    public static void setShowListingTooltip(boolean value) {
        set(LISTING_TOOLTIP, value);
    }

    public static boolean wasTutorialSeen() {
        return get(TUTORIAL_SEEN, false);
    }

    public static void setTutorialseen(boolean value) {
        set(TUTORIAL_SEEN, value);
    }

    public static boolean isCategoriesAlreadyCached() {
        return get(CATEGORIES_ALREADY_CACHED, false);
    }

    public static void setCategoriesAlreadyCached(boolean value) {
        set(CATEGORIES_ALREADY_CACHED, value);
    }

    public static boolean getSendUserInformationEvent() {
        return get(SEND_USER_INFORMATION, true);
    }

    public static void setSendUserInformationEvent(boolean value) {
        set(SEND_USER_INFORMATION, value);
    }

    public static boolean getGaveFeedback() {
        return get(GAVE_FEEDBACK, false);
    }

    public static void setGaveFeedback(boolean value) {
        set(GAVE_FEEDBACK, value);
    }

    public static long getLastAskForFeedback() {
        return get(LAST_ASK_FOR_FEEDBACK, 0l);
    }

    public static void setLastAskForFeedback(long value) {
        set(LAST_ASK_FOR_FEEDBACK, value);
    }

    public static int getActivitiesViews() {
        return get(ACTIVITIES_VIEWS, 0);
    }

    public static void setActivitiesViews(int value) {
        set(ACTIVITIES_VIEWS, value);
    }

    public static String getContactName() {
        return get(CONTACT_NAME, null);
    }

    public static void setContactName(String contactName) {
        set(CONTACT_NAME, contactName);
    }

    public static String getContactPhone() {
        return get(CONTACT_PHONE, null);
    }

    public static void setContactPhone(String contactPhone) {
        set(CONTACT_PHONE, contactPhone);
    }

    public static void setFirstStart(boolean firstStart) {
        set(FIRST_START, firstStart);
    }

    public static boolean isFirstStart() {
        return get(FIRST_START, true);
    }

    public static void setShouldUpdateCountryTag(boolean shouldUpdateCountryTag) {
        set(SHOULD_UPDATE_COUNTRY_TAG, shouldUpdateCountryTag);
    }

    public static boolean shouldUpdateCountryTag() {
        return get(SHOULD_UPDATE_COUNTRY_TAG, true);
    }

    public static void setReplier(boolean replier) {
        set(REPLIER_TAG, replier);
    }

    public static boolean isReplier() {
        return get(REPLIER_TAG, false);
    }

    public static void setLister(boolean lister) {
        set(LISTER_TAG, lister);
    }

    public static boolean isLister() {
        return get(LISTER_TAG, false);
    }

    public static String getContactEmail() {
        return get(CONTACT_EMAIL, null);
    }

    public static void setContactEmail(String contactEmail) {
        set(CONTACT_EMAIL, contactEmail);
    }

    public static int getApiEndpoint() {
        return get(API_ENDPOINT, Constants.Endpoints.PRODUCTION);
    }

    public static void setApiEndpoint(int endpoint) {
        set(API_ENDPOINT, endpoint);
    }

    public static String getLanguageCode() {
        return ResourcesHelper.getString(R.string.language_code);
    }

    public static void setPublishLocation(ResolvedLocation resolvedLocation) {
        set(PUBLISH_LOCATION, gson.toJson(resolvedLocation));
    }

    public static void setUseAutolocation(boolean useAutolocation) {
        set(USE_AUTOLOCATION, useAutolocation);
    }

    public static boolean getUseAutolocation() {
        return get(USE_AUTOLOCATION, true);
    }

    public static ResolvedLocation getPublishLocation() {
        String json = get(PUBLISH_LOCATION, null);
        if (json != null) {
            return gson.fromJson(json, ResolvedLocation.class);
        }
        return null;
    }

    public static void setResolvedLocation(ResolvedLocation resolvedLocation) {
        set(RESOLVED_LOCATION, gson.toJson(resolvedLocation));
        LocationHelper.setCustomApptimizeLocationFilter(resolvedLocation);
    }

    public static ResolvedLocation getResolvedLocation() {
        String json = get(RESOLVED_LOCATION, null);
        if (json != null) {
            return gson.fromJson(json, ResolvedLocation.class);
        }
        return null;
    }

    public static void setAutolocationCity(City city) {
        set(AUTOLOCATION_CITY, gson.toJson(city));
    }

    public static City getAutolocationCity() {
        String json = get(AUTOLOCATION_CITY, null);
        if (json != null) {
            return gson.fromJson(json, City.class);
        }
        return null;
    }

    public static CategoriesCounter getCategoriesCounter() {
        String json = get(CATEGORIES_COUNTER, null);
        if (json != null) {
            return gson.fromJson(json, CategoriesCounter.class);
        }
        return null;
    }

    public static String getMostVisitedCategory() {
        CategoriesCounter counter = getCategoriesCounter();

        if (counter != null) {
            return counter.getMostVisitedCategory();
        }

        return null;
    }

    public static void setCategoriesCounter(CategoriesCounter categoriesCounter) {
        set(CATEGORIES_COUNTER, gson.toJson(categoriesCounter));
    }

    public static void setAroundMeLocation(ResolvedLocation resolvedLocation) {
        set(AROUND_ME_LOCATION, gson.toJson(resolvedLocation));
    }

    public static ResolvedLocation getAroundMeLocation() {
        String json = get(AROUND_ME_LOCATION, null);
        if (json != null) {
            return gson.fromJson(json, ResolvedLocation.class);
        }
        return null;
    }

    public static boolean isUrbanAirshipRegistered() {
        return prefs.getBoolean(URBAN_AIRSHIP_DATA_SENT, false);
    }

    public static void setUrbanAirshipDataSent(boolean sent) {
        set(URBAN_AIRSHIP_DATA_SENT, sent);
    }

    public static int get(String key, int _default) {
        return prefs.getInt(key, _default);
    }

    public static String get(String key, String _default) {
        return prefs.getString(key, _default);
    }

    public static float get(String key, float _default) {
        return prefs.getFloat(key, _default);
    }

    public static boolean get(String key, boolean _default) {
        return prefs.getBoolean(key, _default);
    }

    public static long get(String key, long _default) {
        return prefs.getLong(key, _default);
    }

    public static void set(String key, long value) {
        prefs.edit().putLong(key, value).commit();
    }

    public static void set(String key, int value) {
        prefs.edit().putInt(key, value).commit();
    }

    public static void set(String key, String value) {
        prefs.edit().putString(key, value).commit();
    }

    public static void set(String key, float value) {
        prefs.edit().putFloat(key, value).commit();
    }

    public static void set(String key, boolean value) {
        prefs.edit().putBoolean(key, value).commit();
    }

    public static void remove(String key) {
        prefs.edit().remove(key).commit();
    }

    public static void setUser(User user) {
        set(USER, gson.toJson(user));
    }

    public static User getUser() {
        String json = get(USER, null);
        if (json != null) {
            return gson.fromJson(json, User.class);
        }
        return null;
    }

    public static void setOEMActivated(boolean isActivated) {
        set(OEM_ACTIVATION, isActivated);
    }

    public static boolean isOEMActivated() {
        return get(OEM_ACTIVATION, false);
    }

    public static void setIsNewVersion(boolean isNewVersion) {
        set(IS_NEW_VERSION, isNewVersion);
    }

    public static boolean isNewVersion() {
        return get(IS_NEW_VERSION, false);
    }

    public static void setHasSeenSwapInstructions(boolean seen) {
        set(SWAP_INSTRUCTIONS_SEEN, seen);
    }

    public static boolean isSwapInstructionsSeen() {
        return get(SWAP_INSTRUCTIONS_SEEN, false);
    }
}
