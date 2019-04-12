import 'dart:async';

import 'package:flutter/services.dart';

class EasemobPlugin {
  static const MethodChannel _channel =
      const MethodChannel('easemob_plugin');

  static Future<bool> init() async {
    return await _channel.invokeMethod("init");
  }

  static Future<bool> login(String id, String password) async {
    return await _channel.invokeMethod("login", {"id":id, "password":password});
  }

  static Future<bool> logout() async {
    return await _channel.invokeMethod("logout");
  }

  static Future<String> getAllConversations() async {
    return await _channel.invokeMethod("getAllConversations");
  }

  static Future<String> getAllContactsFromServer() async {
    return await _channel.invokeMethod("getAllContactsFromServer");
  }

  static Future<String> getJoinedGroupsFromServer() async {
    return await _channel.invokeMethod("getJoinedGroupsFromServer");
  }

  static Future<String> getConversationAllMessages(String id) async {
    return await _channel.invokeMethod("getConversationAllMessages", {"id":id});
  }

  static Future<String> loadMoreMsgFromDB(String id, String startMsgId, int pageSize) async {
    return await _channel.invokeMethod("loadMoreMsgFromDB", {"id":id, "startMsgId":startMsgId, "pageSize":pageSize});
  }

  static Future<bool> markAllMessagesAsRead(String id) async {
    return await _channel.invokeMethod("markAllMessagesAsRead", {"id":id});
  }

}
