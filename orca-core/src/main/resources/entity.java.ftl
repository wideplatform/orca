package ${packageName};

import java.util.Collections;
import java.util.Objects;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

@com.iostate.orca.api.Namespace("${namespace}")
public class ${className} extends ${base} {
<#list model.allFields() as field>
    private ${fieldHelper.declaration(field)};
</#list>

<#list model.allFields() as field>
    public ${fieldHelper.type(field)} ${fieldHelper.getter(field)}() {
        return ${field.name};
    }

    public void ${fieldHelper.setter(field)}(${fieldHelper.type(field)} ${field.name}) {
        this.${field.name} = ${field.name};
        markUpdatedField("${field.name}");
    }

</#list>
    private static final Map<String, Function<${className}, Object>> GETTERS;

    static {
        Map<String, Function<${className}, Object>> getters = new HashMap<>();
        <#list model.allFields() as field>
        getters.put("${field.name}", ${className}::${fieldHelper.getter(field)});
        </#list>
        GETTERS = Collections.unmodifiableMap(getters);
    }

    private static final Map<String, BiConsumer<${className}, Object>> SETTERS;

    static {
        Map<String, BiConsumer<${className}, Object>> setters = new HashMap<>();
        <#list model.allFields() as field>
        setters.put("${field.name}", (object, value) -> object.${fieldHelper.setter(field)}((${fieldHelper.type(field)}) value));
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
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ${className} that)) return false;

        <#assign idGetter = fieldHelper.getter(model.idField)>
        if (${idGetter}() != null) return ${idGetter}().equals(that.${idGetter}());
        return false;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
