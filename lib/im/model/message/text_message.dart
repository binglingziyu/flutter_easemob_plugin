
class TextMessage {
  String message;
  TextMessage.fromJson(Map<String, dynamic> json) {
    if (json == null) return;
    this.message = json["message"] as String;
  }
}