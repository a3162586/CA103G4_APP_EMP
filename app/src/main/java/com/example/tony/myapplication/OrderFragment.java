package com.example.tony.myapplication;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class OrderFragment extends Fragment {

    private GridView gdTable;
    private List<MyTable.Table> tableList;
    private View view;

    public OrderFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_order, container, false);

        initTables();

        gdTable = view.findViewById(R.id.gvTable);
        gdTable.setAdapter(new TableAdpter(getActivity(),tableList));
        gdTable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getActivity(),OrderAddActivity.class);
                Bundle bundle = new Bundle();
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        return view;
    }

    private void initTables() {
        tableList = new ArrayList<>();
        for(int i=1 ; i<=MyTable.TOTALTABLES ; i++)
            tableList.add(new MyTable.Table(i,R.drawable.table,"空桌"));
    }

    public class TableAdpter extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private List<MyTable.Table> tableList;

        public TableAdpter(Context context, List<MyTable.Table> tableList) {
            this.tableList = tableList;

            //在fragment中需先取得activity後才能調用getSystemService方法
            layoutInflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return tableList.size();
        }

        @Override
        public Object getItem(int i) {
            return tableList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return tableList.get(i).getTableNo();
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.gridview_table, parent, false);
                holder = new ViewHolder();
                holder.ivTableImg = convertView.findViewById(R.id.ivTableImg);
                holder.tvTableStatus = convertView.findViewById(R.id.tvTableStatus);
                holder.tvTableNo = convertView.findViewById(R.id.tvTableNo);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            MyTable.Table table = tableList.get(i);
            holder.ivTableImg.setImageResource(table.getTableImg());
            holder.tvTableStatus.setText(table.getTableStatus());
            holder.tvTableNo.setText(Integer.toString(table.getTableNo()));

            return convertView;
        }

        private class ViewHolder {
            ImageView ivTableImg;
            TextView tvTableNo, tvTableStatus;
        }
    }


}
