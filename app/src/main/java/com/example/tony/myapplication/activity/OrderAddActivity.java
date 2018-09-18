package com.example.tony.myapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tony.myapplication.OrderInvoiceVO;
import com.example.tony.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class OrderAddActivity extends AppCompatActivity {
    private Button btnMenuCancel,btnMenuOk;
    private ListView menuDetail;
    private List<OrderInvoiceVO> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_add);

        initOrderInvoice();

        btnMenuCancel = findViewById(R.id.btnMenuCancel);
        btnMenuOk = findViewById(R.id.btnMenuOk);
        menuDetail = findViewById(R.id.MenuDetail);
        final OrderInvoiceAdapter adapter = new OrderInvoiceAdapter(this,orderList);
        menuDetail.setAdapter(adapter);
        menuDetail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //新增item點擊事件
            }
        });
        //返回上一頁
        btnMenuCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderAddActivity.this.finish();
            }
        });
        //進入訂單確認頁面
        btnMenuOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderAddActivity.this,OrderConfirmActivity.class);
                Bundle bundle = new Bundle();
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void initOrderInvoice() {

        orderList = new ArrayList<>();
        orderList.add(new OrderInvoiceVO("01", "好吃的拉麵",R.drawable.ic_delete_black_24dp ));
        orderList.add(new OrderInvoiceVO("02", "難吃的拉麵",R.drawable.ic_delete_black_24dp ));
        orderList.add(new OrderInvoiceVO("03", "普通的拉麵",R.drawable.ic_delete_black_24dp ));
        orderList.add(new OrderInvoiceVO("04", "很辣的拉麵",R.drawable.ic_delete_black_24dp ));
        orderList.add(new OrderInvoiceVO("05", "酸酸的拉麵",R.drawable.ic_delete_black_24dp ));
        orderList.add(new OrderInvoiceVO("06", "好吃的拉麵",R.drawable.ic_delete_black_24dp ));
        orderList.add(new OrderInvoiceVO("07", "難吃的拉麵",R.drawable.ic_delete_black_24dp ));
        orderList.add(new OrderInvoiceVO("08", "普通的拉麵",R.drawable.ic_delete_black_24dp ));
        orderList.add(new OrderInvoiceVO("09", "很辣的拉麵",R.drawable.ic_delete_black_24dp ));
        orderList.add(new OrderInvoiceVO("10", "酸酸的拉麵",R.drawable.ic_delete_black_24dp ));

    }

    private class OrderInvoiceAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;
        private List<OrderInvoiceVO> orderList;
        public OrderInvoiceAdapter(Context context, List<OrderInvoiceVO> orderList) {
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
            return Integer.parseInt(orderList.get(position).getInvo_No());
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
            holder.Invo_No.setText(orderInvoice.getInvo_No());
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


}
