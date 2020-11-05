package com.example.rxjavafunny;


import android.annotation.SuppressLint;
import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observables.GroupedObservable;
import io.reactivex.schedulers.Schedulers;

/**
 * 操作符示例
 * 参考：https://www.jianshu.com/p/0f2d6c2387c9
 */
public class OperatorHelper {
    private static final String TAG = "OperatorHelper";

    @SuppressLint("CheckResult")
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
    @SuppressLint("CheckResult")
    public static void backPressureTest() {
        /**
         * filter()
         * 过滤操作符
         * 缺点：事件会丢失
         */
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 100; i++) {
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
                        return integer % 10 == 0;
                    }
                })
                .all(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        Log.e(TAG, "accept(filter all): " + integer);
                        return integer < 200;
                    }
                })
                .toObservable()
                .doOnNext(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        Log.e(TAG, "accept(filter doOnNext): " + aBoolean);
                    }
                })
                .map(new Function<Boolean, Integer>() {
                    @Override
                    public Integer apply(Boolean aBoolean) throws Exception {
                        return aBoolean ? 1 : 0;
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

    }

    /**
     * https://www.jianshu.com/p/b30de498c3cc
     */
    @SuppressLint("CheckResult")
    public static void testOtherOperator() {
        Observable observable1 = Observable.empty();//直接调用onCompleted。
        Observable observable2 = Observable.error(new RuntimeException());//直接调用onError。这里可以自定义异常
        Observable observable3 = Observable.never();//啥都不做

        //timer()   延时
        Observable.timer(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.e(TAG, "accept: " + aLong.toString());
                    }
                });

        //interval()    定时器任务 从0开始
        Observable.interval(10, TimeUnit.SECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.e(TAG, "accept: " + aLong.toString());
                    }
                });

        //range()  创建一个发射指定范围的整数序列的Observable<Integer>
        Observable.range(2, 5)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) {
                        Log.e(TAG, integer.toString());// 2,3,4,5,6  从2开始发射5个数据
                    }
                });

        //concat() 合并Observable，Observable.concat(a,b)等价于a.concatWith(b)
        Observable<Integer> observable11 = Observable.just(1, 2, 3, 4);
        Observable<Integer> observable22 = Observable.just(4, 5, 6);

        Observable.concat(observable11, observable22)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept(concat): " + integer);//1,2,3,4,4,5,6
                    }
                });

        observable11.concatWith(observable22)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept(concatWith): " + integer);//1,2,3,4,4,5,6
                    }
                });

        //startWith()   在数据序列的开头增加一项数据。startWith的内部也是调用了concat
        Observable.just(1, 2, 3)
                .startWith(6)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept(startWith): " + integer);//6,1,2,3
                    }
                });

        //merge()
        //将多个Observable合并为一个(不是按照添加顺序连接，而是按照时间线来连接)
        //一遇到异常将停止发射数据，发送onError通知
        Observable.merge(observable1, observable2)
                .subscribe(new Observer() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        //mergeDelayError()
        //将多个Observable合并为一个(不是按照添加顺序连接，而是按照时间线来连接)
        //将异常延迟到其它没有错误的Observable发送完毕后才发射
        Observable.mergeDelayError(observable1, observable2)
                .subscribe(new Observer() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        //combineLatest()       todo ???

        //filter() 过滤
        Observable.just(1, 2, "3")
                .ofType(Integer.class)  //先过滤类型，去除非integer数据
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer > 1;
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept: " + integer);    //2
                    }
                });

        //ofType() 过滤指定类型
        Observable.just(1, 2, "3")
                .ofType(Integer.class)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept: " + integer);    //1,2
                    }
                });

        //take() 只发射开始的N项数据或者一定时间内的数据。内部通过OperatorTake和OperatorTakeTimed过滤数据
        Observable.just(3, 4, 5, 6)
                .take(3)//发射前三个数据项
                .take(100, TimeUnit.MILLISECONDS)//发射100ms内的数据
                .subscribe();


        //takeLast() 只发射最后的N项数据或者一定时间内的数据
        Observable.just(3, 4, 5, 6, 7, 8)
                .takeLast(3)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        //6,7,8
                    }
                });

        //first() 只发射第一条或默认值
        Observable.just(1, 2, 3, 4, 5)
                .first(3)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept(first): " + integer); //1
                    }
                });

        Observable.empty()
                .first(3)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object obj) throws Exception {
                        Log.e(TAG, "accept(first empty): " + obj.toString()); //3 默认值
                    }
                });

        //first() 只发射第一条或默认值
        Observable.just(1, 2, 3, 4, 5)
                .last(3)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept(last): " + integer);  //5
                    }
                });

        //skip() 跳过开始的n条数据或一定时间内的数据
        Observable.just(1, 2, 3, 4, 5)
                .skip(2)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept(skip): " + integer);  //3,4,5
                    }
                });

        //skipLast() 跳过开始的n条数据或一定时间内的数据
        Observable.just(1, 2, 3, 4, 5)
                .skipLast(2)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept(skipLast): " + integer);  //1,2,3
                    }
                });

        //elementAt() 发射指定索引的数据
        Observable.just(1, 2, 3, 4, 5)
                .elementAt(2)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept(elementAt): " + integer);  //3
                    }
                });

        //ignoreElements() 丢弃所有数据，只发射错误或正常终止的通知
        Observable.just(1, 2, 3, 4, 5)
                .ignoreElements()
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });

        //distinct() 过滤重复数据
        Observable.just(1, 1, 1, 2, 2, 3, 4, 5, 5, 1)
                .distinct()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept(distinct): " + integer);  //1,2,3,4,5
                    }
                });

        //distinctUntilChanged() 过滤连续的重复数据
        Observable.just(1, 1, 1, 2, 2, 3, 4, 5, 5, 1)
                .distinctUntilChanged()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept(distinctUntilChanged): " + integer);  //1,2,3,4,5,1
                    }
                });

        //throttleFirst() 定期发射Observable发射的第一项数据    TODO 不太明白
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> subscriber) throws Exception {
                subscriber.onNext(1);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw Exceptions.propagate(e);
                }
                subscriber.onNext(2);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw Exceptions.propagate(e);
                }
                subscriber.onNext(3);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw Exceptions.propagate(e);
                }
                subscriber.onNext(4);
                subscriber.onNext(5);
                subscriber.onComplete();
            }
        })
                .throttleFirst(999, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept(throttleFirst): " + integer);    //结果为1,3,4
                    }
                });

        //throttleWithTimeout()/debounce()
        // 发射数据时，如果两次数据的发射间隔小于指定时间，就会丢弃前一次的数据,直到指定时间内都没有新数据发射时才进行发射

        //sample/throttleLast
        //定期发射Observable最近的数据，定时取样

        //timeout() 如果原始Observable过了指定的一段时长没有发射任何数据，就发射一个异常或者备用的Observable
