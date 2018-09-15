package com.example.tony.myapplication;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MenuListFragment extends ListFragment {


    public MenuListFragment() {
    }

    private int position = -1;
    private MenuInfoFragment menuInfoFragment = new MenuInfoFragment();
    private MenuInfoCustomFragment menuInfoCustomFragment = new MenuInfoCustomFragment();
    private MenuInfoPopularFragment menuInfoPopularFragment = new MenuInfoPopularFragment();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayList<String> menuList = new ArrayList<>();
        menuList.add("經典餐點");
        menuList.add("客製化餐點");
        menuList.add("人氣餐點");
        setListAdapter(new ArrayAdapter<>(getActivity(), R.layout.fragment_menu_list, menuList));

        if (savedInstanceState != null) {
            position = savedInstanceState.getInt("position", 0);
        }

        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        showInfo(0);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("position", position);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int nextPosition, long id) {
        showInfo(nextPosition);
    }

    private void showInfo(int nextPosition) {

        if (position != nextPosition) {
            Bundle bundle = new Bundle();
            FragmentTransaction ft;

            switch(nextPosition){
                case 0:
                    bundle.putInt("position", nextPosition);
                    menuInfoFragment.setArguments(bundle);

                    ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.MenuInfo, menuInfoFragment);
                    //加入過場動畫
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.commit();
                    break;
                case 1:
                    bundle.putInt("position", nextPosition);
                    menuInfoCustomFragment.setArguments(bundle);

                    ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.MenuInfo, menuInfoCustomFragment);
                    //加入過場動畫
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.commit();
                    break;

                case 2:
                    bundle.putInt("position", nextPosition);
                    menuInfoPopularFragment.setArguments(bundle);

                    ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.MenuInfo, menuInfoPopularFragment);
                    //加入過場動畫
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.commit();
                    break;
            }
            position = nextPosition;
        }
    }
}
