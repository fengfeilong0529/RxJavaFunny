package com.example.rxjavafunny;


import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 操作符示例
 */
public class OperatorHelper {
    private static final String TAG = "OperatorHelper";

    public static void testOperator() {
        /**
         * map()
         * 变换数据类型
         */
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                Log.e(TAG, "subscribe: ");
                e.onNext(false);
                e.onNext(false);
                e.onNext(true);
                e.onComplete();
            }
        })
                .map(new Function<Boolean, String>() {
                    @Override
                    public String apply(Boolean aBoolean) throws Exception {
                        String str;
                        if (aBoolean) {
                            str = "天选之人:" + aBoolean;
                        } else {
                            str = "草根" + aBoolean;
                        }
                        return str;
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.e(TAG, "accept: " + s);
                    }
                });


        /**
         * flatMap()
         * 将一个发送事件的上游Observable变换为多个发送事件的Observables，然后将它们发射的事件合并后放进一个单独的Observable里
         * （不能保证数据顺序）
         */
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
            }
        })
                .flatMap(new Function<Integer, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Integer integer) throws Exception {
                        List<String> list = new ArrayList<>();
                        for (int i = 0; i < 5; i++) {
                            list.add("我是数据:" + integer);
                        }
                        return Observable.fromIterable(list).delay(10, TimeUnit.MILLISECONDS);
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.e(TAG, "accept(flatMap): " + s);
                    }
                });


        /**
         * concatMap()
         * 将一个发送事件的上游Observable变换为多个发送事件的Observables，然后将它们发射的事件合并后放进一个单独的Observable里
         * （保证数据顺序原封不动）
         */
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
            }
        })
                .concatMap(new Function<Integer, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Integer integer) throws Exception {
                        List<String> list = new ArrayList<>();
                        for (int i = 0; i < 5; i++) {
                            list.add("我是数据:" + integer);
                        }
                        return Observable.fromIterable(list).delay(20, TimeUnit.MILLISECONDS);
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.e(TAG, "accept(concatMap): " + s);
                    }
                });


        /**
         * flatMap()
         * 合并请求
         */
//        api.register(new RegisterRequest())            //发起注册请求
//                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
//                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求注册结果
//                .doOnNext(new Consumer<RegisterResponse>() {
//                    @Override
//                    public void accept(RegisterResponse registerResponse) throws Exception {
//                        //先根据注册的响应结果去做一些操作
//                    }
//                })
//                .observeOn(Schedulers.io())                 //回到IO线程去发起登录请求
//                .flatMap(new Function<RegisterResponse, ObservableSource<LoginResponse>>() {
//                    @Override
//                    public ObservableSource<LoginResponse> apply(RegisterResponse registerResponse) throws Exception {
//                        return api.login(new LoginRequest());
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求登录的结果
//                .subscribe(new Consumer<LoginResponse>() {
//                    @Override
//                    public void accept(LoginResponse loginResponse) throws Exception {
//                        Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
//                    }
//                });


        /**
         * zip()
         * 将多个Obsersevable发送的时间逐一组合后，发送给Observer(Observer收到的数据条数取决于Observable中发送数据最少的那个)
         * 使用场景：如将多个请求返回的结果数据组合成新数据，传给下游使用
         */
        Observable.zip(Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                        e.onNext(1);
                        e.onNext(2);
                        e.onNext(3);
                    }
                }).subscribeOn(Schedulers.io()),    //指定子线程后多个Observable交替发送数据，否则多个Observable就在同一线程内，会按顺序依次发送
                Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> e) throws Exception {
                        e.onNext("a");
                        e.onNext("b");
                        e.onNext("c");
                        e.onNext("d");
                        e.onNext("e");
                    }
                }).subscribeOn(Schedulers.io()),    //指定子线程后多个Observable交替发送数据，否则多个Observable就在同一线程内，会按顺序依次发送
                new BiFunction<Integer, String, String>() {
                    @Override
                    public String apply(Integer integer, String s) throws Exception {
                        return integer + s;
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.e(TAG, "accept（zip）: " + s);
                    }
                });
    }
}