//        Observable.create(new ObservableOnSubscribe<Integer>() {
//            @Override
//            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
//                e.onNext(1);
//                SystemClock.sleep(1000);
//                e.onNext(2);
//            }
//        })
//                .timeout(800, TimeUnit.MILLISECONDS)
//                .subscribe(new Consumer<Integer>() {
//                    @Override
//                    public void accept(Integer integer) throws Exception {
//                        Log.e(TAG, "accept(timeout): " + integer);    //1，会抛出异常
//                    }
//                });

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                SystemClock.sleep(1000);
                e.onNext(2);
            }
        })
                .timeout(800, TimeUnit.MILLISECONDS, Observable.just(6, 7, 8)) //指定备用Observable
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept(timeout Observable): " + integer);    //结果为1,6,7,8
                    }
                });

        /**
         * 条件/布尔操作
         */
        //all() 判断所有数据是否满足条件
        Observable.just(1, 2, 3, 4, 5)
                .all(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
//                        return integer < 10;  //true
                        return integer < 4;     //false
                    }
                })
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        Log.e(TAG, "accept(all): " + aBoolean);    //结果为false
                    }
                });

        //any() 判断所有数据是否满足任一条件
        Observable.just(1, 2, 3, 4, 5)
                .any(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
//                        return integer < 10;  //true
                        return integer < 0;  //false
//                        return integer < 4;     //true
                    }
                })
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        Log.e(TAG, "accept(any): " + aBoolean);    //结果为false
                    }
                });

        //isEmpty() 判空  用于判断Observable发射完毕时，有没有发射数据。有数据false，如果只收到了onComplete通知则为true
        Observable.just(1, 2, 3, 4, 5)
                .isEmpty()
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        Log.e(TAG, "accept(isEmpty): " + aBoolean);    //结果为false
                    }
                });

        //contains() 是否包含
        Observable.just(1, 2, 3, 4, 5)
                .contains(2)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        Log.e(TAG, "accept(contains): " + aBoolean);    //结果为true
                    }
                });

        //sequenceEqual() 用于判断两个Observable发射的数据是否相同（数据，发射顺序，终止状态）
        Observable.sequenceEqual(Observable.just(1, 2, 3), Observable.just(1, 2, 3))
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        Log.e(TAG, "accept(sequenceEqual): " + aBoolean);    //结果为true
                    }
                });

        //ambArray() 给定多个Observable，只让第一个发射数据的Observable发射全部数据，其他Observable将会被忽略(先发才收，后发不收)
        Observable.ambArray(Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                SystemClock.sleep(1000);
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();
            }
        }), Observable.just(8, 9, 3))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept(ambArray): " + integer);    //8,9,3
                    }
                });

        //switchIfEmpty() 如果原始Observable没有发送数据，就使用备用的Observable
        Observable.empty()
                .switchIfEmpty(Observable.just(1, 2, 3))
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        Log.e(TAG, "accept(switchIfEmpty): " + o.toString());    //1,2,3
                    }
                });

        //defaultIfEmpty() 如果原始Observable正常终止后仍然没有发射任何数据，就发射一个默认值,内部调用的switchIfEmpty

        //takeUntil() 当发射的数据满足某个条件后（包含该数据），或者第二个Observable发送完毕，终止第一个Observable发送数据
        Observable.just(1, 2, 3, 4, 5, 6)
                .takeUntil(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer == 3;
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept(takeUntil): " + integer);    //1,2,3
                    }
                });

