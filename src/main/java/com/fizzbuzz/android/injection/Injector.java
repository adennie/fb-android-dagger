package com.fizzbuzz.android.injection;

import dagger.ObjectGraph;

public interface Injector {
    public ObjectGraph getObjectGraph();
    public void inject(Object target);
}
