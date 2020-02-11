package nl.daanvanberkel.schiphol;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

public class FlightListViewModel extends AndroidViewModel {

    private LiveData<PagedList<Flight>> flights;

    public FlightListViewModel(@NonNull Application application) {
        super(application);

        FlightDataSourceFactory factory = new FlightDataSourceFactory(getApplication().getApplicationContext());

        PagedList.Config pagedListConfig = (new PagedList.Config.Builder())
                .setEnablePlaceholders(true)
                .setPageSize(20)
                .setPrefetchDistance(40)
                .build();

        flights = new LivePagedListBuilder<>(factory, pagedListConfig)
                .build();
    }

    public LiveData<PagedList<Flight>> getFlights() {
        return flights;
    }
}
