package com.example.tony.myapplication.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tony.myapplication.DeskVO;
import com.example.tony.myapplication.R;
import com.example.tony.myapplication.activity.OrderAddActivity;
import com.example.tony.myapplication.main.Util;
import com.example.tony.myapplication.task.CommonTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class OrderFragment extends Fragment {

    private final static String TAG = "OrderFragment";
    private List<DeskVO> deskList;
    private CommonTask getDeskTask;
    private String branch_no;

    public OrderFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        // check if the device connect to the network
        if (Util.networkConnected(getActivity())) {

            // 宣告JasonObject物件，利用getDeskTask非同步任務連線到Servlet的 if ("getBranchNo".equals(action))
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getBranchNo");

            SharedPreferences preferences = getActivity().getSharedPreferences(Util.PREF_FILE,
                getActivity().MODE_PRIVATE);
            branch_no = preferences.getString("branch_No", "");

            jsonObject.addProperty("branch_no",branch_no);
            String jsonOut = jsonObject.toString();
            getDeskTask = new CommonTask(Util.URL + "AndroidDeskServlet", jsonOut);

            try {

                // 將getDeskTask回傳的result重新轉型回List<DeskVO>物件存入deskList
                String jsonIn = getDeskTask.execute().get();
                Type listType = new TypeToken<List<DeskVO>>() {
                }.getType();
                deskList = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }

        } else {
            Util.showToast(getActivity(), R.string.msg_NoNetwork);
        }

        // 設定TableAdapter帶入參數deskList
        GridView gdTable = view.findViewById(R.id.gvTable);
        gdTable.setAdapter(new TableAdapter(getActivity(),deskList));

        return view;
    }

    public class TableAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private List<DeskVO> deskList;

        private TableAdapter(Context context, List<DeskVO> deskList) {
            this.deskList = deskList;

            // 在fragment中需先取得activity後才能調用getSystemService方法
            layoutInflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return deskList.size();
        }

        @Override
        public Object getItem(int i) {
            return deskList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
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

            DeskVO desk = deskList.get(i);
            final String dek_No = desk.getDek_no();
            final String dek_Id = desk.getDek_id();

            // 根據桌位狀態改變顯示文字及背景顏色
            switch (desk.getDek_status()) {
                case 0:
                    holder.tvTableStatus.setText("空桌");
                    holder.tvTableStatus.setTextColor(getResources().getColor(R.color.colorBlue));
                    holder.tvTableNo.setBackgroundColor(getResources().getColor(R.color.colorGreen));
                    break;
                case 1:
                    holder.tvTableStatus.setText("使用中");
                    holder.tvTableStatus.setTextColor(getResources().getColor(R.color.colorRed));
                    holder.tvTableNo.setBackgroundColor(getResources().getColor(R.color.colorYellow));
                    break;
                case 2:
                    holder.tvTableStatus.setText("已訂位");
                    holder.tvTableStatus.setTextColor(getResources().getColor(R.color.colorBlue));
                    holder.tvTableNo.setBackgroundColor(getResources().getColor(R.color.colorLightBlue));
                    break;
            }

            holder.ivTableImg.setImageResource(R.drawable.table);
            holder.tvTableNo.setText(dek_Id);
            holder.ivTableImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //bundle存桌位流水號、分店編號、桌位編號，轉換頁面OrderAddActivity
                    Intent intent = new Intent(getActivity(), OrderAddActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("dek_No",dek_No);
                    bundle.putString("branch_No",branch_no);
                    bundle.putString("dek_Id",dek_Id);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

            return convertView;
        }

        private class ViewHolder {
            ImageView ivTableImg;
            TextView tvTableNo, tvTableStatus;
        }
    }

    @Override
    public void onPause() {
        if (getDeskTask != null) {
            getDeskTask.cancel(true);
            getDeskTask = null;
        }
        super.onPause();
    }

}