//        Observable.create(new ObservableOnSubscribe<Integer>() {
//            @Override
//            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
//                SystemClock.sleep(1000);
//                e.onNext(1);
//                e.onNext(2);
//                e.onNext(3);
//                e.onComplete();
//            }
//        })
//                .takeUntil(Observable.empty())  //TODO 没太理解
//                .subscribe(new Consumer<Integer>() {
//                    @Override
//                    public void accept(Integer integer) throws Exception {
//                        Log.e(TAG, "accept(takeUntil Observable): " + integer);    //1,2,3
//                    }
//                });


        //takeWhile() 当发射的数据满足某个条件时（不包含该数据），Observable终止发送数据
        Observable.just(1, 2, 3, 4, 5, 6)
                .takeWhile(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer == 3;
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept(takeWhile): " + integer);    //1,2
                    }
                });

        //skipUntil()
        Observable.just(1, 2, 3, 4, 5, 6)
                .skipUntil(Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
//                        SystemClock.sleep(1000);
//                        e.onNext(11);
//                        e.onNext(12);
                    }
                }))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept(skipUntil): " + integer);    //1, 2, 3, 4, 5, 6
                    }
                });

        //skipWhile() 丢弃Observable发射的数据，直到一个指定的条件不成立（不丢弃条件数据）
        Observable.just(1, 2, 3, 4, 5, 6)
                .skipWhile(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer != 3;
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept(skipWhile): " + integer);    //3,4,5,6
                    }
                });


        /**
         * 聚合操作
         */
        //reduce()


        /**
         * 转换操作
         */

        /**
         * 变换操作
         */
        //flatMap() 将Observable发射的数据变换为Observables集合，然后将这些Observable发射的数据平坦化的放进一个单独的Observable，内部采用merge合并
        Observable.just(1, 2, 3)
                .flatMap(new Function<Integer, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(final Integer integer) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<String>() {
                            @Override
                            public void subscribe(ObservableEmitter<String> e) throws Exception {
                                e.onNext("我是组合数据-》" + integer * 10);
                                e.onNext("我是组合数据%-》" + integer * 100);
                            }
                        });
                    }
                })
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        Log.e(TAG, "accept(flatMap): " + o.toString());    //
                    }
                });

        //flatMapIterable() flatMap的作用一样，只不过生成的是Iterable而不是Observable
        Observable.just(1, 2, 3)
                .flatMapIterable(new Function<Integer, Iterable<?>>() {
                    @Override
                    public Iterable<?> apply(Integer integer) throws Exception {
                        return Arrays.asList(integer * 10 + "", integer * 100 + "");
                    }
                })
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        Log.e(TAG, "accept(flatMapIterable): " + o.toString());    //10,100,20,200,30,300
                    }
                });

        //switchMap() 和flatMap很像，将Observable发射的数据变换为Observables集合，当原始Observable发射一个新的数据（Observable）时，它将取消订阅前一个Observable
        //todo 不太明白

        //scan 与reduce很像，对Observable发射的每一项数据应用一个函数，然后按顺序依次发射每一个值
        Observable.just(1, 2, 3, 4, 5)
                .scan(new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer, Integer integer2) throws Exception {
                        Log.e(TAG, "(scan)integer: " + integer + "    ,integer2: " + integer2);
                        return integer + integer2;
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept(scan): " + integer);    //1,3,6,10,15
                    }
                });

        //groupBy() 将Observable分拆为Observable集合，将原始Observable发射的数据按Key分组，每一个Observable发射一组不同的数据
        Observable.just(1, 2, 3, 4, 5)
                .groupBy(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        return integer % 2 == 0 ? "偶数" : "奇数";
                    }
                })
                .subscribe(new Consumer<GroupedObservable<String, Integer>>() {
                    @Override
                    public void accept(GroupedObservable<String, Integer> stringIntegerGroupedObservable) throws Exception {
                        final String key = stringIntegerGroupedObservable.getKey();
//                        Log.e(TAG, "accept(groupBy key): " + key);
                        stringIntegerGroupedObservable.subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Exception {
                                Log.e(TAG, "accept(groupBy key): " + key + "     (value): " + integer);
                            }
                        });
                    }
                });

        //buffer() 定期从Observable收集数据到一个集合，然后把这些数据集合打包发射，而不是一次发射一个
        Observable.just(1, 2, 3, 4, 5, 6, 7)
                .buffer(3)
                .subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(List<Integer> integers) throws Exception {
                        Log.e(TAG, "accept(buffer): " + Arrays.toString(new List[]{integers}));//1, 2, 3] , [4, 5, 6] , [7]
                    }
                });

        //window() 定期将来自Observable的数据分拆成一些Observable窗口，然后发射这些窗口，而不是每次发射一项
        Observable.just(1, 2, 3, 4, 5, 6, 7)
                .window(3)
                .subscribe(new Consumer<Observable<Integer>>() {
                    @Override
                    public void accept(Observable<Integer> integerObservable) throws Exception {
                        //拆分成3个Observable
                        integerObservable.subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Exception {
                                Log.e(TAG, "accept(window): " + integer);       //1, 2, 3, 4, 5, 6, 7
                            }
                        });
                    }
                });


        /**
         * 错误处理/重试机制
         */
        //onErrorResumeNext() 当原始Observable在遇到错误时，使用备用Observable
        Observable.just(1, 2, "a", 3, 4)
                .cast(Integer.class)
                .onErrorResumeNext(Observable.just(5, 6, 7))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept(onErrorResumeNext): " + integer);    //1,2,5,6,7
                    }
                });

        //onExceptionResumeNext()
        // 当原始Observable在遇到异常时，使用备用的Observable。与onErrorResumeNext类似，区别在于onErrorResumeNext可以处理所有的错误，onExceptionResumeNext只能处理异常

        //onErrorReturn() 当原始Observable发送数据出错时，发射一个特定的数据
        Observable.just(1, "a", 2, 3)
                .cast(Integer.class)
                .onErrorReturn(new Function<Throwable, Integer>() {
                    @Override
                    public Integer apply(Throwable throwable) throws Exception {
                        return 100;
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept(onErrorReturn): " + integer);
                    }
                });

        //retry() 当原始Observable在遇到错误时进行重试，重新发射Observable所有数据
        Observable.just(1, 2, "a", 5, 3)
                .cast(Integer.class)
                .retry(3)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "accept(retry): " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "accept(retry): onError(" + e.getMessage() + ")");
                    }

                    @Override
                    public void onComplete() {

                    }
                });//1,2,1,2,1,2,1,2,onError(...)

        //retryWhen() 当原始Observable在遇到错误，将错误传递给另一个Observable来决定是否要重新订阅这个Observable,内部调用的是retry
