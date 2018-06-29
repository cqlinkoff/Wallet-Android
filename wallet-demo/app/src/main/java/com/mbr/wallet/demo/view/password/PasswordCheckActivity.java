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
 * describe 输入密码检查密码是否正确
 * author 王超然 2018/6/26
 */
public class PasswordCheckActivity extends BaseActivity {

    /**
     * 密码输入框
     */
    private EditText pwdEt;

    /**
     * 检查密码按钮
     */
    private Button checkBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pwd_activity_check);
        
        pwdEt = findViewById(R.id.et_pwd);
        checkBtn = findViewById(R.id.btn_check_pwd);
        
        checkBtn.setOnClickListener(__->{
            //检查密码输入是否正确
            checkPwd(pwdEt.getText().toString());
        });
    }

    /**
     * 检查密码是否正确
     * @param pwd 密码
     */
    void checkPwd(String pwd){
        //检查输入密码是否正确
        if("".equals(pwd)) {
            DialogUtil.showMsg("输入密码不能为空");
        } else {
            boolean result = MBRWallet.instance().checkPassword(pwd);
            if(result) {
                DialogUtil.showMsg("输入密码正确");
            } else {
                DialogUtil.showMsg("输入密码错误");
            }
        }
    }

}
