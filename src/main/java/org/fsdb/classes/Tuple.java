package org.fsdb.classes;

public class Tuple<A, B> {
    public final A first;
    public final B second;

    public Tuple(A valA, B valB) {
        first = valA;
        second = valB;
    }
}
