package com.lookeate.android.helpers;

import com.olx.grog.services.arguments.ItemsArguments;
import com.olx.grog.services.arguments.NeighborhoodsArguments;
import com.olx.olx.Constants;
import com.olx.olx.LeChuckApplication;
import com.olx.olx.model.ResolvedLocation;

public class PrefetchHelper {
    private static final String SEARCH_TERM = null;
    private static ResolvedLocation location;
    private static PrefetchHelper instance;
    private static String requestId;

    public static PrefetchHelper getInstance() {
        if (instance == null) {
            instance = new PrefetchHelper();
        }
        return instance;
    }

    public void prefetchEverythingAsync() {
        prefetchItemsAsync();
        prefetchNeighborhoodsAsync();
    }

    public void prefetchItemsAsync() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                location = PreferencesHelper.getResolvedLocation();
                CategoriesCounter subcategoriesCounter = PreferencesHelper.getCategoriesCounter();

                if (subcategoriesCounter != null && location != null) {
                    String mostVisitedSubcategory = subcategoriesCounter.getMostVisitedCategory();

                    if (mostVisitedSubcategory != null) {
                        ItemsArguments itemsPrefetchedArguments =
                                new ItemsArguments(location.getMostAccurateRegion(), SEARCH_TERM, String.valueOf(mostVisitedSubcategory));

                        requestId = String.format(Constants.ApiRequestIds.LISTING_REQUEST_ID, itemsPrefetchedArguments.getCacheKey());
                        itemsPrefetchedArguments.setRequestId(requestId);

                        LeChuckApplication.getApplication().makeServiceCallAsync(itemsPrefetchedArguments);
                    }
                }
            }
        }).run();
    }

    public void prefetchNeighborhoodsAsync() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                location = PreferencesHelper.getPublishLocation();
                if (location == null) {
                    location = PreferencesHelper.getResolvedLocation();
                }

                if (location != null && location.getCity() != null) {
                    NeighborhoodsArguments arguments = new NeighborhoodsArguments(location.getCity().getUrl());
                    arguments.setRequestId(String.format(Constants.ApiRequestIds.NEIGBOURHOOD_REQUEST_ID, location.getCity().getUrl()));
                    LeChuckApplication.getApplication().makeServiceCallAsync(arguments);
                }
            }
        }).run();
    }

}
