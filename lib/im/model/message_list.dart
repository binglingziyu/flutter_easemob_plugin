

import 'package:easemob_plugin/im/model/message/base_message.dart';

class MessageList {
  List<BaseMessage> data = [];
  MessageList.fromJson(List<dynamic> json) {
    if (json == null) return;
    data = json.map((value) {
      return BaseMessage.fromJson(value);
    }).toList();
  }
}