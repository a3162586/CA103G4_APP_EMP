package com.example.tony.myapplication;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class MenuInfoPopularFragment extends Fragment {

    private RecyclerView rvMenuInfo;
    private List<MenuVO.Menu> menuList;
    private int position;

    public MenuInfoPopularFragment() {
    }

    public int getPosition() {
        return position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (container == null) {
            return null;
        }


        Bundle bundle = this.getArguments();
        position = bundle.getInt("position");
        View view = inflater.inflate(R.layout.fragment_menu_info_popular, container, false);

        return view;
    }

}
