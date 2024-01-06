package com.iostate.orca.sql.type;

import com.iostate.orca.metadata.DataType;
import com.iostate.orca.metadata.SimpleDataType;

import java.util.HashMap;
import java.util.Map;

public enum TypeHandlers {
    INSTANCE;

    private final Map<DataType, TypeHandler<?>> classTypeHandlerMap = new HashMap<>();

    {
        classTypeHandlerMap.put(SimpleDataType.BOOLEAN, new BooleanTypeHandler());

        classTypeHandlerMap.put(SimpleDataType.LONG, new LongTypeHandler());

        classTypeHandlerMap.put(SimpleDataType.INT, new IntTypeHandler());
    }

    private final TypeHandler<Object> defaultTypeHandler = new ObjectTypeHandler();

    @SuppressWarnings("unchecked")
    public <T> TypeHandler<T> find(DataType type) {
        TypeHandler<T> typeHandler = (TypeHandler<T>) classTypeHandlerMap.get(type);

        if (typeHandler != null) {
            return typeHandler;
        } else {
            return (TypeHandler<T>) defaultTypeHandler;
        }
    }
}
