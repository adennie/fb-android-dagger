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

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
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

import static com.fizzbuzz.android.dagger.Preconditions.checkState;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Manages an ObjectGraph on behalf of an Application.
 */
public abstract class InjectingApplication
        extends Application
        implements Injector {

    private List<Object> mSeedModules = new ArrayList<Object>();
    private ObjectGraph mObjectGraph;

    public void addSeedModules(List<Object> modules) {
        mSeedModules.addAll(modules);
    }

    /**
     * Creates an object graph for this Application using the modules returned by {@link #getModules()}.
     * <p/>
     * Injects this Application using the created graph.
     */
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

    /**
     * Gets this Application's object graph.
     *
     * @return the object graph
     */
    @Override
    public ObjectGraph getObjectGraph() {
        return mObjectGraph;
    }

    /**
     * Injects a target object using this Application's object graph.
     *
     * @param target the target object
     */
    public void inject(Object target) {
        checkState(mObjectGraph != null, "object graph must be initialized prior to calling inject");
        mObjectGraph.inject(target);
    }

    /**
     * Returns the list of dagger modules to be included in this Application's object graph.  Subclasses that override
     * this method should add to the list returned by super.getModules().
     *
     * @return the list of modules
     */
    protected List<Object> getModules() {
        List<Object> result = new ArrayList<Object>();
        result.addAll(mSeedModules);
        result.add(new InjectingApplicationModule(this, this));
        return result;
    }

    /**
     * The dagger module associated with {@link InjectingApplication}
     */
    @Module(library = true)
    public static class InjectingApplicationModule {
        android.app.Application mApp;
        Injector mInjector;

        /**
         * Class constructor.
         *
         * @param app the Application with which this module is associated
         * @param injector the dagger injector for the Application-scope graph
         */
        public InjectingApplicationModule(android.app.Application app, Injector injector) {
            mApp = app;
            mInjector = injector;
        }

        /**
         * Provides the Application Context associated with this module's graph.
         *
         * @return the Application Context
         */
        @Provides
        @Singleton
        @Application
        public Context provideApplicationContext() {
            return mApp.getApplicationContext();
        }

        /**
         * Provides the Application
         *
         * @return the Application
         */
        @Provides
        @Singleton
        public android.app.Application provideApplication() {
            return mApp;
        }

        /**
         * Provides the Injector for the Application-scope graph
         *
         * @return the Injector
         */
        @Provides
        @Singleton
        @Application
        public Injector provideApplicationInjector() {
            return mInjector;
        }

        /**
         * Defines an qualifier annotation which can be used in conjunction with a type to identify dependencies within
         * this module's object graph.
         *
         * @see <a href="http://square.github.io/dagger/">the dagger documentation</a>
         */
        @Qualifier
        @Target({FIELD, PARAMETER, METHOD})
        @Documented
        @Retention(RUNTIME)
        public @interface Application {
        }
    }
}
