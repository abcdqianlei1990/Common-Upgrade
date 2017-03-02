# Common-Upgrade
app自动更新模块，支持android7.0

#useage
```
//7.0以下版本
UpgradeDialog.getInstance(this).show("有新版本","http://118.178.248.230:8888/app-dxd-debug.apk",null);

//7.0
String authority = "com.upgrade.channey.test.fileProvider";
UpgradeDialog.getInstance(this).show("有新版本","http://118.178.248.230:8888/app-dxd-debug.apk",authority);
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
