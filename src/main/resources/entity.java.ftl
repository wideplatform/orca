package ${packageName};

import java.util.Collections;
import java.util.Objects;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

@com.iostate.orca.api.Namespace("${namespace}")
public class ${model.name} extends ${base} {
<#list model.allFields() as field>
    private ${fieldDeclaration.apply(field)};
</#list>

<#list model.allFields() as field>
    public ${fieldType.apply(field)} ${getter.apply(field)}() {
        return ${field.name};
    }

    public void ${setter.apply(field)}(${fieldType.apply(field)} ${field.name}) {
        this.${field.name} = ${field.name};
        markUpdatedField("${field.name}");
    }
</#list>

    private static final Map<String, Function<${model.name}, Object>> GETTERS;

    static {
        Map<String, Function<${model.name}, Object>> getters = new HashMap<>();
        <#list model.allFields() as field>
        getters.put("${field.name}", ${model.name}::${getter.apply(field)});
        </#list>
        GETTERS = Collections.unmodifiableMap(getters);
    }

    private static final Map<String, BiConsumer<${model.name}, Object>> SETTERS;

    static {
        Map<String, BiConsumer<${model.name}, Object>> setters = new HashMap<>();
        <#list model.allFields() as field>
        setters.put("${field.name}", (object, value) -> object.${setter.apply(field)}((${fieldType.apply(field)}) value));
        </#list>
        SETTERS = Collections.unmodifiableMap(setters);
    }

    @Override
    public Object getFieldValue(String name) {
        Objects.requireNonNull(name, "field name must not be null");
        return GETTERS.get(name)
            .apply(this);
    }

    @Override
    public void setFieldValue(String name, Object value) {
        Objects.requireNonNull(name, "field name must not be null");
        SETTERS.get(name)
            .accept(this, value);
        markUpdatedField(name);
    }
}
