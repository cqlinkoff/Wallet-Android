package com.mbr.wallet.demo.view.password;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;

import com.mbr.wallet.core.MBRWallet;
import com.mbr.wallet.demo.R;
import com.mbr.wallet.demo.util.DialogUtil;
import com.mbr.wallet.demo.view.BaseActivity;

/**
 * SDKDemo
 * describe 密码示例页面，跳转至具体密码功能页面， 菜单功能：检查是否有密码提示、检查是否有密码、获取密码提示
 * author 王超然 2018/6/20
 */
public class PasswordActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pwd_activity_all);

        Button checkPwdBtn = findViewById(R.id.checkPwd);
        Button setPwdBtn = findViewById(R.id.setPwd);
        Button modifyPwdBtn = findViewById(R.id.modifyPwd);

        //检查是否有设置密码
        addMenu("检查是否有密码",v->{checkHasPwd();});
        //检查是否有设置密码提示
        addMenu("检查是否有密码提示",v->{checkHasReminder();});
        //获取密码提示
        addMenu("获取密码提示",v->{obtainReminder();});
        //跳转到检查密码界面
        checkPwdBtn.setOnClickListener(v->{
            routeToCheckPwd();});
        //跳转到设置密码界面
        setPwdBtn.setOnClickListener(v->{
            routeToSetPwd();});
        //跳转到修改密码界面
        modifyPwdBtn.setOnClickListener(v->{ routeToModifyPwd();});
    }

    /**
     * 检查是否有密码
     */
    public void checkHasPwd() {
        //检查是否有密码
        if(MBRWallet.instance().hasPassword()) {
            DialogUtil.showMsg("有密码");
        } else {
            DialogUtil.showMsg("没有密码");
        }
    }

    /**
     * 检查是否有密码提示
     */
    public void checkHasReminder() {
        //检查是否有密码提示
        if(MBRWallet.instance().hasReminder()) {
            DialogUtil.showMsg("有密码提示");
        } else {
            DialogUtil.showMsg("没有密码提示");
        }
    }

    /**
     * 得到密码提示
     */
    public void obtainReminder() {
        //获取密码提示
        String reminder = MBRWallet.instance().getReminder();
        if(reminder != null && !reminder.equals("")) {
            DialogUtil.showMsg("密码提示：" + reminder);
        }
    }

    /**
     * 跳转到检查密码界面
     */
    public void routeToCheckPwd() {
        Intent intent = new Intent();
        intent.setClass(this, PasswordCheckActivity.class);
        startActivity(intent);
    }

    /**
     * 跳转到设置密码界面
     */
    public void routeToSetPwd() {
        Intent intent = new Intent();
        intent.setClass(this, PasswordSetActivity.class);
        startActivity(intent);
    }

    /**
     * 跳转到修改密码界面
     */
    public void routeToModifyPwd() {
        Intent intent = new Intent();
        intent.setClass(this, PasswordModifyActivity.class);
        startActivity(intent);
    }
}
