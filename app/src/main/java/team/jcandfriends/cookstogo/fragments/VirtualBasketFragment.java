package team.jcandfriends.cookstogo.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import team.jcandfriends.cookstogo.R;

public class VirtualBasketFragment extends ExtendedFragment {

    public static final String LABEL = "Virtual Basket";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_virtual_basket, container, false);
    }

    @Override
    public String getFragmentTitle() {
        return LABEL;
    }

}