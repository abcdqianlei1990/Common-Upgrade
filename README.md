[![GitHub release](https://img.shields.io/github/release/abcdqianlei1990/Common-Upgrade.svg)](https://github.com/abcdqianlei1990/Common-Upgrade/releases)
# Common-Upgrade
app自动更新模块，支持android7.0

## useage
see [sample](https://github.com/abcdqianlei1990/Common-Upgrade/tree/master/sample)

## 什么是authority？
authority是content provider中定义的

a.在res目录下新建目录xml，然后xml目录中新建一个名为XXX.xml的文件.
示例如下：
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <paths>
        <files-path path="" name="intfiles" />
        <external-path path="" name="extfiles" />
        <cache-path path="" name="cachefiles" />
    </paths>
</resources>
```
b.在你的manifest中声明provider
```xml
<provider
            android:name="android.support.v4.content.FileProvider"
            android:authorities="com.upgrade.channey.test.fileProvider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/XXX"/>
        </provider>
```



## Step 1.Add it in your root build.gradle at the end of repositories:
```groovy
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
## Step 2. Add the dependency
```groovy
	dependencies {
	        compile 'com.github.abcdqianlei1990:Common-Upgrade:1.13'
	}
 ```
![baidu](http://imgsrc.baidu.com/forum/w%3D580/sign=279d5ba5f4faaf5184e381b7bc5594ed/a5560923dd54564e7bde17babade9c82d0584ff9.jpg "百度logo") 
