package com.childlocator.firebase.data.user;

import com.childlocator.firebase.base.annotation.UserScope;

import dagger.Subcomponent;

@UserScope
@Subcomponent(modules = {UserModule.class})
public interface UserComponent {
}
