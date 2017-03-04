package com.childlocator.firebase.ui.children;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.childlocator.firebase.LocationService;
import com.childlocator.firebase.R;
import com.childlocator.firebase.data.Constants;
import com.childlocator.firebase.data.model.User;
import com.childlocator.firebase.ui.login.LoginActivity;
import com.childlocator.firebase.ui.map.MapActivity;
import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.ArrayList;

public class ChildrenActivity extends AppCompatActivity {
  private Firebase rootDbUrl;
  private Firebase parentDbUrl;
  private Firebase allUsersDbUrl;
  private AuthData mAuthData;
  private String parentUId;
  private String parentEmail;
  private ArrayList<User> children;
  private ChildrenAdapter childrenAdapter;
  private ArrayList<String> emails;
  private ValueEventListener valueEventListenerNodeConnected;
  private User parent;

  @Bind(R.id.btnLogout)
  Button btnLogout;

  @Bind(R.id.lvChildren)
  ListView lvChildren;

  @Bind(R.id.tvParentName)
  TextView tvParentName;

  private FirebaseAuth mAuth;
  private FirebaseAuth.AuthStateListener mAuthListener;
  private static final String LOG_TAG = "ChildrenActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_children);
    ButterKnife.bind(this);
    //ChildrenActivity.this.startService(new Intent(ChildrenActivity.this, LocationService.class));
    //ChildrenActivity.this.startService(new Intent(ChildrenActivity.this, BackgroundLocationService.class));
    rootDbUrl = new Firebase(Constants.FIREBASE_CHILD_LOCATOR_DB_URL);
    emails = new ArrayList<>();
    children = new ArrayList<>();
    childrenAdapter = new ChildrenAdapter(ChildrenActivity.this, 0, children);
    lvChildren.setAdapter(childrenAdapter);
    lvChildren.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parentWhereTheClickHappened, View view, int position, long id) {
        Intent intent = new Intent(ChildrenActivity.this, MapActivity.class);
        User child = children.get(position);
        Gson gson = new Gson();
        intent.putExtra(
                Constants.KEY_SEND_USER,
                gson.toJson(child).toString() + "---" + gson.toJson(parent).toString());
        startActivity(intent);
      }
    });

    mAuth = FirebaseAuth.getInstance();
    mAuthListener = new FirebaseAuth.AuthStateListener() {
      @Override
      public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
          // User is signed in
          Log.d(LOG_TAG, "onAuthStateChanged:signed_in:" + user.getUid());
        } else {
          // User is signed out
          Log.d(LOG_TAG, "onAuthStateChanged:signed_out");
        }

        // ...

        if (user != null) {
          parentUId = user.getUid();
          parentEmail = user.getEmail();
          getCurrentUser(user);
          getAllUsers();
        } else {
          startActivity(new Intent(ChildrenActivity.this, LoginActivity.class));
          finish();
        }
      }
    };
  }

  @Override
  public void onStart() {
    super.onStart();
    mAuth.addAuthStateListener(mAuthListener);
  }

  @Override
  public void onStop() {
    super.onStop();
    if (mAuthListener != null) {
      mAuth.removeAuthStateListener(mAuthListener);
    }
  }

  public void getCurrentUser(FirebaseUser user) {
    parentDbUrl = rootDbUrl
            .child(Constants.NODE_USERS)
            .child(user.getUid());
    parentDbUrl.addValueEventListener(valueEventListenerParent);
    valueEventListenerNodeConnected = rootDbUrl.getRoot()
            .child(".info/connected")
            .addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                  parentDbUrl.child(Constants.NODE_CONNECTION)
                          .setValue(Constants.KEY_ONLINE);
                  parentDbUrl.child(Constants.NODE_CONNECTION)
                          .onDisconnect()
                          .setValue(Constants.KEY_OFFLINE);
                }
              }

              @Override
              public void onCancelled(FirebaseError firebaseError) {
              }
            });
  }

  private ValueEventListener valueEventListenerParent = new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
      User user = dataSnapshot.getValue(User.class);
      tvParentName.setText("Hello " + user.getName());
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {
    }
  };

  public void getAllUsers() {
    allUsersDbUrl = rootDbUrl.child(Constants.NODE_USERS);
    allUsersDbUrl.addChildEventListener(childEventListenerAllUsers);
  }

  private ChildEventListener childEventListenerAllUsers = new ChildEventListener() {
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
      User user = dataSnapshot.getValue(User.class);
      if (!dataSnapshot.getKey().equals(parentUId)) {
        if (!emails.contains(user.getEmail())) {
          emails.add(user.getEmail());
          children.add(user);
          childrenAdapter.notifyDataSetChanged();
        }
      } else {
        parent = user;
      }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
      if (!dataSnapshot.getKey().equals(parentUId)) {
        User user = dataSnapshot.getValue(User.class);
        int index = emails.indexOf(user.getEmail());
        children.set(index, user);
        childrenAdapter.notifyDataSetChanged();
      }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {
    }
  };

  @Override
  protected void onDestroy() {
    super.onDestroy();

    try {
      parentDbUrl.removeEventListener(valueEventListenerParent);
    } catch (Exception e) {
    }

    try {
      allUsersDbUrl.removeEventListener(childEventListenerAllUsers);
    } catch (Exception e) {
    }

    try {
      rootDbUrl.getRoot()
              .child(".info/connected")
              .removeEventListener(valueEventListenerNodeConnected);
    } catch (Exception e) {
    }
  }

  @OnClick(R.id.btnLogout)
  public void btnLogout() {
    if (mAuth != null) {
      stopService(new Intent(this, LocationService.class));

      parentDbUrl.child(Constants.NODE_CONNECTION).setValue(Constants.KEY_OFFLINE);

      // Temporarily solution to delete user from Firebase !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
      // mAuth.getCurrentUser().delete();

      mAuth.signOut();
    }
  }

  public class ChildrenAdapter extends ArrayAdapter<User> {
    private Activity mActivity;
    private ArrayList<User> children;
    @Bind(R.id.tvChildName)
    TextView tvChildName;
    @Bind(R.id.tvStatus)
    TextView tvStatus;

    public ChildrenAdapter(Activity mActivity, int resource, ArrayList<User> children) {
      super(mActivity, resource, children);
      this.mActivity = mActivity;
      this.children = children;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parentOf) {
      if (convertView == null) {
        LayoutInflater layoutInflater = mActivity.getLayoutInflater();
        convertView = layoutInflater.inflate(R.layout.item_list_children, null);
      }

      ButterKnife.bind(this, convertView);
      tvChildName.setText(this.children.get(position).getName());
      tvStatus.setText(this.children.get(position).getConnection());

      if (this.children.get(position).getConnection().equals(Constants.KEY_ONLINE)) {
        this.tvStatus.setTextColor(Color.parseColor("#00FF00"));
      } else {
        this.tvStatus.setTextColor(Color.parseColor("#FF0000"));
      }

      return convertView;
    }
  }
}
