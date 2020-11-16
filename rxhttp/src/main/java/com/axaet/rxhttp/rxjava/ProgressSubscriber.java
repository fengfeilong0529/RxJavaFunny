package com.axaet.rxhttp.rxjava;

import android.content.Context;

import com.axaet.rxhttp.base.ApiException;
import com.axaet.rxhttp.view.ProgressCancelListener;
import com.axaet.rxhttp.view.ProgressDialogHandler;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;

/**
 * date: 2017/12/2
 *
 * @author yuShu
 */
public class ProgressSubscriber<T> implements ProgressCancelListener, Observer<T> {

    public enum EXCEPTION_ENUM {
        SOCKET_TIMEOUT_EXCEPTION(-1, "网络超时"),
        CONNECT_EXCEPTION(-2, "请求失败"),
        UNKNOWN_EXCEPTION(-3, "未知异常");

        private int code;
        private String msg;

        EXCEPTION_ENUM(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public String getMsg() {
            return this.msg;
        }

        public int getCode() {
            return this.code;
        }
    }


    private Disposable s;
    private ProgressDialogHandler mHandler;
    private SubscriberCallBack<T> mCallBack;

    public ProgressSubscriber(SubscriberCallBack<T> callBack, Context context, boolean isShowDialog) {
        this.mCallBack = callBack;
        if (isShowDialog) {
            this.mHandler = new ProgressDialogHandler(context, this, true);
        }
    }

    public ProgressSubscriber(SubscriberCallBack<T> subscriberCallBack) {

    }


    /**
     * send the message to handler for show dialog
     */
    private void showProgressDialog() {
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(ProgressDialogHandler.SHOW_PROGRESS_DIALOG, 500);
        }
    }

    /**
     * send the message to handler for dismiss dialog
     */
    private void dismissProgressDialog() {
        if (mHandler != null) {
            mHandler.obtainMessage(ProgressDialogHandler.DISMISS_PROGRESS_DIALOG).sendToTarget();
            mHandler = null;
        }
        cancel();
    }


    @Override
    public void onCancelProgress() {
        if (mHandler != null) {
            mHandler.removeMessages(ProgressDialogHandler.SHOW_PROGRESS_DIALOG);
        }
        cancel();
    }

    @Override
    public void onSubscribe(Disposable s) {
        if (DisposableHelper.validate(this.s, s)) {
            this.s = s;
            onStart();
        }
    }


    /**
     * Cancels the upstream's disposable.
     */
    private void cancel() {
        Disposable s = this.s;
        this.s = DisposableHelper.DISPOSED;
        s.dispose();
    }

    /**
     * Called once the subscription has been set on this observer; override this
     * to perform initialization.
     */
    private void onStart() {
        showProgressDialog();
    }

    @Override
    public void onNext(T t) {
        if (mCallBack != null) {
            mCallBack.onSuccess(t);
        }
    }

    @Override
    public void onError(Throwable e) {
        if (mHandler != null) {
            mHandler.removeMessages(ProgressDialogHandler.SHOW_PROGRESS_DIALOG);
        }
        dismissProgressDialog();
        if (e instanceof SocketTimeoutException) {
            if (mCallBack != null) {
                mCallBack.onError(EXCEPTION_ENUM.SOCKET_TIMEOUT_EXCEPTION.code, e.getLocalizedMessage());
            }
        } else if (e instanceof ConnectException) {
            if (mCallBack != null) {
                mCallBack.onError(EXCEPTION_ENUM.CONNECT_EXCEPTION.code, e.getLocalizedMessage());
            }
        } else if (e instanceof ApiException) {
            if (mCallBack != null) {
                mCallBack.onError(((ApiException) e).getCode(), e.getMessage());
            }
        } else {
            if (mCallBack != null) {
                mCallBack.onError(EXCEPTION_ENUM.UNKNOWN_EXCEPTION.code, e.getLocalizedMessage());
            }
        }
    }

    @Override
    public void onComplete() {
        if (mHandler != null) {
            mHandler.removeMessages(ProgressDialogHandler.SHOW_PROGRESS_DIALOG);
        }
        dismissProgressDialog();
    }


    public Disposable getDisposable() {
        return s;
    }
}