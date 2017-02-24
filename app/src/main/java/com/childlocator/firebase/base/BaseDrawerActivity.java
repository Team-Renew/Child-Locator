package com.childlocator.firebase.base;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.childlocator.firebase.R;
import com.childlocator.firebase.data.utils.DrawerItemInfo;
import com.childlocator.firebase.ui.drawer.DrawerFragment;

import java.util.ArrayList;

public abstract class BaseDrawerActivity extends BaseActivity {

  @Override
  protected abstract void setupActivityComponent();

  @Override
  protected void onStart() {
    super.onStart();
    this.setupDrawer();
  }

  protected void setupDrawer() {
    View drawerContainer = this.findViewById(R.id.container_drawer);
    if (drawerContainer == null) {
      throw new UnsupportedOperationException("The activity must have an element with id \"container_drawer\"");
    }

    ArrayList<DrawerItemInfo> items = new ArrayList<>();

    items.add(new DrawerItemInfo(1, "Books"));
    items.add(new DrawerItemInfo(2, "Tabs"));
    items.add(new DrawerItemInfo(3, "Another"));
    items.add(new DrawerItemInfo(4, "Another 2"));

    Fragment drawerFragment =
            DrawerFragment.createFragment(items, (view, position, drawerItem) -> {
              switch ((int) drawerItem.getIdentifier()) {
                case 2:
//                  Intent intent = new Intent(this, TabsNavigationActivity.class);
//                  this.startActivity(intent);
                  break;
              }

              return true;
            });

    this.getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.container_drawer, drawerFragment)
            .commit();
  }
}
