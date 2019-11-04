# WxAccessi

两年前接手过一个 安卓微信辅助 的项目，因为里面用到了很多以前没接触过的东西，特此留念下。

这是一个安卓Activity，没有用户界面，通过跨程序启动来激活此服务的功能。

主要使用AccessibilityService(无障碍服务)来实现获取用户界面数据、模拟点击等。

（app\src\main\java\wxaccessi\dun\com\wxaccessi\WXaccessibilityService 里面有各种功能的代码）