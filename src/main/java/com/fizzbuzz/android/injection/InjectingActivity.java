package com.fizzbuzz.android.injection;

import android.app.Activity;
import dagger.ObjectGraph;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

/**
 * Manages an ObjectGraph on behalf of an Activity.  This graph is created by extending the application-scope graph with
 * Activity-specific module(s).
 */
public class InjectingActivity
        extends Activity
        implements Injector {
    private ObjectGraph mObjectGraph;

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // extend the application-scope object graph with the modules for this activity
        mObjectGraph = ((Injector)getApplication()).getObjectGraph().plus(getModules().toArray());

        // now we can inject ourselves
        inject(this);
    }

    // implement Injector interface

    @Override
    public final ObjectGraph getObjectGraph() {
        return mObjectGraph;
    }
    @Override
    public void inject(Object target) {
        checkState(mObjectGraph != null, "object graph must be assigned prior to calling inject");
        mObjectGraph.inject(target);
    }

    protected List<Object> getModules() {
        List<Object> result = new ArrayList<Object>();
        result.add(new InjectingActivityModule(this));
        return result;
    }
}
