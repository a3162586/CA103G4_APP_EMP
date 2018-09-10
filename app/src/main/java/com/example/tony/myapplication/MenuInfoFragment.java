package com.example.tony.myapplication;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuInfoFragment extends Fragment {

    private int position;

    public MenuInfoFragment() {
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
        MenuVO.Menu menu = MenuVO.MENUS[position];
        View view = inflater.inflate(R.layout.fragment_menu_info, container, false);

        ImageView ivMenuPhoto = view.findViewById(R.id.ivMenu_Photo);
        ivMenuPhoto.setImageResource(menu.getMenu_Photo());
        TextView tvMenuID = view.findViewById(R.id.tvMenu_ID);
        tvMenuID.setText(menu.getMenu_Id());
        TextView tvMenuPrice = view.findViewById(R.id.tvMenu_Price);
        tvMenuPrice.setText(Integer.toString(menu.getMenu_Price()));
        TextView tvMenuIntro = view.findViewById(R.id.tvMenu_Intro);
        tvMenuIntro.setText(menu.getMenu_Intro());

        return view;
    }

}
