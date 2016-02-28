package apals.se.spotifyevents;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EventsListActivity extends AppCompatActivity {

    private List<Event> events;
    private EventsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        initializeData();
        setContentView(R.layout.activity_events_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        setTitle("Events");
        toolbar.setTitle("Events");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        setFabClickListener(fab);

        RecyclerView rv = (RecyclerView) findViewById(R.id.events_recycler_view);
        setUpRecyclerView(rv);

        setUpFirebase();
    }

    private void setUpFirebase() {
        Firebase myFirebaseRef = new Firebase("https://vivid-torch-1185.firebaseio.com/");
        myFirebaseRef.child("events").addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (snapshot.getKey().equals("events")) {
                    System.out.println(snapshot.getValue());  //prints "Do you have data? You'll love Firebase."
                    if(snapshot.getValue() == null) return;
                    if(((String) snapshot.getValue()).length() == 0) return;
                    events.add(new Event((String) snapshot.getValue()));
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });
    }


    private void setUpRecyclerView(RecyclerView rv) {
        LinearLayoutManager llm = new LinearLayoutManager(rv.getContext());
        rv.setLayoutManager(llm);
        mAdapter = new EventsAdapter(events);
        rv.setAdapter(mAdapter);
    }


    private void setFabClickListener(FloatingActionButton fab) {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(EventsListActivity.this, CreateEventActivity.class);
                startActivity(i);
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


    // This method creates an ArrayList that has three Event objects
// Checkout the project associated with this tutorial on Github if
// you want to use the same images.
    private void initializeData() {
        events = new ArrayList<>();
    }


}
