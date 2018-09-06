package com.example.tony.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private GridView gdTable;
    private List<MyTable.Table> tableList;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_order:
                    mTextMessage.setText(R.string.title_order);
                    return true;
                case R.id.navigation_serve:
                    mTextMessage.setText(R.string.title_serve);
                    return true;
                case R.id.navigation_delivery:
                    mTextMessage.setText(R.string.title_delivery);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTables();
        findView();

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navigation.getChildAt(0);
        for (int i = 0; i < menuView.getChildCount(); i++) {
            final View iconView = menuView.getChildAt(i).findViewById(android.support.design.R.id.icon);
            final ViewGroup.LayoutParams layoutParams = iconView.getLayoutParams();
            final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, displayMetrics);
            layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, displayMetrics);
            iconView.setLayoutParams(layoutParams);
        }
    }

    private void initTables() {
        tableList = new ArrayList<>();
        for(int i=1 ; i<=MyTable.TOTALTABLES ; i++)
            tableList.add(new MyTable.Table(i,R.drawable.table,"空桌"));
    }

    private void findView() {
        gdTable = findViewById(R.id.gvTable);
        gdTable.setAdapter(new TableAdpter(this,tableList));
        gdTable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                MyTable.Table table = (MyTable.Table) adapterView.getItemAtPosition(i);
                ImageView imageView = new ImageView(MainActivity.this);
                imageView.setImageResource(table.getTableImg());
                Toast toast = new Toast(MainActivity.this);
                toast.setView(imageView);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();

            }
        });
    }

    public class TableAdpter extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private List<MyTable.Table> tableList;

        public TableAdpter(Context context,List<MyTable.Table> tableList) {
            this.tableList = tableList;
            layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
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
