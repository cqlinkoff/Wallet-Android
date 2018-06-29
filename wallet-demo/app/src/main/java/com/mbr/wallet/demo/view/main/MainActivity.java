package com.mbr.wallet.demo.view.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mbr.wallet.demo.R;
import com.mbr.wallet.demo.data.po.SampleCategory;
import com.mbr.wallet.demo.util.DialogUtil;
import com.mbr.wallet.demo.view.BaseActivity;
import com.mbr.wallet.demo.view.account.AccountActivity;
import com.mbr.wallet.demo.view.coin.CoinActivity;
import com.mbr.wallet.demo.view.other.InitActivity;
import com.mbr.wallet.demo.view.password.PasswordActivity;
import com.mbr.wallet.demo.view.transaction.TransactionActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * SDKDemo
 * describe 功能列表分类页面，点击跳转到具体的功能页面
 * author 王超然 2018/6/20
 */
public class MainActivity extends BaseActivity {

    /**
     *  示例功能列表
     */
    private ListView sampleListView;

    /**
     * 示例列表适配器
     */
    private CategoryAdapter sampleListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化列表
        sampleListView = findViewById(R.id.sampleList);
        sampleListAdapter = new CategoryAdapter(new ArrayList<>());
        sampleListView.setAdapter(sampleListAdapter);

        //点击事件
        sampleListView.setOnItemClickListener((adapterView, view, i, l)->{
            //跳转到指定示例分类下
            routeTo(MainActivity.this, sampleListAdapter.getItem(i));
        });

        //加载列表数据
        loadData();
    }

    /**
     * 加载列表数据
     */
    public void loadData() {
        //创建示例类别
        List<SampleCategory> categories = new ArrayList<>();
        categories.add(new SampleCategory("交易", TransactionActivity.class));
        categories.add(new SampleCategory("账户", AccountActivity.class));
        categories.add(new SampleCategory("钱币", CoinActivity.class));
        categories.add(new SampleCategory("密码", PasswordActivity.class));

        //加载列表
        sampleListAdapter.load(categories);
    }

    /**
     * 跳转到指定的分类界面
     * @param context 上下文
     * @param category 示例分类
     */
    public void routeTo(Context context, SampleCategory category) {
        Intent intent = new Intent();
        intent.setClass(context, category.getActivity());
        context.startActivity(intent);
    }

    /**
     * 分类列表适配器
     */
    class CategoryAdapter extends BaseAdapter {

        /**
         * 分类列表
         */
        private List<SampleCategory> categoryList;

        public CategoryAdapter(List<SampleCategory> categoryList) {
            this.categoryList = categoryList;
        }

        /**
         * 加载数据
         * @param categoryList 分类列表
         */
        public void load(List<SampleCategory> categoryList) {
            this.categoryList = categoryList;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return categoryList.size();
        }

        @Override
        public SampleCategory getItem(int i) {
            return categoryList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View rowView = view;
            if(rowView == null) {
                rowView = LayoutInflater.from(viewGroup.getContext()).inflate(android.R.layout.simple_list_item_1, null);
            }

            //设置显示名称
            TextView nameView = rowView.findViewById(android.R.id.text1);
            nameView.setText(categoryList.get(i).getName());

            return rowView;
        }
    }
}
