package com.childlocator.firebase;

import android.app.Application;
import android.content.Context;

import com.childlocator.firebase.base.AppComponent;
import com.childlocator.firebase.base.AppModule;
import com.childlocator.firebase.base.DaggerAppComponent;
import com.childlocator.firebase.data.firebase.FirebaseModule;
import com.childlocator.firebase.data.model.User;
import com.childlocator.firebase.data.user.UserComponent;
import com.childlocator.firebase.data.user.UserModule;
import com.firebase.client.Firebase;

public class App extends Application {
  private AppComponent appComponent;
  private UserComponent userComponent;

  public static App get(Context context) {
    return (App) context.getApplicationContext();
  }

  @Override
  public void onCreate() {
    super.onCreate();

    initAppComponent();
  }

  private void initAppComponent() {
    appComponent = DaggerAppComponent.builder()
            .appModule(new AppModule(this))
            .firebaseModule(new FirebaseModule())
            .build();

    Firebase.setAndroidContext(this);
  }

  public AppComponent getAppComponent() {
    return appComponent;
  }

  public UserComponent createUserComponent(User user) {
    userComponent = appComponent.plus(new UserModule(user));
    return userComponent;
  }

  public UserComponent getUserComponent() {
    return userComponent;
  }

  public void releaseUserComponent() {
    userComponent = null;
  }
}
