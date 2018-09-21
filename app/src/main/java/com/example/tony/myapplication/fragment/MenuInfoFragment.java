package com.example.tony.myapplication.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tony.myapplication.MenuVO;
import com.example.tony.myapplication.OrderInvoiceVO;
import com.example.tony.myapplication.R;
import com.example.tony.myapplication.activity.OrderAddActivity;
import com.example.tony.myapplication.main.Util;
import com.example.tony.myapplication.task.CommonTask;
import com.example.tony.myapplication.task.ImageTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MenuInfoFragment extends Fragment {

    private RecyclerView rvMenuInfo;
//    private int position;
    private final static String TAG = "MenuInfoFragment";
    private View view;
    private CommonTask getMenuTask;
    private ImageTask menuImageTask;
    private List<MenuVO> menuList;

    private OrderAddActivity oaa;

    public MenuInfoFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        oaa = (OrderAddActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (container == null) {
            return null;
        }

        view = inflater.inflate(R.layout.fragment_menu_info, container, false);

        // check if the device connect to the network
        if (Util.networkConnected(getActivity())) {

            //宣告JasonObject物件，利用getMenuTask非同步任務連線到Servlet的 if ("getAll".equals(action))
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            String jsonOut = jsonObject.toString();
            getMenuTask = new CommonTask(Util.URL + "AndroidMenuServlet", jsonOut);

            try {

                //將getMenuTask回傳的result重新轉型回List<MenuVO>物件
                String jsonIn = getMenuTask.execute().get();
                Type listType = new TypeToken<List<MenuVO>>() {
                }.getType();
                menuList = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (menuList == null || menuList.isEmpty()) {
                Util.showToast(getActivity(), R.string.msg_MenusNotFound);
            } else {
                showResult(menuList);
            }

        } else {
            Util.showToast(getActivity(), R.string.msg_NoNetwork);
        }


//        Bundle bundle = this.getArguments();
//        position = bundle.getInt("position");

        return view;
    }

    private class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

        private List<MenuVO> menuList;
        private int imageSize;

        public MenuAdapter(List<MenuVO> menuList) {
            this.menuList = menuList;
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView ivMenu_Photo;
            private TextView tvMenu_ID,tvMenu_Price;
            private Button btnMenu_Add;

            private List<OrderInvoiceVO> orderList;

            public ViewHolder(View view) {
                super(view);
                ivMenu_Photo = view.findViewById(R.id.ivMenu_Photo);
                tvMenu_ID = view.findViewById(R.id.tvMenu_ID);
                tvMenu_Price = view.findViewById(R.id.tvMenu_Price);
                btnMenu_Add = view.findViewById(R.id.btnMenuAdd);
                btnMenu_Add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //btnMenu_Add
                        orderList = new ArrayList<>();
                        orderList.add(new OrderInvoiceVO("1", "拉麵1",R.drawable.ic_delete_black_24dp ));
                        orderList.add(new OrderInvoiceVO("2", "拉麵2",R.drawable.ic_delete_black_24dp ));
                        orderList.add(new OrderInvoiceVO("3", "拉麵3",R.drawable.ic_delete_black_24dp ));
                        orderList.add(new OrderInvoiceVO("4", "拉麵4",R.drawable.ic_delete_black_24dp ));
                        orderList.add(new OrderInvoiceVO("5", "拉麵5",R.drawable.ic_delete_black_24dp ));
                        oaa.setOrderList(orderList);
                    }
                });
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

            final MenuVO menu = menuList.get(position);
            holder.tvMenu_ID.setText(menu.getMenu_Id());
            holder.tvMenu_Price.setText("$"+Integer.toString(menu.getMenu_Price()));

//            BitmapFactory.Options opt = new BitmapFactory.Options();
//            byte[] imageBytes = Base64.decode(menu.getMenu_Photo(), Base64.DEFAULT);
//            // inSampleSize值即為縮放的倍數 (數字越大縮越多)
//            opt.inSampleSize = Util.getImageScale(imageBytes, 160, 160);
//            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length,opt);
//            holder.ivMenu_Photo.setImageBitmap(bitmap);

            //menuImageTask傳入ViewHolder物件，處理完之後會直接將圖show在對應的view上
            String url = Util.URL + "AndroidMenuServlet";
            String pk = menu.getMenu_No();
            menuImageTask = new ImageTask(url, pk, imageSize, holder.ivMenu_Photo);
            menuImageTask.execute();

            holder.ivMenu_Photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(),menu.getMenu_Id(),Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return menuList.size();
        }
    }

    public void showResult(List<MenuVO> result) {

        rvMenuInfo = view.findViewById(R.id.rvMenuInfo);
        rvMenuInfo.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvMenuInfo.setLayoutManager(layoutManager);
        rvMenuInfo.setAdapter(new MenuAdapter(result));

    }

    @Override
    public void onPause() {
        if (getMenuTask != null) {
            getMenuTask.cancel(true);
            getMenuTask = null;
        }
        super.onPause();
    }

}
