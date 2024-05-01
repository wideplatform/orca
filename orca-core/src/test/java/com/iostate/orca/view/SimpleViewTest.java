package com.iostate.orca.view;

import com.iostate.orca.SimpleEntity;
import com.iostate.orca.TestBase;
import com.iostate.orca.api.ViewManager;
import com.iostate.orca.core.ViewManagerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleViewTest extends TestBase {
    private ViewManager viewManager;

    @BeforeEach
    public void setupViewManager() {
        viewManager = new ViewManagerImpl(metadataManager, entityManager);
    }

    @Override
    protected Class<?>[] entities() {
        return new Class[]{SimpleEntity.class};
    }

    @Test
    public void testFind() {
        SimpleEntity prepared = new SimpleEntity();
        prepared.setBoolValue(true);
        prepared.setIntValue(1);
        prepared.setLongValue(Long.MAX_VALUE);
        prepared.setDecValue(new BigDecimal("1.0"));
        prepared.setStrValue("a");
        prepared.setDtValue(Instant.now());
        entityManager.persist(prepared);

        SimpleView view = viewManager.find(SimpleView.class, prepared.getId());

        checkEqual(prepared, view);
    }

    @Test
    public void testFindAll() {
        SimpleEntity prepared1 = prepare();
        SimpleEntity prepared2 = prepare();

        List<SimpleView> views = viewManager.findAll(SimpleView.class);

        assertEquals(2, views.size());
        checkEqual(prepared1, views.get(0));
        checkEqual(prepared2, views.get(1));
    }

    private static void checkEqual(SimpleEntity prepared, SimpleView view) {
        assertEquals(prepared.getId(), view.getId());
        assertEquals(prepared.getBoolValue(), view.getBoolValue());
        assertEquals(prepared.getIntValue(), view.getIntValue());
        assertEquals(prepared.getLongValue(), view.getLongValue());
        assertEquals(prepared.getStrValue(), view.getStrValue());
        checkEqual(prepared.getDecValue(), view.getDecValue());
        checkEqual(prepared.getDtValue(), view.getDtValue());
    }

    private SimpleEntity prepare() {
        SimpleEntity prepared = new SimpleEntity();
        entityManager.persist(prepared);
        return prepared;
    }
}
