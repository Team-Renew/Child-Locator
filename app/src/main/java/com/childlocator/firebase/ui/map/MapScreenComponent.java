package com.childlocator.firebase.ui.map;

import com.childlocator.firebase.base.annotation.ActivityScope;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = {MapScreenModule.class})
public interface MapScreenComponent {
  MapActivity inject(MapActivity activity);
}
