package com.axaet.rxhttp.retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * 自定义解析json数据工厂
 * date: 2018/1/5
 *
 * @author yuShu
 */

public final class CustomJsonConverterFactory extends Converter.Factory {


    public static CustomJsonConverterFactory create() {
        return new CustomJsonConverterFactory();
    }


    private CustomJsonConverterFactory() {
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        return new CustomResponseBodyConverter<>(type);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return new CustomRequestBodyConverter<>();
    }
}
