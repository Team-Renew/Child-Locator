package com.childlocator.firebase.data;

public class Constants {
  public static final String FIREBASE_CHILD_LOCATOR_DB_URL = "https://child-locator-9233a.firebaseio.com/";
  public static final String NODE_USERS = "cl_users";
  public static final String NODE_CONNECTION = "connection";
  public static final String NODE_LATITUDE = "latitude";
  public static final String NODE_LONGITUDE = "longitude";
  public static final String KEY_SEND_USER = "key_send_user";
  public static final String KEY_ONLINE = "online";
  public static final String KEY_OFFLINE = "offline";
  public static final String KEY_EMAIL = "email";
  public static final String KEY_CLOSE = "key_close";

  /**
   * Suppress default constructor for non-instantiability
   */
  private Constants() {
    throw new AssertionError();
  }
}
