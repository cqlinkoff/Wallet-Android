package com.mbr.wallet.demo.view.transaction;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.TextView;

import com.mbr.wallet.core.MBRWallet;
import com.mbr.wallet.demo.R;
import com.mbr.wallet.demo.util.DialogUtil;
import com.mbr.wallet.demo.view.BaseActivity;
import com.mbr.wallet.network.bean.MBRBgCoin;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * wallet-demo
 * describe 示例下订单页面
 * author 王超然 2018/6/27
 */
public class OrderActivity extends BaseActivity {

    /**
     * 示例接口
     */
    private String url = "http://47.100.47.200:9927/home/prepay?coinId=34190899187000&amount=0.00001";

    /**
     * 订单信息
     */
    private TextView orderInfoTv;

    /**
     * 支付按钮
     */
    private Button payBtn;

    /**
     * 订单信息
     */
    private JSONObject orderInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_activity_order);

        orderInfoTv = findViewById(R.id.tv_order);
        payBtn = findViewById(R.id.btn_pay);

        //跳转到支付页面
        payBtn.setOnClickListener(__->{
            Intent intent = new Intent(OrderActivity.this, PayActivity.class);
            intent.putExtra("order", orderInfo.toString());
            startActivity(intent);
        });

        //获取订单信息
        getOrder();
    }

    /**
     * 获取订单信息
     */
    private void getOrder() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        DialogUtil.showLoadingDialog("正在获取订单信息");

        //通过示例接口请求订单信息
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DialogUtil.dissmissLoadingDialog();
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseStr = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //显示订单信息
                        showOrderInfo(responseStr);
                        DialogUtil.dissmissLoadingDialog();
                    }
                });

            }
        });

    }

    /**
     * 显示订单信息
     * @param responseStr 服务器响应数据
     */
    private void showOrderInfo(String responseStr) {
        try {
            JSONObject responseJson = new JSONObject(responseStr);
            //获取响应码
            String responseCode = responseJson.getString("code");
            //如果成功
            if(responseCode.equals("200")) {
                //获取订单信息
                String orderInfoStr = responseJson.getString("data");
                //获取转换为json格式的订单信息
                orderInfo = parseData(orderInfoStr);

                //根据货币id查询货币
                String coinName = queryCoinById(orderInfo.getString("coinId"));
                //在页面上显示
                orderInfoTv.setText("商户名称:" + orderInfo.getString("merchantName")
                                + "\n支付金额:" + orderInfo.getString("amount")
                                + "\n支付货币:" + coinName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析返回的订单数据
     * @param data 订单数据
     * @return 组装成json
     */
    private JSONObject parseData(String data) throws Exception{
        JSONObject dataJson = new JSONObject();

        String[] paramArray = data.split("&");
        for(String param: paramArray) {
            //分割参数
            String[] paramValue = param.split("=");
            dataJson.put(paramValue[0], paramValue[1]);
        }
        //必要的参数,保存订单原始字符串
        dataJson.put("o", data);

        return dataJson;
    }

    /**
     * 根据货币编号查询货币信息
     * @param coinId 货币编号
     * @return 货币
     */
    private String queryCoinById(String coinId) throws Exception {
        List<MBRBgCoin> coinList = MBRWallet.instance().getAllCoins();
        for(MBRBgCoin coin: coinList) {
            if(coin.getCoinId().equals(coinId)) {
                return coin.getAbbr();
            }
        }
        return "";
    }
}
