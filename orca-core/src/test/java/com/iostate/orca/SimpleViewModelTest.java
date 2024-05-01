package com.iostate.orca;

import com.iostate.orca.metadata.SimpleDataType;
import com.iostate.orca.metadata.view.ViewField;
import com.iostate.orca.metadata.view.ViewModel;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleViewModelTest extends ViewModelGenerationTestBase {
    
    @Test
    public void testSimpleView() throws IOException {
        ViewField idField = new ViewField("id", null, SimpleDataType.LONG, true, true);
        ViewField boolField = new ViewField("boolValue", null, SimpleDataType.BOOLEAN, false, true);
        ViewField intField = new ViewField("intValue", null, SimpleDataType.INT, false, true);
        ViewField longField = new ViewField("longValue", null, SimpleDataType.LONG, false, true);
        ViewField decField = new ViewField("decValue", null, SimpleDataType.DECIMAL, false, true);
        ViewField strField = new ViewField("strValue", null, SimpleDataType.STRING, false, true);
        ViewField datetimeField = new ViewField("dtValue", null, SimpleDataType.DATETIME, false, true);

        ViewModel viewModel = new ViewModel("SimpleView", "SimpleEntity", idField);
        viewModel.addDataField(boolField);
        viewModel.addDataField(intField);
        viewModel.addDataField(longField);
        viewModel.addDataField(decField);
        viewModel.addDataField(strField);
        viewModel.addDataField(datetimeField);
        exportCode("view", viewModel);

        assertEquals("SimpleView", viewModel.getName());
        assertEquals("SimpleEntity", viewModel.getEntityModelName());
        Collection<ViewField> allFields = viewModel.allFields();
        assertEquals(7, allFields.size());
        assertEquals(viewModel.getIdField(), allFields.iterator().next());
    }
}
