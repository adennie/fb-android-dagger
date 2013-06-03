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

import android.content.Context;
import dagger.Module;
import dagger.Provides;

import javax.inject.Qualifier;
import javax.inject.Singleton;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The dagger module associated with {@link InjectingActivity}, {@link InjectingFragmentActivity},
 * and {@link InjectingPreferenceActivity}.
 */
@Module(library = true)
public class InjectingActivityModule {
    private android.app.Activity mActivity;
    private Injector mInjector;

    /**
     * Class constructor.
     *
     * @param activity the Activity with which this module is associated.
     * @param injector the Injector for the Application-scope graph
     */
    public InjectingActivityModule(android.app.Activity activity, Injector injector) {
        mActivity = activity;
        mInjector = injector;
    }

    /**
     * Provides the Activity Context
     *
     * @return the Activity Context
     */
    @Provides
    @Singleton
    @Activity
    public android.content.Context provideActivityContext() {
        return (Context)mActivity;
    }
    /**
     * Provides the Activity
     *
     * @return the Activity
     */
    @Provides
    public android.app.Activity provideActivity() {
        return mActivity;
    }

    /**
     * Provides the Injector for the Activity-scope graph
     *
     * @return the Injector
     */
    @Provides
    @Activity
    public Injector provideActivityInjector() {
        return mInjector;
    }

    /**
     * Defines an qualifier annotation which can be used in conjunction with a type to identify dependencies within
     * the object graph.
     *
     * @see <a href="http://square.github.io/dagger/">the dagger documentation</a>
     */
    @Qualifier
    @Target({FIELD, PARAMETER, METHOD})
    @Documented
    @Retention(RUNTIME)
    public @interface Activity {
    }
}
