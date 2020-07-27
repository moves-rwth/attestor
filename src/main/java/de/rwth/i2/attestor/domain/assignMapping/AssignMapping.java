package de.rwth.i2.attestor.domain.assignMapping;

import java.util.HashMap;
import java.util.Map;

public class AssignMapping<I> extends HashMap<Integer, I> {
    public AssignMapping() {
    }

    public AssignMapping(Map<Integer, I> map) {
        this.putAll(map);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        sb.append("Mapping{");
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
