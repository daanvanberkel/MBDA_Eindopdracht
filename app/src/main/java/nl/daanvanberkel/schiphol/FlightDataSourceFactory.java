package nl.daanvanberkel.schiphol;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;

public class FlightDataSourceFactory extends DataSource.Factory<Integer, Flight> {
    private Context context;

    public FlightDataSourceFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public DataSource<Integer, Flight> create() {
        return new FlightDataSource(context);
    }
}
