/*
 * Copyright (c) 2014 Fizz Buzz LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Copyright (c) 2013 Fizz Buzz LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fizzbuzz.android.dagger;

import android.support.v7.app.ActionBarActivity;
import dagger.ObjectGraph;

import java.util.ArrayList;
import java.util.List;

import static com.fizzbuzz.android.dagger.Preconditions.checkState;

/**
 * Manages an ObjectGraph on behalf of a ActionBarActivity.  This graph is created by extending the application-scope
 * graph with ActionBarActivity-specific module(s).
 */
public class InjectingActionBarActivity
        extends ActionBarActivity
        implements Injector {
    private ObjectGraph mObjectGraph;

    // implement Injector interface

    /**
     * Gets this ActionBarActivity's object graph.
     *
     * @return the object graph
     */
    @Override
    public final ObjectGraph getObjectGraph() {
        return mObjectGraph;
    }

    /**
     * Injects a target object using this ActionBarActivity's object graph.
     *
     * @param target the target object
     */
    @Override
    public void inject(Object target) {
        checkState(mObjectGraph != null, "object graph must be assigned prior to calling inject");
        mObjectGraph.inject(target);
    }

    /**
     * Creates an object graph for this ActionBarActivity by extending the application-scope object graph with the
     * modules returned by {@link #getModules()}.
     * <p/>
     * Injects this ActionBarActivity using the created graph.
     */
    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        // expand the application graph with the activity-specific module(s)
        ObjectGraph appGraph = ((Injector) getApplication()).getObjectGraph();
        List<Object> activityModules = getModules();
        mObjectGraph = appGraph.plus(activityModules.toArray());

        // now we can inject ourselves
        inject(this);

        // note: we do the graph setup and injection before calling super.onCreate so that InjectingFragments
        // associated with this InjectingActivity can do their graph setup and injection in their
        // onAttach override.
        super.onCreate(savedInstanceState);


    }

    @Override
    protected void onDestroy() {
        // Eagerly clear the reference to the activity graph to allow it to be garbage collected as
        // soon as possible.
        mObjectGraph = null;

        super.onDestroy();
    }

    /**
     * Returns the list of dagger modules to be included in this ActionBarActivity's object graph.  Subclasses that
     * override this method should add to the list returned by super.getModules().
     *
     * @return the list of modules
     */
    protected List<Object> getModules() {
        List<Object> result = new ArrayList<Object>();
        result.add(new InjectingActivityModule(this, this));
        return result;
    }
}
