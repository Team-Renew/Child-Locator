package com.childlocator.firebase.data.user;

import com.childlocator.firebase.base.annotation.UserScope;
import com.childlocator.firebase.data.model.User;

import dagger.Module;
import dagger.Provides;

@Module
public class UserModule {
  User user;

  public UserModule(User user) {
    this.user = user;
  }

  @Provides
  @UserScope
  User provideUser() {
    return this.user;
  }
}
