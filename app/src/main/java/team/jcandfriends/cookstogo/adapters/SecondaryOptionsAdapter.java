package team.jcandfriends.cookstogo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import team.jcandfriends.cookstogo.R;

public class SecondaryOptionsAdapter extends BaseAdapter {

    private String[] items;

    public SecondaryOptionsAdapter(String[] items) {
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_secondary_option, parent, false);
        }

        ((TextView) convertView.findViewById(R.id.label)).setText(items[position]);

        return convertView;
    }
}