//        Observable.just(1, 2, "a", 5, 3)
//                .cast(Integer.class)
//                .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
//                    @Override
//                    public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
//                        return throwableObservable.delay(1, TimeUnit.SECONDS);
////                        return throwableObservable;
//                    }
//                })
//                .subscribe(new Consumer<Integer>() {
//                    @Override
//                    public void accept(Integer integer) throws Exception {
//                        Log.e(TAG, "accept(retryWhen): " + integer);
//                    }
//                });

        /**
         * 连接操作 todo 后续了解
         */
        //ConnectableObservable.connect()
        //Observable.publish()
        //Observable.replay()
        //ConnectableObservable.refCount()

        /**
         * 阻塞操作
         */
        //BlockingObservable

        /**
         * 工具集
         */
        //materialize()
        //dematerialize()
        //timestamp()
        //timeInterval()
        //serialize()
        //cache()
        //observeOn()
        //subscribeOn()
        //doOnEach()
        //doOnCompleted()
        //doOnError()
        //doOnTerminate()
        //doOnSubscribe()
        //doOnUnsubscribe()
        //finallyDo/doAfterTerminate()
        //delay()
        //delaySubscription()
        //using()
        //single/singleOrDefault()  强制返回单个数据，否则抛出异常或默认数据

        /**
         * 阻塞操作
         */

    }
}
