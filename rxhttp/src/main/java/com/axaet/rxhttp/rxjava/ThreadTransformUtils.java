package com.axaet.rxhttp.rxjava;


import com.axaet.rxhttp.base.ApiException;
import com.axaet.rxhttp.base.BaseResponse;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 从其他线程切换到主线程
 *
 * @author yuShu
 */
public class ThreadTransformUtils {
    public static <T> ObservableTransformer<T, T> defaultSchedulers() {
        return new ObservableTransformer<T, T>() {

            @Override
            public ObservableSource<T> apply(Observable<T> tObservable) {
                return tObservable
                        .unsubscribeOn(Schedulers.io())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }


    /**
     * 统一返回结果处理
     *
     * @param <T> 类型
     * @return Observable
     */
    public static <T> ObservableTransformer<BaseResponse<T>, T> handleResult() {
        return new ObservableTransformer<BaseResponse<T>, T>() {

            @Override
            public ObservableSource<T> apply(Observable<BaseResponse<T>> httpResponseObservable) {
                return httpResponseObservable.flatMap(new Function<BaseResponse<T>, ObservableSource<T>>() {
                    @Override
                    public ObservableSource<T> apply(@NonNull BaseResponse<T> tBaseResponse) throws Exception {
                        if (tBaseResponse != null) {
                            if (tBaseResponse.getErrorCode() == 0) {
                                return createData(tBaseResponse.getData());
                            } else {
                                return Observable.error(new ApiException(tBaseResponse.getErrorCode(), tBaseResponse.getErrorMsg()));
                            }
                        } else {
                            return Observable.error(new ApiException(-1, "json"));
                        }
                    }
                });
            }
        };
    }

    /**
     * 生成Observable
     *
     * @param <T> 类型
     * @return Observable
     */
    private static <T> Observable<T> createData(final T t) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> subscriber) throws Exception {
                try {
                    subscriber.onNext(t);
                    subscriber.onComplete();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}
