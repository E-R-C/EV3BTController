package e_r_c.ev3btcontroller.net.ActionSelector;


import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.UnaryOperator;

public interface DeepCopyable<T extends DeepCopyable<T>> {
    public T deepCopy();

}
