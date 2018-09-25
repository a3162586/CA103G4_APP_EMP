package com.example.tony.myapplication.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.example.tony.myapplication.DeskVO;
import com.example.tony.myapplication.OrderInvoiceVO;
import com.example.tony.myapplication.R;
import com.example.tony.myapplication.main.Util;
import com.example.tony.myapplication.task.CommonTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
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

        rvServe = view.findViewById(R.id.rvDelivery);
        rvServe.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvServe.setLayoutManager(layoutManager);
        rvServe.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        spServeDeskSearch = view.findViewById(R.id.spServeDeskSearch);



//        spServeDeskSearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                String desk = spServeDeskSearch.getSelectedItem().toString();
//                Set<String> set = new LinkedHashSet<>();
//                ArrayAdapter<String> adapter;
//                switch (searchMode) {
//                    case "員工編號":
//
//                        for(DeliveryVO deliveryVO : deliveryList) {
//                            if(deliveryVO.getEmp_no() != null)
//                                set.add(deliveryVO.getEmp_no());
//                        }
//                        String[] empNo = set.toArray(new String[set.size()]);
//
//                        // ArrayAdapter用來管理整個選項的內容與樣式，android.R.layout.simple_spinner_item為內建預設樣式
//                        adapter = new ArrayAdapter<>
//                                (getActivity(), android.R.layout.simple_spinner_item, empNo);
////                        // android.R.layout.simple_spinner_dropdown_item為內建下拉選單樣式
//                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        spDeliverySearchOption.setAdapter(adapter);
//                        spDeliverySearchOption.setSelection(0, true);
//                        break;
//                    case "派送單編號":
//
//                        for(DeliveryVO deliveryVO : deliveryList)
//                            set.add(deliveryVO.getDeliv_no());
//                        String[] deliveryNo = set.toArray(new String[set.size()]);
//
//                        // ArrayAdapter用來管理整個選項的內容與樣式，android.R.layout.simple_spinner_item為內建預設樣式
//                        adapter = new ArrayAdapter<>
//                                (getActivity(), android.R.layout.simple_spinner_item, deliveryNo);
////                        // android.R.layout.simple_spinner_dropdown_item為內建下拉選單樣式
//                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        spDeliverySearchOption.setAdapter(adapter);
//                        spDeliverySearchOption.setSelection(0, true);
//                        break;
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//                // nothing to do
//            }
//
//        });
//
//        spDeliverySearchOption = view.findViewById(R.id.spDeliverySearchOption);
//        spDeliverySearchOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                String searchMode = spDeliverySearchMode.getSelectedItem().toString();
//                String searchOption = spDeliverySearchOption.getSelectedItem().toString();
//                JsonObject jsonObject = new JsonObject();
//                if("員工編號".equals(searchMode)) {
//                    Log.e(TAG,searchMode);
//                    jsonObject.addProperty("action", "getEmpNo");
//                    jsonObject.addProperty("emp_no", searchOption);
//                }
//                else if("派送單編號".equals(searchMode)) {
//                    jsonObject.addProperty("action", "getDelivNo");
//                    jsonObject.addProperty("deliv_no", searchOption);
//                }
//                String jsonOut = jsonObject.toString();
//                updateUI(jsonOut);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//                // nothing to do
//            }
//        });


        // check if the device connect to the network
        if (Util.networkConnected(getActivity())) {

            //宣告JasonObject物件，利用getDeliveryTask非同步任務連線到Servlet的 if ("getAll".equals(action))
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getDeskByOrderTypeAndStatus");
            jsonObject.addProperty("orderType", "0");
            jsonObject.addProperty("orderStatus", "1");
            String jsonOut = jsonObject.toString();
            getServeTask = new CommonTask(Util.URL + "AndroidOrderformServlet", jsonOut);

            try {

                //將getServeTask回傳的result重新轉型回List<DeskVO>物件
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

        return view;

    }

}
