package com.childlocator.firebase.ui.map;

import com.childlocator.firebase.base.BasePresenter;
import com.childlocator.firebase.data.model.User;
import com.childlocator.firebase.data.source.remote.FirebaseUserService;
import com.childlocator.firebase.data.source.remote.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MapScreenPresenter implements BasePresenter {
  private MapActivity activity;
  private User user;
  private FirebaseUserService firebaseUserService;
  private UserService userService;
  private FirebaseAuth firebaseAuth;
  private DatabaseReference databaseRef;

  public MapScreenPresenter(MapActivity activity,
                            User user,
                            FirebaseUserService firebaseUserService,
                            UserService userService) {
    this.activity = activity;
    this.user = user;
    this.firebaseUserService = firebaseUserService;
    this.userService = userService;
    this.firebaseAuth = FirebaseAuth.getInstance();
    this.databaseRef = FirebaseDatabase.getInstance().getReference();
  }

  @Override
  public void subscribe() {
    if (user != null) {
      activity.sendMessageToBreakPreviousScreen();
    }
  }

  @Override
  public void unsubscribe() {

  }

  public void logout() {
    firebaseUserService.logOut(user.getProvider());
  }
}
