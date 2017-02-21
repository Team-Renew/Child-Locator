package com.childlocator.firebase.ui.login;

import com.childlocator.firebase.base.annotation.ActivityScope;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = {LoginScreenModule.class})
public interface LoginScreenComponent {
  LoginActivity inject(LoginActivity activity);
}
