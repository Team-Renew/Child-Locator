package com.childlocator.firebase.ui.splash;

import com.childlocator.firebase.base.annotation.ActivityScope;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = {SplashScreenModule.class})
public interface SplashScreenComponent {
  SplashActivity inject(SplashActivity activity);
}
