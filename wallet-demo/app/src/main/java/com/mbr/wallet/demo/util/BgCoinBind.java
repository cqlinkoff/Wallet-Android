package com.mbr.wallet.demo.util;

import com.mbr.wallet.core.service.MBRAccountant;
import com.mbr.wallet.network.bean.MBRBgCoin;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * author: guojing on 2018/6/16
 * describe:coin bind
 */
public class BgCoinBind {

    private MBRBgCoin coin;

    public BgCoinBind(MBRBgCoin coin){
        this.coin = coin;
    }

    public String displayCount() {
        return displayCount(coin);
    }

    public String displayCountMinimal() {
        return displayCountMinimal(coin);
    }

    /**
     * 转换货币单位，显示时不会显示为很大的一个数字
     * @param coin 货币
     * @return 货币显示字符串
     */
    public static String displayCount(MBRBgCoin coin){
        if(coin == null){
            return null;
        }
        if (coin.getAmount() == null) return "0";
        return MBRAccountant.stripZeros(getAmountScaled2Display(coin));
    }

    public String displayCountMinimal(MBRBgCoin coin) {
        if(coin == null){
            return null;
        }
        if (coin.getAmount() == null) {
            return "0";
        }
        return MBRAccountant.stripZeros(coin.getAmount());
    }

    private static BigDecimal getAmountScaled2Display(MBRBgCoin coin) {
        return toDisplayAmount(coin.getAmount(), new BigInteger(coin.getDecimals()+""));
    }

    private static BigDecimal getAmountScaled(MBRBgCoin coin) {
        if(coin == null){
            return null;
        }
        return MBRAccountant.toReadable(coin.getAmount(), new BigInteger(coin.getDecimals()+""));
    }


    private static BigDecimal toReadable(BigDecimal bigDecimal, BigInteger decimals) {
        if (BigDecimal.ZERO.compareTo(bigDecimal) == 0) {
            return BigDecimal.ZERO;
        } else {
            int scale = decimals.intValue();
            return bigDecimal.scaleByPowerOfTen(-scale);
        }
    }

    public static BigDecimal toDisplayAmount(BigDecimal bigDecimal, BigInteger decimals) {
        BigDecimal value = toReadable(bigDecimal, decimals);
        if (BigDecimal.ZERO.compareTo(value) == 0) {
            return BigDecimal.ZERO;
        } else {
            int decimalsInt = decimals.intValue();
            decimalsInt = Math.min(decimalsInt, 6);
            BigDecimal atom = getAtom(decimalsInt);
            if (bigDecimal.compareTo(atom) < 0) return bigDecimal;
            return value.setScale(decimalsInt, BigDecimal.ROUND_DOWN);
        }
    }

    private static BigDecimal getAtom(int decimals) {
        if (decimals <= 0) return BigDecimal.ZERO;
        return BigDecimal.ONE.scaleByPowerOfTen(-decimals);
    }
}
