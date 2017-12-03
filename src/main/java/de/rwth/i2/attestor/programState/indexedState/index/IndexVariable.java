package de.rwth.i2.attestor.programState.indexedState.index;

import java.util.ArrayList;
import java.util.List;

public class IndexVariable implements IndexSymbol {

    private static final IndexVariable instance = new IndexVariable();
    private final List<IndexSymbol> instantiation = new ArrayList<>();

    public static IndexVariable getIndexVariable() {

        return instance;
    }

    @Override
    public boolean isBottom() {

        return false;
    }


    public void resetInstantiation() {

        this.instantiation.clear();
    }

    public String toString() {

        return "()";
    }

    public boolean equals(Object other) {

        return other == this;
    }

    public int hashCode() {

        return 0;
    }

}
