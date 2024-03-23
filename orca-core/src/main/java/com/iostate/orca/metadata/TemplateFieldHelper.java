package com.iostate.orca.metadata;

@SuppressWarnings("unused")
public class TemplateFieldHelper {
    public String type(Field field) {
        String typeName = field.getDataType().javaTypeName();
        if (typeName.startsWith("java.lang.")) {
            typeName = typeName.replace("java.lang.", "");
        }
        return typeName;
    }

    public String declaration(Field field) {
        String decl = type(field) + " " + field.getName();
        if (field instanceof AssociationField af) {
            if (af.isPlural()) {
                decl += " = new java.util.ArrayList<>()";
            }
        }
        return decl;
    }

    public String getter(Field field) {
        return "get" + capitalize(field.getName());
    }

    public String setter(Field field) {
        return "set" + capitalize(field.getName());
    }

    public boolean isLazy(Field field) {
        return field instanceof AssociationField af && af.getFetchType() == FetchType.LAZY;
    }

    private static String capitalize(String name) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

}
