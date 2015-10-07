package team.jcandfriends.cookstogo;

import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.regex.Pattern;

import team.jcandfriends.cookstogo.R.id;
import team.jcandfriends.cookstogo.R.layout;
import team.jcandfriends.cookstogo.Utils.FilterPredicate;
import team.jcandfriends.cookstogo.managers.SearchManager;

/**
 * Lists all search results
 */
public class SearchResultsAdapter extends Adapter<SearchResultsAdapter.SearchHistoryViewHolder> {

    private ArrayList<String> mSearchHistory;

    public SearchResultsAdapter(ArrayList<String> searchHistory) {
        mSearchHistory = searchHistory;
    }

    @Override
    public SearchResultsAdapter.SearchHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout.item_search_history, parent, false);
        return new SearchResultsAdapter.SearchHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchResultsAdapter.SearchHistoryViewHolder holder, int position) {
        holder.mText.setText(mSearchHistory.get(position));
    }

    public ArrayList<String> filter(final String query, SearchManager<String> searchManager) {
        if (null != query && !query.isEmpty()) {
            mSearchHistory = Utils.filter(mSearchHistory, new FilterPredicate() {
                final Pattern pattern = Pattern.compile(query, Pattern.CASE_INSENSITIVE);

                @Override
                public boolean evaluate(String string) {
                    return this.pattern.matcher(string).find();
                }
            });
        } else {
            mSearchHistory = searchManager.getAll();
        }
        notifyDataSetChanged();
        return mSearchHistory;
    }

    @Override
    public int getItemCount() {
        return mSearchHistory.size();
    }

    public static class SearchHistoryViewHolder extends ViewHolder {

        TextView mText;

        public SearchHistoryViewHolder(View itemView) {
            super(itemView);
            mText = (TextView) itemView.findViewById(id.query);
        }
    }
}
