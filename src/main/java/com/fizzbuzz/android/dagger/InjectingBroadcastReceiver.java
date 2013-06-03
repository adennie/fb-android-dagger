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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;

import javax.inject.Qualifier;
import javax.inject.Singleton;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Manages an ObjectGraph on behalf of a BroadcastReceiver.  This graph is created by extending the application-scope
 * graph with BroadcastReceiver-specific module(s).
 */
public class InjectingBroadcastReceiver
        extends BroadcastReceiver
        implements Injector {

    private Context mContext;
    private ObjectGraph mObjectGraph;

    /**
     * Creates an object graph for this BroadcastReceiver by extending the application-scope object graph with the
     * modules returned by {@link #getModules()}.
     * <p/>
     * Injects this BroadcastReceiver using the created graph.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;

        // extend the application-scope object graph with the modules for this broadcast receiver
        mObjectGraph = ((Injector) context.getApplicationContext()).getObjectGraph().plus(getModules().toArray());

        // then inject ourselves
        mObjectGraph.inject(this);
    }

    /**
     * Gets this BroadcastReceiver's object graph.
     *
     * @return
     */
    @Override
    public ObjectGraph getObjectGraph() {
        return mObjectGraph;
    }

    /**
     * Injects a target object using this BroadcastReceiver's object graph.
     *
     * @param target the target object
     */
    public void inject(Object target) {
        checkState(mObjectGraph != null, "object graph must be initialized prior to calling inject");
        mObjectGraph.inject(target);
    }

    /**
     * Returns the list of dagger modules to be included in this BroadcastReceiver's object graph.  Subclasses that
     * override this method should add to the list returned by super.getModules().
     *
     * @return the list of modules
     */
    protected List<Object> getModules() {
        List<Object> result = new ArrayList<Object>();
        result.add(new InjectingBroadcastReceiverModule(mContext, this, this));
        return result;
    }

    /**
     * The dagger module associated with {@link InjectingBroadcastReceiver}
     */
    @Module(library = true)
    public static class InjectingBroadcastReceiverModule {
        Context mContext;
        android.content.BroadcastReceiver mReceiver;
        Injector mInjector;

        /**
         * Class constructor.
         *
         * @param receiver the InjectingBroadcastReceiver with which this module is associated.
         */
        public InjectingBroadcastReceiverModule(Context context, android.content.BroadcastReceiver receiver, Injector injector) {
            mContext = context;
            mReceiver = receiver;
            mInjector = injector;
        }

        /**
         * Provides the Context for the BroadcastReceiver associated with this graph.
         *
         * @return the BroadcastReceiver Context
         */
        @Provides
        @Singleton
        @BroadcastReceiver
        public Context provideBroadcastReceiverContext() {
            return mContext;
        }
        /**
         * Provides the BroadcastReceiver
         *
         * @return the BroadcastReceiver
         */
        @Provides
        @Singleton
        public android.content.BroadcastReceiver provideBroadcastReceiver() {
            return mReceiver;
        }

        /**
         * Provides the Injector for the BroadcastReceiver-scope graph
         *
         * @return the Injector
         */
        @Provides
        @Singleton
        @BroadcastReceiver
        public Injector provideBroadcastReceiverInjector() {
            return mInjector;
        }
        /**
         * Defines a qualifier annotation which can be used in conjunction with a type to identify dependencies within
         * the object graph.
         *
         * @see <a href="http://square.github.io/dagger/">the dagger documentation</a>
         */
        @Qualifier
        @Target({FIELD, PARAMETER, METHOD})
        @Documented
        @Retention(RUNTIME)
        public @interface BroadcastReceiver {
        }
    }
}