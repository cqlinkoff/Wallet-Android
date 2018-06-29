package com.mbr.wallet.demo.view.other;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mbr.wallet.core.MBRWallet;
import com.mbr.wallet.core.error.MBRWalletException;
import com.mbr.wallet.demo.R;
import com.mbr.wallet.demo.WalletConfig;
import com.mbr.wallet.demo.util.DialogUtil;
import com.mbr.wallet.demo.view.BaseActivity;
import com.mbr.wallet.demo.view.main.MainActivity;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * SDKDemo
 * describe 初始化示例页面，展示初始化和密码设置
 * author 王超然 2018/6/20
 */
public class InitActivity extends BaseActivity {

    /**
     * 初始化状态
     */
    private TextView initStatTv;

    /**
     * 是否有设置钱包密码
     */
    private TextView hasPwdTv;

    /**
     * 设置密码行
     */
    private LinearLayout pwdLL;

    /**
     * 密码输入框
     */
    private EditText pwdEt;

    /**
     * 密码提示
     */
    private EditText pwdReminderEd;

    /**
     * 设置密码按钮
     */
    private Button setPwdBtn;

    /**
     * 下一步按钮
     */
    private Button nextStepBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.init_activity);

        initStatTv = findViewById(R.id.tv_init_state);
        pwdLL = findViewById(R.id.ll_set_pwd);
        hasPwdTv = findViewById(R.id.tv_has_pwd);
        pwdEt = findViewById(R.id.et_pwd);
        pwdReminderEd = findViewById(R.id.et_reminder);
        setPwdBtn = findViewById(R.id.btn_set_pwd);
        nextStepBtn = findViewById(R.id.btn_next_step);

        //设置密码
        setPwdBtn.setOnClickListener(__->{
            setPwd();
        });

        //下一步跳转到主页面
        nextStepBtn.setOnClickListener(__->{
            Intent intent = new Intent(InitActivity.this, MainActivity.class);
            startActivity(intent);
        });

        //初始化
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        Disposable disposable = Observable.create(emitter -> {
            //初始化钱包配置
            WalletConfig.INSTANCE.init(this);
            emitter.onNext("");
            emitter.onComplete();
        })
        .subscribeOn(Schedulers.newThread())
        .doOnSubscribe(__->{
            DialogUtil.showLoadingDialog();
        })
        .subscribeOn(AndroidSchedulers.mainThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(__ -> {
            DialogUtil.dissmissLoadingDialog();
            initStatTv.setText("1、初始化完成");
            //检查是否有密码
            checkPwd();
        }, error->{
            DialogUtil.dissmissLoadingDialog();
            error.printStackTrace();
            initStatTv.setText("1、初始化失败:" + error.getMessage());
        });
    }

    /**
     * 检查密码
     */
    private void checkPwd() {
        //检查是否设置了密码
        boolean hasPwd = MBRWallet.instance().hasPassword();
       if(hasPwd) {
           hasPwdTv.setText("2、已经设置了密码");
           //显示下一步
           showNext();
       } else{
           hasPwdTv.setText("2、请设置钱包密码");
           //显示密码设置
           pwdLL.setVisibility(View.VISIBLE);
       }
    }

    /**
     * 设置密码
     */
    private void setPwd() {
        try {
            String pwd = pwdEt.getText().toString();
            String reminder = pwdReminderEd.getText().toString();
            MBRWallet.instance().setPassword(pwd, reminder);
            hasPwdTv.setText("2、已设置钱包密码");
            //隐藏设置界面
            pwdLL.setVisibility(View.GONE);
            //显示下一步
            showNext();
        } catch (MBRWalletException e) {
            hasPwdTv.setText("设置密码失败" + e.getCode());
            e.printStackTrace();
        }
    }

    /**
     * 显示下一步
     */
    private void showNext() {
        nextStepBtn.setVisibility(View.VISIBLE);
    }
}
