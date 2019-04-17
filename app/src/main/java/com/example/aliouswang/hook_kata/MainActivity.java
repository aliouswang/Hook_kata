package com.example.aliouswang.hook_kata;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class MainActivity extends AppCompatActivity {

    private String apk_name = "app-debug.apk";

    private String dexPath;
    private File fileRelease;

    private DexClassLoader mClassLoader;

    private TextView tv;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        Utils.extractAssets(newBase, apk_name);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File file = this.getFileStreamPath(apk_name);
        dexPath = file.getPath();

        fileRelease = getDir("dex", 0);


        Log.d("hook_kata", "dexpath:" + dexPath);
        Log.d("hook_kata", "fileRelease.getAbsolutePath():" +
                fileRelease.getAbsolutePath());

        mClassLoader = new DexClassLoader(dexPath, fileRelease.getAbsolutePath(),
                null, getClassLoader());


        Button btn_1 = (Button) findViewById(R.id.btn_1);

        tv = (TextView) findViewById(R.id.tv);

        //普通调用，反射的方式
        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
//                Class mLoadClassBean;
//                try {
//                    mLoadClassBean = mClassLoader.loadClass("jianqiang.com.plugin1.Bean");
//                    Object beanObject = mLoadClassBean.newInstance();
//
//                    Method getNameMethod = mLoadClassBean.getMethod("getName");
//                    getNameMethod.setAccessible(true);
//                    String name = (String) getNameMethod.invoke(beanObject);
//
//                    tv.setText(name);
//                    Toast.makeText(getApplicationContext(), name, Toast.LENGTH_LONG).show();
//
//                } catch (Exception e) {
//                    Log.e("DEMO", "msg:" + e.getMessage());
//                }


                try {
                    tryLoadClass();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void tryLoadClass() throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Class beanClass = mClassLoader.loadClass("jianqiang.com.plugin1.Bean");
        if (beanClass != null) {
            Object bean = beanClass.newInstance();

            Method getNameMethod = beanClass.getMethod("getName");
            if (getNameMethod != null) {
                getNameMethod.setAccessible(true);
                String name = (String) getNameMethod.invoke(bean);

                Log.e("hook_kata", "bean name : " + name);
            }
        }
    }
}
