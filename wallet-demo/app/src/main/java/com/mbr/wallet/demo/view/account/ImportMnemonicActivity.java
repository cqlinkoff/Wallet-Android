package com.mbr.wallet.demo.view.account;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.mbr.wallet.core.MBRWallet;
import com.mbr.wallet.core.service.account.MBRAccount;
import com.mbr.wallet.demo.R;
import com.mbr.wallet.demo.util.DialogUtil;
import com.mbr.wallet.demo.view.BaseActivity;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * wallet-demo
 * describe 从助记词中导入账户
 * author 王超然 2018/6/26
 */
public class ImportMnemonicActivity extends BaseActivity {

    /**
     * 账户名称
     */
    private EditText nameEt;

    /**
     * 助记词
     */
    private EditText mnemonicEt;

    /**
     * 密码
     */
    private EditText pwdEt;

    /**
     * 导入
     */
    private Button importBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_activity_import_mnemonic);

        nameEt = findViewById(R.id.et_name);
        mnemonicEt = findViewById(R.id.et_mnemonic);
        pwdEt = findViewById(R.id.et_pwd);
        importBtn = findViewById(R.id.btn_import);

        importBtn.setOnClickListener(__->{
            //根据助记词导入
            importFromMnemonic(nameEt.getText().toString()
                    , mnemonicEt.getText().toString()
                    , pwdEt.getText().toString());
        });
    }

    /**
     * 根据助记词导入账户
     * @param name 账户名称
     * @param mnemonic 助记词
     * @param pwd 密码
     */
    public void importFromMnemonic(String name, String mnemonic, String pwd) {
        Disposable disposable = Observable.create((ObservableEmitter<MBRAccount> emitter)->{
            //根据助记词导入账户
            MBRAccount account = MBRWallet.instance().addAccountWithMnemonic(name, mnemonic, pwd);

            emitter.onNext(account);emitter.onComplete();
        })
        .subscribeOn(Schedulers.newThread())
        .doOnSubscribe(__->{
            DialogUtil.showLoadingDialog();
        })
        .subscribeOn(AndroidSchedulers.mainThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(account->{
            //成功信息
            DialogUtil.showMsg("导入成功");
            DialogUtil.dissmissLoadingDialog();
        },error->{
            //显示错误信息
            DialogUtil.showMsg("导入错误:" +error.getMessage());
            DialogUtil.dissmissLoadingDialog();
            error.printStackTrace();
        });
    }
}
