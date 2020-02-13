package nl.daanvanberkel.schiphol;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;


public class FlightListActivity extends AppCompatActivity {

    private FlightListViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_list);

        viewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(FlightListViewModel.class);
        viewModel.refreshFlights();

        final FlightAdapter adapter = new FlightAdapter();

        final SwipeRefreshLayout swipeContainer = findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.refreshFlights();
            }
        });

        viewModel.getFlights().observe(this, new Observer<PagedList<Flight>>() {
            @Override
            public void onChanged(PagedList<Flight> flights) {
                adapter.submitList(flights);
                swipeContainer.setRefreshing(false);
            }
        });

        adapter.setOnItemClickListener(new FlightAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Flight flight) {
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
            }
        });

        RecyclerView recyclerView = findViewById(R.id.flight_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }
}
