package com.example.tony.myapplication;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MenuInfoFragment extends Fragment {

    private RecyclerView rvMenuInfo;
    private List<MenuVO.Menu> menuList;
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
//        MenuVO.Menu menu = MenuVO.MENUS[position];
        View view = inflater.inflate(R.layout.fragment_menu_info, container, false);

        initTeams();

        rvMenuInfo = view.findViewById(R.id.rvMenuInfo);
        rvMenuInfo.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvMenuInfo.setLayoutManager(layoutManager);
        rvMenuInfo.setAdapter(new TeamAdapter(menuList));

//        ImageView ivMenuPhoto = view.findViewById(R.id.ivMenu_Photo);
//        ivMenuPhoto.setImageResource(menu.getMenu_Photo());
//        TextView tvMenuID = view.findViewById(R.id.tvMenu_ID);
//        tvMenuID.setText(menu.getMenu_Id());
//        TextView tvMenuPrice = view.findViewById(R.id.tvMenu_Price);
//        tvMenuPrice.setText(Integer.toString(menu.getMenu_Price()));

        return view;
    }

    private void initTeams() {

        menuList = new ArrayList<>();
//        for (MenuVO.Menu menu : MenuVO.MENUS) {
//            menuList.add(menu);
            menuList.add(MenuVO.MENUS[0]);
            menuList.add(MenuVO.MENUS[1]);
            menuList.add(MenuVO.MENUS[2]);
//        }

    }

    private class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.ViewHolder> {

        private List<MenuVO.Menu> menuList;

        public TeamAdapter(List<MenuVO.Menu> menuList) {
            this.menuList = menuList;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView ivMenu_Photo;
            private TextView tvMenu_ID,tvMenu_Price;

            public ViewHolder(View view) {
                super(view);
                ivMenu_Photo = view.findViewById(R.id.ivMenu_Photo);
                tvMenu_ID = view.findViewById(R.id.tvMenu_ID);
                tvMenu_Price = view.findViewById(R.id.tvMenu_Price);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_menu, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final MenuVO.Menu menu = menuList.get(position);
            holder.ivMenu_Photo.setImageResource(menu.getMenu_Photo());
            holder.tvMenu_ID.setText(menu.getMenu_Id());
            holder.tvMenu_Price.setText(Integer.toString(menu.getMenu_Price()));

            holder.tvMenu_ID.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(),menu.getMenu_Id(),Toast.LENGTH_SHORT).show();
                }
            });
//
//            holder.tvName.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Toast.makeText(MainActivity.this,team.getTeamName(),Toast.LENGTH_SHORT).show();
//                }
//            });
//
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    String info = team.getTeamId()+" "+team.getTeamName();
//                    Toast.makeText(MainActivity.this,info,Toast.LENGTH_SHORT).show();
//                }
//            });
        }

        @Override
        public int getItemCount() {
            return menuList.size();
        }
    }

}
