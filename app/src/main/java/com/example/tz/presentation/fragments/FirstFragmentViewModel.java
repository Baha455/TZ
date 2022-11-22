package com.example.tz.presentation.fragments;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tz.data.Repository;
import com.example.tz.domain.ApksModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class FirstFragmentViewModel extends ViewModel {

    public MutableLiveData<List<ApksModel>> liveData;

    @Inject
    public FirstFragmentViewModel() {
        liveData = new MutableLiveData<List<ApksModel>>();
    }

    public MutableLiveData<List<ApksModel>> getLiveData() {
        return liveData;
    }

    @Inject
    Repository repo;

    void request(){
        repo.getApks(liveData);
    }

    void getInstalled(ApksModel apk){
        repo.getInstalledApk(apk);
    }
}
