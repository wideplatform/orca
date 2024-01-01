package com.iostate.orca.metadata.serde;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.iostate.orca.metadata.CascadeType;
import com.iostate.orca.metadata.EntityModelRef;
import com.iostate.orca.metadata.FetchType;
import com.iostate.orca.metadata.Field;
import com.iostate.orca.metadata.PluralAssociationField;
import com.iostate.orca.metadata.SimpleDataType;
import com.iostate.orca.metadata.SimpleField;
import com.iostate.orca.metadata.SingularAssociationField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FieldDeserializer extends StdDeserializer<Field> {
    protected FieldDeserializer() {
        super((Class<?>) null);
    }

    protected FieldDeserializer(Class<?> vc) {
        super(vc);
    }

    protected FieldDeserializer(JavaType valueType) {
        super(valueType);
    }

    protected FieldDeserializer(StdDeserializer<?> src) {
        super(src);
    }

    @Override
    public Field deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode root = p.getCodec().readTree(p);
        String name = root.get("name").asText();
        String type = root.get("type").asText();
        String dataType = root.get("dataType").asText();
        JsonNode isIdNode = root.get("isId");
        boolean isId = isIdNode != null && isIdNode.asBoolean();
        JsonNode isNullableNode = root.get("isNullable");
        boolean isNullable = isNullableNode == null || isNullableNode.asBoolean();

        switch (type) {
            case "SimpleField": {
                String columnName = root.get("columnName").asText();
                return new SimpleField(
                        name,
                        columnName,
                        SimpleDataType.valueOf(dataType).javaType(),
                        isId,
                        isNullable);
            }
            case "SingularAssociationField": {
                String columnName = root.get("columnName").asText();
                return new SingularAssociationField(
                        name,
                        columnName,
                        modelRef(root.get("targetModel")),
                        isId,
                        isNullable,
                        fetchType(root.get("fetchType")),
                        cascadeTypes(root.withArray("cascadeTypes"))
                );
            }
            case "PluralAssociationField": {
                return new PluralAssociationField(
                        name,
                        modelRef(root.get("targetModel")),
                        fetchType(root.get("fetchType")),
                        cascadeTypes(root.withArray("cascadeTypes"))
                );
            }
            default:
                throw new IllegalArgumentException("field " + name + " has unknown type: " + type);
        }
    }

    private EntityModelRef modelRef(JsonNode node) {
        String name = node.get("name").asText();
        return new EntityModelRef(name, null);
    }

    private FetchType fetchType(JsonNode node) {
        if (node == null) {
            return null;
        } else {
            return FetchType.valueOf(node.asText());
        }
    }

    private CascadeType[] cascadeTypes(ArrayNode node) {
        if (node == null) {
            return null;
        }
        List<CascadeType> result = new ArrayList<>();
        for (JsonNode jsonNode : node) {
            result.add(CascadeType.valueOf(jsonNode.textValue()));
        }
        return result.toArray(new CascadeType[node.size()]);
    }
}
