package com.ixuea.courses.mymusic.component.api;


import com.ixuea.courses.mymusic.exception.ResponseDecryptException;
import com.ixuea.courses.mymusic.exception.ResponseSignException;
import com.ixuea.courses.mymusic.util.AESUtil;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.courses.mymusic.util.SHAUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * okhttp插件
 * 主要用来处理一些接口数据签名和加密功能
 */
public class NetworkSecurityInterceptor implements Interceptor {
    /**
     * 每个拦截器都会调用
     *
     * @param chain
     * @return
     * @throws IOException
     */
    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        try {
            //获取request
            Request request = chain.request();

            //获取请求的url
            String url = request.url().toString();

            //请求方式
            String method = request.method();

            //请求前处理网络请求

            //获取请求体
            RequestBody requestBody = request.body();
            if (url.endsWith("v2/addresses") && method == "POST") {
                //该接口需要添加参数签名

                //将请求体转为字符串
                String bodyString = getRequestBodyString(requestBody);

                //计算签名
                String sign = SHAUtil.sha256(bodyString);

                //创建请求
                request = request.newBuilder() //添加原来的请求头
                        .headers(request.headers()) //添加签名
                        .addHeader(Constant.HEADER_SIGN, sign) //设置请求方式和请求体
                        .method(method, requestBody)
                        .build();
            } else if (url.endsWith("v3/addresses") && method == "POST") {
                //该接口参数需要加密

                //将请求体转为字符串
                String bodyString = getRequestBodyString(requestBody);

                //将参数加密
                String encryptBodyString = AESUtil.encrypt(bodyString);

                //使用新参数创建请求体
                requestBody = RequestBody.create(encryptBodyString, MediaType.parse("application/json"));

                //创建请求
                request = request.newBuilder() //添加原来的请求头
                        .headers(request.headers()) //设置请求方式和请求体
                        .method(method, requestBody)
                        .build();
            }

            //执行网络请求
            Response response = chain.proceed(request);

            //请求后处理网络请求

            //获取网络响应签名值
            String sign = response.header(Constant.HEADER_SIGN);

            //判断是否有签名
            if (StringUtils.isNotBlank(sign)) {
                //获取响应字符串
                String dataString = getResponseString(response);

                //获取本地数据签名
                String localSign = SHAUtil.sha256(dataString);

                //判断签名
                if (localSign.equals(sign)) {
                    //签名正确

                    //什么也不做
                } else {
                    //签名错误
                    throw new ResponseSignException();
                }
            } else {
                //没有签名

                //什么也不处理

                //真实项目中
                //如果所有接口响应有签名
                //如果判断没有签名
                //就直接抛出错误
//                Timber.d("process not sign:%s %s",method,url);
            }

            //判断响应解密接口
            if (url.endsWith("v3/addresses") && method == "GET") {
                String decryptString;
                try {//该接口响应需要解密

                    //真实项目中可能所有接口都会加密
                    //所以就不用判断了

                    //我们这里为了降低课程难度
                    //所以只加密这个几个接口

                    //获取响应字符串
                    String dataString = getResponseString(response);

                    //解密数据
                    decryptString = AESUtil.decrypt(dataString);
                    //解密成功

                    //就认为数据没问题

                    //创建新的响应体
                    ResponseBody responseBody =
                            ResponseBody.create(decryptString, MediaType.parse("application/json"));

                    //创建响应
                    response = response.newBuilder()
                            .body(responseBody)
                            .headers(response.headers())
                            .build();
                } catch (Exception e) {
                    //解密失败
                    throw new ResponseDecryptException();
                }
            }
            return response;
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * 将请求体转为字符串
     *
     * @param requestBody
     * @return
     */
    private String getRequestBodyString(RequestBody requestBody) throws IOException {
        //创建缓冲
        Buffer buffer = new Buffer();

        //将请求体内容写入缓冲
        requestBody.writeTo(buffer);

        //转为字符串
        return buffer.readUtf8();
    }

    /**
     * 获取响应字符串
     *
     * @param response
     * @return
     */
    private String getResponseString(Response response) throws IOException {
        //获取响应数据
        ResponseBody responseBody = response.body();

        //转为带缓冲的输入流
        BufferedSource source = responseBody.source();

        //读取所有数据
        source.request(Long.MAX_VALUE);

        //获取缓冲器
        Buffer buffer = source.getBuffer();

        //将读取到的数据转为字符串
        return buffer.clone().readString(Charset.forName("UTF-8"));
    }
}
