package mainau.compiler;

import java.util.Collection;
import java.util.List;

public class Util {
     public static <T, O extends T> O returnLastAddToTarget(
             List<O> list,
             Collection<T> target
     ) {
         var last = list.getLast();

         list = list.stream()
                 .filter(statement -> statement != last)
                 .toList();

         target.addAll(list);

         return last;
     }
}
