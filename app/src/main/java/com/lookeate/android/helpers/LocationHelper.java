package com.lookeate.android.helpers;

import android.location.Location;

import com.olx.grog.helpers.ApptimizeHelper;
import com.olx.olx.model.ResolvedLocation;
import com.olx.smaug.api.model.Coordinates;

public class LocationHelper {

    private static final int INVALID_DISTANCE = -1;

    public static int calculateDistanceInKM(ResolvedLocation previousLocation, ResolvedLocation newLocation) {
        if (citiesHaveCoordinates(previousLocation, newLocation)) {

            Coordinates previousLocationCoordinates = previousLocation.getCity().getCoordinates();
            Coordinates locationCoordinates = newLocation.getCity().getCoordinates();
            float[] distanceInMeters = new float[5];

            Location.distanceBetween(previousLocationCoordinates.getLatitude(), previousLocationCoordinates.getLongitude(),
                    locationCoordinates.getLatitude(), locationCoordinates.getLongitude(), distanceInMeters);

            if (distanceInMeters[0] > 1) {
                return (int) (distanceInMeters[0] / 1000);
            } else {
                return INVALID_DISTANCE;
            }
        }
        return INVALID_DISTANCE;
    }

    public static boolean citiesHaveCoordinates(ResolvedLocation previousLocation, ResolvedLocation newLocation) {
        return previousLocation.getCity() != null && newLocation.getCity() != null && previousLocation.getCity().getCoordinates() != null &&
                newLocation.getCity().getCoordinates() != null;
    }

    public static com.olx.smaug.api.model.Location getLocationFromResolvedLocation(ResolvedLocation resolvedLocation) {
        return new com.olx.smaug.api.model.Location(resolvedLocation.getCountry(), resolvedLocation.getState(), resolvedLocation.getCity());
    }

    public static void setCustomApptimizeLocationFilter(ResolvedLocation resolvedLocation) {
        if (resolvedLocation != null) {
            ApptimizeHelper.setCustomFilter(ApptimizeHelper.OLX_COUNTRY, String.valueOf(resolvedLocation.getCountry().getId()));

            if (resolvedLocation.getCity() != null) {
                ApptimizeHelper.setCustomFilter(ApptimizeHelper.OLX_CITY, String.valueOf(resolvedLocation.getCity().getId()));
                ApptimizeHelper.setCustomFilter(ApptimizeHelper.OLX_STATE, String.valueOf(resolvedLocation.getCity().getStateId()));
            }
        }
    }

    public static void setResolvedLocation(ResolvedLocation resolvedLocation) {
        PreferencesHelper.setResolvedLocation(resolvedLocation);
        LocationHelper.setCustomApptimizeLocationFilter(resolvedLocation);
    }
}
