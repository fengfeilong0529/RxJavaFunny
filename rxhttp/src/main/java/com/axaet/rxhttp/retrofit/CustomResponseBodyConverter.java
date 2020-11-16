package com.axaet.rxhttp.retrofit;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.axaet.rxhttp.base.BaseResponse;

import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * 解析返回json数据
 * date: 2018/1/5
 *
 * @author yuShu
 */

final class CustomResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private Type mType;

    CustomResponseBodyConverter(Type mType) {
        this.mType = mType;
    }


    @Override
    public T convert(ResponseBody value) {
        try {
            String body = value.string();
            Log.i("feilong", "body: " + body);
            JSONObject json = JSON.parseObject(body);
            boolean hasCode = json.containsKey("errorCode");
            if (hasCode) {
                int code = json.getInteger("errorCode");
                //有些接口返回内容是标准的code,msg，data格式，统一处理
                String msg = json.getString("errorMsg");
                if (code == 0) {
                    boolean hasData = json.containsKey("data");
                    if (hasData) {
                        return JSON.parseObject(body, mType);
                    } else {
                        return (T) new BaseResponse<>(code, msg, msg);
                    }
                } else {
                    return (T) new BaseResponse<>(code, msg);
                }
            } else {
                //有些接口返回内容是不确定的，返回到上层处理
                return (T) new BaseResponse<>(0, "", body);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            value.close();
        }
    }
}
