package cn.com.idriven.easemob_plugin;

import android.util.Log;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;

import java.io.IOException;

import cn.com.idriven.easemob_plugin.util.StringUtils;
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
  public void onMethodCall(MethodCall call, final Result result) {
    try {
      switch (call.method) {
        case "init":
          String appKey = call.argument("appKey");
          Boolean acceptInvitationAlways = call.argument("acceptInvitationAlways");
          if(acceptInvitationAlways == null) {
            acceptInvitationAlways = false;
          }
          Boolean autoAcceptGroupInvitation = call.argument("autoAcceptGroupInvitation");
          if(autoAcceptGroupInvitation == null) {
            autoAcceptGroupInvitation = false;
          }
          Boolean autoTransferMessageAttachments = call.argument("autoTransferMessageAttachments");
          if(autoTransferMessageAttachments == null) {
            autoTransferMessageAttachments = false;
          }
          Boolean autoDownloadThumbnail = call.argument("autoDownloadThumbnail");
          if(autoDownloadThumbnail == null) {
            autoDownloadThumbnail = false;
          }
          Boolean allowChatroomOwnerLeave = call.argument("allowChatroomOwnerLeave");
          if(allowChatroomOwnerLeave == null) {
            allowChatroomOwnerLeave = true;
          }
          Boolean deleteMessagesAsExitGroup = call.argument("deleteMessagesAsExitGroup");
          if(deleteMessagesAsExitGroup == null) {
            deleteMessagesAsExitGroup = true;
          }
          Boolean useFCM = call.argument("useFCM");
          if(useFCM == null) {
            useFCM = false;
          }
          Boolean usingHttpsOnly = call.argument("usingHttpsOnly");
          if(usingHttpsOnly == null) {
            usingHttpsOnly = false;
          }
          Boolean autoLogin = call.argument("autoLogin");
          if(autoLogin == null) {
            autoLogin = true;
          }
          Boolean debugMode = call.argument("debugMode");
          if(debugMode == null) {
            debugMode = false;
          }
          EMOptions options = new EMOptions();
          // 设置appkey
          if(StringUtils.isNotBlank(appKey)) {
            options.setAppKey(appKey);
          }
          // 默认添加好友时，是不需要验证的 true: 需要  false: 不需要
          options.setAcceptInvitationAlways(acceptInvitationAlways);
          // 设置是否自动接受加群邀请
          options.setAutoAcceptGroupInvitation(autoAcceptGroupInvitation);
          // 是否自动将消息附件上传到环信服务器，默认为True是使用环信服务器上传下载，如果设为 false，需要开发者自己处理附件消息的上传和下载
          options.setAutoTransferMessageAttachments(autoTransferMessageAttachments);
          // 是否自动下载附件类消息的缩略图等，默认为 true 这里和上边这个参数相关联
          options.setAutoDownloadThumbnail(autoDownloadThumbnail);
          // 设置是否允许聊天室owner离开并删除会话记录，意味着owner再不会受到任何消息
          options.allowChatroomOwnerLeave(allowChatroomOwnerLeave);
          // 设置退出(主动和被动退出)群组时是否删除聊天消息
          options.setDeleteMessagesAsExitGroup(deleteMessagesAsExitGroup);
          // 设置是否开启谷歌FCM推送
          options.setUseFCM(useFCM);
          // 使用https进行REST操作，默认值是false。
          options.setUsingHttpsOnly(usingHttpsOnly);
          // 设置自动登录
          options.setAutoLogin(autoLogin);
          //初始化
          EMClient.getInstance().init(registrar.context().getApplicationContext(), options);
          // 在做打包混淆时，关闭debug模式，避免消耗不必要的资源
          EMClient.getInstance().setDebugMode(debugMode);
          result.success(true);
          Log.i("easemob", "环信初始化完成。");
          break;
        case "login":
          String id = call.argument("id");
          String password = call.argument("password");
          Log.i("easemob", "id="+id);
          Log.i("easemob", "password="+password);
          if(StringUtils.isBlank(id)) {
            result.error("id 为空", "用户 id 不能为空", null);
            return;
          }
          if(StringUtils.isBlank(password)) {
            result.error("密码为空", "用户密码不能为空", null);
            return;
          }
          EMClient.getInstance().login(id, password, new EMCallBack() {//回调
            @Override
            public void onSuccess() {
              EMClient.getInstance().groupManager().loadAllGroups();
              EMClient.getInstance().chatManager().loadAllConversations();
              result.success(true);

              Log.i("easemob", "登录成功");
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
              Log.d("main", "登录聊天服务器失败！");
              result.error("登录失败", message, null);
              Log.i("easemob", "登录失败:"+message);
            }
          });
          break;
        case "logout":
          Boolean unbindToken = call.argument("unbindToken");
          if(unbindToken == null) {
            unbindToken = true;
          }
          EMClient.getInstance().logout(unbindToken, new EMCallBack() {
            @Override
            public void onSuccess() {
              result.success(true);
            }

            @Override
            public void onError(int code, String error) {

            }

            @Override
            public void onProgress(int progress, String status) {
              result.error("退出失败", status, null);
            }
          });
          break;
        default:
          result.notImplemented();
          break;
      }
    } catch (Exception e) {
      result.error("IOException encountered", call.method, e);
    }
  }
}
