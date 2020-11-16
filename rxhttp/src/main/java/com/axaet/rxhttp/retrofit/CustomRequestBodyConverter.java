package com.axaet.rxhttp.retrofit;


import com.alibaba.fastjson.JSON;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Converter;

/**
 * 解析发送json数据
 * date: 2018/1/5
 *
 * @author yuShu
 */

final class CustomRequestBodyConverter<T> implements Converter<T, RequestBody> {
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");

    CustomRequestBodyConverter() {
    }

    @Override
    public RequestBody convert(T value) {
        byte[] content = JSON.toJSONBytes(value);
        return RequestBody.create(MEDIA_TYPE, content);
    }
}
