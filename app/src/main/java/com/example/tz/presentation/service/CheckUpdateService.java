package com.example.tz.presentation.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.tz.R;
import com.example.tz.data.Repository;
import com.example.tz.domain.ApksModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import kotlin.reflect.KVisibility;

@AndroidEntryPoint
public class CheckUpdateService extends Service {

    @Inject
    Repository repo;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MutableLiveData<ApksModel> liveData = new MutableLiveData<>();

        ArrayList<ApksModel> list = new ArrayList<>();
        list.clear();
        list.addAll(repo.getApkForService());
        for (ApksModel apk : list) {
            repo.getLatest(liveData, apk.getType());
        }

        Observer<ApksModel> observer = new Observer<ApksModel>() {
            @Override
            public void onChanged(ApksModel apksModels) {
                Log.d("TAG", "Есть обнова ");
                createNotification(apksModels);
            }
        };

        liveData.observeForever(observer);
        logs();
        return Service.START_STICKY;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void logs() {
        Log.d("service", "lods:checkUpdate ");
    }

    public void createNotification(ApksModel apk) {

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "channelID")
                .setContentTitle("Поиск обновлений")
                .setSmallIcon(R.drawable.logo50)
                .setContentText(apk.getTitle())
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int notificationId = 1;
        createChannel(notificationManager);
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    public void createChannel(NotificationManager notificationManager) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        NotificationChannel channel = new NotificationChannel("channelID", "name",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Hello! This is a notification.");
        notificationManager.createNotificationChannel(channel);

    }
}
