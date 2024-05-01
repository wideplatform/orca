package com.iostate.orca.metadata;

import com.iostate.orca.metadata.view.ViewField;

public class ViewFieldHelper {
    public String type(ViewField field) {
        String typeName = field.getDataType().javaTypeName();
        if (typeName.startsWith("java.lang.")) {
            typeName = typeName.replace("java.lang.", "");
        }
        return typeName;
    }

    public String declaration(ViewField field) {
        String decl = type(field) + " " + field.getName();
        if (field.getDataType().name().startsWith("<")) {
            decl += " = new java.util.ArrayList<>()";
        }
        return decl;
    }

    public String getter(ViewField field) {
        return "get" + capitalize(field.getName());
    }

    public String setter(ViewField field) {
        return "set" + capitalize(field.getName());
    }

    private static String capitalize(String name) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

}
