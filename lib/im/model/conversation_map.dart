

import 'package:easemob_plugin/im/model/conversation.dart';

class ConversationMap {
  Map<String, Conversation> data = {};
  ConversationMap.fromJson(Map<String, dynamic> json) {
    if (json == null) return;
    json.forEach((key, value) {
      data.putIfAbsent(key, () {
        return Conversation.fromJson(value);
      });
    });
  }
}