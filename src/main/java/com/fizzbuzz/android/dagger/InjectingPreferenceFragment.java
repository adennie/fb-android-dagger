/*
 * Copyright (c) 2015 Fizz Buzz LLC
 */

package com.fizzbuzz.android.dagger;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.preference.PreferenceFragment;
import dagger.ObjectGraph;

import java.util.ArrayList;
import java.util.List;

import static com.fizzbuzz.android.dagger.Preconditions.checkState;

/**
 * Manages an ObjectGraph on behalf of a PreferenceFragment.  This graph is created by extending the hosting
 * Activity's graph with Fragment-specific module(s).
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class InjectingPreferenceFragment
        extends PreferenceFragment
        implements Injector {
    private ObjectGraph mObjectGraph;
    private boolean mFirstAttach = true;

    /**
     * Creates an object graph for this PreferenceFragment by extending the hosting Activity's object graph with the
     * modules returned by {@link #getModules()}.
     * <p/>
     * Injects this PreferenceFragment using the created graph.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // expand the activity graph with the fragment-specific module(s)
        ObjectGraph appGraph = ((Injector) activity).getObjectGraph();
        List<Object> fragmentModules = getModules();
        mObjectGraph = appGraph.plus(fragmentModules.toArray());

        // make sure it's the first time through; we don't want to re-inject a retained fragment that is going
        // through a detach/attach sequence.
        if (mFirstAttach == true) {
            inject(this);
            mFirstAttach = false;
        }
    }

    @Override
    public void onDestroy() {
        // Eagerly clear the reference to the object graph to allow it to be garbage collected as soon as possible.
        mObjectGraph = null;

        super.onDestroy();
    }

    // implement Injector interface

    /**
     * Gets this PreferenceFragment's object graph.
     *
     * @return the object graph
     */
    @Override
    public final ObjectGraph getObjectGraph() {
        return mObjectGraph;
    }

    /**
     * Injects a target object using this PreferenceFragment's object graph.
     *
     * @param target the target object
     */
    @Override
    public void inject(Object target) {
        checkState(mObjectGraph != null, "object graph must be assigned prior to calling inject");
        mObjectGraph.inject(target);
    }

    /**
     * Returns the list of dagger modules to be included in this PreferenceFragment's object graph.  Subclasses that
     * override this method should add to the list returned by super.getModules().
     *
     * @return the list of modules
     */
    protected List<Object> getModules() {
        List<Object> result = new ArrayList<Object>();
        result.add(new InjectingFragmentModule(this, this));
        return result;
    }
}
