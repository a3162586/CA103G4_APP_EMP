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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tony.myapplication.BranchVO;
import com.example.tony.myapplication.DeliveryVO;
import com.example.tony.myapplication.DeskVO;
import com.example.tony.myapplication.MyTable;
import com.example.tony.myapplication.R;
import com.example.tony.myapplication.activity.DeliveryDetailActivity;
import com.example.tony.myapplication.activity.OrderAddActivity;
import com.example.tony.myapplication.main.Util;
import com.example.tony.myapplication.task.CommonTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class OrderFragment extends Fragment {

    private final static String TAG = "OrderFragment";
    private GridView gdTable;
    private List<DeskVO> deskList;

    private View view;
    private CommonTask getDeskTask;

    public OrderFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_order, container, false);

        // check if the device connect to the network
        if (Util.networkConnected(getActivity())) {

            //宣告JasonObject物件，利用getDeskTask非同步任務連線到Servlet的 if ("getBranchNo".equals(action))
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getBranchNo");

            SharedPreferences preferences = getActivity().getSharedPreferences(Util.PREF_FILE,
                getActivity().MODE_PRIVATE);
            String branch_no = preferences.getString("branch_No", "");
            jsonObject.addProperty("branch_no",branch_no);
            String jsonOut = jsonObject.toString();
            getDeskTask = new CommonTask(Util.URL + "AndroidDeskServlet", jsonOut);

            try {
                //將getDeskTask回傳的result重新轉型回List<DeskVO>物件
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

//        initTables();

        gdTable = view.findViewById(R.id.gvTable);
        gdTable.setAdapter(new TableAdpter(getActivity(),deskList));

        gdTable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


            }
        });

        return view;
    }

//    private void initTables() {
//        deskList = new ArrayList<>();
//        for(int i=1 ; i<=deskList.size() ; i++)
//            deskList.add(new DeskVO(i,R.drawable.table,"空桌"));
//    }

    public class TableAdpter extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private List<DeskVO> deskList;

        public TableAdpter(Context context, List<DeskVO> deskList) {
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
            return Integer.parseInt(deskList.get(i).getDek_id().substring(1));
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
            String status = null;
            final String dek_Id = desk.getDek_id();
            switch (desk.getDek_status()) {
                case 0:
                    status = "空桌";
                    break;
                case 1:
                    status = "使用中";
                    break;
                case 2:
                    status = "已訂位";
                    break;
            }

            holder.ivTableImg.setImageResource(R.drawable.table);
            holder.tvTableStatus.setText(status);
            holder.tvTableNo.setText(dek_Id);
            holder.ivTableImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent intent = new Intent(getActivity(), DeliveryDetailActivity.class);
                    Intent intent = new Intent(getActivity(), OrderAddActivity.class);
                    Bundle bundle = new Bundle();
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


}
