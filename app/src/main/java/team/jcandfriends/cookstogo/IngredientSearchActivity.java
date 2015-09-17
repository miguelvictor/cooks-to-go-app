package team.jcandfriends.cookstogo;

import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import team.jcandfriends.cookstogo.adapters.IngredientAdapter;
import team.jcandfriends.cookstogo.managers.IngredientManager;
import team.jcandfriends.cookstogo.managers.IngredientSearchManager;

/**
 * The activity that lists all recipes which was the result of a search
 */
public class IngredientSearchActivity extends AppCompatActivity implements TextWatcher, TextView.OnEditorActionListener {

    private IngredientSearchManager searchManager;

    private SearchResultsAdapter adapter;
    private ArrayList<String> searchHistoryList;

    private RecyclerView searchHistoryView;
    private RecyclerView searchResultsView;
    private EditText searchField;
    private View progressBar;
    private View noResultsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Toolbar initialization
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Views initialization
        searchHistoryView = (RecyclerView) findViewById(R.id.recycler_view);
        searchResultsView = (RecyclerView) findViewById(R.id.results_view);
        searchField = (EditText) findViewById(R.id.search_field);
        progressBar = findViewById(R.id.progress_bar);
        noResultsView = findViewById(R.id.no_results_found);

        // adapter boilerplate initialization
        searchHistoryView.setLayoutManager(new LinearLayoutManager(this));
        searchHistoryView.setClickable(true);
        searchHistoryView.setHasFixedSize(true);
        searchResultsView.setHasFixedSize(true);
        searchResultsView.setClickable(true);
        searchResultsView.setLayoutManager(new LinearLayoutManager(IngredientSearchActivity.this));

        searchManager = IngredientSearchManager.get(this);

        searchHistoryList = searchManager.getAll();
        adapter = new SearchResultsAdapter(searchHistoryList);
        searchHistoryView.setAdapter(adapter);
        Utils.setOnItemClickListener(this.searchHistoryView, new Utils.SimpleClickListener() {
            @Override
            public void onClick(View view, int position) {
                String query = searchHistoryList.get(position);
                searchField.setText(query);
                showResults(query);
            }
        });

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
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        searchHistoryList = adapter.filter(s.toString(), searchManager);
        searchHistoryView.setVisibility(View.VISIBLE);
        noResultsView.setVisibility(View.GONE);
        searchResultsView.setVisibility(View.GONE);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            // close keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);

            String query = searchField.getText().toString();
            searchManager.add(query);

            // show results
            showResults(query);
            return true;
        }

        return false;
    }

    private void showResults(String query) {
        if (Utils.hasInternet(this)) {
            searchHistoryView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            searchField.clearFocus();

            handleIngredientSearch(query);
        } else {
            Toast.makeText(this, R.string.snackbar_no_internet, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void handleIngredientSearch(String query) {
        final IngredientManager ingredientManager = IngredientManager.get(this);
        ingredientManager.search(query, new IngredientManager.Callbacks() {
            @Override
            public void onSuccess(JSONObject result) {
                final JSONArray results = result.optJSONArray(Api.RESULTS);

                if (results.length() == 0) {
                    findViewById(R.id.no_results_found).setVisibility(View.VISIBLE);
                } else {
                    IngredientAdapter adapter = new IngredientAdapter(results);
                    searchResultsView.setAdapter(adapter);
                    Utils.setOnItemClickListener(searchResultsView, new Utils.SimpleClickListener() {
                        @Override
                        public void onClick(View view, int position) {
                            super.onClick(view, position);

                            final JSONObject ingredient = results.optJSONObject(position);
                            final int ingredientId = ingredient.optInt(Api.INGREDIENT_PK);
                            final String ingredientName = ingredient.optString(Api.INGREDIENT_NAME);

                            ingredientManager.cacheIngredient(ingredient);
                            Utils.startIngredientActivity(IngredientSearchActivity.this, ingredientId, ingredientName);
                        }
                    });
                    searchResultsView.setVisibility(View.VISIBLE);
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure() {
                Toast.makeText(IngredientSearchActivity.this, "Something went wrong. Sorry!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
