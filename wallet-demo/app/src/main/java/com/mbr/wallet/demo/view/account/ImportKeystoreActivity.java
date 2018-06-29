package com.mbr.wallet.demo.view.account;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
 * describe 通过keystore导入账户
 * author 王超然 2018/6/26
 */
public class ImportKeystoreActivity extends BaseActivity {

    /**
     * 账户名称
     */
    private EditText nameEt;

    /**
     * keystore内容
     */
    private EditText keystoreEt;

    /**
     * keystore密码
     */
    private EditText keystorePwdEt;

    /**
     * 密码
     */
    private EditText pwdEt;

    /**
     * 导入按钮
     */
    private Button importBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.account_activity_import_keystore);

        nameEt = findViewById(R.id.et_name);
        keystoreEt = findViewById(R.id.et_keystore);
        keystorePwdEt = findViewById(R.id.et_keystore_pwd);
        pwdEt = findViewById(R.id.et_pwd);
        importBtn = findViewById(R.id.btn_import);

        importBtn.setOnClickListener(__->{
            //通过keystore导入
            String name = nameEt.getText().toString();
            String keystore = keystoreEt.getText().toString();
            String keystorePwd = keystorePwdEt.getText().toString();
            String pwd = pwdEt.getText().toString();

            //根据keystore导入账户
            importFromKeystore(name, keystore, keystorePwd, pwd);
        });
    }

    /**
     * 通过keystore导入账户
     * @param name 账户名称
     * @param keystore 密钥库
     * @param keystorePwd 密钥库密码
     * @param pwd 钱包密码
     */
    public void importFromKeystore(String name, String keystore, String keystorePwd, String pwd) {

        Disposable disposable = Observable.create((ObservableEmitter<MBRAccount> emitter)->{
            //根据keystore导入账户
            MBRAccount account = MBRWallet.instance().addAccountWithKeystore(name, keystore, keystorePwd, pwd);

            emitter.onNext(account);emitter.onComplete();
        })
        .subscribeOn(Schedulers.newThread())
        .doOnSubscribe(__->{
            DialogUtil.showLoadingDialog();
        })
        .subscribeOn(AndroidSchedulers.mainThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(account->{
            DialogUtil.showMsg("导入成功");
            DialogUtil.dissmissLoadingDialog();
        },error->{
            DialogUtil.showMsg("导入错误:" + error.getMessage());
            DialogUtil.dissmissLoadingDialog();
            error.printStackTrace();
        });

    }
}
