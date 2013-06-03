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

import android.app.Service;
import android.content.Context;
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
 * Manages an ObjectGraph on behalf of an Service.  This graph is created by extending the application-scope graph with
 * Service-specific module(s).
 */
public abstract class InjectingService
        extends Service
        implements Injector {

    private Context mContext;
    private ObjectGraph mObjectGraph;

    /**
     * Creates an object graph for this Service by extending the application-scope object graph with the modules
     * returned by {@link #getModules()}.
     * <p/>
     * Injects this Service using the created graph.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        // extend the application-scope object graph with the modules for this service
        mObjectGraph = ((Injector) getApplication()).getObjectGraph().plus(getModules().toArray());

        // then inject ourselves
        mObjectGraph.inject(this);
    }

    /**
     * Gets this Service's object graph.
     *
     * @return
     */
    @Override
    public ObjectGraph getObjectGraph() {
        return mObjectGraph;
    }

    /**
     * Injects a target object using this Service's object graph.
     *
     * @param target the target object
     */
    public void inject(Object target) {
        checkState(mObjectGraph != null, "object graph must be initialized prior to calling inject");
        mObjectGraph.inject(target);
    }

    /**
     * Returns the list of dagger modules to be included in this Service's object graph.  Subclasses that override
     * this method should add to the list returned by super.getModules().
     *
     * @return the list of modules
     */
    protected List<Object> getModules() {
        List<Object> result = new ArrayList<Object>();
        return result;
    }

    /**
     * The dagger module associated with {@link InjectingService}.
     */
    @Module(library = true)
    public static class InjectingServiceModule {
        private android.app.Service mService;
        private Injector mInjector;

        /**
         * Class constructor.
         *
         * @param service the Service with which this module is associated.
         */
        public InjectingServiceModule(android.app.Service service, Injector injector) {
            mService = service;
            mInjector = injector;
        }

        /**
         * Provides the Application Context
         *
         * @return the Application Context
         */
        @Provides
        @Singleton
        @InjectingApplication.InjectingApplicationModule.Application
        public Context provideApplicationContext() {
            return mService.getApplicationContext();
        }

        @Provides
        @Singleton
        public android.app.Service provideService() {
            return mService;
        }

        @Provides
        @Singleton
        @Service
        public Injector provideServiceInjector() {
            return mInjector;
        }

        @Qualifier
        @Target({FIELD, PARAMETER, METHOD})
        @Documented
        @Retention(RUNTIME)
        public @interface Service {
        }
    }
}