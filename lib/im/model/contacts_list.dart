
class ContactsList {
  List<String> data = [];
  ContactsList.fromJson(List<dynamic> json) {
    if (json == null) return;
    data = json.map((value) {
      return "$value";
    }).toList();
  }
}