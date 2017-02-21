package com.childlocator.firebase.ui.map;

import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Button;

import com.childlocator.firebase.App;
//import android.R;
import com.childlocator.firebase.R;
import com.childlocator.firebase.base.BaseActivity;
import com.childlocator.firebase.data.model.User;
import com.childlocator.firebase.ui.login.LoginActivity;
import com.childlocator.firebase.ui.splash.SplashActivity;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapActivity extends BaseActivity {
  public static final int REQUEST_COMPLETED = 1003;

  @Bind(R.id.btnLogout)
  Button btnLogout;

  @Inject
  User user;
  @Inject
  MapScreenPresenter presenter;
  @Inject
  AlertDialog.Builder addAlertDialog;

  public static void startWithUser(final BaseActivity activity, final User user) {
    Intent intent = new Intent(activity, MapActivity.class);
    intent.putExtra("finisher", new ResultReceiver(null) {
      @Override
      protected void onReceiveResult(int resultCode, Bundle resultData) {
        activity.finish();
      }
    });
    App.get(activity).createUserComponent(user);
    activity.startActivityForResult(intent, REQUEST_COMPLETED);
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_map);
    ButterKnife.bind(this);
    Log.d("childlocator", user.getUsername() + " " + user.getEmail() + " " + user.getName());
  }

  @Override
  protected void onResume() {
    super.onResume();
    presenter.subscribe();
  }

  @Override
  protected void onPause() {
    super.onPause();
    //mainAdapter.clearList();
  }

  @Override
  protected void onStop() {
    super.onStop();
    presenter.unsubscribe();
  }

  public void sendMessageToBreakPreviousScreen() {
    ((ResultReceiver) getIntent().getParcelableExtra("finisher"))
            .send(MapActivity.REQUEST_COMPLETED, new Bundle());
  }

  @Override
  protected void setupActivityComponent() {
    App.get(this)
            .getUserComponent()
            .plus(new MapScreenModule(this))
            .inject(this);
  }

  @OnClick(R.id.btnLogout)
  public void onClickLogout() {
    presenter.logout();
    Intent intent = new Intent(this, LoginActivity.class);
    App.get(this).releaseUserComponent();
    startActivity(intent);
    finish();
  }
}
