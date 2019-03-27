package cn.com.idriven.easemob_plugin;

import android.util.Log;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** EasemobPlugin */
public class EasemobPlugin implements MethodCallHandler {

  private final PluginRegistry.Registrar registrar;

  public EasemobPlugin(PluginRegistry.Registrar registrar) {
    this.registrar = registrar;
  }

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "easemob_plugin");
    channel.setMethodCallHandler(new EasemobPlugin(registrar));
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if (call.method.equals("getPlatformVersion")) {
      EMOptions options = new EMOptions();
      // 默认添加好友时，是不需要验证的，改成需要验证
      options.setAcceptInvitationAlways(false);
      // 是否自动将消息附件上传到环信服务器，默认为True是使用环信服务器上传下载，如果设为 false，需要开发者自己处理附件消息的上传和下载
      options.setAutoTransferMessageAttachments(true);
      // 是否自动下载附件类消息的缩略图等，默认为 true 这里和上边这个参数相关联
      options.setAutoDownloadThumbnail(true);
      //初始化
      EMClient.getInstance().init(registrar.context().getApplicationContext(), options);
      //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
      EMClient.getInstance().setDebugMode(true);
      Log.d("main", "EMClinet 启动完成");
      EMClient.getInstance().login("i-driven-hubin", "i-driven", new EMCallBack() {//回调
        @Override
        public void onSuccess() {
          EMClient.getInstance().groupManager().loadAllGroups();
          EMClient.getInstance().chatManager().loadAllConversations();
          Log.d("main", "登录聊天服务器成功！");
        }

        @Override
        public void onProgress(int progress, String status) {

        }

        @Override
        public void onError(int code, String message) {
          Log.d("main", "登录聊天服务器失败！");
        }
      });
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else {
      result.notImplemented();
    }
  }
}
