package com.example.tony.myapplication.activity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tony.myapplication.OrderInvoiceWithMenuVO;
import com.example.tony.myapplication.OrderformVO;
import com.example.tony.myapplication.R;
import com.example.tony.myapplication.main.Util;
import com.example.tony.myapplication.task.CommonTask;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static com.google.android.gms.location.LocationServices.API;

public class DeliveryDetailActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private RecyclerView rvOrderDetail;
    private TextView tvDelivNo,tvEmpName;
    private final static String TAG = "DeliveryDetailActivity";
    private CommonTask getDeliveryTask,getEmpNameTask;

    private static final int MY_REQUEST_CODE = 0;
    private static final int REQUEST_CODE_RESOLUTION = 1;
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private GoogleApiClient googleApiClient;
    private Location location;
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

    private List<OrderformVO> orderList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_detail);
        tvDelivNo = findViewById(R.id.tvDelivNo);
        tvEmpName = findViewById(R.id.tvEmpName);

        // 取得上個頁面傳入的派送單編號、員工編號資料
        Bundle bundle = this.getIntent().getExtras();
        String delivNo = bundle.getString("delivNo");
        String empNo = bundle.getString("empNo");
        tvDelivNo.setText(delivNo);
        tvEmpName.setText(changeEmpNoToEmpName(empNo));

        // check if the device connect to the network
        if (Util.networkConnected(this)) {

            //宣告JasonObject物件，利用getDeliveryTask非同步任務連線到Servlet的 if ("getAll".equals(action))
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getOrderByDelivNo");
            jsonObject.addProperty("delivNo", delivNo);
            String jsonOut = jsonObject.toString();
            getDeliveryTask = new CommonTask(Util.URL + "AndroidOrderformServlet", jsonOut);

            try {

                //將getDeliveryTask回傳的result重新轉型回List<DeliveryVO>物件
                String jsonIn = getDeliveryTask.execute().get();
                Type listType = new TypeToken<List<OrderformVO>>() {
                }.getType();
                orderList = gson.fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (orderList == null || orderList.isEmpty()) {
                Util.showToast(this, R.string.msg_DeliveryNotFound);
            } else {
                showResult(orderList);
            }

        } else {
            Util.showToast(this, R.string.msg_NoNetwork);
        }

//        initAddress();
    }

    @Override
    // 連上Google Play Services，系統呼叫此方法
    public void onConnected(Bundle bundle) {
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                // 兩次定位間隔時間為10000毫秒
                .setInterval(10000)
                // 兩次定位之間最小距離間隔為1000公尺
                .setSmallestDisplacement(1000);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);


    }

    @Override
    // 當連線Google Play Services發生暫停時，系統呼叫此方法
    public void onConnectionSuspended(int cause) {
        Toast.makeText(this, getString(R.string.msg_GoogleApiClientConnectionSuspended), Toast.LENGTH_SHORT).show();
    }

    @Override
    // 當連線Google Play Services失敗時，系統呼叫此方法
    public void onConnectionFailed(ConnectionResult result) {
        Toast.makeText(this, getString(R.string.msg_GoogleApiClientConnectionFailed), Toast.LENGTH_SHORT).show();
        if (!result.hasResolution()) {
            GoogleApiAvailability availability = GoogleApiAvailability.getInstance();
            availability.getErrorDialog(this, result.getErrorCode(), REQUEST_RESOLVE_ERROR).show();
            return;
        }

        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException sie) {
            Log.e(TAG, sie.toString());
        }
    }

    @Override
    // LocationListener：當發生位置改變時，系統呼叫此方法
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    // 導航功能
    public void onDirectClick(View view) {

        StringBuilder sb = new StringBuilder();
        float[] results = new float[1];
        Map<Float, String> map = new TreeMap<>();

        // 取得自己位置的緯經度
        sb.append(location.getLatitude()+","+location.getLongitude()+"/");

        for(OrderformVO orderformVO : orderList) {

            String locationName = orderformVO.getDeliv_addres();

            if (location == null || locationName.isEmpty())
                return;

            Address address = getAddress(locationName);
            if (address == null) {
                Toast.makeText(this, getString(R.string.msg_LocationNotAvailable), Toast.LENGTH_SHORT).show();
                return;
            }

            // 計算自己位置與送餐位置，此2點間的距離(公尺)，結果會存入results[0]
            Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                    address.getLatitude(), address.getLongitude(), results);
            String distance = NumberFormat.getInstance().format(results[0]);
            Log.e(TAG,distance);

            //  取得送餐位置緯經度並與距離參數一起加入map之中
            map.put(results[0],address.getLatitude()+","+address.getLongitude()+"/");

        }

        // 利用treemap特性將key值(距離參數)作升序排序
        for (Map.Entry<Float, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
            sb.append(entry.getValue());
        }

        direct(sb.toString());
    }

    // 將使用者輸入的地名或地址轉成Address物件
    private Address getAddress(String locationName) {
        Geocoder geocoder = new Geocoder(this);
        List<Address> addressList = null;
        try {
            // 解譯地名/地址後可能產生多筆位置資訊，但限定回傳1筆
            addressList = geocoder.getFromLocationName(locationName, 1);
        } catch (IOException ie) {
            Log.e(TAG, ie.toString());
        }

        if (addressList == null || addressList.isEmpty())
            return null;
        else
            // 因為當初限定只回傳1筆，所以只要取得第1個Address物件即可
            return addressList.get(0);
    }

    // 開啟Google地圖應用程式來完成導航要求
    private void direct(String formatURL) {

        // 設定欲前往的Uri，/dir/出發地緯經度/.../.../目的地緯經度
        String uriStr = String.format(Locale.TAIWAN,
//                "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f",
//                "https://www.google.com/maps/dir/24.953972,121.225971/25.0109239,121.2942541/24.778289,120.988108/");
                "https://www.google.com/maps/dir/"+formatURL);

        Log.e(TAG,uriStr);
        Intent intent = new Intent();
        // 指定交由Google地圖應用程式接手
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        // ACTION_VIEW-呈現資料給使用者觀看
        intent.setAction(Intent.ACTION_VIEW);
        // 將Uri資訊附加到Intent物件上
        intent.setData(Uri.parse(uriStr));
        startActivity(intent);
    }

    private void askPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        Set<String> permissionsRequest = new HashSet<>();
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionsRequest.add(permission);
            }
        }

        if (!permissionsRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionsRequest.toArray(new String[permissionsRequest.size()]),
                    MY_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_REQUEST_CODE:
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        String text = getString(R.string.text_ShouldGrant);
                        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }

                break;
        }
    }

    private class DeliveryAdapter extends RecyclerView.Adapter<DeliveryDetailActivity.DeliveryAdapter.ViewHolder> {

        private List<OrderformVO> orderList;

        public DeliveryAdapter(List<OrderformVO> orderList) {
            this.orderList = orderList;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView order_No,order_Price,delivery_Address,mem_No,mem_Name,mem_Id;

            public ViewHolder(View view) {
                super(view);
                order_No = view.findViewById(R.id.order_No);
                order_Price = view.findViewById(R.id.order_Price);
                delivery_Address = view.findViewById(R.id.delivery_Address);
                mem_No = view.findViewById(R.id.mem_No);
                mem_Name = view.findViewById(R.id.mem_Name);
                mem_Id = view.findViewById(R.id.mem_Id);
            }
        }

        @NonNull
        @Override
        public DeliveryDetailActivity.DeliveryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_delivery, parent, false);
            return new DeliveryDetailActivity.DeliveryAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DeliveryDetailActivity.DeliveryAdapter.ViewHolder holder, int position) {

            final OrderformVO order = orderList.get(position);
            holder.order_No.setText("訂單單號 : "+order.getOrder_no());
            holder.order_Price.setText("餐點金額 : $"+Integer.toString(order.getOrder_price()));
            holder.delivery_Address.setText("送餐地址 : "+order.getDeliv_addres());
            holder.mem_No.setText("會員編號 : "+order.getMem_no());
            holder.mem_Name.setText("會員名稱 : "+order.getMemVO().getMem_Name());

            List<OrderInvoiceWithMenuVO> orderInvoiceList;
            StringBuilder memIdStr = new StringBuilder();
            int count = 0;
            try {
                orderInvoiceList = order.getOrderList2();
                for(OrderInvoiceWithMenuVO oiwmVO : orderInvoiceList) {
                    if(count==0)
                        memIdStr.append("訂購餐點 : "+oiwmVO.getMenuVO().getMenu_Id()+"  X 1"+"\n");
                    else
                        memIdStr.append("　　　　   "+oiwmVO.getMenuVO().getMenu_Id()+"  X 1"+"\n");
                    count++;
                }
                holder.mem_Id.setText(memIdStr.substring(0,memIdStr.length()-2));
            } catch (Exception e) {
                holder.mem_Id.setText("mem_Id");
            }

        }

        @Override
        public int getItemCount() {
            return orderList.size();
        }
    }

    public void showResult(List<OrderformVO> result) {

        rvOrderDetail = findViewById(R.id.rvOrderDetail);
        rvOrderDetail.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvOrderDetail.setLayoutManager(layoutManager);
        rvOrderDetail.setAdapter(new DeliveryDetailActivity.DeliveryAdapter(result));

    }

    @Override
    protected void onStart() {
        super.onStart();
        askPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        googleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (getDeliveryTask != null) {
            getDeliveryTask.cancel(true);
            getDeliveryTask = null;
        }
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
    }

    public String changeEmpNoToEmpName(String empNo){

        String empName = null;

        // check if the device connect to the network
        if (Util.networkConnected(this)) {

            //宣告JasonObject物件，利用getDeliveryTask非同步任務連線到Servlet的 if ("getAll".equals(action))
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getEmpNameByEmpNo");
            jsonObject.addProperty("empNo", empNo);
            String jsonOut = jsonObject.toString();
            getEmpNameTask = new CommonTask(Util.URL + "AndroidEmployeeServlet", jsonOut);

            try {

                //將getDeliveryTask回傳的result重新轉型回List<DeliveryVO>物件
                String jsonIn = getEmpNameTask.execute().get();
                Type listType = new TypeToken<String>() {
                }.getType();
                empName = gson.fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (empName == null || empName.isEmpty()) {
                return "NoName";
            }

        } else {
            Util.showToast(this, R.string.msg_NoNetwork);
        }
        return empName;
    }
}
