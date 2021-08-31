package com.davidjo.greenworld.di;

import android.app.Application;

import androidx.room.Room;

import com.davidjo.greenworld.api.PixabayApi;
import com.davidjo.greenworld.data.category.CategoryDao;
import com.davidjo.greenworld.data.category.CategoryDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    // 의존성 주입을 위한 메소드들

    @Provides
    @Singleton
    public FirebaseFirestore provideFirestore() {
        return FirebaseFirestore.getInstance();
    }

    @Provides
    @Singleton
    public FirebaseAuth provideAuth() {
        return FirebaseAuth.getInstance();
    }

    @Provides
    @Singleton
    public CategoryDatabase provideCategoryDatabase(Application app, CategoryDatabase.Callback callback) {
        return Room.databaseBuilder(app, CategoryDatabase.class, "category_database")
                .fallbackToDestructiveMigration()
                .addCallback(callback)
                .build();
    }

    @Provides
    public CategoryDao provideCategoryDao(CategoryDatabase db) {
        return db.categoryDao();
    }

    @Provides
    @Singleton
    public Retrofit provideRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(PixabayApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    public PixabayApi providePixabayApi(Retrofit retrofit) {
        return retrofit.create(PixabayApi.class);
    }

}
