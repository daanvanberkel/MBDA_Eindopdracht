package nl.daanvanberkel.schiphol;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import nl.daanvanberkel.schiphol.helpers.JobServiceStarter;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.preferences_container, new PreferencesFragment())
                .commit();

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("notifications")) {
            if (sharedPreferences.getBoolean(key, true)) {
                JobServiceStarter.startFavoriteFlightJobService(this);
                Toast.makeText(this, getString(R.string.notifications_enabled), Toast.LENGTH_SHORT).show();
            } else {
                JobServiceStarter.stopFavoriteFlightJobService(this);
                Toast.makeText(this, getString(R.string.notifications_disabled), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static class PreferencesFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);
        }
    }
}
