package com.fizzbuzz.android.injection;

import android.preference.PreferenceActivity;
import dagger.ObjectGraph;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

/**
 * Manages an ObjectGraph on behalf of an Activity.  This graph is created by extending the application-scope graph with
 * Activity-specific module(s).
 */
public class InjectingPreferenceActivity
        extends PreferenceActivity
        implements Injector {
    private ObjectGraph mObjectGraph;

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // expand the application graph with the activity-specific module(s)
        ObjectGraph appGraph = ((Injector) getApplication()).getObjectGraph();
        List<Object> activityModules = getModules();
        mObjectGraph = appGraph.plus(activityModules.toArray());

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
