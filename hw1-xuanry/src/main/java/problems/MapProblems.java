package problems;

// import misc.exceptions.NotYetImplementedException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parts b.i and b.ii should go here.
 *
 * (Implement contains3 and intersect as described by the spec
 *  See the spec on the website for examples and more explanation!)
 */
public class MapProblems {

    public static Map<String, Integer> intersect(Map<String, Integer> m1, Map<String, Integer> m2) {
        Map<String, Integer> m = new HashMap<String, Integer>();
        for (String s1 : m1.keySet()){
            int n1 = m1.get(s1);
            for (String s2 : m2.keySet()){
                int n2 = m2.get(s2);
                if (s1.equals(s2) && n1 == n2){
                    m.put(s1, n1);
                }
            }
        }
        return m;
    }

    public static boolean contains3(List<String> input) {
        Map<String, Integer> m = new HashMap<String, Integer>();
        for (String word : input) {
            if (!m.keySet().contains(word)){
                m.put(word, 0);
            }
            m.put(word, m.get(word) + 1);
        }
        int max = 0;
        for (String word : m.keySet()){
            max = Math.max(max, m.get(word));
        }
        return max >= 3;
    }
}

