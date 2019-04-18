package com.example.aliouswang.hook_kata;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aliouswang.plugin_kata.IDynamic;

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
    private ImageView image;

    private AssetManager mAssetManager;
    private Resources mResources;
    private Resources.Theme mTheme;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        Utils.extractAssets(newBase, apk_name);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image = findViewById(R.id.image);

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

                loadResoucres();

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
        Class beanClass = mClassLoader.loadClass("com.example.aliouswang.plugin_kaka.PluginDynamic");
        if (beanClass != null) {
            Object bean = beanClass.newInstance();

            Method getNameMethod = beanClass.getMethod("getString", Context.class);
            Method getImageMethod = beanClass.getMethod("getDrawable", Context.class);
            if (getNameMethod != null) {
                getNameMethod.setAccessible(true);
                getImageMethod.setAccessible(true);

//                IDynamic dynamic = (IDynamic) bean;
//                String name = dynamic.getString(MainActivity.this);

                String name = (String) getNameMethod.invoke(bean, MainActivity.this);

                Log.e("hook_kata", "bean name : " + name);

                Drawable drawable = (Drawable) getImageMethod.invoke(bean, MainActivity.this);

                image.setImageDrawable(drawable);
            }
        }
    }

    private void loadResoucres() {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssertPath = AssetManager.class.getMethod("addAssetPath", String.class);
            addAssertPath.invoke(assetManager, dexPath);
            mAssetManager = assetManager;
        } catch (Exception e) {
            e.printStackTrace();
        }

        mResources = new Resources(mAssetManager, super.getResources().getDisplayMetrics(),
                super.getResources().getConfiguration());
        mTheme = mResources.newTheme();
        mTheme.setTo(super.getTheme());
    }

    @Override
    public AssetManager getAssets() {
        if (mAssetManager != null) return mAssetManager;
        return super.getAssets();
    }

    @Override
    public Resources getResources() {
        if (mResources != null) return mResources;
        return super.getResources();
    }

    @Override
    public Resources.Theme getTheme() {
        if (mTheme != null) return mTheme;
        return super.getTheme();
    }
}
