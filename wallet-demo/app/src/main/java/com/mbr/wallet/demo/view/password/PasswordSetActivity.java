package com.mbr.wallet.demo.view.password;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;

import com.mbr.wallet.core.MBRWallet;
import com.mbr.wallet.demo.R;
import com.mbr.wallet.demo.util.DialogUtil;
import com.mbr.wallet.demo.view.BaseActivity;

/**
 * wallet-demo
 * describe 密码设置界面
 * author 王超然 2018/6/25
 */
public class PasswordSetActivity extends BaseActivity {

    /**
     * 密码输入框
     */
    private EditText pwdEt;

    /**
     * 提示输入框
     */
    private EditText reminderEt;

    /**
     * 确认按钮
     */
    private Button confirmBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pwd_activity_setpwd);

        pwdEt = findViewById(R.id.et_pwd);
        reminderEt = findViewById(R.id.et_reminder);
        confirmBtn = findViewById(R.id.btn_set_pwd);

        confirmBtn.setOnClickListener(__->{
            //设置密码
            setPwd(pwdEt.getText().toString(), reminderEt.getText().toString());
        });
    }

    /**
     * 设置密码
     * @param pwd 密码
     * @param reminder 密码提示
     */
    public void setPwd(String pwd, String reminder) {
        try {
            MBRWallet.instance().setPassword(pwd, reminder);
            DialogUtil.showMsg("设置密码成功");
        } catch (Exception e) {
            DialogUtil.showMsg("设置密码出错:" + e.getMessage());
            e.printStackTrace();
        }
    }
}
