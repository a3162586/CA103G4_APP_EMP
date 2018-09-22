package com.example.tony.myapplication.fragment;


import android.content.Intent;
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
import android.widget.Toast;

import com.example.tony.myapplication.DeliveryVO;
import com.example.tony.myapplication.R;
import com.example.tony.myapplication.activity.DeliveryDetailActivity;
import com.example.tony.myapplication.main.Util;
import com.example.tony.myapplication.task.CommonTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class DeliveryFragment extends Fragment {

    private RecyclerView rvDelivery;
    private Spinner spDeliverySearchMode,spDeliverySearchOption;
    private final static String TAG = "DeliveryFragment";
    private View view;
    private CommonTask getDeliveryTask;
    private List<DeliveryVO> deliveryList = null;

    public DeliveryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (container == null) {
            return null;
        }

        view = inflater.inflate(R.layout.fragment_delivery, container, false);
        spDeliverySearchMode = view.findViewById(R.id.spDeliverySearchMode);
        spDeliverySearchOption = view.findViewById(R.id.spDeliverySearchOption);
        spDeliverySearchMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String searchMode = spDeliverySearchMode.getSelectedItem().toString();
                Set<String> set = new LinkedHashSet<>();
                for(DeliveryVO deliveryVO : deliveryList) {
                    if(deliveryVO.getEmp_no() != null)
                        set.add(deliveryVO.getEmp_no());
                }
                String[] empNo = set.toArray(new String[set.size()]);
                switch (searchMode) {
                    case "員工編號":
                        // ArrayAdapter用來管理整個選項的內容與樣式，android.R.layout.simple_spinner_item為內建預設樣式
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                                (getActivity(), android.R.layout.simple_spinner_item, empNo);
//                        // android.R.layout.simple_spinner_dropdown_item為內建下拉選單樣式
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spDeliverySearchOption.setAdapter(adapter);
                        spDeliverySearchOption.setSelection(0, true);
                        spDeliverySearchOption.setOnItemSelectedListener(listener);
                        break;
                    case "派送單編號":

                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //nothing to do
            }
        });

        // check if the device connect to the network
        if (Util.networkConnected(getActivity())) {

            //宣告JasonObject物件，利用getDeliveryTask非同步任務連線到Servlet的 if ("getAll".equals(action))
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            String jsonOut = jsonObject.toString();
            getDeliveryTask = new CommonTask(Util.URL + "AndroidDeliveryServlet", jsonOut);

            try {

                //將getDeliveryTask回傳的result重新轉型回List<DeliveryVO>物件
                String jsonIn = getDeliveryTask.execute().get();
                Type listType = new TypeToken<List<DeliveryVO>>() {
                }.getType();
                deliveryList = new Gson().fromJson(jsonIn, listType);

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (deliveryList == null || deliveryList.isEmpty()) {
                Util.showToast(getActivity(), R.string.msg_DeliveryNotFound);
            } else {
                showResult(deliveryList);
            }

        } else {
            Util.showToast(getActivity(), R.string.msg_NoNetwork);
        }

        return view;
    }

    private class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.ViewHolder> {

        private List<DeliveryVO> deliveryList;

        public DeliveryAdapter(List<DeliveryVO> deliveryList) {
            this.deliveryList = deliveryList;
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
                deliv_Status.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), DeliveryDetailActivity.class);
                        Bundle bundle = new Bundle();
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_delivery, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            final DeliveryVO delivery = deliveryList.get(position);
            String status = "";
            switch (delivery.getDeliv_status()) {
                case "1":
                    status = "等待派送";
                    break;
                case "2":
                    status = "已派送";
                    holder.deliv_Status.setEnabled(false);
                    break;
                case "3":
                    status = "派送完成";
                    holder.deliv_Status.setEnabled(false);
                    break;
            }

            holder.deliv_No.setText(delivery.getDeliv_no());
            holder.branch_No.setText(delivery.getBranch_no());
            holder.emp_No.setText(delivery.getEmp_no());
            holder.deliv_Status.setText(status);
        }

        @Override
        public int getItemCount() {
            return deliveryList.size();
        }
    }

    public Spinner.OnItemSelectedListener listener = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getActivity(), "test", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // Nothing to do here...
        }
    };

    public void showResult(List<DeliveryVO> result) {

        rvDelivery = view.findViewById(R.id.rvDelivery);
        rvDelivery.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvDelivery.setLayoutManager(layoutManager);
        rvDelivery.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        rvDelivery.setAdapter(new DeliveryAdapter(result));

    }

    private void updateUI(String jsonOut) {
        getDeliveryTask = new CommonTask(Util.URL + "AndroidDeliveryServlet", jsonOut);
        List<DeliveryVO> deliveryList = null;
        try {
            String jsonIn = getDeliveryTask.execute().get();
            Type listType = new TypeToken<List<DeliveryVO>>() {
            }.getType();
            deliveryList = new Gson().fromJson(jsonIn, listType);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        if (deliveryList == null || deliveryList.isEmpty()) {
            Util.showToast(getActivity(), R.string.msg_DeliveryNotFound);
        } else {
            rvDelivery.setAdapter(new DeliveryAdapter(deliveryList));
        }
    }

    @Override
    public void onPause() {
        if (getDeliveryTask != null) {
            getDeliveryTask.cancel(true);
            getDeliveryTask = null;
        }
        super.onPause();
    }

}
