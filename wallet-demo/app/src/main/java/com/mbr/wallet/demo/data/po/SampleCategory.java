package com.mbr.wallet.demo.data.po;

/**
 * SDKDemo
 * describe 示例分类
 * author 王超然 2018/6/20
 */
public class SampleCategory {

    /**
     * 显示名称
     */
    private String name;

    /**
     * 关联的示例页面
     */
    private Class activity;

    public SampleCategory(String name, Class activity) {
        this.name = name;
        this.activity = activity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class getActivity() {
        return activity;
    }

    public void setActivity(Class activity) {
        this.activity = activity;
    }
}
