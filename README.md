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

## To Implement

Must have publishers:
- [ ] `Interval(Duration)` - Every N time frame produce a value (monotonically increasing counter?)
- [ ] `fromIterable(Iterable)` - From an iterable
- [ ] `fromCollection(Collection)` - A collection.

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
- [x] `map` - convert value from one type to another

**Combination Processors** (Take 2 or more streams and combine) (a.k.a vertical merging operations as it combines values across streams)

- [ ] `append(Publishers)` - for each publisher wait till it produces onComplete, elide that signal and then
                       subscribe to next. (a.k.a `concat`)
- [ ] `prepend(Publishers)` == `append(reverse(Publishers))`
- [ ] `startWith(value)` == `prepend(of(value))`
- [ ] `merge(Publishers)` (a.k.a. `or(Publishers)`) - for each stream if it produces a value then pass on value. onComplete if all onComplete, onError if any onError
- [ ] `combineLatest(Publishers)` - for each stream grab latest value and pass through a function and pass on result of function this happens anytime an item is received on any stream. onComplete if all onComplete, onError if any onError
- [ ] `withLatestFrom(Publisher,Publishers)` - for a primary stream, any time an item appears combine it with latest from other streams using function to produce new item. onComplete if all onComplete, onError if any onError
- [ ] `zip(Publishers)` - select N-th value of each stream and combine them using a function. onComplete if all onComplete, onError if any onError
- [ ] `firstEmitting(Publisher...)` or `selectFirstEmitting(Publisher...)` - wait for first publisher to emit a value, select publisher and then cancel other publishers and pass on signals from selected publisher

**Accumulating Processors** (Takes 1 or more values from a single streams and combine) (a.k.a horizontal merging operations as it combines values within streams)

- [ ] `scan((accumulator, item) => {...function...}, initialValue)` - For each value in stream pass it into accumulating function that takes current accumulated value and new value to produce new value. Initial value for accumulator is specified at startup. A new value is emitted for each item.
- [ ] `reduce((accumulator, item) => {...function...}, initialValue)` - Same as scan except final value is emitted on onComplete.
- [ ] `bufferByCount` - wait for Count items and then emit them as an array. onComplete send may remaining?
- [ ] `bufferByTime` - wait for time buffering items.
- [ ] `bufferByPredicate` - use predicate to determine when to emit - predicate passed each item.
- [ ] `bufferBySignal` - Another stream signals when to open and/or close buffering operation.

**Terminator Subscribers**

- [x] `forEach(Action)` - perform action for each value.

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
