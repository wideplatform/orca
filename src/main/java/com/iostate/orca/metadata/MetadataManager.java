package com.iostate.orca.metadata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.iostate.orca.api.BasePO;
import com.iostate.orca.metadata.view.ViewModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class MetadataManager {

    private final Map<String, EntityModel> entityModelMap = new ConcurrentHashMap<>();

    private final Map<String, ViewModel> viewModelMap = new ConcurrentHashMap<>();

    private final Configuration codeTemplateConfig = new Configuration(Configuration.VERSION_2_3_32);

    {
        codeTemplateConfig.setClassLoaderForTemplateLoading(
                Thread.currentThread().getContextClassLoader(),
                "");
        codeTemplateConfig.setDefaultEncoding("UTF-8");
        codeTemplateConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        codeTemplateConfig.setLogTemplateExceptions(false);
        codeTemplateConfig.setFallbackOnNullLoopVariable(false);
    }

    public void loadEntityModel(String yaml) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        EntityModel entityModel = objectMapper.readValue(yaml, EntityModel.class);
        addEntityModel(entityModel);
    }

    public void addEntityModel(EntityModel entityModel) {
        entityModelMap.put(entityModel.getName(), entityModel);
    }

    public Collection<EntityModel> allEntityModels() {
        return entityModelMap.values();
    }

    public void addViewModel(ViewModel viewModel) {
        viewModelMap.put(viewModel.getName(), viewModel);
    }

    public Collection<ViewModel> allViewModels() {
        return viewModelMap.values();
    }

    public EntityModel findEntityByName(String name) {
        return entityModelMap.get(name);
    }

    public EntityModel findEntityByClass(Class<?> cls) {
        return findEntityByName(cls.getSimpleName());
    }

    public ViewModel findViewByName(String name) {
        return viewModelMap.get(name);
    }

    public ViewModel findViewByClass(Class<?> cls) {
        return findViewByName(cls.getSimpleName());
    }

    public String generateYaml(EntityModel entityModel) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        return objectMapper.writeValueAsString(entityModel);
    }

    public String generateJava(EntityModel entityModel, String namespace, String packageName) {
        Map<String, Object> data = new HashMap<>();
        data.put("model", entityModel);
        data.put("base", BasePO.class.getName());
        data.put("namespace", namespace);
        data.put("packageName", packageName);
        addTemplateFunctions(data);
        return render("entity.java.ftl", data);
    }

    private String render(String templateName, Map<String, Object> data) {
        try {
            Template template = codeTemplateConfig.getTemplate(templateName);
            CharArrayWriter out = new CharArrayWriter();
            template.process(data, out);
            return out.toString();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addTemplateFunctions(Map<String, Object> data) {
        Function<Field, String> fieldTypeFunction = field -> {
            String typeName = field.getDataType().javaTypeName();
            if (typeName.startsWith("java.lang.")) {
                typeName = typeName.replace("java.lang.", "");
            }
            return typeName;
        };
        data.put("fieldType", fieldTypeFunction);
        data.put("fieldDeclaration", (Function<Field, String>) field -> {
            String decl = fieldTypeFunction.apply(field) + " " + field.getName();
            if (field instanceof AssociationField) {
                AssociationField a = (AssociationField) field;
                if (a.isPlural()) {
                    decl += " = new java.util.ArrayList<>()";
                }
            }
            return decl;
        });
        data.put("getter", (Function<Field, String>) field -> "get" + capitalize(field.getName()));
        data.put("setter", (Function<Field, String>) field -> "set" + capitalize(field.getName()));
    }

    private static String capitalize(String name) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
