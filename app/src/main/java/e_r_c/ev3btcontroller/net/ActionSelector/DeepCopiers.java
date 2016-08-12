package e_r_c.ev3btcontroller.net.ActionSelector;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.UnaryOperator;

public class DeepCopiers {
    public static <K,V extends DeepCopyable<V>> void copyFromInto(Map<K, V> src, Map<K, V> dest) {
        for (Map.Entry<K, V> entry: src.entrySet()) {
            dest.put(entry.getKey(), entry.getValue().deepCopy());
        }
    }

    public static <T extends DeepCopyable<T>> void copyFromInto(ArrayList<T> src, ArrayList<T> dest) {
        for (T t: src) {
            dest.add(t.deepCopy());
        }
    }
}
