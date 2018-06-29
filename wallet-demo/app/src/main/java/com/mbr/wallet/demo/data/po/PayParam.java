package com.mbr.wallet.demo.data.po;

import java.math.BigDecimal;

/**
 * wallet-demo
 * describe 支付属性
 * author 王超然 2018/6/25
 */
public class PayParam {

    /**
     * 目标地址
     */
    private String address;

    /**
     * 币种id
     */
    private String coinId;

    /**
     * 交易金额
     */
    private BigDecimal amount;

    /**
     * 矿工费
     */
    private BigDecimal gas;

    /**
     * 密码
     */
    private String pwd;

    /**
     * 备注
     */
    private String remark;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCoinId() {
        return coinId;
    }

    public void setCoinId(String coinId) {
        this.coinId = coinId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getGas() {
        return gas;
    }

    public void setGas(BigDecimal gas) {
        this.gas = gas;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}

