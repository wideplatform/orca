package com.iostate.orca.metadata.inverse;

import com.iostate.orca.api.PersistentObject;

public interface Inverse {

    void fill(PersistentObject entity);
}
