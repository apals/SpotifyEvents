package apals.se.spotifyevents;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        PlayerNotificationCallback, ConnectionStateCallback {


    String hardstyle = "spotify:track:3DWmbuWA1JF7QHCoN5JKl7";
    String petter = "spotify:track:7rG7jgVJF0wbd5lI4GnJrI";

    private static final String CLIENT_ID = "77a578db53d54669b5927d5479674958";
    private static final String REDIRECT_URI = "yourcustomprotocol://callback";

    // Request code that will be passed together with authentication result to the onAuthenticationResult callback
    // Can be any integer
    private static final int REQUEST_CODE = 1337;

    private Player mPlayer;
    private String mEventName;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEventName = getIntent().getStringExtra("EVENT_NAME");

        setTitle(mEventName);
        if(getActionBar() != null)
        getActionBar().setTitle(mEventName);
        Firebase.setAndroidContext(this);
        Firebase myFirebaseRef = new Firebase("https://vivid-torch-1185.firebaseio.com/");
        myFirebaseRef.child(mEventName + "/songs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (mPlayer == null) return;
                if (snapshot.getValue() == null) return;
                System.out.println(snapshot.getValue());  //prints "Do you have data? You'll love Firebase."
                if (!mPlayer.isShutdown())
                    mPlayer.play((String) snapshot.getValue());

                if (mListView == null) return;
                if (mListView.getAdapter() == null) return;

                if (((String) snapshot.getValue()).equals(petter)) {
                    ((MyAdapter) mListView.getAdapter()).add("Petter - Vi är");
                } else if (((String) snapshot.getValue()).equals(hardstyle)) {
                    ((MyAdapter) mListView.getAdapter()).add("Refuzion - Hardstyle DNA (Radio Edit)");
                }
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });
        setContentView(R.layout.activity_main);


        mListView = (ListView) findViewById(R.id.queue_list);
        mListView.setAdapter(new MyAdapter(this, R.layout.support_simple_spinner_dropdown_item, new ArrayList<String>()));
        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
                    @Override
                    public void onInitialized(Player player) {
                        mPlayer.addConnectionStateCallback(MainActivity.this);
                        mPlayer.addPlayerNotificationCallback(MainActivity.this);
                        mPlayer.play("spotify:track:2TpxZ7JUBn3uw46aR7qd6V");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable error) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
        Log.d("MainActivity", "Playback event received: " + eventType.name());
        switch (eventType) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String errorDetails) {
        Log.d("MainActivity", "Playback error received: " + errorType.name());
        switch (errorType) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        // VERY IMPORTANT! This must always be called or else you will leak resources
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }


    int i = 0;

    public void queue(View view) {
        Firebase ref = new Firebase("https://vivid-torch-1185.firebaseio.com/");
        if (i % 2 == 0) {
            ref.child(mEventName + "/songs").setValue(petter);
        } else {
            ref.child(mEventName + "/songs").setValue(hardstyle);
        }
        i++;
    }


    class MyAdapter extends ArrayAdapter<String> {

        private Context mContext;
        private ArrayList<String> objects;


        public MyAdapter(Context context, int resource, ArrayList<String> objects) {
            super(context, resource);
            this.objects = objects;
        }

        public void add(String s) {
            objects.add(s);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = getLayoutInflater().inflate(R.layout.queue_item, null, false);
            ((TextView) v.findViewById(R.id.queue_item_name)).setText(objects.get(position));
            return v;
        }

        @Override
        public int getCount() {
            return objects.size();
        }

    }
}
