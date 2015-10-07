package team.jcandfriends.cookstogo;

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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import team.jcandfriends.cookstogo.Utils.SimpleClickListener;
import team.jcandfriends.cookstogo.adapters.IngredientAdapter;
import team.jcandfriends.cookstogo.managers.IngredientManager;
import team.jcandfriends.cookstogo.managers.IngredientManager.Callbacks;
import team.jcandfriends.cookstogo.managers.IngredientSearchManager;

/**
 * The activity that lists all recipes which was the result of a search
 */
public class IngredientSearchActivity extends AppCompatActivity implements TextWatcher, OnEditorActionListener {

    private IngredientSearchManager mSearchManager;

    private SearchResultsAdapter mAdapter;
    private ArrayList<String> mSearchHistoryList;

    private RecyclerView mSearchHistoryView;
    private RecyclerView mSearchResultsView;
    private EditText mSearchField;
    private View mProgressBar;
    private View mNoResultsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Toolbar initialization
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Views initialization
        mSearchHistoryView = (RecyclerView) findViewById(R.id.recycler_view);
        mSearchResultsView = (RecyclerView) findViewById(R.id.results_view);
        mSearchField = (EditText) findViewById(R.id.search_field);
        mProgressBar = findViewById(R.id.progress_bar);
        mNoResultsView = findViewById(R.id.no_results_found);

        mSearchManager = IngredientSearchManager.get(this);
        mSearchHistoryList = mSearchManager.getAll();
        mAdapter = new SearchResultsAdapter(mSearchHistoryList);

        // mAdapter boilerplate initialization
        mSearchHistoryView.setLayoutManager(new LinearLayoutManager(this));
        mSearchHistoryView.setHasFixedSize(true);
        mSearchResultsView.setLayoutManager(new LinearLayoutManager(this));
        mSearchResultsView.setHasFixedSize(true);

        mSearchHistoryView.setAdapter(mAdapter);
        Utils.setOnItemClickListener(mSearchHistoryView, new SimpleClickListener() {
            @Override
            public void onClick(View view, int position) {
                Utils.closeKeyboard(IngredientSearchActivity.this);
                String query = IngredientSearchActivity.this.mSearchHistoryList.get(position);
                IngredientSearchActivity.this.mSearchField.setText(query);
                IngredientSearchActivity.this.showResults(query);
            }
        });

        mSearchField.setOnEditorActionListener(this);
        mSearchField.addTextChangedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_recipes_search, menu);

        menu.findItem(R.id.action_clear_search_text).getIcon().setColorFilter(Colors.BLACK_54, Mode.SRC_IN);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_clear_search_text:
                mSearchField.setText("");
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
        mSearchHistoryList = mAdapter.filter(s.toString(), mSearchManager);
        mSearchHistoryView.setVisibility(View.VISIBLE);
        mNoResultsView.setVisibility(View.GONE);
        mSearchResultsView.setVisibility(View.GONE);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            Utils.closeKeyboard(IngredientSearchActivity.this, mSearchField);

            String query = mSearchField.getText().toString();
            mSearchManager.add(query);

            showResults(query);
            return true;
        }

        return false;
    }

    private void showResults(String query) {
        if (Utils.hasInternet(this)) {
            mSearchHistoryView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            mSearchField.clearFocus();

            handleIngredientSearch(query);
        } else {
            Toast.makeText(this, R.string.snackbar_no_internet, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void handleIngredientSearch(String query) {
        final IngredientManager ingredientManager = IngredientManager.get(this);
        ingredientManager.search(query, new Callbacks() {
            @Override
            public void onSuccess(JSONObject result) {
                final JSONArray results = result.optJSONArray(Api.RESULTS);

                if (results.length() == 0) {
                    IngredientSearchActivity.this.findViewById(R.id.no_results_found).setVisibility(View.VISIBLE);
                } else {
                    IngredientAdapter adapter = new IngredientAdapter(results);
                    IngredientSearchActivity.this.mSearchResultsView.setAdapter(adapter);
                    Utils.setOnItemClickListener(IngredientSearchActivity.this.mSearchResultsView, new SimpleClickListener() {
                        @Override
                        public void onClick(View view, int position) {
                            JSONObject ingredient = results.optJSONObject(position);
                            int ingredientId = ingredient.optInt(Api.INGREDIENT_PK);
                            String ingredientName = ingredient.optString(Api.INGREDIENT_NAME);

                            ingredientManager.cacheIngredient(ingredient);
                            Utils.startIngredientActivity(IngredientSearchActivity.this, ingredientId, ingredientName);
                        }
                    });
                    IngredientSearchActivity.this.mSearchResultsView.setVisibility(View.VISIBLE);
                }

                IngredientSearchActivity.this.mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure() {
                Toast.makeText(IngredientSearchActivity.this, "Something went wrong. Sorry!", Toast.LENGTH_SHORT).show();
                IngredientSearchActivity.this.finish();
            }
        });
    }
}
