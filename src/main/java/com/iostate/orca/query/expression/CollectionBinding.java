package com.iostate.orca.query.expression;

import java.util.Collection;
import java.util.stream.Collectors;

public class CollectionBinding implements Expression {

    private final Collection<Object> bindValueCollection;

    public CollectionBinding(Collection<Object> bindValueCollection) {
        this.bindValueCollection = bindValueCollection;
    }

    @Override
    public String toString() {
        return bindValueCollection.stream()
                .map(v -> new ValueBinding(v).toString())
                .collect(Collectors.joining(", ", "(", ")"));
    }
}
