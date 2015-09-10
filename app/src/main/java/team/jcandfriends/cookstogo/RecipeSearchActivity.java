package team.jcandfriends.cookstogo;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The activity that lists all recipes which was the result of a search
 */
public class RecipeSearchActivity extends AppCompatActivity implements TextWatcher, TextView.OnEditorActionListener {

    private static final String RECIPE_SEARCH_HISTORY = "recipe_search_history";

    private SearchResultsAdapter adapter;
    private EditText searchField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new SearchResultsAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setClickable(true);
        recyclerView.setHasFixedSize(true);
        Utils.setOnItemClickListener(recyclerView, new Utils.SimpleClickListener() {
            @Override
            public void onClick(View view, int position) {
                String query = searchField.getText().toString();
                showResults(query);
            }
        });

        searchField = (EditText) findViewById(R.id.search_field);
        searchField.setOnEditorActionListener(this);
        searchField.addTextChangedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_recipes_search, menu);

        menu.findItem(R.id.action_clear_search_text).getIcon().setColorFilter(Colors.BLACK_54, PorterDuff.Mode.SRC_IN);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_clear_search_text:
                searchField.setText("");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        adapter.filter(this, s.toString());
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            String query = searchField.getText().toString();
            showResults(query);
            return true;
        }

        return false;
    }

    private void showResults (String query) {
        if (Utils.hasInternet(this)) {
            Intent intent = new Intent(this, RecipeSearchResultsActivity.class);
            intent.putExtra(Constants.EXTRA_SEARCH_QUERY, query);
            intent.putExtra(Constants.EXTRA_RECIPES_URL, Api.getSearchRecipeUrl(query));
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, R.string.snackbar_no_internet, Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
