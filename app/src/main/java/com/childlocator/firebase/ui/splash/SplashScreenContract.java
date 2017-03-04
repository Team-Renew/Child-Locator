package com.childlocator.firebase.ui.splash;

import com.childlocator.firebase.data.model.User;

public interface SplashScreenContract {
  interface View {
    void showLoginActivity();

    void showChildrenActivity(User user);
  }

  interface Presenter {

  }
}
