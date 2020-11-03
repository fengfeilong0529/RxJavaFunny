package com.example.rxjavafunny;

import android.util.Log;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Flowable
 * 参考：https://www.jianshu.com/p/9b1304435564
 * https://www.jianshu.com/p/a75ecf461e02
 * Flowable里默认有一个大小为128的缓冲区, 当上下游工作在不同的线程中时, 上游就会先把事件发送到这个缓冲区中
 */
public class FlowableHelper {
    private static final String TAG = "FlowableHelper";
    private static Subscription mSubscription;

    public static void testFlowable() {

        /**
         * BackpressureStrategy.ERROR
         * 上下游流速不均衡时，抛出异常MissingBackpressureException
         */
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onNext(4);
                e.onComplete();
            }
        }, BackpressureStrategy.ERROR)  //上下游流速不均衡时，抛出异常MissingBackpressureException
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.e(TAG, "onSubscribe: ");
                        //向上游拉取多少条数据
                        mSubscription = s;
                        s.request(2);
//                        s.request(Long.MAX_VALUE);
//                        s.cancel();//取消接收数据
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "onNext(ERROR): " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.e(TAG, "onError: " + t.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete: ");
                    }
                });

        /**
         * BackpressureStrategy.BUFFER
         * 上游无论发送多少数据都会存在缓冲区中，缓冲区大小无限制
         * 弊端：如果上下游在不同线程，上游不断发数据下游不request时，存在OOM风险
         */
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 1000; i++) {
                    e.onNext(i);
                }
                e.onComplete();
            }
        }, BackpressureStrategy.BUFFER)  //设置背压策略为BURRER时，上游无论发送多少数据都会存在缓冲区中，缓冲区大小无限制
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.e(TAG, "onSubscribe: ");
                        //向上游拉取多少条数据
                        mSubscription = s;
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "onNext（BUFFER）: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.e(TAG, "onError: " + t.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete: ");
                    }
                });

        /**
         * BackpressureStrategy.DROP
         *
         */
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                for (int i = 0; ; i++) {
                    e.onNext(i);
                }
            }
        }, BackpressureStrategy.DROP)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.e(TAG, "onSubscribe: ");
                        //向上游拉取多少条数据
                        mSubscription = s;
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "onNext（DROP）: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.e(TAG, "onError: " + t.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete: ");
                    }
                });

        /**
         * BackpressureStrategy.LATEST
         *
         */
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                for (int i = 0; ; i++) {
                    e.onNext(i);
                }
            }
        }, BackpressureStrategy.LATEST)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.e(TAG, "onSubscribe: ");
                        //向上游拉取多少条数据
                        mSubscription = s;
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "onNext（DROP）: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.e(TAG, "onError: " + t.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete: ");
                    }
                });
    }

    /**
     * 请求上游数据，调用1次请求1条
     */
    public static void requestData() {
        if (mSubscription != null) {
            mSubscription.request(1);
        }
    }
}
