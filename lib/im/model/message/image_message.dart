
class ImageMessage {
  String fileName;
  String localUrl;
  String remoteUrl;
  String secret;
  bool sendOriginalImage;
  String thumbnailSecret;
  String thumbnailUrl;
  int width;
  int height;
  ImageMessage.fromJson(Map<String, dynamic> json) {
    if (json == null) return;
    this.fileName = json["fileName"] as String;
    this.localUrl = json["localUrl"] as String;
    this.remoteUrl = json["remoteUrl"] as String;
    this.secret = json["secret"] as String;
    this.sendOriginalImage = json["sendOriginalImage"] as bool;
    this.thumbnailSecret = json["thumbnailSecret"] as String;
    this.thumbnailUrl = json["thumbnailUrl"] as String;
    this.width = json["width"] as int;
    this.height = json["height"] as int;
  }
}