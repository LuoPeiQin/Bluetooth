package com.luo.bluetooth.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.View;

import com.luo.bluetooth.R;
import com.luo.bluetooth.customview.searchble.Effectstype;
import com.luo.bluetooth.customview.searchble.NiftyDialogBuilder;

import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * Created by Bruce on 2017/4/12 0012.
 */
public class DialogUtils {
    private static final short DIALOG_SHOW_TIME_SHORT = 1500;
    private static final short DIALOG_SHOW_TIME_LONG = 2500;
    private static NiftyDialogBuilder dialogBuilder;
    private static SweetAlertDialog dialog;

    public static void showProgressbarDialog(Context context) {
        showProgressbarDialog(context, null);
    }

    public static void showProgressbarDialog(Context context, int titleId) {
        showProgressbarDialog(context, context.getString(titleId));
    }

    public static void showProgressbarDialog(Context context, String title) {
        if (!checkReuseable(context, SweetAlertDialog.PROGRESS_TYPE)){
            dialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        }
        dialog.setTitleText(title);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    public static void showNormalDialog(Context context, int text) {
        showNormalDialog(context, context.getString(text));
    }

    public static void showNormalDialog(Context context, String text) {
        showNormalDialog(context, text, new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                dismissDialog(true);
            }
        });
    }

    public static void showNormalDialog(Context context, String text, SweetAlertDialog.OnSweetClickListener listener) {
        if (!checkReuseable(context, SweetAlertDialog.NORMAL_TYPE)){
            dialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        }
        dialog.setContentText(text)
                .setTitleText(null)
                .setConfirmClickListener(listener)
                .show();
    }

    public static SweetAlertDialog showNormalDialog(Context context, String text,
                                                    SweetAlertDialog.OnSweetClickListener listener,
                                                    SweetAlertDialog.OnSweetClickListener cancelListener) {
        if (!checkReuseable(context, SweetAlertDialog.NORMAL_TYPE)){
            dialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        }
        dialog.setContentText(text)
                .setTitleText(null)
                .setConfirmClickListener(listener)
                .setCancelClickListener(cancelListener)
                .show();
        return dialog;
    }

    public static void showNormalDialog(Context context, String title, String text, SweetAlertDialog.OnSweetClickListener listener) {
        if (!checkReuseable(context, SweetAlertDialog.NORMAL_TYPE)){
            dialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        }
        dialog.setContentText(text)
                .setTitleText(title)
                .setConfirmClickListener(listener)
                .show();
    }

    public static SweetAlertDialog showNormalDialog(Context context, String title, String text,
                                                    String confirmText,
                                                    SweetAlertDialog.OnSweetClickListener listener,
                                                    String cancelText,
                                                    SweetAlertDialog.OnSweetClickListener cancelListener) {
        if (!checkReuseable(context, SweetAlertDialog.NORMAL_TYPE)){
            dialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        }
        dialog.setContentText(text)
                .setTitleText(title)
                .setConfirmText(confirmText)
                .setConfirmClickListener(listener)
                .setCancelText(cancelText)
                .setCancelClickListener(cancelListener)
                .show();
        return dialog;
    }

    public static SweetAlertDialog showNormalDialog(Context context, String title,
                                                    String confirmText,
                                                    SweetAlertDialog.OnSweetClickListener listener,
                                                    String cancelText,
                                                    SweetAlertDialog.OnSweetClickListener cancelListener) {
        if (!checkReuseable(context, SweetAlertDialog.NORMAL_TYPE)){
            dialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        }
        dialog.setTitleText(title)
                .setConfirmText(confirmText)
                .setConfirmClickListener(listener)
                .setCancelText(cancelText)
                .setCancelClickListener(cancelListener)
                .show();
        return dialog;
    }

    public static void showLongErrorDialog(final Context context, String title) {
        showLongErrorDialog(context, title, new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                dismissDialog(true);
            }
        });
    }

    public static void showLongErrorDialog(Context context, String title, SweetAlertDialog.OnSweetClickListener listener) {
        showErrorDialog(context, title, DIALOG_SHOW_TIME_LONG, listener);
    }

    public static void showShortErrorDialog(final Context context, int title) {
        showShortErrorDialog(context, context.getString(title));
    }

    public static void showShortErrorDialog(final Context context, String title) {
        showShortErrorDialog(context, title, new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                dismissDialog(true);
            }
        });
    }

    public static void showShortErrorDialog(Context context, String title, SweetAlertDialog.OnSweetClickListener listener) {
        showErrorDialog(context, title, DIALOG_SHOW_TIME_SHORT, listener);
    }

    public static void showErrorDialog(Context context, int title) {
        showErrorDialog(context, context.getString(title));
    }

    public static void showErrorDialog(Context context, String title) {
        showErrorDialog(context, title, new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                dismissDialog(true);
            }
        });
    }

    public static void showErrorDialog(Context context, String title, short duration, SweetAlertDialog.OnSweetClickListener listener) {
        showErrorDialog(context, title, listener);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissDialog(true);
            }
        }, duration);
    }

    public static void showErrorDialog(Context context, String title, SweetAlertDialog.OnSweetClickListener listener) {
        if (!checkReuseable(context, SweetAlertDialog.ERROR_TYPE)){
            dialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
        }
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitleText(title).setConfirmClickListener(listener);
        dialog.show();
    }

    public static void showErrorDialog(Context context, String title,String content, SweetAlertDialog.OnSweetClickListener listener) {
        if (!checkReuseable(context, SweetAlertDialog.ERROR_TYPE)){
            dialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
        }
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitleText(title).setContentText(content).setConfirmClickListener(listener);
        dialog.show();
    }

    public static void showShortSuccessDialog(Context context, int title) {
        showShortSuccessDialog(context, context.getString(title));
    }

    public static void showShortSuccessDialog(Context context, String title) {
        showShortSuccessDialog(context, title, new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                dismissDialog(true);
            }
        });
    }

    public static void showShortSuccessDialog(Context context, String title, SweetAlertDialog.OnSweetClickListener listener) {
        showSuccessDialog(context, title, DIALOG_SHOW_TIME_SHORT, listener);
    }

    public static void showSuccessDialog(Context context, int title) {
        showSuccessDialog(context, context.getString(title));
    }

    public static void showSuccessDialog(Context context, String title) {
        showSuccessDialog(context, title, new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                dismissDialog(true);
            }
        });
    }

    public static void showSuccessDialog(Context context, String title, short duration, SweetAlertDialog.OnSweetClickListener listener) {
        showSuccessDialog(context, title, listener);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissDialog(true);
            }
        }, duration);
    }

    public static void showSuccessDialog(Context context, int title, SweetAlertDialog.OnSweetClickListener listener) {
        showSuccessDialog(context, context.getString(title), listener);
    }

    public static void showSuccessDialog(Context context, String title, SweetAlertDialog.OnSweetClickListener listener) {
        if (!checkReuseable(context, SweetAlertDialog.SUCCESS_TYPE)){
            dialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
        }
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitleText(title)
                .setConfirmClickListener(listener)
                .show();
    }

    public static void showSuccessDialog(Context context, String title, String content, SweetAlertDialog.OnSweetClickListener listener) {
        if (!checkReuseable(context, SweetAlertDialog.SUCCESS_TYPE)){
            dialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
        }
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitleText(title)
                .setContentText(content)
                .setConfirmClickListener(listener)
                .show();
    }

    public static void showShortWarningDialog(Context context, int title) {
        showShortWarningDialog(context, context.getString(title));
    }

    public static void showShortWarningDialog(Context context, String title) {
        showShortWarningDialog(context, title, new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                dismissDialog(true);
            }
        });
    }

    public static void showShortWarningDialog(Context context, String title, SweetAlertDialog.OnSweetClickListener listener) {
        showWarningDialog(context, title, DIALOG_SHOW_TIME_SHORT, listener);
    }

    public static void showWarningDialog(Context context, int id) {
        showWarningDialog(context, context.getString(id));
    }

    public static void showWarningDialog(Context context, String title) {
        showWarningDialog(context, title, new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                dismissDialog(true);
            }
        });
    }

    public static void showWarningDialog(Context context, String title, short duration, SweetAlertDialog.OnSweetClickListener listener) {
        showWarningDialog(context, title, listener);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissDialog(true);
            }
        }, duration);
    }

    public static void showWarningDialog(Context context, String title, SweetAlertDialog.OnSweetClickListener listener) {
        if (!checkReuseable(context, SweetAlertDialog.WARNING_TYPE)){
            dialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        }
        dialog.setTitleText(title).setConfirmClickListener(listener);
        dialog.show();
    }

    public static void showWarningDialog(Context context, String title,String content, SweetAlertDialog.OnSweetClickListener listener) {
        if (!checkReuseable(context, SweetAlertDialog.WARNING_TYPE)){
            dialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        }
        dialog.setTitleText(title).setContentText(content).setConfirmClickListener(listener);
        dialog.show();
    }

    public static void showWarningDialog(Context context, String title,String content, String confirmText,SweetAlertDialog.OnSweetClickListener confirmListener,
                                         String cancelText, SweetAlertDialog.OnSweetClickListener cancelListener) {
        if (!checkReuseable(context, SweetAlertDialog.WARNING_TYPE)){
            dialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        }
        dialog.setTitleText(title)
                .setContentText(content)
                .setConfirmText(confirmText)
                .setConfirmClickListener(confirmListener)
                .setCancelText(cancelText)
                .setCancelClickListener(cancelListener);
        dialog.show();
    }

    public static SweetAlertDialog showCancelableDialog(Context context, String text, SweetAlertDialog.OnSweetClickListener listener){
        if (!checkReuseable(context, SweetAlertDialog.NORMAL_TYPE)){
            dialog = new SweetAlertDialog(context,SweetAlertDialog.NORMAL_TYPE);
        }
        dialog.setCancelText("取消")
                .setConfirmText("确认")
                .setContentText(text)
                .setTitleText(null)
                .setCancelClickListener(listener);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        return dialog;
    }

    private static boolean checkReuseable(Context context, int dialogType){
        if (dialog==null)
            return false;
        if (!isMatchingCurrentContext(context) || dialog.getAlerType() != dialogType){
            dialog.dismiss();
            dialog = null;
            return false;
        }
        return true;
    }

    public static boolean isMatchingCurrentContext(Context context){
        return ((ContextWrapper)dialog.getContext()).getBaseContext() == context;
    }

    public static Dialog getDialog(){
        return dialog;
    }

    public static void dismissDialog() {
        dismissDialog(false);
    }

    public static void dismissDialog(boolean animate) {
        if (dialog!=null){
            if (animate)
                dialog.cancel();
            else
                dialog.dismiss();
            dialog = null;
        }
    }

    public static void dismissMatchCurrentDialog(Context context){
        if (dialog!=null && isMatchingCurrentContext(context))
            dismissDialog(false);
    }

    /******************************************NiftyDialogBuilder************************************************/

    /**
     * 弹出提示框
     *
     * @param titleId
     * @param contentId
     */
    public static void showDialog(Context context, int titleId, int contentId) {
        String title = context.getString(titleId);
        String content = context.getString(contentId);
        showNiftyTipDialog(context, title, content);
    }

    public static void showNiftyTipDialog(Context context, String title, String content) {
        if (dialogBuilder == null) {
            dialogBuilder = NiftyDialogBuilder.getInstance(context);
        }
        dialogBuilder.withTitle(title)//.withTitle(null)  no title
                .removeAllCustomView()
                .withEffect(Effectstype.values()[9])
                .withTitleColor("#FFFFFF")                                  //def
                .withDividerColor("#11000000")                              //def
                .withMessage(content)                     //.withMessage(null)  no Msg
                .withMessageColor("#FFFFFFFF")                              //def  | withMessageColor(int resid)
                .withDialogColor("#33ffff")                               //def  | withDialogColor(int resid)
//                .withIcon(ContextCompat.getDrawable(context, R.mipmap.icon_staginfo))
                .withDuration(700)                                          //def
                //   .withEffect(effect)                                         //def Effectstype.Slidetop
                .withButton1Text(context.getString(R.string.btn_ok))                                    //def gone
                .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
                //  .setCustomView(R.layout.custom_view,v.getContext())         //.setCustomView(View or ResId,context)
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                    }
                })
                .show();
    }

    public static void showNiftyCustonDialog(Context context, int title, View view) {
        showNiftyCustonDialog(context, context.getString(title), view);
    }

    public static void showNiftyCustonDialog(Context context, String title, View view) {
        showNiftyCustonDialog(context, title, view, null);
    }

    public static void showNiftyCustonDialog(Context context, String title, View view, DialogInterface.OnDismissListener listener) {
        dialogBuilder = NiftyDialogBuilder.getInstance(context);
        dialogBuilder.withTitle(title)//.withTitle(null)  no title
                .setCustomView(view, context)
                .withEffect(Effectstype.values()[9])
                .withTitleColor("#FFFFFF")                                  //def
                .withDividerColor("#FFFFFF")                              //def
                .withMessage(null)                     //.withMessage(null)  no Msg
                .withMessageColor("#FFFFFF")                              //def  | withMessageColor(int resid)
                .withDialogColor("#1fbaf3")                               //def  | withDialogColor(int resid)
//                .withIcon(ContextCompat.getDrawable(context, R.mipmap.icon_staginfo))
                .withDuration(700)                                          //def
                //   .withEffect(effect)                                         //def Effectstype.Slidetop
                .withButton1Text(context.getString(R.string.btn_cancel))                                    //def gone
                .isCancelableOnTouchOutside(false)                           //def    | isCancelable(true)
                //  .setCustomView(R.layout.custom_view,v.getContext())         //.setCustomView(View or ResId,context)
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                    }
                })
                .setOnDismissListener(listener);
        dialogBuilder.show();
    }

    public static void dismissNiftyDialog() {
        if (dialogBuilder != null) {
            dialogBuilder.dismiss();
        }
    }

}
