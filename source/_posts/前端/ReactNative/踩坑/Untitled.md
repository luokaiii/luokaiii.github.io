1. WebView 从 ReactNative 中移除

   1. 解决方案：使用 react-native-webview 第三方库
2. yarn add react-native-webview 后直接启动失败
1. yarn link react-native-webview
   2. 解决方案：找到 ${workspace}/android/build.gradle，将 compileSdkVersion 改为 29.
   3. 参考资料：https://github.com/react-native-community/react-native-webview/blob/master/docs/Getting-Started.md
3. 编译时出现：Error：Execution failed for task ':app:processDebugResources'.

   1. 原因：builToolsVersion、compileSDKVersion、targetSDKVersion不一致
   2. 解决方案，同2里的，将 targetSdkVersion 也改为29.
4. onNavigationStateChange 方法不响应
   1. 原因：该方法不支持SPA应用
   2. 解决思路：重写 webviewClient 中跳转的方法，使其产生响应
   3. 参考资料：https://blog.csdn.net/hxl517116279/article/details/105244511
   4. 参考资料2：https://medium.com/@tanjohnny/webview-of-your-spa-in-react-native-a06b0aa2e85d



