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

import com.example.tony.myapplication.AddressVO;
import com.example.tony.myapplication.DeliveryVO;
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
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.google.android.gms.location.LocationServices.API;

public class DeliveryDetailActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private RecyclerView rvOrderDetail;
    private final static String TAG = "DeliveryDetailActivity";
    private View view;
    private CommonTask getDeliveryTask;

    private static final int MY_REQUEST_CODE = 0;
    private static final int REQUEST_CODE_RESOLUTION = 1;
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private GoogleApiClient googleApiClient;
    private Location location;

    private List<AddressVO> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_detail);

        // check if the device connect to the network
        if (Util.networkConnected(this)) {

            //宣告JasonObject物件，利用getMenuTask非同步任務連線到Servlet的 if ("getAll".equals(action))
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            String jsonOut = jsonObject.toString();
            getDeliveryTask = new CommonTask(Util.URL + "AndroidDeliveryServlet", jsonOut);

            List<DeliveryVO> deliveryList = null;
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
                Util.showToast(this, R.string.msg_DeliveryNotFound);
            } else {
                showResult(deliveryList);
            }

        } else {
            Util.showToast(this, R.string.msg_NoNetwork);
        }

        initAddress();
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

        // 取得自己位置的緯經度
        sb.append(location.getLatitude()+","+location.getLongitude()+"/");

        for(AddressVO addressVO : list) {

            String locationName = addressVO.getAddress();

            if (location == null || locationName.isEmpty())
                return;

            Address address = getAddress(locationName);
            if (address == null) {
                Toast.makeText(this, getString(R.string.msg_LocationNotAvailable), Toast.LENGTH_SHORT).show();
                return;
            }

            //  取得派送單內所有訂單的送餐位置緯經度
            sb.append(address.getLatitude()+","+address.getLongitude()+"/");

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

        private List<DeliveryVO> deliveryList;

        public DeliveryAdapter(List<DeliveryVO> deliveryList) {
            this.deliveryList = deliveryList;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView order_No,delivery_Address;

            public ViewHolder(View view) {
                super(view);
                order_No = view.findViewById(R.id.order_No);
                delivery_Address = view.findViewById(R.id.delivery_Address);
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

            final DeliveryVO delivery = deliveryList.get(position);
            holder.order_No.setText(delivery.getDeliv_no());
            holder.delivery_Address.setText(delivery.getEmp_no());

        }

        @Override
        public int getItemCount() {
            return deliveryList.size();
        }
    }

    public void showResult(List<DeliveryVO> result) {

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

    public void initAddress() {
        list.add(new AddressVO("桃園市桃園區慈華街24巷5號"));
        list.add(new AddressVO("桃園市桃園區寶慶路47號"));
        list.add(new AddressVO("桃園市中壢區中和路102號"));
    }
}
