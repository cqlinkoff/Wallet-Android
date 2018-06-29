package com.mbr.wallet.demo.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

/**
 * wallet-demo
 * describe
 * author 王超然 2018/6/22
 */
public class DialogUtil {

    private static Context context;

    /**
     * 进度框
     */
    private static ProgressDialog progressDialog;

    /**
     * 单个输入框监听
     */
    public interface OnSingleEditListener{
        /**
         * 完成
         * @param text 完成时输入的内容
         */
        public void onComplete(String text);
    }

    /**
     * 初始化
     * @param ctx 上下文
     */
    public static void init(Context ctx) {
        context = ctx;
    }

    /**
     * 显示加载进度框
     */
    public static void showLoadingDialog() {
        showLoadingDialog("正在处理");
    }

    /**
     * 根据输入内容显示加载进度框
     * @param msg 要显示的信息
     */
    public static void showLoadingDialog(String msg) {
        if(progressDialog == null || !progressDialog.isShowing()) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(msg);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    /**
     * 隐藏进度框
     */
    public static void dissmissLoadingDialog() {
        if(progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /**
     * 显示信息
     * @param msg 信息
     */
    public static void showMsg(String msg) {
        //设置显示文本
        TextView msgView = new TextView(context);
        msgView.setSelected(true);
        msgView.setClickable(true);
        msgView.setText(msg);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        msgView.setLayoutParams(lp);
        //显示对话框
        Dialog dialog = new AlertDialog.Builder(context).setMessage(msg).create();
        dialog.show();
    }

    /**
     * 显示信息
     * @param msg 提示信息
     * @param positiveListener 确认按钮监听
     */
    public static void showMsg(String msg, DialogInterface.OnClickListener positiveListener) {
        Dialog dialog = new AlertDialog.Builder(context).setMessage(msg).setPositiveButton("确认", positiveListener).create();
        dialog.show();
    }

    /**
     * 显示单个输入框
     * @param msg 提示信息
     * @param listener 点击确认监听
     */
    public static void showSingleEdit(String msg, OnSingleEditListener listener) {
        //创建输入框
        EditText inputEt = new EditText(context);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        inputEt.setLayoutParams(lp);
        //创建对话框
        Dialog dialog = new AlertDialog.Builder(context).setView(inputEt).setMessage(msg)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onComplete(inputEt.getText().toString());
                        dialogInterface.dismiss();
                    }
                }).create();
        //显示
        dialog.show();
    }
}
