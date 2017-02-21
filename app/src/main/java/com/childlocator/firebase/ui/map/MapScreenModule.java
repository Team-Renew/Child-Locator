package com.childlocator.firebase.ui.map;

import android.support.v7.app.AlertDialog;

import com.childlocator.firebase.base.annotation.ActivityScope;
import com.childlocator.firebase.data.model.User;
import com.childlocator.firebase.data.source.remote.FirebaseUserService;
import com.childlocator.firebase.data.source.remote.UserService;

import dagger.Module;
import dagger.Provides;

@Module
public class MapScreenModule {
  private MapActivity activity;

  public MapScreenModule(MapActivity activity) {
    this.activity = activity;
  }

  @ActivityScope
  @Provides
  MapActivity provideMapActivity() {
    return activity;
  }

  @ActivityScope
  @Provides
  MapScreenPresenter provideMapScreenPresenter(User user,
                                               FirebaseUserService firebaseUserService,
                                               UserService userService) {
    return new MapScreenPresenter(activity, user, firebaseUserService, userService);
  }

  @Provides
  @ActivityScope
  AlertDialog.Builder provideAlertDialogBuilder() {
    return new AlertDialog.Builder(activity);
  }
}
