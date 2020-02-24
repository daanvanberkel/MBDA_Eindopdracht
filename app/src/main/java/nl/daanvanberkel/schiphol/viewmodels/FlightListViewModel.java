package nl.daanvanberkel.schiphol.viewmodels;

import android.app.Application;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import java.util.ArrayList;

import nl.daanvanberkel.schiphol.models.Flight;
import nl.daanvanberkel.schiphol.FlightDataSourceFactory;

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

    public void addToContacts() {
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        int rawContactInsertIndex = operations.size();

        // Create new empty contact
        operations.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
            .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
            .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());

        //Phone Number
        operations.add(ContentProviderOperation
            .newInsert(ContactsContract.Data.CONTENT_URI)
            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
            .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, "0207940800")
            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK).build());

        //Display name
        operations.add(ContentProviderOperation
            .newInsert(ContactsContract.Data.CONTENT_URI)
            .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactInsertIndex)
            .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
            .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, "Schiphol Airport")
            .build());

        //Postal Address
        operations.add(ContentProviderOperation
            .newInsert(ContactsContract.Data.CONTENT_URI)
            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
            .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE )
            .withValue(ContactsContract.CommonDataKinds.StructuredPostal.STREET, "Evert v/d Beekstraat 202")
            .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE )
            .withValue(ContactsContract.CommonDataKinds.StructuredPostal.CITY, "Schiphol")
            .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE )
            .withValue(ContactsContract.CommonDataKinds.StructuredPostal.REGION, "Noord-Holland")
            .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE )
            .withValue(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE, "1118CP")
            .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE )
            .withValue(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY, "Nederland")
            .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE )
            .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK)
            .build());

        try {
            ContentProviderResult[] res = getApplication().getApplicationContext().getContentResolver().applyBatch(ContactsContract.AUTHORITY, operations);
        } catch (RemoteException| OperationApplicationException e) {
            e.printStackTrace();
            Toast.makeText(getApplication().getApplicationContext(), "Er is een fout opgetreden tijdens het opslaan van schiphol in uw contacten. Probeer het later nog eens.", Toast.LENGTH_LONG).show();
        }
    }
}
