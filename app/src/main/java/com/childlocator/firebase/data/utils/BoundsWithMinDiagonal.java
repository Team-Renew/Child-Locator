package com.childlocator.firebase.data.utils;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class BoundsWithMinDiagonal {
  public static final LatLngBounds createBoundsWithMinDiagonal(
          MarkerOptions firstMarker, MarkerOptions secondMarker) {
    LatLngBounds.Builder builder = new LatLngBounds.Builder();
    builder.include(firstMarker.getPosition());
    builder.include(secondMarker.getPosition());

    LatLngBounds tmpBounds = builder.build();
    /** Add 2 points 1000m northEast and southWest of the center.
     * They increase the bounds only, if they are not already larger
     * than this.
     * 1000m on the diagonal translates into about 709m to each direction. */
    LatLng center = tmpBounds.getCenter();
    LatLng northEast = move(center, 709, 709);
    LatLng southWest = move(center, -709, -709);
    builder.include(southWest);
    builder.include(northEast);
    return builder.build();
  }

  public static final LatLngBounds createBoundsWithMinDiagonal(
          LatLng firstPosition, LatLng secondPosition) {
    LatLngBounds.Builder builder = new LatLngBounds.Builder();
    builder.include(firstPosition);
    builder.include(secondPosition);

    LatLngBounds tmpBounds = builder.build();
    /** Add 2 points 1000m northEast and southWest of the center.
     * They increase the bounds only, if they are not already larger
     * than this.
     * 1000m on the diagonal translates into about 709m to each direction. */
    LatLng center = tmpBounds.getCenter();
    LatLng northEast = move(center, 709, 709);
    LatLng southWest = move(center, -709, -709);
    builder.include(southWest);
    builder.include(northEast);
    return builder.build();
  }

  private static final double EARTHRADIUS = 6366198;
  /**
   * Create a new LatLng which lies toNorth meters north and toEast meters
   * east of startLL
   */
  private static LatLng move(LatLng startLL, double toNorth, double toEast) {
    double lonDiff = meterToLongitude(toEast, startLL.latitude);
    double latDiff = meterToLatitude(toNorth);
    return new LatLng(startLL.latitude + latDiff, startLL.longitude
            + lonDiff);
  }

  private static double meterToLongitude(double meterToEast, double latitude) {
    double latArc = Math.toRadians(latitude);
    double radius = Math.cos(latArc) * EARTHRADIUS;
    double rad = meterToEast / radius;
    return Math.toDegrees(rad);
  }


  private static double meterToLatitude(double meterToNorth) {
    double rad = meterToNorth / EARTHRADIUS;
    return Math.toDegrees(rad);
  }
}
