# Common-Upgrade
app自动更新模块，支持android7.0

#useage
```
//7.0以下版本
UpgradeDialog.getInstance(this).show("有新版本","http://118.178.248.230:8888/app-dxd-debug.apk",null);

//7.0
String authority = "com.upgrade.channey.test.fileProvider"; //**即manifest中provider的authority**
UpgradeDialog.getInstance(this).show("有新版本","http://118.178.248.230:8888/app-dxd-debug.apk",authority);
```
##什么是authority？
authority是content provider中定义的

a.在res目录下新建目录xml，然后xml目录中新建一个名为XXX.xml的文件.
示例如下：
```
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
```
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



##Step 1.Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
##Step 2. Add the dependency

	dependencies {
	        compile 'com.github.abcdqianlei1990:Common-Upgrade:1.0.7'
	}
  
![baidu](http://imgsrc.baidu.com/forum/w%3D580/sign=279d5ba5f4faaf5184e381b7bc5594ed/a5560923dd54564e7bde17babade9c82d0584ff9.jpg "百度logo") 
