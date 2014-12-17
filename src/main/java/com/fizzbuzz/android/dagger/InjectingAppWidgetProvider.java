/*
 * Copyright (c) 2014 Fizz Buzz LLC
 */

package com.fizzbuzz.android.dagger;

import android.appwidget.AppWidgetProvider;
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

import static com.fizzbuzz.android.dagger.Preconditions.checkState;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Manages an ObjectGraph on behalf of an AppWidgetProvider.  This graph is created by extending the application-scope
 * graph with AppWidgetProvider-specific module(s).
 */
public class InjectingAppWidgetProvider
        extends AppWidgetProvider
        implements Injector {

    private Context mContext;
    private ObjectGraph mObjectGraph;

    /**
     * Creates an object graph for this AppWidgetProvider by extending the application-scope object graph with the
     * modules returned by {@link #getModules()}.
     * <p/>
     * Injects this AppWidgetProvider using the created graph.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;

        // extend the application-scope object graph with the modules for this AppWidgetProvider
        mObjectGraph = ((Injector) context.getApplicationContext()).getObjectGraph().plus(getModules().toArray());

        // then inject ourselves
        mObjectGraph.inject(this);

        super.onReceive(context, intent);
    }

    /**
     * Gets this AppWidgetProvider's object graph.
     *
     * @return the object graph
     */
    @Override
    public ObjectGraph getObjectGraph() {
        return mObjectGraph;
    }

    /**
     * Injects a target object using this AppWidgetProvider's object graph.
     *
     * @param target the target object
     */
    public void inject(Object target) {
        checkState(mObjectGraph != null, "object graph must be initialized prior to calling inject");
        mObjectGraph.inject(target);
    }

    /**
     * Returns the list of dagger modules to be included in this AppWidgetProvider's object graph.  Subclasses that
     * override this method should add to the list returned by super.getModules().
     *
     * @return the list of modules
     */
    protected List<Object> getModules() {
        List<Object> result = new ArrayList<Object>();
        result.add(new InjectingAppWidgetProviderModule(mContext, this, this));
        return result;
    }

    /**
     * The dagger module associated with {@link com.fizzbuzz.android.dagger.InjectingAppWidgetProvider}
     */
    @Module(library = true)
    public static class InjectingAppWidgetProviderModule {
        Context mContext;
        android.appwidget.AppWidgetProvider mAppWidgetProvider;
        Injector mInjector;

        /**
         * Class constructor.
         *
         * @param appWidgetProvider the InjectingAppWidgetProvider with which this module is associated.
         */
        public InjectingAppWidgetProviderModule(Context context, android.appwidget.AppWidgetProvider appWidgetProvider, Injector injector) {
            mContext = context;
            mAppWidgetProvider = appWidgetProvider;
            mInjector = injector;
        }

        /**
         * Provides the Context for the AppWidgetProvider associated with this graph.
         *
         * @return the AppWidgetProvider Context
         */
        @Provides
        @Singleton
        @AppWidgetProvider
        public Context provideAppWidgetProviderContext() {
            return mContext;
        }
        /**
         * Provides the AppWidgetProvider
         *
         * @return the AppWidgetProvider
         */
        @Provides
        @Singleton
        public android.appwidget.AppWidgetProvider provideAppWidgetProvider() {
            return mAppWidgetProvider;
        }

        /**
         * Provides the Injector for the AppWidgetProvider-scope graph
         *
         * @return the Injector
         */
        @Provides
        @Singleton
        @AppWidgetProvider
        public Injector provideAppWidgetProviderInjector() {
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
        public @interface AppWidgetProvider {
        }
    }
}