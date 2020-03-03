package nl.daanvanberkel.schiphol;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import nl.daanvanberkel.schiphol.helpers.JobServiceStarter;


public class FlightListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_list);

        MobileAds.initialize(this, initializationStatus -> {
        });

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Receive data from another app
        Intent startIntent = getIntent();
        if (startIntent.getAction() != null && startIntent.getAction().equals(Intent.ACTION_SEND)) {
            Toast.makeText(this, startIntent.getStringExtra(Intent.EXTRA_TEXT), Toast.LENGTH_LONG).show();
        }

        // Restart background job service to be the latest version of the service
        JobServiceStarter.restartFavoriteFlightJobService(getApplicationContext());

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment_container, new FlightListFragment())
                .commit();
    }
}
