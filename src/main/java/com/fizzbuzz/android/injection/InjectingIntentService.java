package com.fizzbuzz.android.injection;

import android.content.Context;
import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.fizzbuzz.android.injection.Injector;
import dagger.ObjectGraph;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

public abstract class InjectingIntentService
        extends WakefulIntentService
        implements Injector {

    private Context mContext;
    private ObjectGraph mObjectGraph;

    public InjectingIntentService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // extend the application-scope object graph with the modules for this service
        mObjectGraph = ((Injector)getApplication()).getObjectGraph().plus(getModules().toArray());

        // then inject ourselves
        mObjectGraph.inject(this);
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
        return result;
    }
}