package com.example.tony.myapplication.fragment;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tony.myapplication.MenuVO;
import com.example.tony.myapplication.R;
import com.example.tony.myapplication.Util;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MenuInfoFragment extends Fragment {

    private RecyclerView rvMenuInfo;
    private int position;
    private final static String TAG = "MenuInfoFragment";
    private View view;
    private AsyncTask retrieveMenuTask;

    public MenuInfoFragment() {
    }

    class RetrieveMenuTask extends AsyncTask<String , Integer, List<MenuVO>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<MenuVO> doInBackground(String... params) {
            String url = params[0];
            String param = params[1];
            String jsonIn;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("param", param);
            jsonIn = getRemoteData(url, jsonObject.toString());
            Gson gson = new Gson();
            Type listType = new TypeToken<List<MenuVO>>() {
            }.getType();
            return gson.fromJson(jsonIn, listType);
        }

        @Override
        protected void onPostExecute(List<MenuVO> result) {
            showResult(result);

        }

    }

    public int getPosition() {
        return position;
    }

    private void showToast(Context context, int messageId) {
        Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (container == null) {
            return null;
        }

        view = inflater.inflate(R.layout.fragment_menu_info, container, false);

        // check if the device connect to the network
        if (networkConnected()) {
            retrieveMenuTask = new RetrieveMenuTask().execute(Util.URL, "ALL");
        } else {
            showToast(getActivity(), R.string.msg_NoNetwork);
        }


        Bundle bundle = this.getArguments();
        position = bundle.getInt("position");

        return view;
    }

    private class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

        private List<MenuVO> menuList;

        public MenuAdapter(List<MenuVO> menuList) {
            this.menuList = menuList;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView ivMenu_Photo;
            private TextView tvMenu_ID,tvMenu_Price;

            public ViewHolder(View view) {
                super(view);
                ivMenu_Photo = view.findViewById(R.id.ivMenu_Photo);
                tvMenu_ID = view.findViewById(R.id.tvMenu_ID);
                tvMenu_Price = view.findViewById(R.id.tvMenu_Price);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_menu, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            final MenuVO menu = menuList.get(position);
            holder.tvMenu_ID.setText(menu.getMenu_Id());
            holder.tvMenu_Price.setText(Integer.toString(menu.getMenu_Price()));

            byte[] imageBytes = Base64.decode(menu.getMenu_Photo(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holder.ivMenu_Photo.setImageBitmap(bitmap);

            holder.tvMenu_ID.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(),menu.getMenu_Id(),Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return menuList.size();
        }
    }

    // check if the device connect to the network
    private boolean networkConnected() {
        ConnectivityManager conManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private String getRemoteData(String url, String outStr) {
        HttpURLConnection connection = null;
        StringBuilder inStr = new StringBuilder();
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true); // allow inputs
            connection.setDoOutput(true); // allow outputs
            // 不知道請求內容大小時可以呼叫此方法將請求內容分段傳輸，設定0代表使用預設大小
            connection.setChunkedStreamingMode(0);
            connection.setUseCaches(false); // do not use a cached copy
            connection.setRequestMethod("POST");
            connection.setRequestProperty("charset", "UTF-8");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            bw.write(outStr);
            Log.d(TAG, "output: " + outStr);
            bw.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    inStr.append(line);
                }
            } else {
                Log.d(TAG, "response code: " + responseCode);
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        Log.d(TAG, "input: " + inStr);
        return inStr.toString();
    }

    public void showResult(List<MenuVO> result) {

        rvMenuInfo = view.findViewById(R.id.rvMenuInfo);
        rvMenuInfo.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvMenuInfo.setLayoutManager(layoutManager);
        rvMenuInfo.setAdapter(new MenuAdapter(result));

    }

    @Override
    public void onPause() {
        if (retrieveMenuTask != null) {
            retrieveMenuTask.cancel(true);
            retrieveMenuTask = null;
        }
        super.onPause();
    }

}
