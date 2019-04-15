

import 'package:easemob_plugin/im/model/group.dart';

class GroupList {
  List<Group> data = [];
  GroupList.fromJson(List<dynamic> json) {
    if (json == null) return;
    data = json.map((value) {
      return Group.fromJson(value);
    }).toList();
  }
}