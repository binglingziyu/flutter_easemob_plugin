package cn.com.idriven.easemob_plugin;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.NetUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
  private final MethodChannel channel;

  public EasemobPlugin(PluginRegistry.Registrar registrar, MethodChannel channel) {
    this.registrar = registrar;
    this.channel = channel;
    JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.DisableCircularReferenceDetect.mask;
  }

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "easemob_plugin");
    channel.setMethodCallHandler(new EasemobPlugin(registrar, channel));
  }

  @Override
  public void onMethodCall(MethodCall call, final Result result) {
    try {
      switch (call.method) {
        case "init":
          easeMobInit(call, result);
          break;
        case "login":
          easeMobLogin(call, result);
          break;
        case "logout":
          easeMobLogout(call, result);
          break;
        case "getAllConversations":
          Map<String, EMConversation> allConversations = EMClient.getInstance().chatManager().getAllConversations();
          Log.i("easemob", "会话内容："+ JSON.toJSONString(allConversations));
          result.success(JSON.toJSONString(allConversations));
          break;
        case "getJoinedGroupsFromServer":
          EMClient.getInstance().groupManager().asyncGetJoinedGroupsFromServer(new EMValueCallBack<List< EMGroup >>() {
            @Override
            public void onSuccess(List<EMGroup> groups) {
              Log.i("easemob", "已加入群组列表："+ JSON.toJSONString(groups));
              result.success(JSON.toJSONString(groups));
            }
            @Override
            public void onError(int error, String errorMsg) {
              Log.e("easemob", "获取已加入群组列表失败："+ errorMsg);
              result.error("获取已加入群组列表失败", errorMsg, null);
            }
          });
          break;
        case "getAllContactsFromServer":
          EMClient.getInstance().contactManager().aysncGetAllContactsFromServer(new EMValueCallBack<List<String>>() {
            @Override
            public void onSuccess(List<String> usernames) {
              Log.i("easemob", "好友列表："+ JSON.toJSONString(usernames));
              result.success(JSON.toJSONString(usernames));
            }
            @Override
            public void onError(int error, String errorMsg) {
              Log.e("easemob", "获取好友列表失败："+ errorMsg);
              result.error("获取好友列表失败", errorMsg, null);
            }
          });
          break;
        case "getConversationAllMessages": {
          String id = call.argument("id");
          EMConversation conversation = EMClient.getInstance().chatManager().getConversation(id);
          List<EMMessage> allMessages = new ArrayList<>();
          if (conversation != null) {
            conversation.markAllMessagesAsRead();
            allMessages = conversation.loadMoreMsgFromDB("", 20);
          }
          Log.e("easemob", "会话消息列表：" + JSON.toJSONString(allMessages));
          result.success(JSON.toJSONString(allMessages));
          break;
        }
        case "loadMoreMsgFromDB": {
          String id = call.argument("id");
          String startMsgId = call.argument("startMsgId");
          int pageSize = getIntegerArgument(call, "pageSize", 0);
          EMConversation conversation = EMClient.getInstance().chatManager().getConversation(id);
          List<EMMessage> allMessages = new ArrayList<>();
          if (conversation != null) {
            allMessages = conversation.loadMoreMsgFromDB(startMsgId, pageSize);
          }
          Log.e("easemob", "加载更多会话消息列表：" + JSON.toJSONString(allMessages));
          result.success(JSON.toJSONString(allMessages));
          break;
        }
        case "markAllMessagesAsRead": {
          String id = call.argument("id");
          EMConversation conversation = EMClient.getInstance().chatManager().getConversation(id);
          if (conversation != null) {
            conversation.markAllMessagesAsRead();
          }
          result.success(true);
          break;
        }
        case "markMessageAsRead": {
          String id = call.argument("id");
          String msgId = call.argument("msgId");
          EMConversation conversation = EMClient.getInstance().chatManager().getConversation(id);
          if (conversation != null) {
            conversation.markMessageAsRead(msgId);
          }
          result.success(true);
          break;
        }
        case "deleteConversation": {
          String id = call.argument("id");
          boolean deleteMessages = getBooleanArgument(call, "deleteMessages", false);
          EMClient.getInstance().chatManager().deleteConversation(id, deleteMessages);
          result.success(true);
          break;
        }
        default:
          result.notImplemented();
          break;
      }
    } catch (Exception e) {
      result.error("IOException encountered", call.method, e);
    }
  }

  /**
   * 环信初始化
   * @param call MethodCall
   * @param result Result
   */
  private void easeMobInit(MethodCall call, Result result) {
    String appKey = call.argument("appKey");
    boolean acceptInvitationAlways = getBooleanArgument(call, "acceptInvitationAlways", true);
    boolean autoAcceptGroupInvitation = getBooleanArgument(call, "autoAcceptGroupInvitation", false);
    boolean autoTransferMessageAttachments = getBooleanArgument(call, "autoTransferMessageAttachments", false);
    boolean autoDownloadThumbnail = getBooleanArgument(call, "autoDownloadThumbnail", false);
    boolean allowChatroomOwnerLeave = getBooleanArgument(call, "allowChatroomOwnerLeave", true);
    boolean deleteMessagesAsExitGroup = getBooleanArgument(call, "deleteMessagesAsExitGroup", true);
    boolean useFCM = getBooleanArgument(call, "useFCM", false);
    boolean usingHttpsOnly = getBooleanArgument(call, "usingHttpsOnly", false);
    boolean autoLogin = getBooleanArgument(call, "autoLogin", true);
    boolean debugMode = getBooleanArgument(call, "debugMode", false);
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

    try {
      EMClient.getInstance().groupManager().asyncGetJoinedGroupsFromServer(new EMValueCallBack<List< EMGroup >>() {
        @Override
        public void onSuccess(List<EMGroup> value) {
        }
        @Override
        public void onError(int error, String errorMsg) {
        }
      });
    } catch (Exception e) {
      Log.i("easemob", "获取已加入的群失败, reason:" + e.getMessage());
    }

    // 注册一个监听连接状态的listener
    EMClient.getInstance().addConnectionListener(new MyConnectionListener());
    // 注册消息监听来接收消息。
    EMClient.getInstance().chatManager().addMessageListener(msgListener);
  }

  /**
   * 环信登录
   * @param call MethodCall
   * @param result Result
   */
  private void easeMobLogin(MethodCall call, final Result result) {
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
  }

  /**
   * 环信退出登录
   * @param call MethodCall
   * @param result Result
   */
  private void easeMobLogout(MethodCall call, final Result result) {
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
        result.error("退出失败", error, null);
      }

      @Override
      public void onProgress(int progress, String status) {}
    });
  }

  private boolean getBooleanArgument(MethodCall call, String key, boolean defaultValue) {
    Boolean value = call.argument(key);
    return value == null ? defaultValue : value;
  }

  private int getIntegerArgument(MethodCall call, String key, int defaultValue) {
    Integer value = call.argument(key);
    return value == null ? defaultValue : value;
  }

  //
  private EMMessageListener msgListener = new EMMessageListener() {

    @Override
    public void onMessageReceived(List<EMMessage> messages) {
      //收到消息
      Log.i("easemob", JSON.toJSONString(messages));
      channel.invokeMethod("onMessageReceived", JSON.toJSONString(messages));
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
      //收到透传消息
    }

    @Override
    public void onMessageRead(List<EMMessage> messages) {
      //收到已读回执
    }

    @Override
    public void onMessageDelivered(List<EMMessage> message) {
      //收到已送达回执
    }
    @Override
    public void onMessageRecalled(List<EMMessage> messages) {
      //消息被撤回
    }

    @Override
    public void onMessageChanged(EMMessage message, Object change) {
      //消息状态变动
    }
  };

  //实现ConnectionListener接口
  private class MyConnectionListener implements EMConnectionListener {
    @Override
    public void onConnected() {
    }
    @Override
    public void onDisconnected(final int error) {
          if(error == EMError.USER_REMOVED){
            // 显示帐号已经被移除
          }else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
            // 显示帐号在其他设备登录
          } else {
            if (NetUtils.hasNetwork(registrar.context())) {
              //连接不到聊天服务器

            } else {
              //当前网络不可用，请检查网络设置
            }
          }
        }
  }

  private void test() {
//    EMClient.getInstance().groupManager().
//            .getJoinedGroupsFromServer();
  }

}
