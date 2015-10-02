package team.jcandfriends.cookstogo;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.widget.Toast;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setUpUI();
        setDrawerSelectedItem(R.id.navigation_settings);

        getFragmentManager().beginTransaction().replace(R.id.container, new SettingsFragment()).commit();
    }

    @Override
    public boolean shouldPerformNavigationClick(MenuItem menuItem) {
        return menuItem.getItemId() != R.id.navigation_settings;
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Toast.makeText(getActivity(), "onPreferenceChange : " + key, Toast.LENGTH_SHORT).show();

            if (getString(R.string.preference_clear_recipes_cache_key).equals(key)) {
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.dialog_clear_recipes_title)
                        .setMessage(R.string.dialog_clear_recipes_summary)
                        .setPositiveButton("Clear", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create()
                        .show();
            } else if (getString(R.string.preference_clear_ingredients_cache_key).equals(key)) {
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.dialog_clear_ingredients_title)
                        .setMessage(R.string.dialog_clear_ingredients_summary)
                        .setPositiveButton("Clear", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create()
                        .show();
            }
        }
    }

}
