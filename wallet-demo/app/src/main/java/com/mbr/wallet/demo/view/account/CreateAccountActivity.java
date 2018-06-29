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
 * describe 创建账户页面
 * author 王超然 2018/6/26
 */
public class CreateAccountActivity extends BaseActivity {

    /**
     * 昵称
     */
    private EditText nameEt;

    /**
     * 密码
     */
    private EditText pwdEt;

    /**
     * 创建按钮
     */
    private Button createBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_activity_create);

        nameEt = findViewById(R.id.et_name);
        pwdEt = findViewById(R.id.et_pwd);
        createBtn = findViewById(R.id.btn_create);

        createBtn.setOnClickListener(__->{
            //在当前钱包创建账户
            createAccount(nameEt.getText().toString(), pwdEt.getText().toString());
        });
    }

    /**
     * 创建账户
     * @param name 名称
     * @param pwd 密码
     */
    public void createAccount(String name, String pwd) {
        Disposable disposable = Observable.create((ObservableEmitter<MBRAccount> emitter)->{
            //添加新账户,设置一个随机名称
            MBRAccount account = MBRWallet.instance().addAccount(name,pwd);

            emitter.onNext(account);
            emitter.onComplete();
        })
        .subscribeOn(Schedulers.newThread())
        .doOnSubscribe(__->{
            DialogUtil.showLoadingDialog();
        })
        .subscribeOn(AndroidSchedulers.mainThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe((account)->{
            DialogUtil.showMsg("创建成功");

            DialogUtil.dissmissLoadingDialog();
        },error->{
            DialogUtil.dissmissLoadingDialog();
            //打印出错信息
            DialogUtil.showMsg(error.getMessage());
            error.printStackTrace();
        });
    }
}
