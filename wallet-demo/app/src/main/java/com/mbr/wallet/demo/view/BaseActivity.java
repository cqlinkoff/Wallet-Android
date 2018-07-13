package com.mbr.wallet.demo.view;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mbr.wallet.demo.R;
import com.mbr.wallet.demo.util.DialogUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * SDKDemo
 * describe 基础活动页面
 * author 王超然 2018/6/20
 */
public class BaseActivity extends AppCompatActivity {

    /**
     * 菜单对应点击事件
     */
    private Map<String, View.OnClickListener> menuMap = new HashMap<>();

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //初始化工具栏
        initToolbar();
    }

    /**
     * 初始化工具栏
     */
    private void initToolbar() {
        //设置工具栏
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getClass().getSimpleName());
        setSupportActionBar(toolbar);
    }

    /**
     * 添加菜单
     * @param menuTitle 菜单标题
     * @param clickListener 菜单点击事件
     */
    protected void addMenu(String menuTitle, View.OnClickListener clickListener) {
        menuMap.put(menuTitle, clickListener);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DialogUtil.init(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DialogUtil.init(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //添加菜单
        Set<String > menuTitles = menuMap.keySet();
        for(String title: menuTitles) {
            menu.add(title);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //根据标签获取点击事件
        View.OnClickListener menuListener = menuMap.get(item.getTitle().toString());
        //触发点击事件
        menuListener.onClick(item.getActionView());
        return super.onOptionsItemSelected(item);
    }
}
