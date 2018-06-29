package com.mbr.wallet.demo.view.transaction;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbr.wallet.core.MBRWallet;
import com.mbr.wallet.core.error.MBRWalletException;
import com.mbr.wallet.core.service.account.MBRAccount;
import com.mbr.wallet.core.service.transaction.MBRTransferParam;
import com.mbr.wallet.demo.R;
import com.mbr.wallet.demo.data.po.PayParam;
import com.mbr.wallet.demo.util.DialogUtil;
import com.mbr.wallet.demo.view.BaseActivity;
import com.mbr.wallet.network.bean.MBRBgCoin;
import com.mbr.wallet.network.bean.response.MBRTxResponse;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * wallet-demo
 * describe 转账页面
 * author 王超然 2018/6/26
 */
public class TransferActivity extends BaseActivity {

    /**
     * 汇款账户
     */
    private MBRAccount account;

    /**
     * 汇款人昵称
     */
    private Spinner fromNameSp;

    /**
     * 收款人地址
     */
    private EditText addressEt;

    /**
     * 交易币种
     */
    private Spinner coinTypeSp;

    /**
     * 交易数量
     */
    private EditText amountEt;

    /**
     * 矿工费
     */
    private EditText gasEt;

    /**
     * 密码
     */
    private EditText pwdEt;

    /**
     * 备注
     */
    private EditText remarkEt;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_activity_transfer);

        fromNameSp = findViewById(R.id.sp_from_name);
        addressEt = findViewById(R.id.tv_address);
        coinTypeSp = findViewById(R.id.sp_coin_type);
        amountEt = findViewById(R.id.tv_amount);
        gasEt = findViewById(R.id.tv_gas_price);
        pwdEt = findViewById(R.id.tv_pwd);
        remarkEt = findViewById(R.id.tv_remark);

        Button transferBtn = findViewById(R.id.btn_transfer);
        Button checkBalanceBtn = findViewById(R.id.btn_check_balance);

        //检查余额
        checkBalanceBtn.setOnClickListener(__->{
            try {
                checkBalance();
                DialogUtil.showMsg("余额足够本次转账");
            } catch (MBRWalletException e){
                DialogUtil.showMsg(e.getCode());
                e.printStackTrace();
            }catch (Exception e) {
                e.printStackTrace();
                DialogUtil.showMsg("检查余额失败:" + e.getMessage());
            }
        });
        //转账
        transferBtn.setOnClickListener(__->{transferWidthPwd();});


        //初始化默认账户
        initAccount();
    }

    /**
     * 初始化默认账户信息
     */
    private void initAccount() {
        try {
            //查询所有币种
            List<MBRBgCoin> coinList = MBRWallet.instance().getAllCoins();
            //初始化币种选择框
            CoinTypeAdapter coinTypeAdapter = new CoinTypeAdapter(coinList);
            coinTypeSp.setAdapter(coinTypeAdapter);

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
     * 通过密码进行支付
     */
    private void transferWidthPwd() {
            Disposable disposable = Observable.create((ObservableEmitter<MBRTxResponse> emitter)->{
                PayParam payParam = getPayParam();
                MBRTransferParam transferParam = new MBRTransferParam();
                //设置支付账号地址
                transferParam.setAddressFrom(account.getAddress());
                //设置收款账号地址
                transferParam.setAddressTo(payParam.getAddress());
                //设置转账金额
                transferParam.setAmount(payParam.getAmount().toString());
                //设置矿工费
                transferParam.setGasPrice(payParam.getGas().toString());
                //设置交易币种
                transferParam.setCoinId(payParam.getCoinId());
                //设置备注
                transferParam.setMemo(payParam.getRemark());

                //检查余额
                checkBalance();

                //开始交易
                MBRTxResponse response = MBRWallet.instance().transferWithPwd(transferParam, payParam.getPwd());
                emitter.onNext(response);
                emitter.onComplete();
            }).subscribeOn(Schedulers.newThread())
            .doOnSubscribe(__->{
                DialogUtil.showLoadingDialog();
            })
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(mbrTxResponse -> {

                //在对话框中显示响应结果
                ObjectMapper mapper = new ObjectMapper();
                String responseStr = mapper.writeValueAsString(mbrTxResponse);
                JSONObject responseJson = new JSONObject(responseStr);
                //交易编号
                String txId = responseJson.getString("txId");
                //交易参数
                String txParams = responseJson.getString("txParams");

                DialogUtil.showMsg("转账成功!钱币会在一段时间后进入收款方账户\n交易编号:" + txId + "\n交易参数:" + txParams);
                DialogUtil.dissmissLoadingDialog();

            },  error->{
                //错误信息
                error.printStackTrace();
                DialogUtil.dissmissLoadingDialog();
                DialogUtil.showMsg("转账失败:"+ error.getMessage());
            });

    }

    /**
     * 检查余额
     */
    public void checkBalance() throws Exception {
        PayParam payParam = getPayParam();
        MBRWallet.instance().checkInsufficientFund(account.getAccountId(), payParam.getCoinId(), payParam.getAmount(), payParam.getGas());
    }

    /**
     * 获取参数信息
     * @return 支付参数
     */
    private PayParam getPayParam() throws Exception {
        if(addressEt.getText().toString().equals("")) {
            throw new Exception("请输入收款人地址");
        }
        if(amountEt.getText().toString().equals("")) {
            throw new Exception("请输入转账金额");
        }
        if(gasEt.getText().toString().equals("")) {
            throw new Exception("请输入矿工费");
        }

        PayParam payParam = new PayParam();
        payParam.setAddress(addressEt.getText().toString());
        payParam.setAmount(new BigDecimal(amountEt.getText().toString()));
        payParam.setGas(new BigDecimal(gasEt.getText().toString()));
        payParam.setPwd(pwdEt.getText().toString());
        payParam.setRemark(remarkEt.getText().toString());

        //获取当前选中的账户
        account = ((MBRAccount) fromNameSp.getSelectedItem());

        if(coinTypeSp.getSelectedItem() != null) {
            payParam.setCoinId(((MBRBgCoin) coinTypeSp.getSelectedItem()).getCoinId());
        }

        return payParam;
    }

    /**
     * 币种适配器
     */
    class CoinTypeAdapter extends BaseAdapter {

        /**
         * 币种列表
         */
        private List<MBRBgCoin> coinList;

        public CoinTypeAdapter(List<MBRBgCoin> coinList) {
            this.coinList = coinList;
        }

        @Override
        public int getCount() {
            return coinList.size();
        }

        @Override
        public MBRBgCoin getItem(int i) {
            return coinList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view == null) {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(android.R.layout.simple_list_item_1, null);
            }
            TextView textView = view.findViewById(android.R.id.text1);
            textView.setText(coinList.get(i).getAbbr());
            return view;
        }
    }

    /**
     * 账户适配器
     */
    class AccountAdapter extends BaseAdapter{

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
