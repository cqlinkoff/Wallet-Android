package com.mbr.wallet.demo;

import android.content.Context;

import com.mbr.wallet.core.MBRWallet;
import com.mbr.wallet.core.config.IMBRWalletConfigDelegate;
import com.mbr.wallet.core.config.MBRWalletCompatibleConfig;
import com.mbr.wallet.core.config.MBRWalletConfigDelegate;
import com.mbr.wallet.network.config.IMBRWalletNetConfigDelegate;
import com.mbr.wallet.network.config.MBRWalletNetConfigDelegate;


/**
 * author: guojing on 2018/6/18
 * describe:钱包基本配置
 */
public enum WalletConfig implements IMBRWalletConfigDelegate,IMBRWalletNetConfigDelegate {
    INSTANCE;
    private Context context;
    private MBRWalletNetConfigDelegate mNetConfigDelegate;
    private MBRWalletCompatibleConfig mCompatibleConfig;
    private MBRWalletConfigDelegate mWalletDelegate;

    /**
     * 初始化
     * @param context 上下文
     */
    public void init(Context context){
        this.context = context;
        //网络配置
        if(mNetConfigDelegate == null){
            mNetConfigDelegate = new MBRWalletNetConfigDelegate();
            mNetConfigDelegate.setHost("http://47.100.18.6:9900");
            mNetConfigDelegate.setChannel("73088886094000");
            mNetConfigDelegate.setPushId("");
        }
        //兼容配置
        if(mCompatibleConfig == null){
            mCompatibleConfig = new MBRWalletCompatibleConfig();
            mCompatibleConfig.setBelowFingerReset(false);
            mCompatibleConfig.setEnableTouchId(false);
            mCompatibleConfig.setPrefix("");
        }

        //钱包配置
        if(mWalletDelegate == null){
            mWalletDelegate = new MBRWalletConfigDelegate();
            mWalletDelegate.setCompatibleConfig(mCompatibleConfig);
            mWalletDelegate.setNetworkConfigDelegate(this);
            mWalletDelegate.setChain("-1");
        }
        try {
            MBRWallet.instance().init(this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public String getLanguage() {
        return "zh-CN";
    }

    @Override
    public MBRWalletNetConfigDelegate getMBRWalletNetConfig() {
        return mNetConfigDelegate;
    }

    @Override
    public MBRWalletConfigDelegate getMBRWalletConfig() {
        return mWalletDelegate;
    }

}
