package com.example.rxjavafunny;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.rxjavafunny.helper.rxjava2.FlowableHelper;
import com.example.rxjavafunny.helper.rxjava2.OperatorHelper;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 官网学习笔记（ReactiveX/RxJava文档中文版）：https://mcxiaoke.gitbooks.io/rxdocs/content/
 * 推荐Rxjava系列：https://www.jianshu.com/u/c50b715ccaeb
 * RxJava 2.0操作符整理：https://www.jianshu.com/p/b30de498c3cc
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Disposable mDisposable;
    private CompositeDisposable mCompositeDisposable;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test();

//        RetrofitManager.create(HttpService.class)
//                .login("fengfeilong", "123456")
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<BaseResponse>() {
//                    @Override
//                    public void accept(BaseResponse baseResponse) throws Exception {
//                        Log.e(TAG, "accept: " + baseResponse.toString());
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        Log.e(TAG, "accept: " + throwable.getMessage());
//                    }
//                });
    }

    @SuppressLint("CheckResult")
    private void test() {
        /**
         * onError()与onComplete()为互斥关系
         */
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                Log.i(TAG, "subscribe: ----------");
                SystemClock.sleep(10000);
                e.onNext("hi");
                e.onNext("nihao");
                e.onNext("1");
                e.onNext("2");
                e.onNext("3");
                e.onNext("4");
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())               //执行在子线程Schedulers.io()与Schedulers.newThread()类似，Schedulers.io()可重用效率更高
                .observeOn(AndroidSchedulers.mainThread())  //回调在主线程
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(TAG, "onSubscribe: ");
                        mDisposable = d;
//                        addDisposable(d);
                    }

                    @Override
                    public void onNext(String value) {
                        Log.i(TAG, "onNext: " + value);

                        if (TextUtils.equals("1", value) && mDisposable != null) {
                            Toast.makeText(MainActivity.this, value, Toast.LENGTH_SHORT).show();
                            mDisposable.dispose();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });


        /**
         * subscribeOn()多次调用，仅第1次有效
         * observeOn()多次调用，多次生效
         */
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                Log.i(TAG, "subscribe22: ----------");
                e.onNext("a");
                e.onNext("b");
                e.onNext("c");
                e.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {     //若只需onNext()，则创建Consumer对象即可
                    @Override
                    public void accept(String s) throws Exception {
                        Log.i(TAG, "accept: " + s);
                    }
                });

        /**
         * Schedulers.io() 代表io操作的线程, 通常用于网络,读写文件等io密集型的操作
         * Schedulers.computation() 代表CPU计算密集型的操作, 例如需要大量计算的操作
         * Schedulers.newThread() 代表一个常规的新线程
         * AndroidSchedulers.mainThread() 代表Android的主线程
         */
        Observable.just(1)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept: 当前Observer所处线程=" + Thread.currentThread().getName());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept:222 当前Observer所处线程=" + Thread.currentThread().getName());
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept: " + integer + "  Observer线程-->  " + Thread.currentThread().getName());
                    }
                });

        /**
         * onErrorResumeNext()   当原始Observable在遇到错误时，使用备用Observable
         */
        Observable.just(1, 2, "a", 3, 4)
                .cast(Integer.class)
                .onErrorResumeNext(Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                        Toast.makeText(MainActivity.this, "出错了，启用另一个Observable", Toast.LENGTH_SHORT).show();
                    }
                }))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept(onErrorResumeNext2): " + integer);    //1,2,5,6,7
                    }
                });


        OperatorHelper.testOperator();
        OperatorHelper.testOtherOperator();
        FlowableHelper.testFlowable();
    }

    /**
     * 当有多个Disposable时，添加到容器CompositeDisposable中，销毁时销毁CompositeDisposable即可
     *
     * @param disposable
     */
    private void addDisposable(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //界面销毁时，停止接收事件，停止UI操作
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        if (mCompositeDisposable != null) {
//            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
        }
    }
}
