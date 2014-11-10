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

import android.app.Activity;
import dagger.ObjectGraph;

import java.util.ArrayList;
import java.util.List;

import static com.fizzbuzz.android.dagger.Preconditions.checkState;

/**
 * Manages an ObjectGraph on behalf of an Activity.  This graph is created by extending the application-scope graph with
 * Activity-specific module(s).
 */
public class InjectingActivity
        extends Activity
        implements Injector {
    private ObjectGraph mObjectGraph;

    /**
     * Gets this Activity's object graph.
     *
     * @return the object graph
     */
    @Override
    public final ObjectGraph getObjectGraph() {
        return mObjectGraph;
    }

    /**
     * Injects a target object using this Activity's object graph.
     *
     * @param target the target object
     */
    @Override
    public void inject(Object target) {
        checkState(mObjectGraph != null, "object graph must be assigned prior to calling inject");
        mObjectGraph.inject(target);
    }

    // implement Injector interface

    /**
     * Creates an object graph for this Activity by extending the application-scope object graph with the modules
     * returned by {@link #getModules()}.
     * <p/>
     * Injects this Activity using the created graph.
     */
    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        // extend the application-scope object graph with the modules for this activity
        mObjectGraph = ((Injector) getApplication()).getObjectGraph().plus(getModules().toArray());

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
     * Returns the list of dagger modules to be included in this Activity's object graph.  Subclasses that override
     * this method should add to the list returned by super.getModules().
     *
     * @return the list of modules
     */
    protected List<Object> getModules() {
        List<Object> result = new ArrayList<Object>();
        result.add(new InjectingActivityModule(this, this));
        return result;
    }
}
