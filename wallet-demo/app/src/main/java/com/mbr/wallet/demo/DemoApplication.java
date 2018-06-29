package com.mbr.wallet.demo;

import android.support.multidex.MultiDex;
import android.widget.Toast;

import com.mbr.wallet.core.MBRWallet;
import com.mbr.wallet.core.error.MBRWalletException;
import com.mbr.wallet.core.service.account.MBRAccount;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import silib.app.LibApplication;

/**
 * SDKDemo
 * describe 必须要继承LibApplication
 * author 王超然 2018/6/21
 */
public class DemoApplication extends LibApplication {

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
