package team.jcandfriends.cookstogo;

import android.os.Bundle;
import android.view.MenuItem;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setUpUI();
        setDrawerSelectedItem(R.id.navigation_settings);
    }

    @Override
    public boolean shouldPerformNavigationClick(MenuItem menuItem) {
        return menuItem.getItemId() != R.id.navigation_settings;
    }

}
