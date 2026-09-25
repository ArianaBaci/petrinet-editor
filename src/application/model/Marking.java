package application.model;

import java.util.HashMap;
import java.util.Map;

public class Marking {
    private Map<Integer,Integer> marking;

    public Marking() {
        marking = new HashMap<Integer,Integer>();
    }

    public Map<Integer, Integer> getMarking() {
        return marking;
    }

    public String toString(){
        String ret = "(";
        for(Map.Entry<Integer,Integer> entry : marking.entrySet()){
            ret += "PlaceID:" + entry.getKey() + ", Tokens:" + entry.getValue() + " - ";
        }
        ret = ret.substring(0, ret.length() -3) + ")";
        return ret;
    }
}
