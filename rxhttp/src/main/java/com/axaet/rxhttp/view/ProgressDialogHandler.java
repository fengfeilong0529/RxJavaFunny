package com.axaet.rxhttp.view;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;

import com.axaet.iosdialog.view.ProgressDialog;
import com.axaet.rxhttp.R;

import java.lang.ref.WeakReference;


/**
 * show and dismiss dialog
 * date: 2017/12/2
 *
 * @author YuShu
 */
public class ProgressDialogHandler extends Handler {

    public static final int SHOW_PROGRESS_DIALOG = 1;
    public static final int DISMISS_PROGRESS_DIALOG = 2;

    private ProgressDialog pd;
    private boolean cancelable;
    private WeakReference<Context> weakReference;
    private ProgressCancelListener mProgressCancelListener;

    public ProgressDialogHandler(Context context, ProgressCancelListener mProgressCancelListener,
                                 boolean cancelable) {
        super();
        this.weakReference = new WeakReference<>(context);
        this.mProgressCancelListener = mProgressCancelListener;
        this.cancelable = cancelable;
    }

    /**
     * show dialog
     */
    private void initProgressDialog() {
        if (pd == null) {
            if (weakReference == null) {
                return;
            }
            Activity activity = (Activity) weakReference.get();
            if (activity == null || activity.isDestroyed()) {
                return;
            }
            pd = ProgressDialog.show(activity, activity.getString(R.string.dialog_msg_request), cancelable, null);
            if (cancelable) {
                pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        dialogInterface.cancel();
                        mProgressCancelListener.onCancelProgress();
                    }
                });
            }
        }
    }

    /**
     * cancel dialog
     */
    private void dismissProgressDialog() {
        if (pd != null) {
            if (pd.isShowing()){
                pd.dismiss();
            }
            pd = null;
        }
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case SHOW_PROGRESS_DIALOG:
                initProgressDialog();
                break;
            case DISMISS_PROGRESS_DIALOG:
                dismissProgressDialog();
                break;
            default:
        }
    }
}
