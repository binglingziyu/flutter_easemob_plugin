

import 'package:easemob_plugin/im/model/message/base_message.dart';

class Conversation {
  List<BaseMessage> allMessages;
  int allMsgCount;
//  cache
  bool group;
  BaseMessage lastMessage;
  BaseMessage lastMessageFromOthers;
  String messageAttachmentPath;
  String type;
  int unreadMsgCount;
  Conversation.fromJson(Map<String, dynamic> json) {
    if (json == null) return;
    this.allMessages = (json["allMessages"] as List)?.map((message) => BaseMessage.fromJson(message))?.toList();
    this.allMsgCount = json["allMsgCount"] as int;
    this.group = json["group"] as bool;
    this.lastMessage = BaseMessage.fromJson(json["lastMessage"]);
    this.lastMessageFromOthers = BaseMessage.fromJson(json["lastMessageFromOthers"]);
    this.messageAttachmentPath = json["messageAttachmentPath"] as String;
    this.type = json["type"] as String;
    this.unreadMsgCount = json["unreadMsgCount"] as int;
  }
}
