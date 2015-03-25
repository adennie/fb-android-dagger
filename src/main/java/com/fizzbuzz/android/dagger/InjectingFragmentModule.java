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

import android.app.Fragment;
import dagger.Module;
import dagger.Provides;

import javax.inject.Qualifier;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The dagger module associated with {@link InjectingFragment}, {@link InjectingDialogFragment} and {@link
 * InjectingListFragment}.
 */
@Module(library = true)
public class InjectingFragmentModule {
    private android.support.v4.app.Fragment mSupportV4Fragment;
    private android.app.Fragment mFragment;
    private Injector mInjector;

    /**
     * Class constructor.
     *
     * @param fragment the Fragment with which this module is associated.
     */
    public InjectingFragmentModule(android.support.v4.app.Fragment fragment, Injector injector) {
        mSupportV4Fragment = fragment;
        mInjector = injector;
    }

    /**
     * Class constructor.
     *
     * @param fragment the Fragment with which this module is associated.
     */
    public InjectingFragmentModule(android.app.Fragment fragment, Injector injector) {
        mFragment = fragment;
        mInjector = injector;
    }

    /**
     * Provides a Support v4 Fragment
     *
     * @return the Fragment
     */
    @Provides
    public android.support.v4.app.Fragment provideSupportV4Fragment() {
        return mSupportV4Fragment;
    }

    /**
     * Provides a Fragment
     *
     * @return the Fragment
     */
    @Provides
    public android.app.Fragment provideFragment() {
        return mFragment;
    }

    /**
     * Provides the Injector for the Fragment-scope graph
     *
     * @return the Injector
     */
    @Provides
    @Fragment
    public Injector provideFragmentInjector() {
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
    public @interface Fragment {
    }
}
