package com.mbr.wallet.demo.view.account;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mbr.wallet.core.MBRWallet;
import com.mbr.wallet.core.error.MBRWalletException;
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
 * describe 账户维护页面，菜单功能：生成助记词、生成keystore、删除账户、重命名
 * author 王超然 2018/6/26
 */
public class AccountManageActivity extends BaseActivity {

    /**
     * 维护账户
     */
    private MBRAccount account;

    /**
     * 输出标识
     */
    private TextView outputTagTv;

    /**
     * 输出内容
     */
    private TextView outputTv;

    interface OnCheckPwdListener {
        /**
         * 检查密码完成
         * @param isCorrect 是否正确
         * @param pwd 密码
         */
        public void onCheckPwd(boolean isCorrect, String pwd);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_activity_manage);
        //初始化账户信息显示
        account = (MBRAccount) getIntent().getSerializableExtra("account");
        showAccount(account);

        //输出文本框
        outputTagTv = findViewById(R.id.tv_output_tag);
        outputTv = findViewById(R.id.tv_output);

        addMenu("重命名",__->{
            //检查密码
            checkPassword((isCorrect, pwd) -> {
                if(isCorrect) {
                    //重命名
                    DialogUtil.showSingleEdit("输入新的昵称", this::renameAccount);
                }
            });
        });

        addMenu("删除账户",__->{
            //检查密码
            checkPassword((isCorrect, pwd) -> {
                if(isCorrect) {
                    //删除账户
                    deleteAccount(pwd);
                }
            });
        });

        addMenu("生成助记词",__->{
            //检查密码
            checkPassword((isCorrect,pwd) -> {
                //生成助记词
                generateMnemonic(pwd);
            });
        });

        addMenu("生成keystore",__->{
            //检查密码
            checkPassword((isCorrect, pwd) -> {
                DialogUtil.showSingleEdit("设置密钥库密码", keystorePwd -> {
                    //生成密钥库
                    generateKeystore(keystorePwd, pwd);
                });
            });
        });

    }

    /**
     * 显示账户
     * @param account 账户
     */
    private void showAccount(MBRAccount account) {
        //名称
        TextView nameTv = findViewById(R.id.tv_name);
        //编号
        TextView idTextView = findViewById(R.id.accountId);
        //描述
        TextView descTextView = findViewById(R.id.desc);

        //根据账户填充信息
        nameTv.setText(account.getNickName());
        idTextView.setText("id：" + account.getAccountId());
        descTextView.setText("备份：" + account.getBackup() + "\t状态：" + account.getStatus());
    }

    /**
     * 检查密码
     * @param listener 检查密码监听
     */
    private void checkPassword(OnCheckPwdListener listener) {
        DialogUtil.showSingleEdit("输入密码", text->{
            //检查输入密码是否正确
            boolean result = MBRWallet.instance().checkPassword(text);
            //回调监听
            listener.onCheckPwd(result, text);
            //密码错误显示提示
            if(!result) {
                Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 重命名
     * @param newName 新名称
     */
    public void renameAccount(String newName) {
        try {
            //重命名账户,设置一个随机名称
            MBRAccount renamedAccount = MBRWallet.instance().renameAccount(account.getAccountId(), newName);
            this.account = renamedAccount;
            //重新显示账户信息
            showAccount(renamedAccount);

        } catch (MBRWalletException e) {
            DialogUtil.showMsg("修改失败" + e.getCode());
            e.printStackTrace();
        }

    }


    /**
     * 删除账户
     * @param pwd 密码
     */
    public void deleteAccount(String pwd) {
        DialogUtil.showMsg("确认删除当前账户吗", (dialog, i)->{
            try {
                //删除账户
                MBRWallet.instance().deleteAccount(account.getAccountId(), pwd);
                finish();
            } catch (MBRWalletException e) {
                e.printStackTrace();
                DialogUtil.showMsg(e.getCode());
            }
        });
    }

    /**
     * 生成助记词
     * @param pwd 密码
     */
    public void generateMnemonic(String pwd) {
        try {
            //获取账户助记词，
            String mnemonic = MBRWallet.instance().getAccountMnemonic(account.getAccountId(), pwd);
            showOutput("助记词：", mnemonic);
        } catch (MBRWalletException e) {
            e.printStackTrace();
            DialogUtil.showMsg("生成失败:" + e.getCode());
        }
    }

    /**
     * 生成keystore
     * @param keystorePwd 密钥库密码
     * @param pwd 密码
     */
    public void generateKeystore(String keystorePwd, String pwd) {

            Disposable disposable = Observable.create((ObservableEmitter<String> emitter)->{
                //生成keystore
                String keystore = MBRWallet.instance().exportAccountKeystore(account.getAccountId(), keystorePwd, pwd);
                Log.d("Keystore", keystore);
                emitter.onNext(keystore);emitter.onComplete();
            })
            .subscribeOn(Schedulers.newThread())
            .doOnSubscribe(__->{
                DialogUtil.showLoadingDialog("生成keystore大概需要两分钟，请耐心等待");
            })
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(keystore->{
                showOutput("keystore:", keystore);
                DialogUtil.dissmissLoadingDialog();
            }, error->{
                DialogUtil.dissmissLoadingDialog();
                DialogUtil.showMsg("生成失败:" + error.getMessage());
            });
    }

    /**
     * 显示输出内容
     * @param tag 输出标识
     * @param msg 内容
     */
    private void showOutput(String tag, String msg) {
        outputTagTv.setText(tag);
        outputTv.setText(msg);
    }
}
