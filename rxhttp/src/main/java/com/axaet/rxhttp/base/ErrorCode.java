package com.axaet.rxhttp.base;

/**
 * date: 2018/3/1
 *
 * @author yuShu
 */

public class ErrorCode {


    /**
     * 网络错误
     */
    public static final int NETWORK_ERROR = -1;
    /**
     * 数据为null
     */
    public static final int RETURN_NULL_DATA = -3;


    /**
     * 操作成功
     */
    public static final int OPERATION_SUCCESS = 0;
    /**
     * 请求参数错误
     */
    public static final int PARAMETER_ERROR = 1;
    /**
     * 服务器sql执行失败
     */
    public static final int SQL_EXECUTION_EXCEPTION = 2;

    /**
     * 验证码错误
     */
    public static final int VERIFY_CODE_ERROR = 3;
    /**
     * 发送验证码失败
     */
    public static final int VERIFY_CODE_SENT_FAILED = 4;
    /**
     * token失效
     */
    public static final int TOKEN_VERIFY_FAILED = 5;

    /**
     * 用户名已存在
     */
    public static final int ACCOUNT_DUPLICATE = 6;
    /**
     * 绑定重复
     */
    public static final int BINDING_REPEATED = 7;
    /**
     * 没有该用户
     */
    public static final int NO_THIS_USER = 8;
    /**
     * 设备号错误
     */
    public static final int WRONG_DEVICE_NUMBER = 9;
    /**
     * 更新设备信息出错
     */
    public static final int FAILED_UPDATE_INFO = 10;
    /**
     * 删除失败
     */
    public static final int FAILED_DELETE = 11;
    /**
     * 用户重复
     */
    public static final int USER_DUPLICATE = 12;
    /**
     * 产品id错误
     */
    public static final int PRODUCT_ID_ERROR = 13;

    /**
     * 账户已禁用
     */
    public static final int ACCOUNT_DISABLED = 15;
    /**
     * 用户未激活
     */
    public static final int ACCOUNT_NOT_ACTIVATED = 16;
    /**
     * 用户名或密码错误
     */
    public static final int WRONG_ACCOUNT_PASSWORD = 17;
    /**
     * 更新token失败
     */
    public static final int UPDATE_TOKEN_FAILED = 18;
    /**
     * 设备号没有授权
     */
    public static final int DEVNO_NO_AUTHORIZATION = 20;

    /**
     * 添加智能设备如果和就执行出现同一个设备，不能被添加
     */
    public static final Integer ERR_CODE_DEVICE_REPET = 22;
    /**
     * 手动类型不能添加定时条件
     */
    public static final Integer ERR_CODE_MANUAL_NO_TIMING = 23;
    /**
     * 手动类型不能添加智能条件
     */
    public static final Integer ERR_CODE_MANUAL_NO_NOOPSYCHE = 24;
    /**
     * 自动类型至少添加一个智能条件
     */
    public static final Integer ERR_CODE_SELFMOTION_LEAST_ONE = 25;
    /**
     * 智能条件数据格式不符合
     */
    public static final Integer ERR_CODE_CONDITION_FORMAT = 26;
    /**
     * 智能任务数据格式不符合
     */
    public static final Integer ERR_CODE_TASK_FORMAT = 27;
    /**
     * 至少添加一个执行任务
     */
    public static final Integer ERR_CODE_TASK_LEAST_ONE = 28;
    /**
     * 手动类型不能修改智能条件
     */
    public static final Integer ERR_CODE_MANUAL_UPD_CONDITION = 29;

    /**
     * 保存智能场景日志失败
     */
    public static final Integer ERR_CODE_SAVE_LOGS = 30;
    /**
     * 当前场景被禁用
     */
    public static final Integer ERR_CODE_SCENE_FORBI = 31;
    /**
     * 当前场景不在生效时间内
     */
    public static final Integer ERR_CODE_INVALID_SCENE = 32;
    /**
     * 当前场景不存在
     */
    public static final Integer ERR_CODE_NO_SCENE = 33;
    /**
     * 智能场景执行频率过高,请稍后再试
     */
    public static final Integer ERR_CODE_MANY_TIMES = 34;
    /**
     * 该场景被禁用,原因：存在不属于您的设备
     */
    public static final Integer ERR_CODE_NO_DEVICE = 35;
    /**
     * 操作失败
     */
    public static final Integer ERR_CODE_FAIL_OPERATION = 36;
    /**
     * 不存在该房间
     */
    public static final Integer ERR_CODE_NO_ROOM = 37;
    /**
     * 指定返回语言的参数错误
     */
    public static final Integer ERR_CODE_LANGUAGE_PARAM = 38;
    /**
     * 不合法的手机号码
     */
    public static final Integer ERR_CODE_ILLEGAL_PHONE = 39;
    /**
     * 该地区不支持该手机号码
     */
    public static final Integer ERR_CODE_UNSURPPORT_PHONE = 40;
    /**
     * 该账号为微信直接授权登录,不能进行解绑操作
     */
    public static final Integer ERR_CODE_NO_UNBIND_WX = 41;
    /**
     * 请求第三方红外失败
     */
    public static final Integer ERR_CODE_FAIL_INFRARED_REQUEST = 42;
    /**
     * 请求第三方红外数据格式错误
     */
    public static final Integer ERR_CODE_INFRARED_WRONG_DATA = 43;
    /**
     * 不能对当前正在使用的家庭进行删除
     */
    public static final Integer ERR_CODE_CANNOT_OPE = 44;
    /**
     * 当前key未授权
     */
    public static final Integer ERR_CODE_KEY_UNAUTH = 45;
    /**
     * 系统错误,生成token失败
     */
    public static final Integer ERR_CODE_SYS_ERR_TOKEN = 46;
    /**
     * 由于厂家原因,该设备唯一ID与其它设备重复,请联系厂家更换设备
     */
    public static final Integer ERR_CODE_REDEVNO = 47;
    /**
     * 不能添加名字为ETMars的家庭信息
     */
    public static final Integer ERR_CODE_CANNOT_ADD_ETMARS = 48;
    /**
     * 不能删除名字为ETMars默认家庭信息
     */
    public static final Integer ERR_CODE_CANNOT_DEL_ETMARS = 49;
    /**
     * key已过期,请重新登陆
     */
    public static final Integer ERR_CODE_THIRDKEY_UNVALID = 50;
    /**
     * 未关注奥星澳公众号,请前往关注
     */
    public static final Integer ERR_CODE_NOT_CONCERN_PUBLIC_ACCOUNT = 51;
    /**
     * 获取session_key失败
     */
    public static final Integer ERR_CODE_GET_SESSION_KEY_FAIL = 52;

    /**
     * 处理异常
     */
    public static final int RUN_EXCEPTION = 1000;
    /**
     * 设备不在线
     */
    public static final int DEVICE_NOT_ONLINE = 1004;
    /**
     * 请求超时
     */
    public static final int COMMAND_TIMEOUT = 1103;
    /**
     * 设备执行失败
     */
    public static final int DEVICE_EXECUTION_FAILED = 1104;


    /**
     * 录音没有权限
     */
    public static final int RECORD_NO_PERMISSION = 20006;

    /**
     * 录音没有说话
     */
    public static final int RECORD_NO_SPEAK = 10118;

    /**
     * 获取结果超时
     */
    public static final int RECORD_TIME_OUT = 20002;

    /**
     * 网络失败
     */
    public static final int RECORD_NET_ERROR = 20001;


}
