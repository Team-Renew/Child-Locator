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
import com.google.firebase.database.FirebaseDatabase;
//import com.firebase.client.Firebase;
//import com.google.firebase.database.DatabaseReference;

public class App extends Application {
  private AppComponent appComponent;
  private UserComponent userComponent;

  public static App get(Context context) {
    return (App) context.getApplicationContext();
  }

  @Override
  public void onCreate() {
    super.onCreate();

//    // initialize Firebase library once with an Android Context
//    Firebase.setAndroidContext(this);

    /*
     * Firebase apps automatically handle temporary network interruptions. Cached data is
     * available while offline and Firebase resends any writes when network connectivity is restored.
     *
     * When you enable disk persistence, your app writes the data locally to the device so your
     * app can maintain state while offline, even if the user or operating system restarts the app.
     *
     * You can enable disk persistence with just one line of code.
     */
    FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    initAppComponent();
  }

  private void initAppComponent() {
    appComponent = DaggerAppComponent.builder()
            .appModule(new AppModule(this))
            .firebaseModule(new FirebaseModule())
            .build();
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
