package team.jcandfriends.cookstogo;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public final class Utils {

    public static void setUpUi(final AppCompatActivity activity) {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final DrawerLayout drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        ListView primaryOptions = (ListView) activity.findViewById(R.id.primaryOptions);
        primaryOptions.setAdapter(new Adapters.PrimaryOptionsAdapter(Constants.PRIMARY_OPTIONS, Constants.PRIMARY_OPTIONS_ICONS));
        primaryOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            int previouslySelectedItem = 0;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (previouslySelectedItem != position) {
                    setItemAsDeselected(activity, parent.getChildAt(previouslySelectedItem));
                    setItemAsSelected(activity, view, position);
                    previouslySelectedItem = position;
                }

                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        // setting up the first item on the sidebar
        Fragments.ExtendedFragment defaultFragment = getFragmentAt(0);
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, defaultFragment)
                .commit();
        activity.getSupportActionBar().setTitle(defaultFragment.getFragmentTitle());

        ListView secondaryOptions = (ListView) activity.findViewById(R.id.secondaryOptions);
        secondaryOptions.setAdapter(new Adapters.SecondaryOptionsAdapter(Constants.SECONDARY_OPTIONS));
    }

    private static void setItemAsSelected(AppCompatActivity activity, View view, int position) {
        view.setBackgroundColor(Colors.BLACK_5);
        ((ImageView) view.findViewById(R.id.icon)).setColorFilter(Colors.PRIMARY_COLOR, PorterDuff.Mode.SRC_IN);
        ((TextView) view.findViewById(R.id.label)).setTextColor(Colors.PRIMARY_COLOR);

        FragmentTransaction ftx = activity.getSupportFragmentManager().beginTransaction();
        Fragments.ExtendedFragment fragment = getFragmentAt(position);
        ftx.replace(R.id.container, fragment);
        ftx.commit();
        activity.getSupportActionBar().setTitle(fragment.getFragmentTitle());
    }

    private static void setItemAsDeselected(AppCompatActivity activity, View view) {
        view.setBackgroundColor(Color.WHITE);
        ((ImageView) view.findViewById(R.id.icon)).setColorFilter(Colors.BLACK_54, PorterDuff.Mode.SRC_IN);
        ((TextView) view.findViewById(R.id.label)).setTextColor(Colors.BLACK_87);
    }

    private static Fragments.ExtendedFragment getFragmentAt(int position) {
        switch (position) {
            case 0:
                return new Fragments.HomeFragment();
            case 1:
                return new Fragments.RecipesFragment();
            case 2:
                return new Fragments.IngredientsFragment();
            case 3:
                return new Fragments.VirtualBasketFragment();
        }
        return new Fragments.SettingsFragment();
    }

}
