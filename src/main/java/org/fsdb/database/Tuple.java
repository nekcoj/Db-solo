package org.fsdb.database;

public class Tuple<A, B> {
    public A first;
    public B second;

    public Tuple(A valA, B valB) {
        first = valA;
        second = valB;
    }
}