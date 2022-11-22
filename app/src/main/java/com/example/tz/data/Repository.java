package com.example.tz.data;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.tz.domain.ApksModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {

    Api api;
    ArrayList<ApksModel> list;
    public Repository(Api api){
        this.api = api;
        this.list = new ArrayList<>();
    }

    public void getApks(MutableLiveData<List<ApksModel>> liveData){
        api.getApks().enqueue(new Callback<List<ApksModel>>() {
            @Override
            public void onResponse(Call<List<ApksModel>> call, Response<List<ApksModel>> response) {
                liveData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<List<ApksModel>> call, Throwable t) {

            }
        });
    }

    public void getLatest(MutableLiveData<ApksModel> liveData, String type){
        api.getLatestVersion(type).enqueue(new Callback<ApksModel>() {
            @Override
            public void onResponse(Call<ApksModel> call, Response<ApksModel> response) {
                liveData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<ApksModel> call, Throwable t) {

            }
        });
    }

    public void getInstalledApk(ApksModel apk){
        list.add(apk);
    }

    public List<ApksModel> getApkForService(){
        return list;
    }

}
