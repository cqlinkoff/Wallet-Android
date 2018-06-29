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
 * describe 密码修改界面
 * author 王超然 2018/6/25
 */
public class PasswordModifyActivity extends BaseActivity {

    /**
     * 旧密码
     */
    private EditText oldPwdEt;

    /**
     * 新密码
     */
    private EditText newPwdEt;

    /**
     * 密码提示
     */
    private EditText reminderEt;

    /**
     * 确认按钮
     */
    private Button confirmBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pwd_activity_modifypwd);

        oldPwdEt = findViewById(R.id.oldPwd);
        newPwdEt = findViewById(R.id.newPwd);
        reminderEt = findViewById(R.id.et_reminder);
        confirmBtn = findViewById(R.id.btn_set_pwd);

        confirmBtn.setOnClickListener(__->{

            String oldPwd = oldPwdEt.getText().toString();
            String newPwd = newPwdEt.getText().toString();
            String reminder = reminderEt.getText().toString();

            //修改密码
            modifyPwd(oldPwd, newPwd, reminder);
        });
    }

    /**
     * 修改密码
     * @param oldPwd 旧密码
     * @param newPwd 新密码
     * @param reminder 密码提示
     */
    public void modifyPwd(String oldPwd, String newPwd, String reminder) {
        try {
            MBRWallet.instance().modifyPassword(oldPwd, newPwd, reminder);
            DialogUtil.showMsg("修改密码成功");
        } catch (Exception e) {
            DialogUtil.showMsg("修改密码出错:" + e.getMessage());
            e.printStackTrace();
        }
    }
}
