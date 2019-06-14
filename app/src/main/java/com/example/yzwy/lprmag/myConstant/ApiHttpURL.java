package com.example.yzwy.lprmag.myConstant;

/**
 * HTTP 请求地址
 */
public class ApiHttpURL {

    //##############################################################################################
    /**
     * =============================================================================================
     * 系统管理
     */
    /*
     * 用户登陆验证接口
     * 支持向服务器上传图片和上传停车信息
     * */
    public static String LoginVerification = "http://120.196.115.230:8399/Message/LoginVerification";

    /*
     * 用户添加接口
     * 支持后台添加用户信息
     * */
    public static String AddUserInfo = "http://120.196.115.230:8399/Message/AddUserInfo";


    //##############################################################################################
    /**
     * =============================================================================================
     * 容错接口
     */


    /**
     * 容错机制错误日志接口
     */
    public static String ErrorLogUpLoad = "http://120.196.115.230:8399/Message/ErrorLogUpLoad";


}