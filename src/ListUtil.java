import net.zemberek.yapi.Kok;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Master on 21.03.2016.
 */
public class ListUtil {
    public static List<Kok> union(List<Kok> list1, List<Kok> list2) {//union kendisini almıyor
        HashSet<Kok> set = new HashSet<Kok>();

        set.addAll(list1);
        set.addAll(list2);

        return new ArrayList<Kok>(set);
    }
}
