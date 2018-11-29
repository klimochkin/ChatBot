package vkbot.business.utils;


import java.util.Comparator;
import java.util.Map;

public class MapComparator implements Comparator<Object> {

    Map<String, Integer> map;

    public MapComparator(Map<String, Integer> map) {
        this.map = map;
    }

    public int compare(Object o1, Object o2) {

        if (map.get(o2) == map.get(o1))
            return 1;
        else
            return ((Integer) map.get(o2)).compareTo((Integer)
                    map.get(o1));

    }


}