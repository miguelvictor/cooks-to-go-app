package team.jcandfriends.cookstogo;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
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
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import team.jcandfriends.cookstogo.R.anim;
import team.jcandfriends.cookstogo.R.id;
import team.jcandfriends.cookstogo.R.layout;
import team.jcandfriends.cookstogo.R.string;
import team.jcandfriends.cookstogo.Utils.SimpleClickListener;
import team.jcandfriends.cookstogo.adapters.RecipeAdapter;
import team.jcandfriends.cookstogo.managers.RecipeManager;
import team.jcandfriends.cookstogo.managers.RecipeManager.Callbacks;
import team.jcandfriends.cookstogo.managers.RecipeSearchManager;

/**
 * The activity that lists all recipes which was the result of a search
 */
public class RecipeSearchActivity extends AppCompatActivity implements TextWatcher, OnEditorActionListener {

    private RecipeSearchManager searchManager;

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
        this.setContentView(layout.activity_search);

        // Toolbar initialization
        Toolbar toolbar = (Toolbar) this.findViewById(id.toolbar);
        this.setSupportActionBar(toolbar);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Views initialization
        this.searchHistoryView = (RecyclerView) this.findViewById(id.recycler_view);
        this.searchResultsView = (RecyclerView) this.findViewById(id.results_view);
        this.searchField = (EditText) this.findViewById(id.search_field);
        this.progressBar = this.findViewById(id.progress_bar);
        this.noResultsView = this.findViewById(id.no_results_found);

        // adapter boilerplate initialization
        this.searchHistoryView.setLayoutManager(new LinearLayoutManager(this));
        this.searchHistoryView.setClickable(true);
        this.searchHistoryView.setHasFixedSize(true);
        this.searchResultsView.setHasFixedSize(true);
        this.searchResultsView.setClickable(true);
        this.searchResultsView.setLayoutManager(new LinearLayoutManager(this));

        this.searchManager = RecipeSearchManager.get(this);

        this.searchHistoryList = this.searchManager.getAll();
        this.adapter = new SearchResultsAdapter(this.searchHistoryList);
        this.searchHistoryView.setAdapter(this.adapter);
        Utils.setOnItemClickListener(searchHistoryView, new SimpleClickListener() {
            @Override
            public void onClick(View view, int position) {
                String query = RecipeSearchActivity.this.searchHistoryList.get(position);
                View currentFocus = RecipeSearchActivity.this.getCurrentFocus();
                if (null != currentFocus) {
                    InputMethodManager imeManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imeManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                }
                RecipeSearchActivity.this.searchField.setText(query);
                RecipeSearchActivity.this.showResults(query);
            }
        });

        this.searchField.setOnEditorActionListener(this);
        this.searchField.addTextChangedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.menu_recipes_search, menu);

        menu.findItem(id.action_clear_search_text).getIcon().setColorFilter(Colors.BLACK_54, Mode.SRC_IN);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case id.action_clear_search_text:
                this.searchField.setText("");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.overridePendingTransition(anim.abc_fade_in, anim.abc_fade_out);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        this.searchHistoryList = this.adapter.filter(s.toString(), this.searchManager);
        this.searchHistoryView.setVisibility(View.VISIBLE);
        this.noResultsView.setVisibility(View.GONE);
        this.searchResultsView.setVisibility(View.GONE);
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
            this.searchHistoryView.setVisibility(View.GONE);
            this.progressBar.setVisibility(View.VISIBLE);
            this.searchField.clearFocus();

            this.handleRecipeSearch(query);
        } else {
            Toast.makeText(this, string.snackbar_no_internet, Toast.LENGTH_SHORT).show();
            this.finish();
        }
    }

    private void handleRecipeSearch(String query) {
        final RecipeManager recipeManager = RecipeManager.get(this);
        recipeManager.search(query, new Callbacks() {
            @Override
            public void onSuccess(JSONObject result) {
                final JSONArray results = result.optJSONArray(Api.RESULTS);

                if (results.length() == 0) {
                    RecipeSearchActivity.this.findViewById(id.no_results_found).setVisibility(View.VISIBLE);
                } else {
                    RecipeAdapter adapter = new RecipeAdapter(results);
                    RecipeSearchActivity.this.searchResultsView.setAdapter(adapter);
                    Utils.setOnItemClickListener(RecipeSearchActivity.this.searchResultsView, new SimpleClickListener() {
                        @Override
                        public void onClick(View view, int position) {
                            super.onClick(view, position);

                            JSONObject recipe = results.optJSONObject(position);
                            int recipeId = recipe.optInt(Api.RECIPE_PK);
                            String recipeName = recipe.optString(Api.RECIPE_NAME);

                            recipeManager.cacheRecipe(recipe);
                            Utils.startRecipeActivity(RecipeSearchActivity.this, recipeId, recipeName);
                        }
                    });
                    RecipeSearchActivity.this.searchResultsView.setVisibility(View.VISIBLE);
                }

                RecipeSearchActivity.this.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure() {
                Toast.makeText(RecipeSearchActivity.this, "Something went wrong. Sorry!", Toast.LENGTH_SHORT).show();
                RecipeSearchActivity.this.finish();
            }
        });
    }

}
