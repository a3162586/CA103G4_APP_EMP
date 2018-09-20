package com.example.tony.myapplication.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tony.myapplication.R;
import com.example.tony.myapplication.main.Util;
import com.example.tony.myapplication.task.CommonTask;
import com.google.gson.JsonObject;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmpId,etPassword;
    private CommonTask isEmployeeTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //將偏好設定檔內的log資訊清空
        SharedPreferences preferences = getSharedPreferences(
                Util.PREF_FILE, MODE_PRIVATE);
        preferences.edit().putBoolean("login", false)
                .putString("emp_Acnum", "")
                .putString("emp_Psw", "").apply();

        findViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //可設定已登入過則無法返回login畫面
//        SharedPreferences preferences = getSharedPreferences(Util.PREF_FILE,
//                MODE_PRIVATE);
//        boolean login = preferences.getBoolean("login", false);
//        if (login) {
//            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(this,MainActivity.class);
//            Bundle bundle = new Bundle();
//            intent.putExtras(bundle);
//            startActivity(intent);
//        }
    }

    private void findViews() {
        etEmpId = findViewById(R.id.etEmpId);
        etPassword = findViewById(R.id.etPassword);
    }

    public void onLoginClick(View view) {

        String emp_Acnum = etEmpId.getText().toString().trim();
        String emp_Psw = etPassword.getText().toString().trim();
//        if (emp_Acnum.length() <= 0 || emp_Psw.length() <= 0) {
//            Toast.makeText(this, "帳號密碼錯誤，請重新輸入!", Toast.LENGTH_SHORT).show();
//            return;
//        }
        //isEmployee(emp_Acnum, emp_Psw)
        if (true) {
            SharedPreferences preferences = getSharedPreferences(
                    Util.PREF_FILE, MODE_PRIVATE);
            preferences.edit().putBoolean("login", true)
                    .putString("emp_Acnum", emp_Acnum)
                    .putString("emp_Psw", emp_Psw).apply();
//            finish();
            Intent intent = new Intent(this,MainActivity.class);
            Bundle bundle = new Bundle();
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            Toast.makeText(this, "帳號密碼錯誤，請重新輸入!", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean isEmployee(final String emp_Acnum, final String emp_Psw) {
        boolean isEmployee = false;
        if (Util.networkConnected(this)) {
            String url = Util.URL + "AndroidEmployeeServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "isEmployee");
            jsonObject.addProperty("emp_Acnum", emp_Acnum);
            jsonObject.addProperty("emp_Psw", emp_Psw);
            String jsonOut = jsonObject.toString();
            isEmployeeTask = new CommonTask(url, jsonOut);
            try {
                String result = isEmployeeTask.execute().get();
                isEmployee = Boolean.valueOf(result);
            } catch (Exception e) {
                Log.e("LoginActivity", e.toString());
                isEmployee = false;
            }
        } else {
            Util.showToast(this, R.string.msg_NoNetwork);
        }
        return isEmployee;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isEmployeeTask != null) {
            isEmployeeTask.cancel(true);
        }
    }

}
