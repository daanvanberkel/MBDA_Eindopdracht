package nl.daanvanberkel.schiphol;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

public class FlightListViewModel extends AndroidViewModel {

    private LiveData<PagedList<Flight>> flights;
    private FlightDataSourceFactory factory;

    public FlightListViewModel(@NonNull Application application) {
        super(application);

        factory = new FlightDataSourceFactory(getApplication().getApplicationContext());
    }

    public LiveData<PagedList<Flight>> getFlights() {
        return flights;
    }

    public void refreshFlights() {
        if (factory.flightLiveData.getValue() != null) {
            factory.flightLiveData.getValue().invalidate();
        }

        PagedList.Config pagedListConfig = (new PagedList.Config.Builder())
                .setEnablePlaceholders(true)
                .setPageSize(20)
                .setPrefetchDistance(40)
                .build();

        flights = new LivePagedListBuilder<>(factory, pagedListConfig)
                .build();
    }
}
