// "Replace Stream API chain with loop" "true"

import java.util.Arrays;
import java.util.List;

public class Main {
  private static String test(List<String> list) {
      if (list == null) {
          return null;
      } else {
          for (String str : list) {
              if (str.contains("x")) {
                  return str;
              }
          }
          return null;
      }
  }

  public static void main(String[] args) {
    System.out.println(test(Arrays.asList("a", "b", "syz")));
  }
}