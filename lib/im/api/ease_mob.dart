
import 'dart:async';
import 'dart:convert';

import 'package:easemob_plugin/easemob_plugin.dart';
import 'package:easemob_plugin/im/model/contacts_list.dart';
import 'package:easemob_plugin/im/model/conversation_map.dart';
import 'package:easemob_plugin/im/model/group_list.dart';
import 'package:easemob_plugin/im/model/message_list.dart';
import 'package:easemob_plugin/util/string_util.dart';

class EaseMob {

  // 工厂模式
  factory EaseMob() =>_getInstance();
  static EaseMob get instance => _getInstance();
  static EaseMob _instance;
  EaseMob._internal();
  static EaseMob _getInstance() {
    if (_instance == null) {
      _instance = new EaseMob._internal();
    }
    return _instance;
  }

  Future<ConversationMap> getAllConversations() async {
    String allConversationsStr = await EasemobPlugin.getAllConversations();
    if(StringUtil.isBlank(allConversationsStr)) {
      throw "没有获取到会话";
    }
    return ConversationMap.fromJson(json.decode(allConversationsStr));
  }

  Future<ContactsList> getAllContactsFromServer() async {
    String allContactsStr = await EasemobPlugin.getAllContactsFromServer();
    if(StringUtil.isBlank(allContactsStr)) {
      throw "没有获取到好友列表";
    }
    return ContactsList.fromJson(json.decode(allContactsStr));
  }

  Future<GroupList> getJoinedGroupsFromServer() async {
    String allJoinedGroupsStr = await EasemobPlugin.getJoinedGroupsFromServer();
    if(StringUtil.isBlank(allJoinedGroupsStr)) {
      throw "没有获取到已加入的群组列表";
    }
    return GroupList.fromJson(json.decode(allJoinedGroupsStr));
  }

  Future<MessageList> getConversationAllMessages(String id) async {
    String conversationAllMessages = await EasemobPlugin.getConversationAllMessages(id);
    if(StringUtil.isBlank(conversationAllMessages)) {
      throw "没有获取到消息列表";
    }
    return MessageList.fromJson(json.decode(conversationAllMessages));
  }

  Future<MessageList> loadMoreMsgFromDB(String id, String startMsgId, int pageSize) async {
    String loadMoreConversationMessages = await EasemobPlugin.loadMoreMsgFromDB(id, startMsgId, pageSize);
    if(StringUtil.isBlank(loadMoreConversationMessages)) {
      throw "没有获取到更多消息列表";
    }
    return MessageList.fromJson(json.decode(loadMoreConversationMessages));
  }

  Future<bool> markAllMessagesAsRead(String id) async {
    bool markAllMessagesAsRead = await EasemobPlugin.markAllMessagesAsRead(id);
    return markAllMessagesAsRead;
  }

  Future<bool> markMessageAsRead(String id, String msgId) async {
    bool markMessageAsRead = await EasemobPlugin.markMessageAsRead(id, msgId);
    return markMessageAsRead;
  }

  Future<bool> deleteConversation(String id) async {
    bool deleteConversation = await EasemobPlugin.deleteConversation(id);
    return deleteConversation;
  }

}