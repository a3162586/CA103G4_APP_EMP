package com.example.tony.myapplication.activity;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tony.myapplication.CouponVO;
import com.example.tony.myapplication.OrderInvoiceVO;
import com.example.tony.myapplication.OrderformVO;
import com.example.tony.myapplication.R;
import com.example.tony.myapplication.main.Util;
import com.example.tony.myapplication.task.CommonTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class OrderConfirmActivity extends AppCompatActivity {

    private final static String TAG = "OrderConfirmActivity";
    private RecyclerView rvOrderDetail;
    private ImageView ivQrcode;
    private TextView tvQrcode,tvDeskNum,tvTotalAmount,tvDiscountTotalAmount;
    private Button btnMenuModify,btnMenuSubmit;
    private static final String PACKAGE = "com.google.zxing.client.android";
    private List<OrderInvoiceVO> orderList;
    private Gson gson = new Gson();
    private CommonTask orderAddTask,getCouponTask;
    private int totalAmount,discountTotalAmount;
    private CouponVO coupon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);
        findViews();
    }

    private void findViews() {

        // 取得上個頁面傳入的桌位編號、分店編號、桌位流水號、餐點明細List資料
        Bundle bundle = this.getIntent().getExtras();
        final String dek_Id = bundle.getString("dek_Id");
        final String branch_No = bundle.getString("branch_No");
        final String dek_No = bundle.getString("dek_No");
        totalAmount = bundle.getInt("totalAmount");
        discountTotalAmount = totalAmount;
        orderList = (List<OrderInvoiceVO>) bundle.getSerializable("orderList");

        tvDeskNum = findViewById(R.id.tvDeskNum);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvDiscountTotalAmount = findViewById(R.id.tvDiscountTotalAmount);
        ivQrcode = findViewById(R.id.ivQrcode);
        tvQrcode = findViewById(R.id.tvQrcode);
        btnMenuModify = findViewById(R.id.btnMenuModify);
        btnMenuSubmit = findViewById(R.id.btnMenuSubmit);
        rvOrderDetail = findViewById(R.id.rvOrderDetail);

        rvOrderDetail.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvOrderDetail.setLayoutManager(layoutManager);

        // 設定分隔線樣式
        rvOrderDetail.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        rvOrderDetail.setAdapter(new OrderAdapter(orderList));

        tvDeskNum.setText("桌位"+dek_Id);
        tvTotalAmount.setText("$"+Integer.toString(totalAmount));

        // 切換至掃描qrcode頁面
        ivQrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                try {
                    startActivityForResult(intent, 0);
                }
                // 如果沒有安裝Barcode Scanner，就跳出對話視窗請user安裝
                catch (ActivityNotFoundException ex) {
                    showDownloadDialog();
                }
            }
        });

        // 返回上一頁
        btnMenuModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderConfirmActivity.this.finish();
            }
        });

        // 訂單確認送出
        btnMenuSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Util.networkConnected(OrderConfirmActivity.this)) {
                    String url = Util.URL + "AndroidOrderformServlet";

                    OrderformVO order = new OrderformVO();
//                    int sum = 0;
//                    for (OrderBook book : CART) {
//                        sum += book.getPrice() * book.getQuantity();
//                    }
//                    order.setAmount(sum);
                    order.setDek_no(dek_No);
                    order.setBranch_no(branch_No);
                    order.setOrder_type(0);
                    if(discountTotalAmount != totalAmount)
                        order.setOrder_price(discountTotalAmount);
                    else
                        order.setOrder_price(totalAmount);
                    order.setOrder_status(1);
                    order.setOrder_pstatus(1);
                    order.setOrderList(orderList);

                    // 宣告JasonObject物件，利用orderAddTask非同步任務連線到Servlet的 if ("add".equals(action))
                    String ordStr = gson.toJson(order);
                    Log.e("ordStr", ordStr);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "add");
                    jsonObject.addProperty("order", ordStr);
                    try {
                        jsonObject.addProperty("coupSn", coupon.getCoup_Sn());
                    } catch (NullPointerException ne) {
                        jsonObject.addProperty("coupSn", "");
                    }

                    String jsonOut = jsonObject.toString();
                    orderAddTask = new CommonTask(url, jsonOut);

                    // 訂單新增成功會轉換至MainActivity頁面，失敗則show出FailCreateOrder訊息
                    OrderformVO successOrder = null;
                    try {

                        // 回傳的Json字串以OrderformVO格式解析
                        String result = orderAddTask.execute().get();
                        successOrder = gson.fromJson(result, OrderformVO.class);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }

                    if (successOrder == null) {
                        Util.showToast(OrderConfirmActivity.this, R.string.msg_FailCreateOrder);
                    } else {
                        Intent intent = new Intent(OrderConfirmActivity.this, MainActivity.class);
                        Bundle bundle = new Bundle();
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }

            }
        });
    }

    private class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

        OrderAdapter(List<OrderInvoiceVO> orderList) {
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView serialNum,meals_No,meals_Name,meals_Price,meals_Count;

            ViewHolder(View view) {
                super(view);
                serialNum = view.findViewById(R.id.serialNum);
                meals_No = view.findViewById(R.id.meals_No);
                meals_Name = view.findViewById(R.id.meals_Name);
                meals_Price = view.findViewById(R.id.meals_Price);
                meals_Count = view.findViewById(R.id.meals_Count);
            }
        }

        @NonNull
        @Override
        public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_order, parent, false);
            return new OrderAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, int position) {

            final OrderInvoiceVO orderInvoiceVO = orderList.get(position);

            holder.serialNum.setText(Integer.toString(position+1));
            holder.meals_No.setText(orderInvoiceVO.getMenu_No());
            holder.meals_Name.setText(orderInvoiceVO.getMenu_Id());
            if(orderInvoiceVO.getMenu_Price() != null)
                holder.meals_Price.setText("$"+Integer.toString(orderInvoiceVO.getMenu_Price()));
            holder.meals_Count.setText(Integer.toString(CalQuantity()));
        }

        @Override
        public int getItemCount() {
            return orderList.size();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            String message = "";
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                message = contents;
                getCoupon(message);
            } else if (resultCode == RESULT_CANCELED) {
//                message = "Scan was Cancelled!";
                message = "M-00000000001";
            }
            getCoupon(message);
            tvQrcode.setText(message);
        }
    }

    private void showDownloadDialog() {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(this);
        downloadDialog.setTitle("No Barcode Scanner Found");
        downloadDialog.setMessage("Please download and install Barcode Scanner!");
        downloadDialog.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Uri uri = Uri.parse("market://search?q=pname:" + PACKAGE);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        try {
                            startActivity(intent);
                        } catch (ActivityNotFoundException ex) {
                            Log.e(ex.toString(),
                                    "Play Store is not installed; cannot install Barcode Scanner");
                        }
                    }
                });
        downloadDialog.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        downloadDialog.show();
    }

    private void getCoupon(String coupSn) {

        if (Util.networkConnected(this)) {

            //宣告JasonObject物件，利用getCouponTask非同步任務連線到Servlet的 if ("getAll".equals(action))
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getCouponByCoupSn");
            jsonObject.addProperty("coupSn", coupSn);
            String jsonOut = jsonObject.toString();
            getCouponTask = new CommonTask(Util.URL + "AndroidCouponServlet", jsonOut);

            try {

                //將getCouponTask回傳的result重新轉型回CouponVO物件
                String jsonIn = getCouponTask.execute().get();
                Type listType = new TypeToken<CouponVO>() {
                }.getType();
                coupon = gson.fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (coupon == null) {
                Util.showToast(this, R.string.msg_CouponNotFound);
            } else {
                discountTotalAmount = totalAmount;
                discountTotalAmount -= coupon.getCoucatVO().getCoucat_Value();
                tvTotalAmount.setPaintFlags(tvTotalAmount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvDiscountTotalAmount.setText("$"+Integer.toString(discountTotalAmount));
            }

        } else {
            Util.showToast(this, R.string.msg_NoNetwork);
        }

    }

    // 計算同類餐點數量
    private int CalQuantity() {
        return 1;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (orderAddTask != null) {
            orderAddTask.cancel(true);
        }
    }
}
