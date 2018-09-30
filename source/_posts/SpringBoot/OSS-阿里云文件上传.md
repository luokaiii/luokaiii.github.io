---
title: 基于阿里云OSS的文件上传服务
date: 2018-09-30 14:46:03
tags:
 - java
 - SpringBoot
categories: 
 - Java成神之路
 - SpringBoot
---
# 基于阿里云OSS的文件上传服务

## 1.阿里云 OSS 服务介绍

    OSS(Object Storage Service) 即对象存储服务，在 OSS 中每个文件都有一个 Key，通过这个 Key 来指向不同的文件对象。      

    如果你提交的key为"/attachment/2016/123.txt"，那么在web管理平台上你可以看到上述以"/"分开的文件夹形式，即 OSS 中的 Key 就是"/attachement/2016/123.txt"

## 2.Java SDK

> aliyun-sdk-oss

## 3.如何使用 OSS

    阿里云OSS方服务，通过自身提供的Client来实现上传和下载。所以在使用OSS服务上传文件时，需要创建三个类： Client类 、 Config类 和 上传类。

## 4.构建 OSS Client 类

> 获得 OSS 连接的工具类

    @Component
    public class DefaultOssClient{

        @Value("${oss.endpoint})
        String ENDPOINT;

        @Value("${oss.access.key.id}")
        String ACCESS_KEY_ID;

        @Value("${oss.access.key.secret}")
        String ACCESS_KEY_SECRET;

        @Value("${oss.bucket.name}")
        String BUCKET_NAME;

        private static OSSClient client = new OSSClient(ENDPOINT,ACCESS_KEY_ID,ACCESS_KEY_SECRETT,DefaultConfig.getDefaultConfig());

        static OSSClient getConnection(){
            if(client == null){
                client = new OSSClient(ENDPOINT,ACCESS_KEY_ID,ACCESS_KEY_SECRET,DefaultConfig.getDefaultConfig());
            }
            return client;
        }

        static void shutdownOSSClient(){
            client.shutdown();
            client = null;
        }
    }

> 创建一个保存 OSS 地址的文件类，这个文件用户返回给界面

    public class OSSFile{
        String fileId;
        String ossUrl;
        String fielname;
        double sizeKb;

        /* getter and setter*/
    }

> 构建 OSS Config 类，用于指定文件上传的配置

    static final ClientConfiguration conf = new ClientCOnfiguration();

    public class DefaultConfig{
        private DefaultConfig(){
            conf.setMaxConnections(100);
            conf.setConnectionTimeout(5000);
            conf.setMaxErrorRetry(3);
            conf.setSocketTimeout(2000);
        }

        static ClientConfiguration getDefaultConfig(){
            return conf;
        }
    }

> 构建 OSS 文件上传类

    public class OSSUpload{

        static String put1(File file){
            String return_key = null;
            try{
                OSSClient client = DefaultOssClient.getConnection();
                if(file !=null){
                    String fileName = file.getName();
                    String timeStamp = Date2Str.getCurrentTimeStamp();
                    String timeDate = Date2Str.getCurrentDate5();
                    String key = Constant.bashFilePath + timeDate + timeStamp +"/" +fileName;

                    client.putObject(new PutObjectRequest(Constant.bucketName,key,file));

                    return_key = key;
                }
                DefaultOssClient.shutdownOSSClient();
            }catch(ClientException e){
                return null;
            }
            return return_key;
        }

        static String put2(InputStream in,String filename){
            String return_key = null;
            try{
                OSSClient client = DefaultOssClient.getConnection();
                if(in != null){
                    String fileName = filename;
                    try{
                        fileName = new String(filename.getBytes("ISO-8859-1"),"UTF-8");
                    }catch(UnsupportedEncodingException e){
                        e.printStackTrace();
                    }
                    String timeStamp = System.getCurrentTimeStamp();
                    String timeDate = Date2Str.getCurrentDate5();
                    String key = Constant.bashFilePath + timeDate + timeStamp + "/" + fileName;

                    client.put(new PutObjectRequest(Constant.bucketName,key,in));

                    return_key = key;
                }

                DefaultOssClient.shutdownOSSClient();
            }catch(ClientException e){
                return null;
            }

            return return_key;
        }
    }

> 上传测试

    public class FileUpload{
        public static void main(String[] args){
            try{
                uploadOSS();
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        public static void uploadOSS() throws ClientProtocolExcetion{
            HttpPost httpPost = new HttpPost("http://127.0.0.1:7001/test/autonavi/shanghai/api/attachment/oss/);
            httpPost.addHeader("key","123");
            httpPost.addHeader("user","123");
            httpPost.addHeader("method","123");
            httpPost.addHeader("filename",new String("黄山［哈哈］.jpg".getBytes("UTF-8"),"ISO-8859-1"));
            httpPost.addHeader("type","01");

            FileEntity reqEntity = new FileEntity(new File("/Users/123/Pictures/Huangshan.jpg"));

            httpPost.setEntity(reqEntity);

            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(httpPost);

            System.out.println(response);
        }
    }