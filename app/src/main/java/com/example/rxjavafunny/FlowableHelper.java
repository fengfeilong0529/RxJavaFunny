package com.example.rxjavafunny;

import android.util.Log;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

/**
 * Flowable
 * 参考：https://www.jianshu.com/p/9b1304435564
 * Flowable里默认有一个大小为128的缓冲区, 当上下游工作在不同的线程中时, 上游就会先把事件发送到这个缓冲区中
 */
public class FlowableHelper {
    private static final String TAG = "FlowableHelper";
    private static Subscription mSubscription;

    public static void testFlowable() {

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
                        Log.e(TAG, "onNext: " + integer);
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
