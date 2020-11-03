package com.example.rxjavafunny;


import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * 操作符示例
 * 参考：https://www.jianshu.com/p/0f2d6c2387c9
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

        backPressureTest();
    }

    /**
     * BackPressure背压
     * 控制数据流速，避免Observable发送数据太频繁，Observer接收处理不过来导致异常
     */
    public static void backPressureTest() {
        /**
         * filter()
         * 过滤操作符
         * 缺点：事件会丢失
         */
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; ; i++) {
                    e.onNext(i);
                }
            }
        })
                .subscribeOn(Schedulers.io())
                //filter()
                //过滤操作符
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer % 100 == 0;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer value) {
                        Log.e(TAG, "onNext(filter): " + value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError(filter): " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        /**
         *  sample()
         *  取样操作符，不管Observable发送数据多频繁，都只每隔2秒取一次数据发给Observer
         *  缺点：事件会丢失
         */
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; ; i++) {
                    e.onNext(i);
                }
            }
        })
                .subscribeOn(Schedulers.io())
                //sample()
                //取样操作符，不管Observable发送数据多频繁，都只每隔2秒取一次数据发给Observer
                .sample(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer value) {
                        Log.e(TAG, "onNext(sample): " + value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError(sample): " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        /**
         * 发送数据的源头处，控制发送频率
         * 事件不会丢失
         */
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; ; i++) {
                    e.onNext(i);
                    Thread.sleep(2000);
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer value) {
                        Log.e(TAG, "onNext(sleep 2000ms): " + value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError(sleep 2000ms): " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        //-----------------------------------------------sample采样-----------------------------------------------------------

        Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; ; i++) {
                    emitter.onNext(i);
                }
            }
        }).subscribeOn(Schedulers.io()).sample(2, TimeUnit.SECONDS); //进行sample采样

        Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("A");
            }
        }).subscribeOn(Schedulers.io());

        Observable.zip(observable1, observable2, new BiFunction<Integer, String, String>() {
            @Override
            public String apply(Integer integer, String s) throws Exception {
                return integer + s;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, s);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.w(TAG, throwable);
            }
        });

        //-----------------------------------------延时2秒-----------------------------------------------------------------

        Observable<Integer> observable01 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; ; i++) {
                    emitter.onNext(i);
                    Thread.sleep(2000);  //发送事件之后延时2秒
                }
            }
        }).subscribeOn(Schedulers.io());

        Observable<String> observable02 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("A");
            }
        }).subscribeOn(Schedulers.io());

        Observable.zip(observable01, observable02, new BiFunction<Integer, String, String>() {
            @Override
            public String apply(Integer integer, String s) throws Exception {
                return integer + s;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, s);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.w(TAG, throwable);
            }
        });

        //----------------------------------------------------------------------------------------------------------

    }
}
