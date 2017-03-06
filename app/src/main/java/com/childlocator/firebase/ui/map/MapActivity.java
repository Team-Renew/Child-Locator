package com.childlocator.firebase.ui.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.childlocator.firebase.R;
import com.childlocator.firebase.base.BaseDrawerActivity;
import com.childlocator.firebase.data.Constants;
import com.childlocator.firebase.data.model.User;
import com.childlocator.firebase.data.utils.BoundsWithMinDiagonal;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class MapActivity extends BaseDrawerActivity implements OnMapReadyCallback, RoutingListener {

  private DatabaseReference rootDbUrl;
  private GoogleMap mMap;
  private LatLng parentLocation;
  private LatLng childLocation;
  private User parent;
  private User child;
  private DatabaseReference parentDbUrl;
  private DatabaseReference childDbUrl;

  private Bitmap parentBitmap;
  private Bitmap childBitmap;

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

  public Bitmap base64stringToBitMap(String b64encodedString) {
    try {
      byte[] encodedBytes = Base64.decode(b64encodedString, Base64.DEFAULT);
      Bitmap bitmap = BitmapFactory.decodeByteArray(encodedBytes, 0, encodedBytes.length);
      return bitmap;
    } catch (Exception e) {
      e.getMessage();
      return null;
    }
  }

  public String bitmapToCompressedBase64string(Bitmap bitmap) {
    ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bYtE);
    bitmap.recycle();
    byte[] byteArray = bYtE.toByteArray();
    return Base64.encodeToString(byteArray, Base64.DEFAULT);
  }

  private Bitmap getResizedBitmap(Bitmap imageBitmap, int width, int height) {
    Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
    return resizedBitmap;
  }

  private Bitmap getMarkerBitmapFromView(Bitmap bitmap) {

    View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
            .inflate(R.layout.view_custom_marker, null);

    ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);
    markerImageView.setImageBitmap(bitmap);
    markerImageView.setCropToPadding(false);

    customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
    customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
    customMarkerView.buildDrawingCache();
    Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
            Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(returnedBitmap);
    canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
    Drawable drawable = customMarkerView.getBackground();
    if (drawable != null)
      drawable.draw(canvas);
    customMarkerView.draw(canvas);
    return returnedBitmap;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_map);
    ButterKnife.bind(this);
    rootDbUrl = FirebaseDatabase.getInstance().getReference();

    polylines = new ArrayList<>();

    SupportMapFragment mapFragment =
            (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);
    String jsonReceiverUser = getIntent().getStringExtra(Constants.KEY_SEND_USER).split("---")[1]; // temporarily swap
    String jsonCurrentUser = getIntent().getStringExtra(Constants.KEY_SEND_USER).split("---")[0];
    Gson gson = new Gson();
    child = gson.fromJson(jsonReceiverUser, User.class);
    parent = gson.fromJson(jsonCurrentUser, User.class);
    parentLocation = new LatLng(parent.getLatitude(), parent.getLongitude());
    childLocation = new LatLng(child.getLatitude(), child.getLongitude());

    parentBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_user);
    parentBitmap = getResizedBitmap(parentBitmap, 100, 100);

    DatabaseReference parentDbUrl = rootDbUrl
            .child(Constants.NODE_USERS)
            .child(parent.getUid());

    parentDbUrl.addListenerForSingleValueEvent(
            new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (user == null || user.getEmail() == null) {
                  parentBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_user);
                  parentBitmap = getResizedBitmap(parentBitmap, 100, 100);
                } else {
                  byte[] decodedString = Base64.decode(user.getPhoto_url(), Base64.DEFAULT);
                  Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                  parentBitmap = getResizedBitmap(bitmap, 100, 100);

                  parentBitmap = getMarkerBitmapFromView(parentBitmap);
                }
              }

              @Override
              public void onCancelled(DatabaseError databaseError) {
                parentBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_user);
                parentBitmap = getResizedBitmap(parentBitmap, 100, 100);

                Toast.makeText(
                        MapActivity.this,
                        "parentDbUrl: failed listening",
                        Toast.LENGTH_LONG)
                        .show();
              }
            }
    );

    childBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_user);
    childBitmap = getResizedBitmap(childBitmap, 100, 100);

    DatabaseReference childDbUrl = rootDbUrl
            .child(Constants.NODE_USERS)
            .child(child.getUid());

    childDbUrl.addListenerForSingleValueEvent(
            new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (user == null || user.getEmail() == null) {
                  childBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_user);
                  childBitmap = getResizedBitmap(childBitmap, 100, 100);
                } else {
                  byte[] decodedString = Base64.decode(user.getPhoto_url(), Base64.DEFAULT);
                  Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                  childBitmap = getResizedBitmap(bitmap, 100, 100);

                  childBitmap = getMarkerBitmapFromView(childBitmap);
                }
              }

              @Override
              public void onCancelled(DatabaseError databaseError) {
                childBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_user);
                childBitmap = getResizedBitmap(childBitmap, 100, 100);

                Toast.makeText(
                        MapActivity.this,
                        "childDbUrl: failed listening",
                        Toast.LENGTH_LONG)
                        .show();
              }
            }
    );
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
    public void onCancelled(DatabaseError databaseError) {
      Toast.makeText(
              MapActivity.this,
              "parentDbUrl: failed listening",
              Toast.LENGTH_LONG)
              .show();
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
    public void onCancelled(DatabaseError databaseError) {
      Toast.makeText(
              MapActivity.this,
              "childDbUrl: failed listening",
              Toast.LENGTH_LONG)
              .show();
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
      LatLngBounds bounds = BoundsWithMinDiagonal.createBoundsWithMinDiagonal(
              parentLocation,
              childLocation
      );
      try {
        mMap.animateCamera(CameraUpdateFactory
                .newLatLngBounds(bounds, 100));
      } catch (Exception e) {
      }
    }, 500);
  }


  public void onEvent(Location currentLocation) {
    LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
    routing(latLng, childLocation);
  }

  public void routing(LatLng a, LatLng b) {
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
    CameraUpdate center = CameraUpdateFactory.newLatLng(parentLocation);
    CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

    mMap.moveCamera(center);
    mMap.animateCamera(zoom, 1000, null);

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
        LatLngBounds bounds = BoundsWithMinDiagonal.createBoundsWithMinDiagonal(
                parentLocation,
                childLocation
        );

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

        Polyline polyline = mMap.addPolyline(polyOptions);
        polylines.add(polyline);
      }

      Toast.makeText(
              getApplicationContext(),
              "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + " m", // : duration - " + route.get(i).getDurationValue(),
              Toast.LENGTH_LONG)
              .show();
    }

    // Parent (start) marker
    MarkerOptions options = new MarkerOptions();
    options.position(parentLocation);
    options.icon(BitmapDescriptorFactory.fromBitmap(parentBitmap));
    Marker markerParent = mMap.addMarker(options);
    markerParent.showInfoWindow();

    // Child (end) marker
    options = new MarkerOptions();
    options.position(childLocation);
    options.icon(BitmapDescriptorFactory.fromBitmap(childBitmap));
    Marker markerChild = mMap.addMarker(options);
    markerChild.showInfoWindow();
  }

  @Override
  public void onRoutingFailure(RouteException e) {
    // The Routing request failed
    if (e != null) {
      Toast.makeText(this, "Error: No valid driving route!", Toast.LENGTH_LONG).show();
    } else {
      Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
    }
  }
}
