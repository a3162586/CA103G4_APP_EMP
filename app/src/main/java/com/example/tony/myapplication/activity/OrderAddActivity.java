package com.example.tony.myapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tony.myapplication.OrderInvoiceVO;
import com.example.tony.myapplication.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OrderAddActivity extends AppCompatActivity {
    private TextView tvDeskNum;
    private Button btnMenuCancel,btnMenuOk;
    private ListView menuDetail;
    private OrderInvoiceAdapter adapter;
    private String branch_No,dek_No;
    private List<OrderInvoiceVO> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_add);

        tvDeskNum = findViewById(R.id.tvDeskNum);
        btnMenuCancel = findViewById(R.id.btnMenuCancel);
        btnMenuOk = findViewById(R.id.btnMenuOk);
        menuDetail = findViewById(R.id.MenuDetail);

        Bundle bundle = this.getIntent().getExtras();
        final String dek_Id = bundle.getString("dek_Id");
        branch_No = bundle.getString("branch_No");
        dek_No = bundle.getString("dek_No");
        tvDeskNum.setText("桌位"+dek_Id);

        // 加入上下格線，初始化orderList
        menuDetail.addHeaderView(new View(this));
        menuDetail.addFooterView(new View(this));
        initOrderList(new ArrayList<OrderInvoiceVO>());

        // 返回上一頁
        btnMenuCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderAddActivity.this.finish();
            }
        });
        // 進入訂單確認頁面
        btnMenuOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderAddActivity.this,OrderConfirmActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("dek_Id",dek_Id);
                bundle.putString("branch_No",branch_No);
                bundle.putString("dek_No",dek_No);
                bundle.putSerializable("orderList", (Serializable) orderList);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private class OrderInvoiceAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;
        private List<OrderInvoiceVO> orderList;
        private OrderInvoiceAdapter(Context context, List<OrderInvoiceVO> orderList) {
            this.orderList = orderList;
            layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return orderList.size();
        }

        @Override
        public Object getItem(int position) {
            return orderList.get(position);
        }

        @Override
        public long getItemId(int position) {
//            return Integer.parseInt(orderList.get(position).getInvo_No());
            return position;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {

            ViewHolder holder;

            if (view == null) {
                holder = new ViewHolder();
                view = layoutInflater.inflate(R.layout.listview_menu, viewGroup, false);
                holder.Invo_No = view.findViewById(R.id.Invo_No);
                holder.Menu_Id = view.findViewById(R.id.Menu_Id);
                holder.ivDelete = view.findViewById(R.id.ivDelete);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            final OrderInvoiceVO orderInvoice = orderList.get(position);
            holder.Invo_No.setText(Integer.toString(position+1));
            holder.Menu_Id.setText(orderInvoice.getMenu_Id());
            holder.ivDelete.setImageResource(orderInvoice.getDeleteIcon());

            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    orderList.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(OrderAddActivity.this,orderInvoice.getMenu_Id()+"已刪除",Toast.LENGTH_SHORT).show();
                }
            });

            return view;
        }

        private class ViewHolder {
            ImageView ivDelete;
            TextView Invo_No, Menu_Id;
        }
    }

    // 初始化orderList
    public void initOrderList(List<OrderInvoiceVO> orderList) {
        for(int i=1; i<=5; i++)
            orderList.add(new OrderInvoiceVO("", "","",null));
        this.orderList = orderList;
        adapter = new OrderInvoiceAdapter(this,orderList);
        menuDetail.setAdapter(adapter);
    }


    // 刷新orderList
    public void setOrderList(List<OrderInvoiceVO> orderList) {
        this.orderList = orderList;
        adapter = new OrderInvoiceAdapter(this,orderList);
        menuDetail.setAdapter(adapter);
    }


}
