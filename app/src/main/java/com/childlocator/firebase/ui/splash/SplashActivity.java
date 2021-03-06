package com.childlocator.firebase.ui.splash;

import android.content.Intent;

import com.childlocator.firebase.App;
import com.childlocator.firebase.R;
import com.childlocator.firebase.base.BaseActivity;
import com.childlocator.firebase.data.model.User;
import com.childlocator.firebase.ui.children.ChildrenActivity;
import com.childlocator.firebase.ui.login.LoginActivity;

import javax.inject.Inject;

public class SplashActivity extends BaseActivity implements SplashScreenContract.View {

  @Inject
  SplashScreenPresenter presenter;

  @Override
  protected void setupActivityComponent() {
    setContentView(R.layout.activity_splash);

    App.get(this).getAppComponent()
            .plus(new SplashScreenModule(this))
            .inject(this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    presenter.subscribe();
  }

  @Override
  protected void onStop() {
    super.onStop();
    presenter.unsubscribe();
  }

  @Override
  public void showLoginActivity() {
    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
    startActivity(intent);
    finish();
  }

  @Override
  public void showChildrenActivity(User user) {
    Intent intent = new Intent(SplashActivity.this, ChildrenActivity.class);
    startActivity(intent);
    finish();
  }
}
