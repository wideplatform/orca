package com.iostate.orca.metadata;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.iostate.orca.api.BaseEntityObject;
import com.iostate.orca.api.BaseViewObject;
import com.iostate.orca.api.Namespace;
import com.iostate.orca.metadata.dto.EntityModelDto;
import com.iostate.orca.metadata.dto.ViewModelDto;
import com.iostate.orca.metadata.view.ViewModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MetadataManager {

    private final Map<String, EntityModel> entityModelMap = new ConcurrentHashMap<>();

    private final Map<String, ViewModel> viewModelMap = new ConcurrentHashMap<>();

    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    {
        yamlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

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

    private String resolveModelName(Class<?> entityClass) {
        String namespace = entityClass.getAnnotation(Namespace.class).value();
        String directory = namespace.isEmpty() ? "" : namespace + '/';
        return directory + entityClass.getSimpleName();
    }

    private EntityModel loadEntityModelByName(String name) {
        String path = "models/" + name + ".yml";
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                return null;
            }
            byte[] bytes = in.readAllBytes();
            return loadEntityModelFromYaml(new String(bytes, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private EntityModel loadEntityModelFromYaml(String yaml) throws JsonProcessingException {
        EntityModelDto entityModelDto = yamlMapper.readValue(yaml, EntityModelDto.class);
        EntityModel entityModel = new EntityModelConverter(this).entityModel(entityModelDto);
        addEntityModel(entityModel);
        return entityModel;
    }


    private ViewModel loadViewModelByName(String name) {
        String path = "models/" + name + ".yml";
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                return null;
            }
            byte[] bytes = in.readAllBytes();
            return loadViewModelFromYaml(new String(bytes, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private ViewModel loadViewModelFromYaml(String yaml) throws JsonProcessingException {
        ViewModelDto viewModelDto = yamlMapper.readValue(yaml, ViewModelDto.class);
        ViewModel viewModel = new ViewModelConverter(this).viewModel(viewModelDto);
        addViewModel(viewModel);
        return viewModel;
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
        EntityModel entityModel = entityModelMap.get(name);
        if (entityModel != null) {
            return entityModel;
        } else {
            return loadEntityModelByName(name);
        }
    }

    public EntityModel findEntityByClass(Class<?> cls) {
        return findEntityByName(resolveModelName(cls));
    }

    public ViewModel findViewByName(String name) {
        ViewModel viewModel = viewModelMap.get(name);
        if (viewModel != null) {
            return viewModel;
        } else {
            return loadViewModelByName(name);
        }
    }

    public ViewModel findViewByClass(Class<?> cls) {
        return findViewByName(resolveModelName(cls));
    }

    public String generateYaml(EntityModel entityModel) throws JsonProcessingException {
        return yamlMapper.writeValueAsString(entityModel.toDto());
    }

    public String generateYaml(ViewModel viewModel) throws JsonProcessingException {
        return yamlMapper.writeValueAsString(viewModel.toDto());
    }

    public String generateJava(EntityModel entityModel, String namespace, String packageName) {
        Map<String, Object> data = new HashMap<>();
        data.put("model", entityModel);
        data.put("className", entityModel.getName());
        data.put("base", BaseEntityObject.class.getName());
        data.put("namespace", namespace);
        data.put("packageName", packageName);
        data.put("fieldHelper", new TemplateFieldHelper());
        return render("entity.java.ftl", data);
    }

    public String generateJava(ViewModel viewModel, String namespace, String packageName) {
        Map<String, Object> data = new HashMap<>();
        data.put("model", viewModel);
        data.put("className", viewModel.getName());
        data.put("base", BaseViewObject.class.getName());
        data.put("namespace", namespace);
        data.put("packageName", packageName);
        data.put("fieldHelper", new ViewFieldHelper());
        return render("view.java.ftl", data);
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

}
