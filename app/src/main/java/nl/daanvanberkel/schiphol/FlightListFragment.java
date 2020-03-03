package nl.daanvanberkel.schiphol;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import nl.daanvanberkel.schiphol.viewmodels.FlightListViewModel;

public class FlightListFragment extends Fragment {

    public static final int MY_PERMISSIONS_REQUEST_WRITE_CONTACTS = 1;

    private FlightListViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.flight_list_fragment, container, false);
        setHasOptionsMenu(true);

        viewModel = new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()).create(FlightListViewModel.class);
        viewModel.refreshFlights();

        final FlightAdapter adapter = new FlightAdapter();

        final SwipeRefreshLayout swipeContainer = view.findViewById(R.id.swipe_container);
        // Handle "pull to refresh"
        swipeContainer.setOnRefreshListener(() -> viewModel.refreshFlights());

        // Handle newly loaded flights
        viewModel.getFlights().observe(this, flights -> {
            adapter.submitList(flights);
            swipeContainer.setRefreshing(false);
        });

        // Handle click on flight in the list
        adapter.setOnItemClickListener(flight -> {
            if (getActivity().findViewById(R.id.flight_detail_fragment_container) != null) {
                // Tablet, show fragment next to flight list

                Bundle arguments = new Bundle();
                arguments.putSerializable(FlightDetailFragment.ARG_FLIGHT, flight);

                FlightDetailFragment detailFragment = new FlightDetailFragment();
                detailFragment.setArguments(arguments);

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flight_detail_fragment_container, detailFragment)
                        .commit();
            } else {
                // Phone, open detail activity

                Intent intent = new Intent(getActivity(), FlightDetailActivity.class);
                intent.putExtra(FlightDetailActivity.EXTRA_FLIGHT, flight);
                startActivity(intent);
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.flight_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem settingsItem = menu.findItem(R.id.main_menu_settings);

        settingsItem.setOnMenuItemClickListener(item -> {
            Intent intent = new Intent(getContext(), SettingsActivity.class);
            startActivity(intent);

            return true;
        });

        MenuItem contactItem = menu.findItem(R.id.main_menu_contact);

        contactItem.setOnMenuItemClickListener(item -> {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS}, MY_PERMISSIONS_REQUEST_WRITE_CONTACTS);
                return false;
            }

            viewModel.addToContacts();
            Toast.makeText(getContext(), getString(R.string.added_to_contact), Toast.LENGTH_SHORT).show();

            return true;
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewModel.addToContacts();

                Toast.makeText(getContext(), getString(R.string.added_to_contact), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), getString(R.string.no_contact_permission), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
