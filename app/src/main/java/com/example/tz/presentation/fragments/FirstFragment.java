package com.example.tz.presentation.fragments;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.tz.R;
import com.example.tz.databinding.FragmentFirstBinding;
import com.example.tz.domain.ApksModel;
import com.example.tz.domain.Status;
import com.example.tz.presentation.adapters.RvAdapter;
import com.example.tz.presentation.service.CheckUpdateService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FirstFragment extends Fragment implements RvAdapter.ItemClick {

    private FragmentFirstBinding binding;

    private RvAdapter rvAdapter;
    private NavController navController;

    FirstFragmentViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initRv();
        makRequest();
        navController = Navigation.findNavController(view);
    }

    private void initRv() {
        rvAdapter = new RvAdapter(this);
        binding.rvApks.setAdapter(rvAdapter);
    }

    private void makRequest() {
        viewModel = new ViewModelProvider(requireActivity()).get(FirstFragmentViewModel.class);
        viewModel.request();

        viewModel.getLiveData().observe(getViewLifecycleOwner(), new Observer<List<ApksModel>>() {
            @Override
            public void onChanged(List<ApksModel> apksModels) {
                //Добавил еще один объект т.к апк с ссылок не устанавливаются и нужен апк для
                //проверки статуса "Установлен" и "Есть обновление"
                //Для проверка открытия приложения можно открыть приложение Лантер, идёт 6м по списку
                //Перед этим надо установить hits, репо на него я тоже кинул
                //Для проверки статуса "Есть обновление" есть последний элемент
                ApksModel lanter = new ApksModel(
                        "http://update.paymob.ru:9996/assets/software/ru.avangard.metalist/1.24-avangard_cashback.sign.apk",
                        "1.0",
                        "org.lanter.hits",
                        "http://update.paymob.ru:9996/assets/software/ru.m4bank.bog/logo50.png",
                        null,
                        "Lanter",
                        "Not gay");
                apksModels.add(lanter);
                ArrayList<ApksModel> list = new ArrayList<>();
                for (ApksModel apk : apksModels) {

                    list.addAll(checkStatus(apk));
                }
                rvAdapter.setList(list);

            }
        });
    }

    private boolean appInstalledOrNot(String packageName) {
        PackageManager pm = requireActivity().getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    private boolean getPackageName(String packageName) {
        final PackageManager pm = requireActivity().getPackageManager();
        String fullPath = "/storage/sdcard/Download/" + packageName + ".apk";
        PackageInfo info = pm.getPackageArchiveInfo(fullPath, 0);
        return info != null;
    }

    private Boolean getPackageVersionTwo(ApksModel apk) {
        PackageManager pm = requireActivity().getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(apk.getType(), PackageManager.GET_ACTIVITIES);
            if (!Objects.equals(info.versionName, apk.getVersion())) {
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return false;
    }

    private ArrayList<ApksModel> checkStatus(ApksModel apk){
        ArrayList<ApksModel> list = new ArrayList<ApksModel>();
        if (getPackageVersionTwo(apk)) {
            apk.setStatus(Status.haveUpdated);
            list.add(apk);
        } else if (appInstalledOrNot(apk.getType())) {
            apk.setStatus(Status.installed);
            list.add(apk);
            viewModel.getInstalled(apk);
        } else if (getPackageName(apk.getType())) {
            apk.setStatus(Status.downloaded);
            list.add(apk);
        } else {
            apk.setStatus(Status.canInstalled);
            list.add(apk);
        }
        service();
        return list;
    }

    @Override
    public void onItemCLick(ApksModel apk) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", apk);
        navController.navigate(R.id.action_firstFragment_to_secondFragment2, bundle);
    }

    private void service(){
        Intent service = new Intent(requireContext(), CheckUpdateService.class);
        requireContext().startService(service);
    }
}
