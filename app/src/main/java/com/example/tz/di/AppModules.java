package com.example.tz.di;

import com.example.tz.data.Api;
import com.example.tz.data.Repository;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class AppModules {

    @Provides
    @Singleton
    public Api getApi(){
        return new Retrofit.Builder()
                .baseUrl("http://update.paymob.ru:9996/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(http())
                .build().create(Api.class);
    }

    public HttpLoggingInterceptor provideInterceptor(){
        return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    public OkHttpClient http(){
       return new OkHttpClient.Builder()
               .addInterceptor(provideInterceptor())
               .build();
    }

    @Provides
    @Singleton
    public Repository provideRepo(){
        return new Repository(getApi());
    }

}
