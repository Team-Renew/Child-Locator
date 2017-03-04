package com.childlocator.firebase.ui.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.childlocator.firebase.R;
import com.childlocator.firebase.base.BaseDrawerActivity;
import com.childlocator.firebase.data.Constants;
import com.childlocator.firebase.data.model.User;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class MapActivity extends BaseDrawerActivity implements OnMapReadyCallback, RoutingListener {

  private Firebase rootDbUrl;
  private GoogleMap mMap;
  private LatLng parentLocation;
  private LatLng childLocation;
  private User parent;
  private User child;
  private Firebase parentDbUrl;
  private Firebase childDbUrl;

  //private ProgressDialog progressDialog;
  private List<Polyline> polylines;
  private static final int[] COLORS = new int[]{
          R.color.primary_dark,
          R.color.primary,
          R.color.foreground_material_light,
          R.color.accent,
          R.color.primary_dark_material_light};

  @Bind(R.id.btnRouting)
  Button btnRouting;

  @Bind(R.id.btnCancelRouting)
  Button btnCancelRouting;

  private boolean routing = false;

  private static final String LOG_TAG = "MapActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_map);
    ButterKnife.bind(this);
    rootDbUrl = new Firebase(Constants.FIREBASE_CHILD_LOCATOR_DB_URL);

    polylines = new ArrayList<>();

    SupportMapFragment mapFragment =
            (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);
    String jsonReceiverUser = getIntent().getStringExtra(Constants.KEY_SEND_USER).split("---")[0];
    String jsonCurrentUser = getIntent().getStringExtra(Constants.KEY_SEND_USER).split("---")[1];
    Gson gson = new Gson();
    child = gson.fromJson(jsonReceiverUser, User.class);
    parent = gson.fromJson(jsonCurrentUser, User.class);
    parentLocation = new LatLng(parent.getLatitude(), parent.getLongitude());
    childLocation = new LatLng(child.getLatitude(), child.getLongitude());
  }

  @OnClick(R.id.btnRouting)
  public void setBtnRouting() {
    routing = true;
    routing(parentLocation, childLocation);
  }

  @OnClick(R.id.btnCancelRouting)
  public void setBtnCancelRouting() {
    routing = false;
    routing(parentLocation, childLocation);
  }

  // remove after exam demonstration
  private ValueEventListener valueEventListenerParent = new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
      try {
        User user = dataSnapshot.getValue(User.class);
        parentLocation = new LatLng(user.getLatitude(), user.getLongitude());
        routing(parentLocation, childLocation);
      } catch (Exception e) {
      }
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }
  };

  private ValueEventListener valueEventListenerChild = new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
      try {
        User user = dataSnapshot.getValue(User.class);
        childLocation = new LatLng(user.getLatitude(), user.getLongitude());
        routing(parentLocation, childLocation);
      } catch (Exception e) {
      }
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }
  };

  @Override
  protected void onDestroy() {
    super.onDestroy();
    try {
      EventBus.getDefault().unregister(this);
      parentDbUrl.removeEventListener(valueEventListenerParent);
      childDbUrl.removeEventListener(valueEventListenerChild);
    } catch (Exception e) {
    }
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      return;
    }

    mMap.setMyLocationEnabled(true);

    // remove after exam demonstration
    parentDbUrl = rootDbUrl
            .child(Constants.NODE_USERS)
            .child(parent.getUid());

    childDbUrl = rootDbUrl
            .child(Constants.NODE_USERS)
            .child(child.getUid());

    parentDbUrl.addValueEventListener(valueEventListenerParent);
    childDbUrl.addValueEventListener(valueEventListenerChild);

    EventBus.getDefault().register(this);
    Handler handler = new Handler();
    handler.postDelayed(() -> {
      LatLngBounds.Builder builder = new LatLngBounds.Builder();
      builder.include(this.parentLocation);
      builder.include(this.childLocation);
      LatLngBounds bounds = builder.build();
      try {
        mMap.animateCamera(CameraUpdateFactory
                .newLatLngBounds(bounds, 80));
      } catch (Exception e) {
      }
    }, 500);
  }


  public void onEvent(Location currentLocation) {
    LatLng lng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
    routing(lng, childLocation);
  }

  public void routing(LatLng a, LatLng b) {
//    progressDialog = ProgressDialog.show(this, "Please wait.",
//            "Fetching route information.", true);

    // https://github.com/jd-alexander/Google-Directions-Android
    Routing routing = new Routing.Builder()
            .travelMode(Routing.TravelMode.DRIVING) // .WALKING)
            .withListener(MapActivity.this)         /* Listener that delivers routing results. */
            .waypoints(a, b)
            .build();
    routing.execute();
  }

  @Override
  public void onRoutingStart() {
    // The Routing Request starts
  }

  @Override
  public void onRoutingCancelled() {
    Log.i(LOG_TAG, "Routing was cancelled.");
  }

  @Override
  public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
    mMap.clear();
    //progressDialog.dismiss();
    CameraUpdate center = CameraUpdateFactory.newLatLng(parentLocation);
    CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

    mMap.moveCamera(center);
    mMap.animateCamera(zoom);

    if (polylines.size() > 0) {
      for (Polyline poly : polylines) {
        poly.remove();
      }
    }

    polylines = new ArrayList<>();

    //add route(s) to the map.
    for (int i = 0; i < route.size(); i++) {

      //In case of more than 5 alternative routes
      int colorIndex = i % COLORS.length;

      PolylineOptions polyOptions = new PolylineOptions();
      polyOptions.color(getResources().getColor(COLORS[colorIndex]));
      polyOptions.width(10 + i * 3);
      polyOptions.addAll(route.get(i).getPoints());
      if (routing) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(parentLocation);
        builder.include(childLocation);
        LatLngBounds bounds = builder.build();

        mMap.animateCamera(CameraUpdateFactory
                .newLatLngBounds(bounds, 80));

        Polyline polyline = mMap.addPolyline(polyOptions);
        polylines.add(polyline);
      }

      Toast.makeText(
              getApplicationContext(),
              "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + " m", // : duration - " + route.get(i).getDurationValue(),
              Toast.LENGTH_LONG)
              .show();
    }

    // Start marker
    MarkerOptions options = new MarkerOptions();
    options.position(parentLocation);
    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
    Marker markerParent = mMap.addMarker(options);
    markerParent.showInfoWindow();

    // End marker
    options = new MarkerOptions();
    options.position(childLocation);
    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
    Marker markerChild = mMap.addMarker(options);
    markerChild.showInfoWindow();
}

  @Override
  public void onRoutingFailure(RouteException e) {
    // The Routing request failed
    //progressDialog.dismiss();
    if (e != null) {
      Toast.makeText(this, "Error: No valid driving route!", Toast.LENGTH_LONG).show();
    } else {
      Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
    }
  }
}
