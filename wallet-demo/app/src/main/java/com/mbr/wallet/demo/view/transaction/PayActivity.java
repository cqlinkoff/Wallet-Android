package com.mbr.wallet.demo.view.transaction;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbr.wallet.core.MBRWallet;
import com.mbr.wallet.core.error.MBRWalletException;
import com.mbr.wallet.core.service.account.MBRAccount;
import com.mbr.wallet.core.service.transaction.MBRPayParam;
import com.mbr.wallet.demo.R;
import com.mbr.wallet.demo.data.po.PayParam;
import com.mbr.wallet.demo.util.DialogUtil;
import com.mbr.wallet.demo.view.BaseActivity;
import com.mbr.wallet.network.bean.MBRBgCoin;
import com.mbr.wallet.network.bean.response.MBRPayResponse;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * wallet-demo
 * describe 支付页面
 * author 王超然 2018/6/26
 */
public class PayActivity extends BaseActivity {

    /**
     * 支付账户
     */
    private MBRAccount account;

    /**
     * 订单信息显示
     */
    private TextView orderTv;

    /**
     * 支付人昵称
     */
    private Spinner fromNameSp;

    /**
     * 矿工费
     */
    private TextView gasTv;

    /**
     * 备注
     */
    private TextView memoTv;

    /**
     * 密码
     */
    private TextView pwdTv;

    /**
     * 订单信息
     */
    private String orderInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_activity_pay);

        orderTv =findViewById(R.id.tv_order);
        fromNameSp = findViewById(R.id.sp_from_name);
        gasTv = findViewById(R.id.tv_gas_price);
        memoTv = findViewById(R.id.tv_memo);
        pwdTv = findViewById(R.id.tv_pwd);

        Button payBtn = findViewById(R.id.btn_pay);
        payBtn.setOnClickListener(__->{
            payWithPwd();
        });

        //获取订单信息
        orderInfo = getIntent().getStringExtra("order");
        showOrderInfo(orderInfo);

        //初始化账户下拉选
        initAccount();
    }

    /**
     * 显示订单信息
     * @param orderInfoStr 订单信息
     */
    private void showOrderInfo(String orderInfoStr) {
        try {
            JSONObject orderInfo = new JSONObject(orderInfoStr);
            //根据货币id查询货币
            String coinName = queryCoinById(orderInfo.getString("coinId"));
            //在页面上显示
            orderTv.setText("商户名称:" + orderInfo.getString("merchantName")
                    + "\n支付金额:" + orderInfo.getString("amount")
                    + "\n支付货币:" + coinName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用密码支付
     */
    public void payWithPwd() {

        String gas = gasTv.getText().toString();
        String memo = memoTv.getText().toString();
        String pwd = pwdTv.getText().toString();
        account = (MBRAccount) fromNameSp.getSelectedItem();

        //支付参数
        MBRPayParam mbrPayParam = new MBRPayParam();
        //设置支付账号的地址
        mbrPayParam.setAddressFrom(account.getAddress());
        //设置矿工费,矿工费只能是ETH
        mbrPayParam.setGasPrice(gas);
        //设置备注
        mbrPayParam.setMemo(memo);

        Disposable disposable =Observable.create((ObservableEmitter<MBRPayResponse> emitter) ->{
            //检查余额
            checkBalance(gas);

            //调用支付接口
            MBRPayResponse response = MBRWallet.instance().payWithPwd(mbrPayParam, pwd);
            emitter.onNext(response);
            emitter.onComplete();
        })
                .subscribeOn(Schedulers.newThread())
                .doOnSubscribe(__->{
                    DialogUtil.showLoadingDialog();
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mbrPayResponse -> {
                    //支付成功后需要一定时间货币才会汇入商家账户
                    DialogUtil.showMsg("支付成功");

                    DialogUtil.dissmissLoadingDialog();
                },error->{
                    DialogUtil.showMsg("支付失败：" + error.getMessage());
                    error.printStackTrace();
                    DialogUtil.dissmissLoadingDialog();
                });
    }

    /**
     * 初始化默认账户信息
     */
    private void initAccount() {
        try {

            //查询所有账户
            List<MBRAccount> accountList = MBRWallet.instance().getAllAccount();
            if(accountList != null && accountList.size() != 0) {
                //初始化选择列表
                AccountAdapter accountAdapter = new AccountAdapter(accountList);
                fromNameSp.setAdapter(accountAdapter);
            }
        } catch (MBRWalletException e) {
            DialogUtil.showMsg(e.getCode());
        }
    }

    /**
     * 根据货币编号查询货币信息
     * @param coinId 货币编号
     * @return 货币
     */
    private String queryCoinById(String coinId) throws Exception {
        List<MBRBgCoin> coinList = MBRWallet.instance().getAllCoins();
        for(MBRBgCoin coin: coinList) {
            if(coin.getCoinId().equals(coinId)) {
                return coin.getAbbr();
            }
        }
        return "";
    }

    /**
     * 检查余额
     * @param gas 矿工费
     */
    public void checkBalance(String gas) throws Exception {
        JSONObject orderJson = new JSONObject(orderInfo);

        MBRWallet.instance().checkInsufficientFund(account.getAccountId()
                , orderJson.getString("coinId")
                , new BigDecimal(orderJson.getString("amount"))
                , new BigDecimal(gas));
    }

    /**
     * 账户适配器
     */
    class AccountAdapter extends BaseAdapter {

        /**
         * 账户列表
         */
        private List<MBRAccount> accountList;

        public AccountAdapter(List<MBRAccount> accountList) {
            this.accountList = accountList;
        }

        /**
         * 重新加载数据
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
                convertView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, null);
            }
            TextView textView = convertView.findViewById(android.R.id.text1);
            textView.setText(accountList.get(position).getNickName());
            return convertView;
        }
    }
}
