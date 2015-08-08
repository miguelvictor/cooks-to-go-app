package team.jcandfriends.cookstogo;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import team.jcandfriends.cookstogo.adapters.PrimaryOptionsAdapter;
import team.jcandfriends.cookstogo.adapters.SecondaryOptionsAdapter;
import team.jcandfriends.cookstogo.fragments.ExtendedFragment;
import team.jcandfriends.cookstogo.fragments.HomeFragment;
import team.jcandfriends.cookstogo.fragments.IngredientsFragment;
import team.jcandfriends.cookstogo.fragments.RecipesFragment;
import team.jcandfriends.cookstogo.fragments.SettingsFragment;
import team.jcandfriends.cookstogo.fragments.VirtualBasketFragment;

public class MainActivity extends AppCompatActivity {

    private static final String LAST_ACCESSED_FRAGMENT = "last_accessed_fragment";
    private static final String APP_DEBUG = "APP_DEBUG";

    private ListView primaryOptions;
    private int currentItem = 0;

    private static void setItemAsSelected(AppCompatActivity activity, View view, int position) {
        view.setBackgroundColor(Colors.BLACK_5);
        ((ImageView) view.findViewById(R.id.icon)).setColorFilter(Colors.PRIMARY_COLOR, PorterDuff.Mode.SRC_IN);
        ((TextView) view.findViewById(R.id.label)).setTextColor(Colors.PRIMARY_COLOR);

        FragmentTransaction ftx = activity.getSupportFragmentManager().beginTransaction();
        ExtendedFragment fragment = getFragmentAt(position);
        ftx.replace(R.id.container, fragment);
        ftx.commit();
        activity.getSupportActionBar().setTitle(fragment.getFragmentTitle());
    }

    private static void setItemAsDeselected(AppCompatActivity activity, View view) {
        view.setBackgroundColor(Color.WHITE);
        ((ImageView) view.findViewById(R.id.icon)).setColorFilter(Colors.BLACK_54, PorterDuff.Mode.SRC_IN);
        ((TextView) view.findViewById(R.id.label)).setTextColor(Colors.BLACK_87);
    }

    private static ExtendedFragment getFragmentAt(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new RecipesFragment();
            case 2:
                return new IngredientsFragment();
            case 3:
                return new VirtualBasketFragment();
        }
        return new SettingsFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        primaryOptions = (ListView) findViewById(R.id.primaryOptions);
        primaryOptions.setAdapter(new PrimaryOptionsAdapter(Constants.PRIMARY_OPTIONS, Constants.PRIMARY_OPTIONS_ICONS));
        primaryOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int newPosition, long id) {
                if (currentItem != newPosition) {
                    setItemAsDeselected(MainActivity.this, parent.getChildAt(currentItem));
                    setItemAsSelected(MainActivity.this, view, newPosition);
                    currentItem = newPosition;
                    currentItem = newPosition;
                }

                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        ListView secondaryOptions = (ListView) findViewById(R.id.secondaryOptions);
        secondaryOptions.setAdapter(new SecondaryOptionsAdapter(Constants.SECONDARY_OPTIONS));

        secondaryOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                if (position == 0) {
                    intent = new Intent(MainActivity.this, HelpActivity.class);
                } else {
                    intent = new Intent(MainActivity.this, FeedbackActivity.class);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                MainActivity.this.startActivity(intent);
            }
        });

        if (savedInstanceState == null) {
            // setting up the first item on the sidebar
            Log.v(APP_DEBUG, "Restore: savedInstanceState is null");
            ExtendedFragment defaultFragment = getFragmentAt(0);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, defaultFragment)
                    .commit();
            getSupportActionBar().setTitle(defaultFragment.getFragmentTitle());
        } else {
            int lastPosition = savedInstanceState.getInt(LAST_ACCESSED_FRAGMENT);
            Log.v(APP_DEBUG, "Restoring selected fragment position of " + lastPosition);
            setItemAsSelected(this, primaryOptions.getChildAt(lastPosition), lastPosition);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // saving the actual fragment -- useful to restore state of recipes fragment
        // getSupportFragmentManager().putFragment(outState, LAST_ACCESSED_FRAGMENT, getSupportFragmentManager().findFragmentById(R.id.container));

        outState.putInt(LAST_ACCESSED_FRAGMENT, currentItem);
        Log.v(APP_DEBUG, "onSaveInstanceState: Last position -> " + currentItem);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.v(APP_DEBUG, "Melvin gago tang ina");
    }
}
