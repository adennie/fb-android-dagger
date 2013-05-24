package com.fizzbuzz.android.injection;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import dagger.ObjectGraph;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

public abstract class InjectingApplication
        extends Application
        implements Injector {

    private ObjectGraph mObjectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        // initialize object graph and inject this
        mObjectGraph = ObjectGraph.create(getModules().toArray());
        mObjectGraph.inject(this);

        // debug mode stuff
        if ((getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) == 1) {
            mObjectGraph.validate(); // validate dagger's object graph
        }
    }

    @Override
    public ObjectGraph getObjectGraph() {
        return mObjectGraph;
    }

    public void inject(Object target) {
        checkState(mObjectGraph != null, "object graph must be initialized prior to calling inject");
        mObjectGraph.inject(target);
    }

    protected List<Object> getModules() {
        List<Object> result = new ArrayList<Object>();
        result.add(new InjectingApplicationModule(this));
        return result;
    }
}
