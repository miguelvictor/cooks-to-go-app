package team.jcandfriends.cookstogo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.SearchHistoryViewHolder> {

    private ArrayList<String> searchHistory;

    public SearchResultsAdapter(ArrayList<String> searchHistory) {
        this.searchHistory = searchHistory;
    }

    @Override
    public SearchHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_history, parent, false);
        return new SearchHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchHistoryViewHolder holder, int position) {
        holder.text.setText(searchHistory.get(position));
    }

    @Override
    public int getItemCount() {
        return searchHistory.size();
    }

    public static class SearchHistoryViewHolder extends RecyclerView.ViewHolder {

        TextView text;

        public SearchHistoryViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.query);
        }
    }
}
