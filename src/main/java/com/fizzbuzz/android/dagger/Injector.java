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

import dagger.ObjectGraph;

public interface Injector {
    /**
     * Gets the object graph for this component.
     * @return the object graph
     */
    public ObjectGraph getObjectGraph();

    /**
     * Injects a target object using this component's object graph.
     * @param target the target object
     */
    public void inject(Object target);
}
