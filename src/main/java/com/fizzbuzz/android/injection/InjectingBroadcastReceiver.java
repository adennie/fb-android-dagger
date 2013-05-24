package com.fizzbuzz.android.injection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.fizzbuzz.android.injection.Injector;
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

public class InjectingBroadcastReceiver
        extends BroadcastReceiver
        implements Injector {

    private Context mContext;
    private ObjectGraph mObjectGraph;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;

        // extend the application-scope object graph with the modules for this broadcast receiver
        mObjectGraph = ((Injector)context.getApplicationContext()).getObjectGraph().plus(getModules().toArray());

        // then inject ourselves
        mObjectGraph.inject(this);
    }

    @Override
    public ObjectGraph getObjectGraph() {
        return mObjectGraph;
    }

    public void inject(Object target) {
        checkState(mObjectGraph != null, "object graph must be initialized prior to calling inject");
        mObjectGraph.inject(target);
    }

    protected List<Object> getModules() {
        List<Object> result = new ArrayList<Object>();
        result.add(new InjectingBroadcastReceiverModule(mContext));
        return result;
    }

    @Module(library=true)
    public static class InjectingBroadcastReceiverModule {
        Context mBrContext;

        public InjectingBroadcastReceiverModule(Context brContext) {
            mBrContext = brContext;
        }

        @Provides
        @Singleton
        @BroadcastReceiver
        public Context provideBroadcastReceiverContext() {
            return mBrContext;
        }

        @Qualifier
        @Target({FIELD, PARAMETER, METHOD})
        @Documented
        @Retention(RUNTIME)
        public @interface BroadcastReceiver {
        }
    }
}