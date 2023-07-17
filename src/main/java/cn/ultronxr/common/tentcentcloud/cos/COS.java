package cn.ultronxr.common.tentcentcloud.cos;

import cn.ultronxr.common.constant.ResBundle;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.StorageClass;
import com.qcloud.cos.region.Region;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

/**
 * @author Ultronxr
 * @date 2023/07/17 11:56:14
 * @description 有关腾讯云 COS 云对象存储SDK的方法 <br/>
 *              更多内容参见其官网：<a href="https://cloud.tencent.com/document/product/436/10199">Java SDK</a>
 */
@Component
@Slf4j
public class COS {

    // 创建 COSClient 实例，这个实例用来后续调用请求
    public COSClient createCOSClient() {
        // 设置用户身份信息。
        // SECRETID 和 SECRETKEY 请登录访问管理控制台 https://console.cloud.tencent.com/cam/capi 进行查看和管理
        // 用户的 SecretId，建议使用子账号密钥，授权遵循最小权限指引，降低使用风险。子账号密钥获取可参见 https://cloud.tencent.com/document/product/598/37140
        String secretId = ResBundle.TENCENT_CLOUD.getString("secret.id");
        // 用户的 SecretKey，建议使用子账号密钥，授权遵循最小权限指引，降低使用风险。子账号密钥获取可参见 https://cloud.tencent.com/document/product/598/37140
        String secretKey = ResBundle.TENCENT_CLOUD.getString("secret.key");
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);

        // ClientConfig 中包含了后续请求 COS 的客户端设置：
        ClientConfig clientConfig = new ClientConfig();

        // 设置 bucket 的地域
        // COS_REGION 请参见 https://cloud.tencent.com/document/product/436/6224
        clientConfig.setRegion(new Region("ap-shanghai"));

        // 设置请求协议, http 或者 https
        // 5.6.53 及更低的版本，建议设置使用 https 协议
        // 5.6.54 及更高版本，默认使用了 https
        clientConfig.setHttpProtocol(HttpProtocol.https);

        // 以下的设置，是可选的：

        // 设置 socket 读取超时，默认 30s
        clientConfig.setSocketTimeout(30*1000);
        // 设置建立连接超时，默认 30s
        clientConfig.setConnectionTimeout(30*1000);

        // 如果需要的话，设置 http 代理，ip 以及 port
        //clientConfig.setHttpProxyIp("httpProxyIp");
        //clientConfig.setHttpProxyPort(80);

        // 生成 cos 客户端。
        return new COSClient(cred, clientConfig);
    }

    public void uploadFile(String objectKeyFileName, String localFilePath) {
        // 调用 COS 接口之前必须保证本进程存在一个 COSClient 实例，如果没有则创建
        // 详细代码参见本页：简单操作 -> 创建 COSClient
        COSClient cosClient = createCOSClient();

        // 存储桶的命名格式为 BucketName-APPID，此处填写的存储桶名称必须为此格式
        String bucketName = ResBundle.TENCENT_CLOUD.getString("bucket.name");
        // 对象键(Key)是对象在存储桶中的唯一标识。
        String key = ResBundle.TENCENT_CLOUD.getString("object.key.path") + objectKeyFileName;
        // 本地文件路径
        //localFilePath = "/path/to/localFile";
        File localFile = new File(localFilePath);

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, localFile);

        // 设置存储类型（如有需要，不需要请忽略此行代码）, 默认是标准(Standard), 低频(standard_ia)
        // 更多存储类型请参见 https://cloud.tencent.com/document/product/436/33417
        putObjectRequest.setStorageClass(StorageClass.Standard);

        try {
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
            log.info("COS上传文件完毕。ID={}", putObjectResult.getRequestId());
            //System.out.println(putObjectResult.getRequestId());
        } catch (CosClientException e) {
            e.printStackTrace();
        }

        // 确认本进程不再使用 cosClient 实例之后，关闭即可
        cosClient.shutdown();
    }

    public void uploadStream() {
        // 调用 COS 接口之前必须保证本进程存在一个 COSClient 实例，如果没有则创建
        // 详细代码参见本页：简单操作 -> 创建 COSClient
        COSClient cosClient = createCOSClient();

        // 存储桶的命名格式为 BucketName-APPID，此处填写的存储桶名称必须为此格式
        String bucketName = ResBundle.TENCENT_CLOUD.getString("bucket.name");
        // 对象键(Key)是对象在存储桶中的唯一标识。
        String key = "exampleobject";

        // 这里创建一个 ByteArrayInputStream 来作为示例，实际中这里应该是您要上传的 InputStream 类型的流
        int inputStreamLength = 1024 * 1024;
        byte[] data = new byte[inputStreamLength];
        InputStream inputStream = new ByteArrayInputStream(data);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        // 上传的流如果能够获取准确的流长度，则推荐一定填写 content-length
        // 如果确实没办法获取到，则下面这行可以省略，但同时高级接口也没办法使用分块上传了
        objectMetadata.setContentLength(inputStreamLength);

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, inputStream, objectMetadata);

        // 设置存储类型（如有需要，不需要请忽略此行代码）, 默认是标准(Standard), 低频(standard_ia)
        // 更多存储类型请参见 https://cloud.tencent.com/document/product/436/33417
        putObjectRequest.setStorageClass(StorageClass.Standard);

        try {
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
            System.out.println(putObjectResult.getRequestId());
        } catch (CosClientException e) {
            e.printStackTrace();
        }

        // 确认本进程不再使用 cosClient 实例之后，关闭即可
        cosClient.shutdown();
    }

}
