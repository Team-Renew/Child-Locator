package com.childlocator.firebase.base;

import com.childlocator.firebase.data.firebase.FirebaseModule;
import com.childlocator.firebase.data.user.UserComponent;
import com.childlocator.firebase.data.user.UserModule;
import com.childlocator.firebase.ui.login.LoginScreenComponent;
import com.childlocator.firebase.ui.login.LoginScreenModule;
import com.childlocator.firebase.ui.splash.SplashScreenComponent;
import com.childlocator.firebase.ui.splash.SplashScreenModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, FirebaseModule.class})
public interface AppComponent {

  SplashScreenComponent plus(SplashScreenModule activityModule);

  LoginScreenComponent plus(LoginScreenModule activityModule);

  UserComponent plus(UserModule userModule);
}
