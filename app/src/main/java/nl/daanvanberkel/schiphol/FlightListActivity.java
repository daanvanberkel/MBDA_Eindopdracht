package nl.daanvanberkel.schiphol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import nl.daanvanberkel.schiphol.helpers.JobServiceStarter;
import nl.daanvanberkel.schiphol.viewmodels.FlightListViewModel;


public class FlightListActivity extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_WRITE_CONTACTS = 1;

    private FlightListViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_list);

        // Receive data from another app
        // Run example:
        // ~/Library/Android/sdk/platform-tools/adb shell am start -a android.intent.action.SEND -t text/plain -c android.intent.category.DEFAULT --es android.intent.extra.TEXT "Dit\ is\ data\ van\ een\ andere\ app"
        Intent startIntent = getIntent();
        if (startIntent.getAction() != null && startIntent.getAction().equals(Intent.ACTION_SEND)) {
            Toast.makeText(this, startIntent.getStringExtra(Intent.EXTRA_TEXT), Toast.LENGTH_SHORT).show();
        }

        // Restart background job service to be the latest version of the service
        JobServiceStarter.restartFavoriteFlightJobService(getApplicationContext());

        viewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(FlightListViewModel.class);
        viewModel.refreshFlights();

        final FlightAdapter adapter = new FlightAdapter();

        final SwipeRefreshLayout swipeContainer = findViewById(R.id.swipe_container);
        // Handle "pull to refresh"
        swipeContainer.setOnRefreshListener(() -> viewModel.refreshFlights());

        // Handle newly loaded flights
        viewModel.getFlights().observe(this, flights -> {
            adapter.submitList(flights);
            swipeContainer.setRefreshing(false);
        });

        // Handle click on flight in the list
        adapter.setOnItemClickListener(flight -> {
            if (findViewById(R.id.flight_detail_fragment_container) != null) {
                // Tablet, show fragment next to flight list

                Bundle arguments = new Bundle();
                arguments.putSerializable(FlightDetailFragment.ARG_FLIGHT, flight);

                FlightDetailFragment detailFragment = new FlightDetailFragment();
                detailFragment.setArguments(arguments);

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flight_detail_fragment_container, detailFragment)
                        .commit();
            } else {
                // Phone, open detail activity

                Intent intent = new Intent(FlightListActivity.this, FlightDetailActivity.class);
                intent.putExtra(FlightDetailActivity.EXTRA_FLIGHT, flight);
                startActivity(intent);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.flight_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem settingsItem = menu.findItem(R.id.main_menu_settings);

        settingsItem.setOnMenuItemClickListener(item -> {
            Intent intent = new Intent(FlightListActivity.this, SettingsActivity.class);
            startActivity(intent);

            return true;
        });

        MenuItem contactItem = menu.findItem(R.id.main_menu_contact);

        contactItem.setOnMenuItemClickListener(item -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS}, MY_PERMISSIONS_REQUEST_WRITE_CONTACTS);
                return false;
            }

            viewModel.addToContacts();
            Toast.makeText(this, "Schiphol is toegevoegd aan uw contacten", Toast.LENGTH_SHORT).show();

            return true;
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewModel.addToContacts();

                Toast.makeText(this, "Schiphol is toegevoegd aan uw contacten", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "De app heeft geen toegang tot uw contacten", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
