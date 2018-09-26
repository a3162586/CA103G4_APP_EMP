package com.example.tony.myapplication.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.tony.myapplication.DeskVO;
import com.example.tony.myapplication.OrderInvoiceVO;
import com.example.tony.myapplication.OrderInvoiceWithMenuVO;
import com.example.tony.myapplication.R;
import com.example.tony.myapplication.main.Util;
import com.example.tony.myapplication.task.CommonTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ServeFragment extends Fragment {

    private RecyclerView rvServe;
    private Spinner spServeDeskSearch;
    private final static String TAG = "ServeFragment";
    private CommonTask getServeTask;
    private List<OrderInvoiceVO> orderInvoiceList = null;
    private List<DeskVO> deskList = null;

    public ServeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_serve, container, false);

        if (container == null) {
            return null;
        }

        rvServe = view.findViewById(R.id.rvServe);
        rvServe.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvServe.setLayoutManager(layoutManager);
        rvServe.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        spServeDeskSearch = view.findViewById(R.id.spServeDeskSearch);

        // check if the device connect to the network
        if (Util.networkConnected(getActivity())) {

            // 宣告JasonObject物件，利用getDeliveryTask非同步任務連線到Servlet的 if ("getAll".equals(action))
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getDeskByOrderTypeAndStatus");
            jsonObject.addProperty("orderType", "0");
            jsonObject.addProperty("orderStatus", "1");
            String jsonOut = jsonObject.toString();
            getServeTask = new CommonTask(Util.URL + "AndroidOrderformServlet", jsonOut);

            try {

                // 將getServeTask回傳的result重新轉型回List<DeskVO>物件
                String jsonIn = getServeTask.execute().get();
                Type listType = new TypeToken<List<DeskVO>>() {
                }.getType();
                deskList = new Gson().fromJson(jsonIn, listType);

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (deskList == null || deskList.isEmpty())
                Util.showToast(getActivity(), R.string.msg_DeskNotFound);

        } else {
            Util.showToast(getActivity(), R.string.msg_NoNetwork);
        }

        List<String> list = new ArrayList<>();
        list.add("請選擇...");
        ArrayAdapter<String> adapter;

        for(DeskVO deskVO : deskList) {
            if(deskVO.getDek_id() != null)
                list.add(deskVO.getDek_id());
        }
        String[] dek_Id = list.toArray(new String[list.size()]);

        // ArrayAdapter用來管理整個選項的內容與樣式，android.R.layout.simple_spinner_item為內建預設樣式
        adapter = new ArrayAdapter<>
                (getActivity(), android.R.layout.simple_spinner_item, dek_Id);
                        // android.R.layout.simple_spinner_dropdown_item為內建下拉選單樣式
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spServeDeskSearch.setAdapter(adapter);
        spServeDeskSearch.setSelection(0, true);
        spServeDeskSearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String defaultOption = spServeDeskSearch.getSelectedItem().toString();
                if(!"請選擇...".equals(defaultOption)) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "getOrderNoByDekNoAndOrderStatus");
                    jsonObject.addProperty("dekNo", deskList.get(i-1).getDek_no());
                    jsonObject.addProperty("orderStatus", "1");
                    String jsonOut = jsonObject.toString();
                    updateUI(jsonOut);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // nothing to do
            }
        });


        return view;

    }

    private class ServeAdapter extends RecyclerView.Adapter<ServeFragment.ServeAdapter.ViewHolder> {

        private List<OrderInvoiceWithMenuVO> serveList;

        public ServeAdapter(List<OrderInvoiceWithMenuVO> serveList) {
            this.serveList = serveList;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView deliv_No,branch_No,emp_No;
            private Button deliv_Status;

            public ViewHolder(View view) {
                super(view);
                deliv_No = view.findViewById(R.id.deliv_No);
                branch_No = view.findViewById(R.id.branch_No);
                emp_No = view.findViewById(R.id.emp_No);
                deliv_Status = view.findViewById(R.id.deliv_Status);
//                deliv_Status.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent intent = new Intent(getActivity(), DeliveryDetailActivity.class);
//                        Bundle bundle = new Bundle();
//                        intent.putExtras(bundle);
//                        startActivity(intent);
//                    }
//                });
            }
        }

        @NonNull
        @Override
        public ServeFragment.ServeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_serve, parent, false);
            return new ServeFragment.ServeAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ServeFragment.ServeAdapter.ViewHolder holder, int position) {

            final OrderInvoiceWithMenuVO orderInvoiceWithMenuVO = serveList.get(position);

            holder.deliv_No.setText(orderInvoiceWithMenuVO.getOrder_no());
            holder.branch_No.setText(orderInvoiceWithMenuVO.getMenuVO().getMenu_Id());
            holder.emp_No.setText("$" + orderInvoiceWithMenuVO.getMenuVO().getMenu_Price());
            holder.deliv_Status.setText("test");
            holder.deliv_Status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent intent = new Intent(getActivity(), DeliveryDetailActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("delivNo",delivery.getDeliv_no());
//                    bundle.putString("empNo",delivery.getEmp_no());
//                    intent.putExtras(bundle);
//                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return serveList.size();
        }
    }

    private void updateUI(String jsonOut) {
        getServeTask = new CommonTask(Util.URL + "AndroidOrderformServlet", jsonOut);
        List<OrderInvoiceWithMenuVO> serveList = null;
        try {
            String jsonIn = getServeTask.execute().get();
            Type listType = new TypeToken<List<OrderInvoiceWithMenuVO>>() {
            }.getType();
            serveList = new Gson().fromJson(jsonIn, listType);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        if (serveList == null || serveList.isEmpty()) {
            Util.showToast(getActivity(), R.string.msg_DeskNotFound);
        } else {
            rvServe.setAdapter(new ServeFragment.ServeAdapter(serveList));
        }
    }

}
