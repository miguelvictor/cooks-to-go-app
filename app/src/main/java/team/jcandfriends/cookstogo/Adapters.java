package team.jcandfriends.cookstogo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public final class Adapters {

    private Adapters() {
    }

    public static class PrimaryOptionsAdapter extends BaseAdapter {

        private String[] items;
        private int[] itemIcons;

        public PrimaryOptionsAdapter(String[] items, int[] itemIcons) throws RuntimeException {
            if (items.length != itemIcons.length) {
                throw new RuntimeException("PrimaryOptionsAdapter: Items length must be equal to ItemIcons length.");
            }

            this.items = items;
            this.itemIcons = itemIcons;
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
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_primary_option, parent, false);
            }

            ((TextView) convertView.findViewById(R.id.label)).setText(items[position]);
            ((ImageView) convertView.findViewById(R.id.icon)).setImageResource(itemIcons[position]);

            return convertView;
        }
    }

    public static class SecondaryOptionsAdapter extends BaseAdapter {

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

}
