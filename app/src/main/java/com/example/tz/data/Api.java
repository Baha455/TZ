package com.example.tz.data;

import com.example.tz.domain.ApksModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface Api {

    @GET("software/latest/all")
    Call<List<ApksModel>> getApks();

    @GET("software/latest/{type}")
    Call<ApksModel> getLatestVersion(@Path("type") String type);
}
