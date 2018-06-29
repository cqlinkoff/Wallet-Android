package com.mbr.wallet.demo.view.transaction;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.Toast;

import com.mbr.wallet.core.MBRWallet;
import com.mbr.wallet.core.error.MBRWalletException;
import com.mbr.wallet.core.service.account.MBRAccount;
import com.mbr.wallet.demo.R;
import com.mbr.wallet.demo.util.DialogUtil;
import com.mbr.wallet.demo.view.BaseActivity;
import com.mbr.wallet.network.bean.MBRBgCoin;

import java.util.List;

/**
 * SDKDemo
 * describe 交易入口，可以跳转到支付和转账页面
 * author 王超然 2018/6/20
 */
public class TransactionActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_activity_main);

        Button payBtn = findViewById(R.id.btn_pay);
        Button transferBtn = findViewById(R.id.btn_transfer);

        //支付
        payBtn.setOnClickListener(__->{
            if(hasAccount()) {
                //跳转到下订单页面
                Intent intent = new Intent(TransactionActivity.this, OrderActivity.class);
                startActivity(intent);
            } else {
                DialogUtil.showMsg("当前钱包中没有账户");
            }
        });

        //转账
        transferBtn.setOnClickListener(__->{
            if(hasAccount()) {
                //跳转到转账页面
                Intent intent = new Intent(TransactionActivity.this, TransferActivity.class);
                startActivity(intent);
            } else {
                DialogUtil.showMsg("当前钱包中没有账户");
            }
        });
    }

    /**
     * 是否有账户
     * @return 是否有账户
     */
    private boolean hasAccount() {
        boolean hasAccount = false;
        try {

            //默认获取账户列表中的第一个账户
            List<MBRAccount> accountList = MBRWallet.instance().getAllAccount();
            if(accountList != null && accountList.size() != 0) {
                hasAccount = true;
            }
        } catch (MBRWalletException e) {
            e.printStackTrace();
        }

        return hasAccount;
    }
}
