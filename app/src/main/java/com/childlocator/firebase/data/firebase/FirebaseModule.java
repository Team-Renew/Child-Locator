package com.childlocator.firebase.data.firebase;

import android.app.Application;

import com.childlocator.firebase.data.source.remote.FirebaseUserService;
import com.childlocator.firebase.data.source.remote.UserService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class FirebaseModule {
  @Provides
  @Singleton
  public FirebaseUserService provideFirebaseUserService(Application application) {
    return new FirebaseUserService(application);
  }

  @Provides
  @Singleton
  public UserService provideUserService(Application application) {
    return new UserService(application);
  }
}