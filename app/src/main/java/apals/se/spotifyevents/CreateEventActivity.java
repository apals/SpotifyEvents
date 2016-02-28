package apals.se.spotifyevents;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.firebase.client.Firebase;

public class CreateEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
    }

    public void confirmEvent(View view) {
        String eventName = ((EditText) findViewById(R.id.edittext_event_name)).getText().toString();
        Firebase ref = new Firebase("https://vivid-torch-1185.firebaseio.com/");
        ref.child("events").setValue(eventName);
        finish();
    }
}
