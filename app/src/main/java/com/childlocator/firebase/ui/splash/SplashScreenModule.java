package com.childlocator.firebase.ui.splash;

import com.childlocator.firebase.base.annotation.ActivityScope;
import com.childlocator.firebase.data.source.remote.UserService;

import dagger.Module;
import dagger.Provides;

@Module
public class SplashScreenModule {
  private SplashActivity activity;

  public SplashScreenModule(SplashActivity activity) {
    this.activity = activity;
  }

  @Provides
  @ActivityScope
  SplashActivity provideSplashActivity() {
    return activity;
  }

  @Provides
  @ActivityScope
  SplashScreenPresenter provideSplashScreenPresenter(UserService userService) {
    return new SplashScreenPresenter(activity, userService);
  }
}
