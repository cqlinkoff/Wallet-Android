package com.mbr.wallet.demo.view.coin;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbr.wallet.core.MBRWallet;
import com.mbr.wallet.core.service.account.MBRAccount;
import com.mbr.wallet.core.service.coin.MBRCoinGasInfo;
import com.mbr.wallet.demo.R;
import com.mbr.wallet.demo.util.DialogUtil;
import com.mbr.wallet.demo.view.BaseActivity;
import com.mbr.wallet.network.bean.MBRBgCoin;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * SDKDemo
 * describe 钱币示例页面，菜单功能：查询货币列表、同步所有货币信息、查询强制币列表、查询非强制币列表、获取矿工费信息
 * author 王超然 2018/6/20
 */
public class CoinActivity extends BaseActivity {

    /**
     * 货币列表
     */
    private ListView coinListView;

    /**
     * 列表适配器
     */
    private CoinListAdapter listAdapter;

    /**
     * 当前选中的钱币
     */
    private MBRBgCoin selectCoin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin);

        //货币列表
        coinListView = findViewById(R.id.coinList);
        listAdapter = new CoinListAdapter(new ArrayList<>());
        coinListView.setAdapter(listAdapter);

        //同步所有货币信息
        addMenu("同步所有货币信息",__->{syncAllCoinList();});
        //查询货币列表
        addMenu("查询货币列表",__->{getAllCoins();});
        //查询强制币列表
        addMenu("查询强制币列表",__->{getForceCoins();});
        //查询非强制币列表
        addMenu("查询非强制币列表",__->{getUnForceCoins();});
        //获取矿工费信息
        addMenu("获取矿工费信息",__->{getCoinGasInfo();});

        //初始化钱币列表
        getAllCoins();
    }

    /**
     * 根据数据展示钱币列表
     * @param coinList 钱币列表
     */
    public void showCoinList(List<MBRBgCoin> coinList) {
        listAdapter.reload(coinList);
        Toast.makeText(this, "列表刷新成功", Toast.LENGTH_SHORT).show();
    }

    /**
     * 同步所有币列表
     */
    public void syncAllCoinList() {
        Disposable disposable = Observable.create(emitter-> {
            //同步所有货币列表，同步后更新本地数据库中货币信息
            MBRWallet.instance().syncAllCoinList();
            emitter.onNext("");emitter.onComplete();
        })
        .subscribeOn(Schedulers.newThread())
        .doOnSubscribe(__->{
            DialogUtil.showLoadingDialog();
        })
        .subscribeOn(AndroidSchedulers.mainThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(__-> {
            DialogUtil.dissmissLoadingDialog();
            DialogUtil.showMsg("同步成功");
        },error->{
            DialogUtil.dissmissLoadingDialog();
            DialogUtil.showMsg(error.getMessage());
        });
    }

    /**
     * 获取所有币种
     */
    public void getAllCoins() {
        try {
            //获取钱包内所有货币信息
            List<MBRBgCoin>  coinList = MBRWallet.instance().getAllCoins();
            showCoinList(coinList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取强制币
     */
    public void getForceCoins() {
        try {
            //获取钱包内所有强制货币信息
            List<MBRBgCoin>  coinList = MBRWallet.instance().getForceCoins();
            showCoinList(coinList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取非强制币
     */
    public void getUnForceCoins() {
        try {
            //获取钱包内所有非强制货币信息
            List<MBRBgCoin>  coinList = MBRWallet.instance().getUnForceCoins();
            showCoinList(coinList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取指定币种矿工费信息
     */
    public void getCoinGasInfo() {
        try {
            if(selectCoin == null) {
                DialogUtil.showMsg("请点击列表前的圆形按钮,选择要查询的币种");
                return;
            }
            String coinId = selectCoin.getCoinId();
            //获取指定货币的gas信息
            MBRCoinGasInfo gasInfo = MBRWallet.instance().getCoinGasInfo(coinId);
            //转换信息对象为json格式
            ObjectMapper mapper = new ObjectMapper();
            String gasInfoStr = mapper.writeValueAsString(gasInfo);
            JSONObject gasInfoJson = new JSONObject(gasInfoStr);
            //默认矿工费
            BigDecimal gasDefault = new BigDecimal(gasInfoJson.getString("gasDefault"));
            //矿工费上限
            BigDecimal gasMax = new BigDecimal(gasInfoJson.getString("gasMax"));
            //矿工费下限
            BigDecimal gasMin = new BigDecimal(gasInfoJson.getString("gasMin"));
            //不使用科学计数法
            NumberFormat nf = NumberFormat.getInstance();
            nf.setGroupingUsed(false);
            //显示信息
            DialogUtil.showMsg("钱币名称:" + selectCoin.getName() + "\n钱币编号:" + coinId + "\n默认矿工费:"
                    + gasDefault.toPlainString() + "\n矿工费上限:" + gasMax + "\n矿工费下限:" + gasMin);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 货币列表适配器
     */
    class CoinListAdapter extends BaseAdapter {

        /**
         * 当前选中按钮
         */
        private CompoundButton currentChecked;

        /**
         * 货币列表
         */
        private List<MBRBgCoin> coinList;

        public CoinListAdapter(List<MBRBgCoin> coinList) {
            this.coinList = coinList;
        }

        /**
         * 重新加载列表
         * @param coinList 货币列表
         */
        public void reload(List<MBRBgCoin> coinList) {
            this.coinList = coinList;
            notifyDataSetChanged();

            selectCoin = null;
            currentChecked = null;
        }

        @Override
        public int getCount() {
            return coinList.size();
        }

        @Override
        public MBRBgCoin getItem(int position) {
            return coinList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coin, null);
            }

            //选择按钮
            RadioButton checkRadio = convertView.findViewById(R.id.rb_check);

            //图标
            ImageView iconIv = convertView.findViewById(R.id.iv_coin_icon);
            //id
            TextView idTv = convertView.findViewById(R.id.tv_id);
            //缩写
            TextView abbrTv = convertView.findViewById(R.id.tv_abbr);
            //所属链
            TextView chainTv = convertView.findViewById(R.id.tv_chain);
            //名称
            TextView nameTv = convertView.findViewById(R.id.tv_name);
            //标准
            TextView standardTv = convertView.findViewById(R.id.tv_standard);
            //是否为强制币
            TextView forceTv = convertView.findViewById(R.id.tv_is_force);
            //地址token
            TextView token = convertView.findViewById(R.id.tv_token);

            //获取当前item展示的货币
            MBRBgCoin coin = coinList.get(position);

            //加载图片
            Glide.with(parent.getContext()).load(coin.getIcon()).into(iconIv);
            idTv.setText("id: " + coin.getCoinId());
            abbrTv.setText("缩写: " + coin.getAbbr());
            chainTv.setText("所属链: " + coin.getChain());
            nameTv.setText("名称: " + coin.getName());
            standardTv.setText("标准: " + coin.getStandard());
            forceTv.setText("是否强制: " + ("0".equals(coin.getIsForceShow()) ? "是" : "否"));
            token.setText("token: " + coin.getTokenAddress());

            checkRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if(isChecked) {
                        //设置上一个选中按钮状态为未选中
                        if(currentChecked != null) {
                            currentChecked.setChecked(false);
                        }
                        //保存当前选中
                        currentChecked = buttonView;
                        selectCoin = coin;
                    }
                }
            });

            return convertView;
        }
    }
}
