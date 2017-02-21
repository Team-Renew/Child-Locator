package com.childlocator.firebase.data.user;

import com.childlocator.firebase.base.annotation.UserScope;
import com.childlocator.firebase.ui.map.MapScreenComponent;
import com.childlocator.firebase.ui.map.MapScreenModule;

import dagger.Subcomponent;

@UserScope
@Subcomponent(modules = {UserModule.class})
public interface UserComponent {
  MapScreenComponent plus(MapScreenModule activityModule);
}
