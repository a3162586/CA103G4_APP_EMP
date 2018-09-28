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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tony.myapplication.DeskVO;
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

    private Button serveUpdate;
    private RecyclerView rvServe;
    private Spinner spServeDeskSearch;
    private final static String TAG = "ServeFragment";
    private CommonTask getServeTask;
    private List<OrderInvoiceWithMenuVO> serveList = null;
    private List<DeskVO> deskList = null;
    private List<String> updateStatusList = new ArrayList<>();
    private Gson gson = new Gson();
    private int itemPosition = 0;

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
        serveUpdate = view.findViewById(R.id.serveUpdate);
        serveUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(serveList != null) {
                    String updateStatusListStr = gson.toJson(updateStatusList);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "updateInvoStatus");
                    jsonObject.addProperty("updateStatusList", updateStatusListStr);
                    String jsonOut = jsonObject.toString();
                    updateInvoStatus(jsonOut);

                    JsonObject jsonObject2 = new JsonObject();
                    jsonObject2.addProperty("action", "getOrderNoByDekNoAndOrderStatus");
                    jsonObject2.addProperty("dekNo", deskList.get(itemPosition).getDek_no());
                    jsonObject2.addProperty("orderStatus", "1");
                    String jsonOut2 = jsonObject2.toString();
                    updateUI(jsonOut2);
                }
            }
        });


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

        List<String> splist = new ArrayList<>();
        splist.add("請選擇...");

        for(DeskVO deskVO : deskList) {
            if(deskVO.getDek_id() != null)
                splist.add(deskVO.getDek_id());
        }
        String[] dek_Id = splist.toArray(new String[splist.size()]);
        spinnerInit(dek_Id);

        return view;

    }

    private class ServeAdapter extends RecyclerView.Adapter<ServeFragment.ServeAdapter.ViewHolder> {

        private List<OrderInvoiceWithMenuVO> serveList;

        public ServeAdapter(List<OrderInvoiceWithMenuVO> serveList) {
            this.serveList = serveList;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView order_No,menu_Name,menu_Price;
            private CheckBox serveCheck;

            public ViewHolder(View view) {
                super(view);
                order_No = view.findViewById(R.id.order_No);
                menu_Name = view.findViewById(R.id.menu_Name);
                menu_Price = view.findViewById(R.id.menu_Price);
                serveCheck = view.findViewById(R.id.serveCheck);
            }
        }

        @NonNull
        @Override
        public ServeFragment.ServeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_serve, parent, false);
            return new ServeFragment.ServeAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ServeFragment.ServeAdapter.ViewHolder holder, final int position) {

            final OrderInvoiceWithMenuVO orderInvoiceWithMenuVO = serveList.get(position);

            holder.order_No.setText(orderInvoiceWithMenuVO.getOrder_no());
            holder.menu_Name.setText(orderInvoiceWithMenuVO.getMenuVO().getMenu_Id());
            holder.menu_Price.setText("$" + orderInvoiceWithMenuVO.getMenuVO().getMenu_Price());
            CheckBox.OnCheckedChangeListener chklistener = new CheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if(holder.serveCheck.isChecked())
                        updateStatusList.add(orderInvoiceWithMenuVO.getInvo_no());
                    else
                        updateStatusList.remove(orderInvoiceWithMenuVO.getInvo_no());
                }
            };
            holder.serveCheck.setOnCheckedChangeListener(chklistener);

        }

        @Override
        public int getItemCount() {
            return serveList.size();
        }
    }

    private void updateUI(String jsonOut) {
        getServeTask = new CommonTask(Util.URL + "AndroidOrderformServlet", jsonOut);
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
            deskList.get(itemPosition).getDek_no();
            rvServe.setAdapter(null);
        } else {
            rvServe.setAdapter(new ServeFragment.ServeAdapter(serveList));
        }
    }

    private void updateInvoStatus(String jsonOut) {
        getServeTask = new CommonTask(Util.URL + "AndroidOrderformServlet", jsonOut);
        String resultCheck = null;
        try {
            String jsonIn = getServeTask.execute().get();
            Type type = new TypeToken<String>() {
            }.getType();
            resultCheck = new Gson().fromJson(jsonIn, type);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        if (resultCheck == null || resultCheck.isEmpty()) {
            Util.showToast(getActivity(), R.string.msg_DeskNotFound);
        } else {
            Toast.makeText(getActivity(), "出餐狀態更新成功!", Toast.LENGTH_SHORT).show();
        }
    }

    private void spinnerInit(String[] dek_Id) {
        // ArrayAdapter用來管理整個選項的內容與樣式，android.R.layout.simple_spinner_item為內建預設樣式
        ArrayAdapter<String> adapter;
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
                    itemPosition = i-1;
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
    }

    @Override
    public void onPause() {
        if (getServeTask != null) {
            getServeTask.cancel(true);
            getServeTask = null;
        }
        super.onPause();
    }

}
