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
import android.support.v4.app.DialogFragment;
import dagger.ObjectGraph;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

/**
 * Manages an ObjectGraph on behalf of an ListFragment.  This graph is created by extending the hosting Activity's graph
 * with Fragment-specific module(s).
 */
public class InjectingDialogFragment
        extends DialogFragment
        implements Injector {
    private ObjectGraph mObjectGraph;
    private boolean mFirstAttach = true;

    /**
     * Creates an object graph for this ListFragment by extending the hosting Activity's object graph with the modules
     * returned by {@link #getModules()}.
     * <p/>
     * Injects this ListFragment using the created graph.
     */
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
    @Override public void onDestroy() {
        // Eagerly clear the reference to the fragment graph to allow it to be garbage collected as
        // soon as possible.
        mObjectGraph = null;

        super.onDestroy();
    }

    /**
     * Gets this ListFragment's object graph.
     *
     * @return the object graph
     */
    @Override
    public final ObjectGraph getObjectGraph() {
        return mObjectGraph;
    }

    /**
     * Injects a target object using this ListFragment's object graph.
     * @param target the target object
     */
    @Override
    public void inject(Object target) {
        checkState(mObjectGraph != null, "object graph must be assigned prior to calling inject");
        mObjectGraph.inject(target);
    }

    /**
     * Returns the list of dagger modules to be included in this ListFragment's object graph.  Subclasses that override
     * this method should add to the list returned by super.getModules().
     *
     * @return the list of modules
     */
    protected List<Object> getModules() {
        List<Object> result = new ArrayList<Object>();
        result.add(new InjectingFragmentModule(this, this));
        return result;
    }
}
