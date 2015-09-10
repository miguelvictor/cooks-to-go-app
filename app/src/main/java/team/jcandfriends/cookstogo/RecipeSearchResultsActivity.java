package team.jcandfriends.cookstogo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONObject;

import team.jcandfriends.cookstogo.adapters.RecipeAdapter;
import team.jcandfriends.cookstogo.tasks.RecommendRecipesTask;

public class RecipeSearchResultsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private View progressBar;
    private RecommendRecipesTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_search_results);

        Intent intent = getIntent();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(intent.getStringExtra(Constants.EXTRA_SEARCH_QUERY));
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);

        String url = intent.getStringExtra(Constants.EXTRA_RECIPES_URL);

        task = new RecommendRecipesTask(new RecommendRecipesTask.Callbacks() {
            @Override
            public void onPreExecute() {
            }

            @Override
            public void onPostExecute(JSONObject recipes) {
                if (null != recipes) {
                    RecipeAdapter adapter = new RecipeAdapter(recipes.optJSONArray(Api.RECIPE_RESULTS));
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setClickable(true);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(RecipeSearchResultsActivity.this));

                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(RecipeSearchResultsActivity.this, "Something went wrong. Sorry!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
        task.execute(url);

        Utils.initializeImageLoader(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (task.getStatus() != AsyncTask.Status.FINISHED) {
            task.cancel(true);
        }
    }
}