##fb-android-dagger

A set of helper classes for using [dagger][dagger_url] with Android components such as Applications, Activities, Fragments, BroadcastReceivers, and Services.
  [dagger_url]: https://github.com/square/dagger
  
Maven users: 

```xml
<dependency>
   <groupId>com.fizz-buzz</groupId>
   <artifactId>fb-android-dagger</artifactId>
   <version>1.0.3</version>
</dependency>
```

####Overview

fb-android-dagger provides a set of base classes for using dagger in Android applications.  It follows a convention exemplified by Square's sample application, whereby a base class calls getModules() to gather the set of modules that its subclass(es) need to be part of the graph, then creates an object graph from those modules and injects itself.

The following classes are provided, each implementing this technique in the method indicated in parentheses:

 - `InjectingApplication` (onCreate)
 - `InjectingBroadcastReceiver` (onReceive)
 - `InjectingService` (onCreate)
 - `InjectingActivity` (onCreate)
 - `InjectingFragmentActivity` (onCreate)
 - `InjectingPreferenceActivity` (onCreate)
 - `InjectingFragment` (onAttach)
 - `InjectingListFragment` (onAttach)
 - `InjectingDialogFragment` (onAttach)

####The graphs

`InjectingApplication` creates an application-scope graph.  

`InjectingBroadcastReciever`, `InjectingService`, and `InjectingActivity`/`InjectingFragmentActivity`/`InjectingPreferenceActivity` each extend the application-scope graph with their own module(s), resulting in a graph scoped to their own component.

`InjectingFragment`/`InjectingListFragment``/InjectingDialogFragment` extend their corresponding activity's graph.

####Modules

Each component type has an associated module:
 - `InjectingActivityModule`
 - `InjectingServiceModule`
 - `InjectingBroadcastRecieverModule`
 - `InjectingActivityModule`
 - `InjectingFragmentModule`

The last two are shared by the three injecting activity classes and the three injecting fragment classes, respectively.  These modules define provider methods which enable injection of objects relevant to their component type.  They all have a provider that returns the component itself in the form of an Injector interface:

```java
public interface Injector {
    public ObjectGraph getObjectGraph();
    public void inject(Object target);
}
```

Qualifier annotations defined in the various modules allow for specific components' injectors to be accessed:

```java
class Foo1 {
    @Inject @Application Injector appInjector;
    @Inject @Activity Injector activityInjector;
    @Inject @Fragment Injector fragInjector;
    //...
}
```

Application and Activity Contexts are similarly accessible:

```java
class Foo2 {
    @Inject @Application Context appContext;
    @Inject @Activity Context activityContext;
    // ...
}
```
    
...as are the typed components themselves:

```java
class Foo3 {
    @Inject Application theApp;
    @Inject Activity myActivity;
    @Inject Fragment myFragment;
    // ...
}
```
    


####Developed by

Andy Dennie - andy@fizz-buzz.com

####License

Copyright 2014 Fizz Buzz LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
