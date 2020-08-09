package de.rwth.i2.attestor.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AssignMapping<I> extends HashMap<Integer, I> {

    public void assign(Integer key, I value) {
        if (containsKey(key)) {
            replace(key, value);
        } else {
            put(key, value);
        }
    }

    public void assignAll(Map<Integer, I> map) {
        for (Entry<Integer, I> entry : map.entrySet()) {
            assign(entry.getKey(), entry.getValue());
        }
    }

    public void unassign(Integer key) {
        remove(key);
    }

    public void unassignAll(Set<Integer> keys) {
        for (Integer key : keys) {
            remove(key);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        sb.append("Assign{");
        for (Integer key : keySet()) {
            sb.append(" ");
            sb.append(key);
            sb.append("->");
            sb.append(get(key));
        }
        sb.append("}");

        return sb.toString();
    }
}
