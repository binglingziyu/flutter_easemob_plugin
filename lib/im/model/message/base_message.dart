
import 'package:easemob_plugin/im/model/message/image_message.dart';
import 'package:easemob_plugin/im/model/message/text_message.dart';

class BaseMessage {
  bool acked;
  dynamic body;
  String chatType;
  bool delivered;
  String from;
  bool listened;
  String msgId;
  int msgTime;
  String to;
  String type;
  bool unread;
  String userName;

  BaseMessage.fromJson(Map<String, dynamic> json) {
    if(json == null) return;
    this.acked = json["acked"] as bool;
    this.chatType = json["chatType"] as String;
    this.delivered = json["delivered"] as bool;
    this.from = json["from"] as String;
    this.listened = json["listened"] as bool;
    this.msgId = json["msgId"] as String;
    this.msgTime = json["msgTime"] as int;
    this.to = json["to"] as String;
    this.type = json["type"] as String;
    this.unread = json["unread"] as bool;
    this.userName = json["userName"] as String;

    if(type == "TXT") {
        this.body = TextMessage.fromJson(json["body"]);
    } else if(type == "IMAGE") {
        this.body = ImageMessage.fromJson(json["body"]);
    }
  }
}