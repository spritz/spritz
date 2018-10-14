# streak

[![Build Status](https://secure.travis-ci.org/realityforge-experiments/streak.png?branch=master)](http://travis-ci.org/realityforge-experiments/streak)
[<img src="https://img.shields.io/maven-central/v/org.realityforge.streak/streak.svg?label=latest%20release"/>](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.realityforge.streak%22%20a%3A%22streak%22)

Streak contains some experiments with reactive streaming code.

## Links

* ReactiveX Scheduler: http://reactivex.io/documentation/scheduler.html
* ReactiveX Operators: http://reactivex.io/documentation/operators.html
* Reactive Streams Specification: https://github.com/reactive-streams/reactive-streams-jvm
* Reactive Streams Simple unicast implementation: https://github.com/reactive-streams/reactive-streams-jvm/tree/v1.0.2/examples/src/main/java/org/reactivestreams/example/unicast
* A basic js implementation: https://github.com/zenparsing/zen-observable
* RxJava implementation: https://github.com/ReactiveX/RxJava
* Reactor Overview - reactor another implementation: https://www.infoq.com/articles/reactor-by-example
* "Learning Observable By Building Observable": https://medium.com/@benlesh/learning-observable-by-building-observable-d5da57405d87

* Best API seems to be a combination of [Bacon](http://baconjs.github.io/api2.html) and
  [xstream](https://github.com/staltz/xstream)

* A very interesting library conceptually is callbag. It explicitly calls out differences between push/pull sources
  and push/pull sinks.
  - https://github.com/staltz/callbag-basics/blob/master/readme.md#api
  - https://github.com/callbag/callbag/blob/master/getting-started.md
  - https://github.com/staltz/callbag-basics
  - Operators - https://github.com/callbag/callbag/wiki

In particular here is a snippet of some terminology.

* source: a callbag that delivers data
* sink: a callbag that receives data
* puller sink: a sink that actively requests data from the source
* pullable source: a source that delivers data only on demand (on receiving a request)
* listener sink: a sink that passively receives data from the source
* listenable source: source which sends data to the sink without waiting for requests
* operator: a callbag based on another callbag which applies some operation

## To Implement

**Source Factories**

- [ ] `Interval(Duration)` - Every N time frame produce a value (monotonically increasing counter?)
- [ ] `fromIterable(Iterable)` - From an iterable
- [ ] `fromCollection(Collection)` - A collection.
- [ ] `fromPromise(Promise)` - This conversion is also built into several other higher-order operations (i.e. `switchMap()` so that if you map to to promise it will convert to promise).

Must have processors:

**Filtering Processors** (Remove items from stream)

- [x] `Filter(Predicate)` - only pass next value if predicate returns true
- [x] `Take(Count)` - take first "count" items (then unsubscribe from source?)
- [x] `First` == `Take(1)`
- [x] `Skip(Count)` - filter out the first "count" items
- [x] `SkipUntil(Predicate)` - filter out all items until predicate returns true the first time.
- [ ] `TaskLast(Count)` -- take last "count" items. i.e. Wait to onComplete and send last Count items. Needs a buffer `Count` long.
- [ ] `Last` == `TaskLast(1)`

**Transformation Processors** (Take items from one stream and transform them)

- [ ] `flatMap(Function<Publisher[]>)` - given one input, produce zero or more publishers. The items from publishers are flattened into source stream.
                                         Alternatively we could replace with `map(Function<Publisher[]>).flatten()` which seems a better approach.
- [x] `map` - convert value from one type to another

**Combination Processors** (Take 2 or more streams and combine) (a.k.a vertical merging operations as it combines values across streams)

- [ ] `append(Publishers) == merge(1,Publishers)` - for each publisher wait till it produces onComplete, elide that signal and then subscribe to next. (a.k.a `concat` or `concatAll`)
- [ ] `prepend(Publishers)` == `append(reverse(Publishers))`
- [ ] `startWith(value)` == `prepend(of(value))`

- [ ] `merge(ConcurrentCount,Publishers)` (a.k.a. `or(Publishers)`) - for each stream if it produces a value then pass on value. onComplete if all onComplete, onError if any onError. Optionally can pass a `ConcurrentCount` which is the maximum number of concurrent observers, `0` to disable.
- [ ] `combineLatest(Publishers)` - for each stream grab latest value and pass through a function and pass on result of function this happens anytime an item is received on any stream. onComplete if all onComplete, onError if any onError
- [ ] `withLatestFrom(Publisher,Publishers)` - for a primary stream, any time an item appears combine it with latest from other streams using function to produce new item. onComplete if all onComplete, onError if any onError
- [ ] `zip(Publishers)` - select N-th value of each stream and combine them using a function. onComplete if all onComplete, onError if any onError
- [ ] `firstEmitting(Publisher...)` or `selectFirstEmitting(Publisher...)` - wait for first publisher to emit a value, select publisher and then cancel other publishers and pass on signals from selected publisher

**Accumulating Processors** (Takes 1 or more values from a single streams and combine) (a.k.a horizontal merging operations as it combines values within streams)

- [ ] `scan((accumulator, item) => {...function...}, initialValue)` - For each value in stream pass it into accumulating function that takes current accumulated value and new value to produce new value. Initial value for accumulator is specified at startup. A new value is emitted for each item.
- [ ] `bufferByCount` - wait for Count items and then emit them as an array. onComplete send may remaining?
- [ ] `bufferByTime` - wait for time buffering items.
- [ ] `bufferByPredicate` - use predicate to determine when to emit - predicate passed each item.
- [ ] `bufferBySignal` - Another stream signals when to open and/or close buffering operation.

**HigherOrder Observers**

- [ ] `switch` - Input stream contains streams. Each time new item appears, switch stage unsubscribes from current (if any) and subscribes to new item.
- [ ] `switchMap(MapFn) == map(MapFn).switch()` - Extremely useful

All the windowing functions take an input stream that they cut up into segments where each segment is a new stream.

- [ ] `window(ControlStream)` - Create an inner stream each time next occurs on control stream and forward all `onNext` calls onto inner stream.
- [ ] `windowByTime(WindowTime)` - Create a new inner stream every `WindowTime` time.
- [ ] `windowByCount(Count)` - Create a new inner stream every `Count` items.
- [ ] `window(OnControlStream,OffControlStream)` - Create a new inner stream every starting on signal from `OnControlStream` and then ending when signal occurs on `OffControlStream`.

Other

- [ ] `groupBy(GroupByFunction)` - Create an inner stream based on group returned by function. Stream can be concurrent.

**Terminator Subscribers**

- [x] `forEach(Action)` - perform action for each value.
- [ ] `reduce((accumulator, item) => {...function...}, initialValue) == scan((accumulator, item) => {...function...}, initialValue).last(1)` - Same as scan except final value is emitted on onComplete.

-----

- [ ] `delayBy(DelayFunction) where DelayTime DelayFunction(Item)` - delay each item by variable time returned by delay function. This involves buffering them for a time and may result in reordered messages.
- [ ] `delay(DelayTime) == delayBy(_ -> DelayTime) ` - delay each item by DelayTime. This involves buffering them by a fixed time.
- [ ] `delaySubscriptionBy(DelayFunction) where DelayTime DelayFunction(Subscription)` - delay subscription of upstream by variable time returned by delay function.
- [ ] `delaySubscription(DelayTime) == delaySubscriptionBy(_ -> DelayTime)` - delay subscription of upstream by DelayTime
- [ ] `peek(Action)` - perform an action on each value that goes by
- [ ] `distinct()` - only send item first time it appears in stream. Potentially needs a very large map in which items are registered. A variant on this accepts another stream and when that stream emits an item the registry is cleared.
- [ ] `distinctInSuccession()` or `distinctUntilChanged()` - only send item first time it appears in stream. Need to buffer last.
- [ ] `sort()` - buffer all items until onComplete then apply some sorting
- [ ] `debounceBy(DebounceFunction) where DebounceTime DebounceFunction(Item)` - emit an item from stream if DebounceTime has passed without another value being emitted from upstream.
- [ ] `debounce(DebounceTime) == debounceBy(_ -> DebounceTime)`
- [ ] `throttle(ThrottleTime)` - wait for `ThrottleTime` after an emit before being able to emit again. Contrast this with debounce which is "wait for silence of time X then emit" and this which is "emit then silence for X time". Can be implemented as filter.

**Control FLow**

- [ ] `catch((error, downstream) -> myFunction)` - If `onError` then call callback. Often this will replace onError signal with onComplete or do something like omit error and re-subscribe
- [ ] `retry(RetryCount)` - The retry operator is an `onError` operator that simply resubscribes on error and starts again and it will do it `RetryCount` times or infinite times if not specified. If ceases to retry then pass onError signal down.
- [ ] `retryWhen(RetryCount, RetryWhenFunction)` - Like retry but a function indicates when should start retry.
- [ ] `repeat(RepeatCount)` - Replace `onComplete` with subscription to stream again a `RepeatCount` number of times..

**Subjects**

- [ ] `multicast(Subject)` - Add Subject as observer. Returns a "Connectible" processor that you need to call Connect on before it activates.
- [ ] `refCount()` - Called on a "Connectible" processor. Will call connect after a subscriber added to it, and will disconnect when no more subscribers.
- [ ] `publish() == multicast(new Subject())`
- [ ] `share() == publish().refCount()`
- [ ] `publishReplay(count) == multicast(new ReplaySubject(count))`
- [ ] `publishBehaviour(initialValue) == multicast(new BehaviourSubject(initialValue))`

Multicast producers allow you to add N (a.k.a. subscribe) multiple subscribers which it will publish to. i.e. they
are processors - both publishers an subscribers. In rxjs they are `Subjects`. There is also `BehaviourSubjects` that
cache the last value emitted so new subscriptions will send the "current" value or the last value emitted by upstream.
`ReplaySubjects` are similar to `BehaviourSubjects` except they cache N values rather than 1 and replay all N when new
subscribers connect. `BehaviourSubjects` may also have a time window after which signals will be dropped. So replay
subjects have a buffer bound by size and time window. `BehaviourSubjects` will also not emit a value if you subscribe
after they are complete where-as replay will always replay signals.


- [ ] `replay()` - Replay many signals before or after completion
- [ ] `behaviourSubject()` - Replay one value, only before completion
- [ ] `asyncSubject()` - Replay one, only after completion


Note: Several of the above functions take functions that control when an event occurs (i.e. when an event is
delayed to, when a retry occurs). These methods should also take an observable that signals when the action should
occur and will be unsubscribed from after that.

## TODO

The factory DSL (i.e. `var a = Streak.of( 1, 2, 3, 4 ).filter( v -> v > 2 ).first()`) contains no execution data
and thus could subscribe to the same sequence multiple times to get multiple executions. "Subjects" are used to
create a single execution that are streaks could subscribe to.

`StreamingProperties` or `StreamingValues` may be a different way to think of an event stream.

### Schedulers

Each executor has N circular queues to perform tasks with N being the number of priority levels. There is different
executors for different events:
* `currentTask` executor. i.e. Will run tasks next explicit trigger. Same as current loop as in Arez.
* Maybe microtask executor? (i.e. `Promise.resovle().then( () -> doStuff() )`). Micro tasks run after javascript task stack returns to runtime.
* `requestAnimationFrame` executor
* `requestIdleCallback` executor.

There is also a `setTimout`/`setInterval` schedulers that will keep task records and then queue them on `currentTask`
executor and trigger the executor.

Whenever an executor is processing tasks it is marked as current and any task that schedule using default mechanisms
are added to the same executor.

Each executor may have different policy on determining when to stop processing tasks.
* Arez uses round based approach that but will try to process all.
* `requestIdleCallback` uses a deadline and will keep processing work until deadline would be exceeded (uses a guess at mimium task times).
* Others may keep processing tasks until a minimum time  has been processed or a single round has occurred or etc.

A task can only be scheduled on a single executor at any one time.

### Rethink

A stream is a series of steps. Some sequences of steps are push based. i.e. The agent that pushes value executes
the sequence of steps and can not be told to stop generating values except via unsusbcribe. Some sequences are
pull based in that the agent that requests a number of values and each step will execute the steps in the requesters
execution context or if not enough values are present could execute in a scheduler context but will only send as many
values as has been requested. Separating these sequences are barriers and the barriers may consist of queues. Sometimes
these barriers will try and signal to upstream to pause delivery (will be ignored for push based sequences) or will
employ other strategies to deal with event stream  not being consumed at a fast enough rate (i.e. store, drop newest,
drop latest etc). Barriers that are queues will often be scheduled in the same executor when values are added but may
also be scheduled at some point in the future. (i.e. schedule at next requestIdleCallback)

* `.queue()` - create a barrier containing a queue

* Maybe the flow control object can be passed up and down without wrapping if step needs no mods


    interface FlowControl
    {
      // pull based
      void requestItems(Count)

      // push based with flow control
      void activate()
      void pause()
      void resume()
      void deactivate()

      // push based without flow control
      void activate()
      void deactivate()
    }


Some notes about xstream that may be useful inspiration:

    In xstream, all streams are hot. They are a hybrid between RxJS’s Subject and a publish-refCount cold Observable.
    All streams keep a list of listeners, and have operators to create new streams dependent on the source stream.
    Stream execution is lazy through reference counting, with a synchronous start (“connect”) and an asynchronous
    and cancelable stop (“disconnect”). This is built to allow for those cases where we synchronously swap the single
    listener of a stream but we don’t want to restart the stream’s execution. The goal is to have a smart default
    behavior that “just works” transparently in Cycle.js apps. But we want to keep laziness, to avoid wasting resources.

Alternative design: Only the "source" publisher can perform flow control. That is optionally accessible
from subscription via something like `@Nullable Subscription.getFlowControl()`

### React4j Integration

Interesting idea - enhance react4j so that can convert handlers into stream sources. Also bind props to output
streams. This is based on vue integration that uses similar thing inside template language ala
https://egghead.io/courses/build-async-vue-js-apps-with-rxjs - In react4j the approach is probably to create a
wrapper component? stream pipes in new props each time?
