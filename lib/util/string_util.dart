
class StringUtil {

  static bool isBlank(String str) {
    if(str == null) {
      return true;
    }
    if(str.trim().length == 0) {
      return true;
    }
    return false;
  }

}