package de.rwth.i2.attestor.domain;

import java.util.HashMap;
import java.util.Map;

public class AssignMapping<I> extends HashMap<Integer, I> {

    public void assign(Integer key, I value) {
        put(key, value);
    }

    public void assignAll(Map<Integer, I> map) {
        putAll(map);
        for (Entry<Integer, I> entry : map.entrySet()) {
            assign(entry.getKey(), entry.getValue());
        }
    }

    public void unassign(Integer key) {
        remove(key);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        sb.append("Assign{");
        for (Entry<Integer, I> entry : entrySet()) {
            sb.append(" ");
            sb.append(entry.getKey());
            sb.append("->");
            sb.append(entry.getValue());
        }
        sb.append("}");

        return sb.toString();
    }
}
