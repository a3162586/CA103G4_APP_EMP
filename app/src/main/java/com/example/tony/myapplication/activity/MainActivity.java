package com.example.tony.myapplication.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.tony.myapplication.fragment.DeliveryFragment;
import com.example.tony.myapplication.fragment.OrderFragment;
import com.example.tony.myapplication.R;
import com.example.tony.myapplication.fragment.ServeFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            //切換fragment畫面(點餐、出餐、外送)
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_order:
                    fragmentTransaction.replace(R.id.frameLayout,new OrderFragment());
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_serve:
                    fragmentTransaction.replace(R.id.frameLayout,new ServeFragment());
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_delivery:
                    fragmentTransaction.replace(R.id.frameLayout,new DeliveryFragment());
                    fragmentTransaction.commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //底部Menu的ICON大小調整
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navigation.getChildAt(0);
        for (int i = 0; i < menuView.getChildCount(); i++) {
            final View iconView = menuView.getChildAt(i).findViewById(android.support.design.R.id.icon);
            final ViewGroup.LayoutParams layoutParams = iconView.getLayoutParams();
            final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, displayMetrics);
            layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, displayMetrics);
            iconView.setLayoutParams(layoutParams);
        }

        initFragment();
    }

    //依權限登入後預設畫面
    private void initFragment() {
        OrderFragment orderFragment = new OrderFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frameLayout,orderFragment)
                .show(orderFragment)
                .commit();
    }

}
