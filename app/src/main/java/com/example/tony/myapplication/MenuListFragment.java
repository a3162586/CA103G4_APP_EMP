package com.example.tony.myapplication;


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

    private int position;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayList<String> menuList = new ArrayList<>();
        for (MenuVO.Menu menu : MenuVO.MENUS) {
            menuList.add(menu.getMenu_Id());
        }

        setListAdapter(new ArrayAdapter<>(getActivity(), R.layout.fragment_menu_list, menuList));

        if (savedInstanceState != null) {
            position = savedInstanceState.getInt("position", 0);
        }

        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        showInfo();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("position", position);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        this.position = position;
        showInfo();
    }

    private void showInfo() {
        MenuInfoFragment menuInfoFragment = (MenuInfoFragment) getFragmentManager().findFragmentById(R.id.MenuInfo);
        if (menuInfoFragment == null || menuInfoFragment.getPosition() != position) {
            menuInfoFragment = new MenuInfoFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            menuInfoFragment.setArguments(bundle);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.MenuInfo, menuInfoFragment);
            //加入過場動畫
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
        }
    }
}
