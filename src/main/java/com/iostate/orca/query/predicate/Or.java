package com.iostate.orca.query.predicate;

import com.iostate.orca.query.SqlBuilder;

class Or extends Compound {

    private final Predicate[] members;

    public Or(Predicate... members) {
        this.members = members;
    }

    @Override
    public void accept(SqlBuilder sqlBuilder) {
        int count = 0;
        for (Predicate member : members) {
            if (count++ > 0) {
                sqlBuilder.addString(" OR ");
            }
            acceptMember(member, sqlBuilder);
        }
    }

    private void acceptMember(Predicate member, SqlBuilder sqlBuilder) {
        if (member instanceof Or) {
            member.accept(sqlBuilder);
        } else if (member instanceof Compound) {
            sqlBuilder.addString("(");
            member.accept(sqlBuilder);
            sqlBuilder.addString(")");
        } else {
            member.accept(sqlBuilder);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        int count = 0;
        for (Predicate member : members) {
            if (count++ > 0) {
                sb.append(" OR ");
            }
            appendMember(member, sb);
        }
        return sb.toString();
    }

    private void appendMember(Predicate member, StringBuilder sb) {
        if (member instanceof Or) {
             sb.append(member);
        } else if (member instanceof Compound) {
            sb.append("(").append(member).append(")");
        } else {
            sb.append(member);
        }
    }
}
