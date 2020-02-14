package nl.daanvanberkel.schiphol;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;

import nl.daanvanberkel.schiphol.helpers.JobServiceStarter;
import nl.daanvanberkel.schiphol.viewmodels.FlightListViewModel;


public class FlightListActivity extends AppCompatActivity {

    private FlightListViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_list);

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
}
