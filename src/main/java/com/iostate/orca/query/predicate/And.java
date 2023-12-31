package com.iostate.orca.query.predicate;

class And extends Compound {

    private final Predicate a;
    private final Predicate b;

    public And(Predicate a, Predicate b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        appendMember(a, builder);
        builder.append(" AND ");
        appendMember(b, builder);
        return builder.toString();
    }

    private void appendMember(Predicate member, StringBuilder builder) {
        if (member instanceof And) {
            builder.append(member);
        } else if (member instanceof Compound) {
            builder.append('(').append(member).append(')');
        } else {
            builder.append(member);
        }
    }
}
