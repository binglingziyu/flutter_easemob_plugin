
class Group {
  String groupId;
  String groupName;
  String description;
  int maxUserCount;
  Group.fromJson(Map<String, dynamic> json) {
    if (json == null) return;
    this.groupId = json["groupId"] as String;
    this.groupName = json["groupName"] as String;
    this.description = json["description"] as String;
    this.maxUserCount = json["maxUserCount"] as int;
  }
}