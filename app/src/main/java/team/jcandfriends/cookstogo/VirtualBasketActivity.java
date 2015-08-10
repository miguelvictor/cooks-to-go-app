package team.jcandfriends.cookstogo;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class VirtualBasketActivity extends BaseActivity {

    private boolean isList = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_basket);
        setUpUI();
        setDrawerSelectedItem(R.id.navigation_virtual_basket);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_virtual_basket, menu);

        MenuItem toggleViewItem = menu.findItem(R.id.action_toggle_view);
        boolean isList = Utils.getPersistedBoolean(this, Constants.VIEW_TYPE, true);

        if (!isList) {
            this.isList = false;
            toggleViewItem.setIcon(R.mipmap.ic_view_agenda_white_24dp);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent intent = new Intent(this, RecipeSearchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                return true;
            case R.id.action_toggle_view:
                if (isList) {
                    isList = false;
                    item.setIcon(R.mipmap.ic_view_agenda_white_24dp);
                    Utils.persistBoolean(this, Constants.VIEW_TYPE, false);
                } else {
                    isList = true;
                    item.setIcon(R.mipmap.ic_view_quilt_white_24dp);
                    Utils.persistBoolean(this, Constants.VIEW_TYPE, true);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean shouldPerformNavigationClick(MenuItem menuItem) {
        return menuItem.getItemId() != R.id.navigation_virtual_basket;
    }

}
