package com.mbr.wallet.demo.view.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.mbr.wallet.core.MBRWallet;
import com.mbr.wallet.core.service.account.MBRAccount;
import com.mbr.wallet.demo.R;
import com.mbr.wallet.demo.util.DialogUtil;
import com.mbr.wallet.demo.view.BaseActivity;
import com.mbr.wallet.network.bean.MBRBgCoin;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * SDKDemo
 * describe 账户示例页面，显示账户列表，菜单功能：创建账户、同步账户余额
 * author 王超然 2018/6/20
 */
public class AccountActivity extends BaseActivity {

    /**
     * 账户列表视图
     */
    private ListView accountListView;

    /**
     * 账户列表视图适配器
     */
    private AccountListAdapter listAdapter = new AccountListAdapter(new ArrayList<>());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_activity_main);

        //初始化列表
        accountListView = findViewById(R.id.lv_account);
        accountListView.setAdapter(listAdapter);

        addMenu("新增或导入",__->{
            //跳转到添加账户页面
            routeToAddAccount();
        });

        addMenu("同步账户余额",__->{
            //同步账户
            syncAccount();
        });

        //账户列表点击
        accountListView.setOnItemClickListener((adapterView, view, i, l) -> {
            MBRAccount account = listAdapter.getItem(i);
            //跳转到单个账户管理页面
            routeToSingleAccount(account);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //加载账户列表
        loadAccountList();
    }

    /**
     * 加载账户列表
     */
    private void loadAccountList() {
        try {
            //查询所有账户
            List<MBRAccount> accountList = MBRWallet.instance().getAllAccount();
            if(accountList != null) {
                //重新加载列表
                listAdapter.reload(accountList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 同步账户钱币信息
     */
    public void syncAccount() {
        Disposable disposable = Observable.create(emitter->{
            //同步所有账户余额
            MBRWallet.instance().syncAllAccountBalance();
            //刷新列表
            emitter.onNext("");
            emitter.onComplete();
        })
        .subscribeOn(Schedulers.newThread())
        .doOnSubscribe(__->{
            DialogUtil.showLoadingDialog();
        })
        .subscribeOn(AndroidSchedulers.mainThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(__->{
            DialogUtil.showMsg("同步余额成功");
            DialogUtil.dissmissLoadingDialog();
            loadAccountList();
        },error->{
            //错误信息
            error.printStackTrace();
            DialogUtil.showMsg(error.getMessage());
            DialogUtil.dissmissLoadingDialog();
        });
    }

    /**
     * 跳转到添加账户页面
     */
    private void routeToAddAccount() {
        Intent intent = new Intent(this, AddAccountActivity.class);
        startActivity(intent);
    }

    /**
     * 跳转到单个账户管理页面
     * @param account 账户
     */
    private void routeToSingleAccount(MBRAccount account) {
        Intent intent = new Intent(this, AccountManageActivity.class);
        intent.putExtra("account", account);
        startActivity(intent);
    }

    /**
     * 账户列表适配器
     */
    class AccountListAdapter extends BaseAdapter {

        private List<MBRAccount> accountList;

        public AccountListAdapter(List<MBRAccount> accountList) {
            this.accountList = accountList;
        }

        /**
         * 重新加载列表
         * @param accountList 账户列表
         */
        public void reload(List<MBRAccount> accountList) {
            this.accountList = accountList;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return accountList.size();
        }

        @Override
        public MBRAccount getItem(int position) {
            return accountList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account, null);
            }
            TextView nameTv = convertView.findViewById(R.id.tv_name);
            TextView idTextView = convertView.findViewById(R.id.accountId);
            TextView descTextView = convertView.findViewById(R.id.desc);
            TextView coinTextView = convertView.findViewById(R.id.coinInfo);

            //根据账户填充信息
            MBRAccount account = accountList.get(position);
            nameTv.setText(account.getNickName());
            idTextView.setText("id：" + account.getAccountId());
            descTextView.setText("备份：" + account.getBackup() + "\t状态：" + account.getStatus());
            //循环添加持有币信息
            StringBuilder coinInfo = new StringBuilder();
            for(MBRBgCoin coin: account.getCoins()) {
                coinInfo.append(coin.getAbbr() + "：" + coin.getAmount() + "\t");
            }
            coinTextView.setText(coinInfo.toString());

            return convertView;
        }
    }
}
