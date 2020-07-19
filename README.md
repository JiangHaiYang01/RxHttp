
Retrofit2 与 协程 结合，网络请求框架

<!--more -->


> 建议在 [博客说明](https://allens.icu/posts/d45b0d85/#more) 中查看，阅读体验更佳

# 前言

之前撸过一个 RxJava 版本的 网络请求框架 [RxJava 版本](https://jianghaiyang01.github.io/posts/a50b3f1e/)，协程版本是在RxJava版本基础上去除了 RxJava 替换成了 协程，






# 下载

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```
allprojects {
	repositories {
		...
		maven { url 'https://www.jitpack.io' }
	}
}
```

Step 2. Add the dependency

```
dependencies {
        implementation 'com.github.JiangHaiYang01:RxHttp:0.0.2'
}
```


当前最新版本

[![](https://www.jitpack.io/v/JiangHaiYang01/RxHttp.svg)](https://www.jitpack.io/#JiangHaiYang01/RxHttp)

# 使用介绍

## 基础使用

### 配置retrifot

```java
rxHttp = RxHttp.Builder()
    .baseUrl("https://www.wanandroid.com")
    .isLog(true)
    .level(HttpLevel.BODY)
    .writeTimeout(10)
    .readTimeout(10)
    .connectTimeout(10)
    .build(this)
```


### Get


```java
  private fun getRequest() {
        launch {
            Log.i(TAG, "get 方法启动 线程 ${Thread.currentThread().name}")
            val data = rxHttp
                .create()
                .addParameter("k", "java")
                .doGet(parameter = "wxarticle/chapters/json", tClass = TestBean::class.java)
                .result({
                    Log.i(TAG, "success ${Thread.currentThread().name} info $it ")
                    log.text = it.toString()
                }, {
                    Log.i(TAG, "error ${Thread.currentThread().name} info ${it.toString()} ")
                    log.text = it.toString()
                })
        }
    }
```
### Post

```java
 private fun postRequest() {
        launch {
            rxHttp
                .create()
                .addParameter("title", "123456")
                .addParameter("author", "123456")
                .addParameter("link", "123456")
                .doPost("lg/collect/add/json", TestBean::class.java)
                .result(
                    {
                        log.text = it.toString()
                    },
                    {
                        log.text = it.message.toString()
                    }
                )
        }
    }
```

> 说明

``create`` 方法创建一个请求
使用 ``addParameter`` 添加请求参数
使用 ``addHeard`` 添加请求头
使用 ``bindEvent`` 绑定生命周期
使用 ``addFile`` 添加上传的文件（在上传时候使用才有效）


## 绑定生命周期

使用上几乎和 RxJava 版本相同，但是因为协程的特性，绑定生命周期的事情交给协程去处理了


## 断点下载

- 启动下载

```java
rxHttp.create().doDownLoad(info.taskId, info.url, getBasePath(this), info.saveName, this)
```

接口返回

```java
interface DownLoadProgressListener {
    /**
     * 下载进度
     *
     * @param key url
     * @param progress  进度
     * @param read  读取
     * @param count 总共长度
     * @param done  是否完成
     */
    fun onUpdate(
        key: String,
        progress: Int,
        read: Long,
        count: Long,
        done: Boolean
    )
}


interface OnDownLoadListener : DownLoadProgressListener {


    //等待下载
    fun onDownLoadPrepare(key: String)

    //进度
    fun onDownLoadProgress(key: String, progress: Int)

    //下载失败
    fun onDownLoadError(key: String, throwable: Throwable)

    //下载成功
    fun onDownLoadSuccess(key: String, path: String)

    //下载暂停
    fun onDownLoadPause(key: String)

    //下载取消
    fun onDownLoadCancel(key: String)
}

```

- 取消某一个下载任务

```java
 doDownLoadCancel(key: String)
```

- 暂停某一个下载任务

```java
doDownLoadPause(key: String)
```

- 取消全部任务

```java
doDownLoadCancelAll
```

- 暂停全部任务

```java
doDownLoadPauseAll
```

## 上传

- 启动上传任务

```java
private fun startUploadSuspend(info: UpLoadInfo) {
    rxHttp.create()
        .addFile("uploaded_file", File(info.path))
        .addHeard("heard", "1")
        .addParameter("parameter", "2")
        .doUpload(
            info.taskId,
            "http://t.xinhuo.com/index.php/Api/Pic/uploadPic",
            TestBean::class.java,
            this
        )
}
```

- 取消某一个上传任务

```java
doUpLoadCancel(tag:String)
```

## 加入其他自定义的解析器

 项目本身 加入了解析器

 ```java
client.addConverterFactory(GsonConverterFactory.create())             // json 解析器
client.addCallAdapterFactory(RxJava2CallAdapterFactory.create())      // 支持RxJava
 ```
 如果想支持其他解析器也是可以的

 在 build RxHttp 的时候 使用 ``addBuilderClientListener`` 添加解析器

 eg:

 ```java
 rxHttp = RxHttp.Builder()
            .baseUrl("https://www.wanandroid.com")
            .isLog(true)
            .level(HttpLevel.BODY)
            .writeTimeout(10)
            .readTimeout(10)
            .connectTimeout(10)
            .addBuilderClientListener(object : OnBuildClientListener {
                    override fun addBuildClient(): MutableSet<Any> {
                        return mutableSetOf(GsonConverterFactory.create(),RxJava2CallAdapterFactory.create())
                    }
                })
            .build(this)
 ```

## 是否显示日志打印 && 日志级别

```
···
.isLog(true)
.level(HttpLevel.BODY)
···
```


## 保存网络请求的log

有时候 在调试的时候可能需要将 网络请求的log 保存到 本地文件，这里也提供接口，开发者可使用 ``addLogListener`` 自行处理log文件

eg:

```java
rxHttp = RxHttp.Builder()
    .baseUrl("https://www.wanandroid.com")
    .isLog(true)
    .level(HttpLevel.BODY)
    .writeTimeout(10)
    .readTimeout(10)
    .addLogListener(this)
    .connectTimeout(10)
    .build(this)
```

![日志截图](http://allens-blog.oss-cn-beijing.aliyuncs.com/allens-blog/yzxj0.png)


## 有时候可能不需要那么多的日志 可以使用 ``addLogFilter ``自定添加过滤器


eg:

```java
  rxHttp = RxHttp.Builder()
            .baseUrl("https://www.wanandroid.com")
            .isLog(true)
            .level(HttpLevel.BODY)
            .writeTimeout(10)
            .readTimeout(10)
            .addLogFilter(object :OnLogFilterListener{
                override fun filter(message: String): Boolean {
                    if(message.contains("adb")){
                        return true
                    }
                    return false
                }
            })
            .connectTimeout(10)
            .build(this)
```

> 注意 上传和下载的日志已经过滤了，是不会显示上传和下载的日志的，这里主要是防止  ``@Steam`` 注解失效


##  网络cache


在构建 RxHttp 的时候  使用  ``cacheType``  方法，构建缓存策略

提供 4中缓存策略,默认是没有网络缓存的

```java
enum class CacheType {
    //不加入缓存的逻辑
    NONE,

    //有网时:每次都请求实时数据； 无网时:无限时请求有网请求好的数据；
    HAS_NETWORK_NOCACHE_AND_NO_NETWORK_NO_TIME,

    //有网时:特定时间之后请求数据； 无网时:无限时请求有网请求好的数据；
    HAS_NETWORK_CACHE_TIME_AND_NO_NETWORK_NO_TIME,

    //有网时:每次都请求实时数据； 无网时:特定时间之前请求有网请求好的数据；
    HAS_NETWORK_NOCACHE_AND_NO_NETWORK_HAS_TIME,

    //有网时:特定时间之后请求数据； 无网时:特定时间之前请求有网请求好的数据；
    HAS_NETWORK_CACHE_TIME_AND_NO_NETWORK_HAS_TIME,
}
```

当使用cache 的时候 提供下面api 处理缓存时间等

- cacheNetWorkTimeOut

有网时:特定时间之后请求数据；（比如：特定时间为20s） 默认20

- cacheNoNetWorkTimeOut

无网时:特定时间之前请求有网请求好的数据；（（比如：特定时间为30天） 默认30 天  单位（秒）

- cacheSize

缓存大小  默认10M

- cachePath

缓存位置 默认沙盒目录下 cacheHttp 文件夹

## Cookie 拦截器


```java
  fun addCookieInterceptor(
            cookieListener: OnCookieListener,
            onCookieInterceptor: OnCookieInterceptor
        )
```

# 源码

[Github](https://github.com/JiangHaiYang01/RxHttp)
[博客说明](https://allens.icu/posts/d45b0d85/#more)

# License

```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
