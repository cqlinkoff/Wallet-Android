package com.mbr.wallet.demo.view.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;

import com.mbr.wallet.demo.R;
import com.mbr.wallet.demo.view.BaseActivity;

/**
 * wallet-demo
 * describe 管理新增账户功能页面，跳转到具体的新增或导入页面
 * author 王超然 2018/6/26
 */
public class AddAccountActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_activity_add_manager);

        Button createBtn = findViewById(R.id.btn_create);
        Button importFromMnemonicBtn = findViewById(R.id.btn_import_mnemonic);
        Button importFromKeystoreBtn = findViewById(R.id.btn_import_keystore);

        createBtn.setOnClickListener(__->{
            //跳转到新增账户页面
            Intent intent = new Intent(AddAccountActivity.this, CreateAccountActivity.class);
            startActivity(intent);
        });

        importFromMnemonicBtn.setOnClickListener(__->{
            //跳转到助记词导入账户页面
            Intent intent = new Intent(AddAccountActivity.this, ImportMnemonicActivity.class);
            startActivity(intent);
        });

        importFromKeystoreBtn.setOnClickListener(__->{
            //跳转到keystore导入账户页面
            Intent intent = new Intent(AddAccountActivity.this, ImportKeystoreActivity.class);
            startActivity(intent);
        });
    }
}
