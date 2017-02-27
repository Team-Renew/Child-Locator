package com.childlocator.firebase.base;

import android.support.v4.app.Fragment;
import android.view.View;

import com.childlocator.firebase.R;
import com.childlocator.firebase.data.utils.DrawerItemInfo;
import com.childlocator.firebase.ui.drawer.DrawerFragment;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;

import java.util.ArrayList;

public abstract class BaseDrawerActivity extends BaseActivity {
  public enum Menu {
    USER(1),
    USER_FAMILY(2),
    ZONE_ALERTS(3),
    SETTINGS(4),
    ABOUT(5);

    public final long id;

    Menu(int id) {
      this.id = id;
    }
  }

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

    items.add((DrawerItemInfo) new DrawerItemInfo()
            .withName("user")
            .withIdentifier(Menu.USER.id)
            .withIcon(R.drawable.ic_user));
    items.add((DrawerItemInfo) new DrawerItemInfo()
            .withName("user's children")
            .withIdentifier(Menu.USER_FAMILY.id)
            .withIcon(R.drawable.ic_children));
    items.add((DrawerItemInfo) new DrawerItemInfo()
            .withName("Zone Alerts")
            .withIdentifier(Menu.ZONE_ALERTS.id)
            .withIcon(R.drawable.ic_alert_zones));
    items.add((DrawerItemInfo) new DrawerItemInfo()
            .withName("Settings")
            .withIdentifier(Menu.SETTINGS.id)
            .withIcon(R.drawable.ic_settings));
    items.add((DrawerItemInfo) new DrawerItemInfo()
            .withName("About")
            .withIdentifier(Menu.ABOUT.id)
            .withIcon(R.drawable.ic_about));

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
