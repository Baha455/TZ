package com.example.tz.presentation.fragments;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tz.App;
import com.example.tz.BuildConfig;
import com.example.tz.databinding.FragmentSecondBinding;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import com.example.tz.domain.ApksModel;
import com.example.tz.presentation.service.CheckUpdateService;
import com.squareup.picasso.Picasso;

import java.io.File;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class SecondFragment extends Fragment {

    private com.example.tz.databinding.FragmentSecondBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ApksModel args = (ApksModel) getArguments().getSerializable("data");
        setupData(args);
        service();
    }

    private void download(ApksModel apk) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apk.getLink()));
        request.setTitle(apk.getTitle()+".apk");
        request.setDescription("Идёт скачивание");
        request.setMimeType("application/vnd.android.package-archive");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, apk.getType()+".apk");

        DownloadManager manager = (DownloadManager)requireContext().getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    private void setupData(ApksModel args){
        Picasso.get().load(args.getLogo50Link()).into(binding.ivLogo);
        binding.tvTitle.setText(args.getTitle());
        binding.tvDescription.setText(args.getDescription());
        switch (args.getStatus()){
            case canInstalled: canInstalled(args);
            break;
            case haveUpdated: haveUpdate(args);
            break;
            case installed: installed(args);
            break;
            case downloaded: downloaded(args);
            break;
        }
    }

    private void haveUpdate(ApksModel args){
        binding.btnDownload.setText("Обновить");
        binding.btnDownload.setOnClickListener(view -> {
            download(args);
        });
    }

    private void canInstalled(ApksModel args){
        binding.btnDownload.setText("Скачать");
        binding.btnDownload.setOnClickListener(view -> {
            download(args);
        });
    }

    private void installed(ApksModel args){
        binding.btnDownload.setText("Открыть");
        binding.btnDelete.setVisibility(View.VISIBLE);
        binding.btnDownload.setOnClickListener(view -> {
            openApp(args);
        });
    }

    private void downloaded(ApksModel args){
        binding.btnDownload.setText("Установить");
        binding.btnDownload.setOnClickListener(view -> {
            installApk(args);
        });
    }

    private void openApp(ApksModel args) {
        Intent launchIntent = requireActivity().getPackageManager().getLaunchIntentForPackage(args.getType());
        startActivity(launchIntent);
    }

    private void installApk(ApksModel args){
        File toInstall = new File("/storage/sdcard/Download/", args.getType() + ".apk");
        Uri apkUri = Uri.fromFile(toInstall);
        Intent intent= new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void service(){
        binding.btnService.setOnClickListener(view -> {
            Intent service = new Intent(requireContext(), CheckUpdateService.class);
            requireContext().startService(service);
        });
    }


}
