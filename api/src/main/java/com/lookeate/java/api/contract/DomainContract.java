package com.lookeate.java.api.contract;

import java.util.ArrayList;

import retrofit.http.GET;
import retrofit.http.Path;

public interface DomainContract {

    @GET("/countries")
    ArrayList<?> countries();

    @GET("/countries/{url}/categories")
    ArrayList<?> categories(@Path("url") String countryUrl);

}
