package team.jcandfriends.cookstogo;


import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

public final class Utils {

    public static void setUpUi (AppCompatActivity activity) {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DrawerLayout drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        ListView primaryOptions = (ListView) activity.findViewById(R.id.primaryOptions);
        primaryOptions.setAdapter(new Adapters.PrimaryOptionsAdapter(Constants.PRIMARY_OPTIONS, Constants.PRIMARY_OPTIONS_ICONS));

        ListView secondaryOptions = (ListView) activity.findViewById(R.id.secondaryOptions);
        secondaryOptions.setAdapter(new Adapters.SecondaryOptionsAdapter(Constants.SECONDARY_OPTIONS));
    }

}
