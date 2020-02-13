package nl.daanvanberkel.schiphol;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import nl.daanvanberkel.schiphol.models.Flight;

public class FlightDataSourceFactory extends DataSource.Factory<Integer, Flight> {
    private Context context;

    public MutableLiveData<FlightDataSource> flightLiveData;

    public FlightDataSourceFactory(Context context) {
        this.context = context;
        flightLiveData = new MutableLiveData<>();
    }

    @NonNull
    @Override
    public DataSource<Integer, Flight> create() {
        FlightDataSource dataSource = new FlightDataSource(context);
        flightLiveData.postValue(dataSource);
        return dataSource;
    }
}
