package com.childlocator.firebase.data.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class User {
//  private String id;
//  private String name;
//  private String email;


  @NonNull
  String uid;
  @Nullable
  String parentUid;
  @Nullable
  String username;
  @Nullable
  String email;
  @VisibleForTesting
  @Nullable
  String password;
  @Nullable
  String provider;
  @Nullable
  String photo_url;
  @Nullable
  String name;

  private String connection;
  private String createdAt;
  private double latitude;
  private double longitude;

  @Nullable
  //Position lastPosition;

  public static User newInstance(FirebaseUser firebaseUser, UserInfo provider) {
    User user = new User(firebaseUser.getUid());
    user.setProvider(provider.getProviderId());

    // TODO : refactoring
    if (provider.getProviderId().equals("password")) {
      user.setEmail(firebaseUser.getEmail());
    }

    return user;
  }

  public User() {
  }

  public User(String uid) {
    this.uid = uid;
  }

  public User(
          String uid,
          String username,
          String email,
          String provider,
          String photo_url,
          String name,
          String connection,
          String createdAt,
          double latitude,
          double longitude) {
    setUid(uid);
    setUsername(username);
    setEmail(email);
    setProvider(provider);
    setPhoto_url(photo_url);
    setName(name);

    setConnection(connection);
    setCreatedAt(createdAt);
    setLatitude(latitude);
    setLongitude(longitude);
  }

  @NonNull
  public String getUid() {
    return uid;
  }

  public void setUid(@NonNull String uid) {
    this.uid = uid;
  }

  @Nullable
  public String getEmail() {
    return email;
  }

  public void setEmail(@Nullable String email) {
    this.email = email;
  }

  @Nullable
  public String getPhoto_url() {
    return photo_url;
  }

  public void setPhoto_url(@Nullable String photo_url) {
    this.photo_url = photo_url;
  }

  @Nullable
  public String getName() {
    return name;
  }

  public void setName(@Nullable String name) {
    this.name = name;
  }

  @Nullable
  public String getProvider() {
    return provider;
  }

  public void setProvider(@Nullable String provider) {
    this.provider = provider;
  }

  @Nullable
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getConnection() {
    return connection;
  }

  public void setConnection(String connection) {
    this.connection = connection;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }
}