# streak

[![Build Status](https://secure.travis-ci.org/realityforge-experiments/streak.png?branch=master)](http://travis-ci.org/realityforge-experiments/streak)
[<img src="https://img.shields.io/maven-central/v/org.realityforge.streak/streak.svg?label=latest%20release"/>](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.realityforge.streak%22%20a%3A%22streak%22)

Streak contains some experiments with reactive streaming code or reactive streams. It is best
used when coordinating events.

A stream is a sequence of events over time. For example a stream of click events from a UI control
or a stream of messages over a WebSocket. A listener reacts to events emitted by a stream and these
events may be values, errors and completion notification. Operators filter, transform and combine
input streams. An operator never modifies the input stream but instead creates a new stream.

----

We should adopt terminology from microprofile reactive streams proposal as it will soon be present in most java
backend APIs.

* https://github.com/eclipse/microprofile-reactive-streams
* https://github.com/eclipse/microprofile-reactive-messaging

----

Seriously consider aligning with RXJS 7 as they seem to have adopted some interesting ideas.
See https://github.com/ReactiveX/rxjs/blob/experimental/EXPERIMENTAL_NOTES.md

## Links

* [Most](https://mostcore.readthedocs.io/en/latest/concepts.html)
* [Bacon](http://baconjs.github.io/api2.html)
* [ReactiveX Scheduler](http://reactivex.io/documentation/scheduler.html)
* [ReactiveX Operators](http://reactivex.io/documentation/operators.html)
* [RxJava](https://github.com/ReactiveX/RxJava)
* [RxJs](https://rxjs-dev.firebaseapp.com/api)
* [Reactor Overview](https://www.infoq.com/articles/reactor-by-example)

* A very interesting library conceptually is callbag. It explicitly calls out differences between push/pull sources/sinks.
  - https://github.com/staltz/callbag-basics/blob/master/readme.md#api
  - https://github.com/callbag/callbag/blob/master/getting-started.md
  - https://github.com/staltz/callbag-basics
  - Operators - https://github.com/callbag/callbag/wiki

## To Implement

Continue the build out based on https://egghead.io/courses/build-your-own-rxjs-pipeable-operators

Add test infrastructure based on https://www.infoq.com/articles/reactor-by-example

**Source Factories**

- [ ] `fromEvent(EventSource)` - Create events from DOM/JS event source.
- [ ] `fromIterable(Iterable)` - From an iterable
- [ ] `fromPromise(Promise)` - This conversion is also built into several other higher-order operations (i.e. `switchMap()` so that if you map to to promise it will convert to promise).
- [ ] `fail()` - Create a stream that immediately emits an error signal.

**Filtering Operators**

- [ ] `FilterByControlStreams(OnControlStream, OffControlStream)` - allow elements to pass after `OnControlStream` emits an element but before `OffControlStream` emits an element. Complete if both streams complete. Error if either completes.
- [ ] `TakeUntil(ControlStream)` a.k.a. `FilterByControlStreams(ControlStream.mapTo(true).startWith(true).take(2), Streak.empty())` - take until `ControlStream` emits an element or completes.
- [ ] `SkipUntil(ControlStream)` a.k.a. `FilterByControlStreams(Streak.empty(),ControlStream.first())` - skip until `ControlStream` emits an element or completes.

**Combination Operators** (Take 2 or more streams and combine) (a.k.a vertical merging operations as it combines values across streams)

- [ ] `combineLatest(Streams)` - for each stream grab latest value and pass through a function and pass on result of function this happens anytime an item is received on any stream. onComplete if all onComplete, onError if any onError
- [ ] `withLatestFrom(Stream,Streams)` - for a primary stream, any time an item appears combine it with latest from other streams using function to produce new item. onComplete if all onComplete, onError if any onError
- [ ] `zip(Streams)` - select N-th value of each stream and combine them using a function. onComplete if all onComplete, onError if any onError
- [ ] `firstEmitting(Stream...)` or `selectFirstEmitting(Stream...)` or `race(Stream...)` - wait for first publisher to emit a value, select publisher and then cancel other publishers and pass on signals from selected publisher

**Accumulating Operators** (Takes 1 or more values from a single streams and combine) (a.k.a horizontal merging operations as it combines values within streams)

- [ ] `bufferByCount` - wait for Count items and then emit them as an array. onComplete send may remaining?
- [ ] `bufferByTime` - wait for time buffering items.
- [ ] `bufferByPredicate` - use predicate to determine when to emit - predicate passed each item.
- [ ] `bufferBySignal` - Another stream signals when to open and/or close buffering operation.

**HigherOrder Operators**

All the windowing functions take an input stream that they cut up into segments where each segment is a new stream.

- [ ] `window(ControlStream)` - Create an inner stream each time next occurs on control stream and forward all `onNext` calls onto inner stream.
- [ ] `windowByTime(WindowTime)` - Create a new inner stream every `WindowTime` time.
- [ ] `windowByCount(Count)` - Create a new inner stream every `Count` items.
- [ ] `window(OnControlStream,OffControlStream)` - Create a new inner stream every starting on signal from `OnControlStream` and then ending when signal occurs on `OffControlStream`.

Other

- [ ] `groupBy(GroupByFunction)` - Create an inner stream based on group returned by function. Stream can be concurrent.

**Terminator Subscribers**

- [ ] `reduce((accumulator, item) => {...function...}, initialValue) == scan((accumulator, item) => {...function...}, initialValue).last(1)` - Same as scan except final value is emitted on onComplete.

-----

- [ ] `delayBy(DelayFunction) where DelayTime DelayFunction(Item)` - delay each item by variable time returned by delay function. This involves buffering them for a time and may result in reordered messages.
- [ ] `delay(DelayTime) == delayBy(_ -> DelayTime) ` - delay each item by DelayTime. This involves buffering them by a fixed time.
- [ ] `delaySubscriptionBy(DelayFunction) where DelayTime DelayFunction(Subscription)` - delay subscription of upstream by variable time returned by delay function.
- [ ] `delaySubscription(DelayTime) == delaySubscriptionBy(_ -> DelayTime)` - delay subscription of upstream by DelayTime
- [ ] `sort()` - buffer all items until onComplete then apply some sorting
- [ ] `debounceBy(DebounceFunction) where DebounceTime DebounceFunction(Item)` - delay emit an item from stream if `DebounceTime` has passed without another value being emitted from upstream.
- [ ] `debounce(DebounceTime) == debounceBy(_ -> DebounceTime)` - only emit an item from an Observable if a `DebounceTime` timespan has passed without it emitting another item.
- [ ] `throttle(ThrottleTime)` - wait for `ThrottleTime` after an emit before being able to emit again. Contrast this with debounce which is "wait for silence of time X then emit" and this which is "emit then silence for X time". Can be implemented as filter.

**Control Flow**

- [ ] `catch((error, downstream) -> myFunction)` - If `onError` then call callback. Often this will replace onError signal with onComplete or do something like omit error and re-subscribe
- [ ] `retry(RetryCount)` - The retry operator is an `onError` operator that simply resubscribes on error and starts again and it will do it `RetryCount` times or infinite times if not specified. If ceases to retry then pass onError signal down.
- [ ] `retryWhen(RetryCount, RetryWhenFunction)` - Like retry but a function indicates when should start retry.
- [ ] `repeat(RepeatCount)` - Replace `onComplete` with subscription to stream again a `RepeatCount` number of times.
- [ ] `endWhen(ControlStream)` - Complete the stream when the `ControlStream` emits a value or completes.

**Subjects**

- [ ] `multicast(Subject)` - Add Subject as observer. Returns a "Connectible" processor that you need to call Connect on before it activates.
- [ ] `refCount()` - Called on a "Connectible" processor. Will call connect after a subscriber added to it, and will disconnect when no more subscribers.
- [ ] `publish() == multicast(new Subject())`
- [ ] `share() == publish().refCount()`
- [ ] `publishReplay(count) == multicast(new ReplaySubject(count))`
- [ ] `publishBehaviour(initialValue) == multicast(new BehaviourSubject(initialValue))`
- [ ] `remember() == multicast(new BehaviourSubject())` - create a subject that remembers the last value

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

## Fetch/http controller

* Make use of https://developer.mozilla.org/en-US/docs/Web/API/AbortController

## TODO

The factory DSL (i.e. `var a = Streak.of( 1, 2, 3, 4 ).filter( v -> v > 2 ).first()`) contains no execution data
and thus could subscribe to the same sequence multiple times to get multiple executions. "Subjects" are used to
create a single execution that are streaks could subscribe to.

`StreamingProperties` or `StreamingValues` may be a different way to think of an event stream.

Consider renaming `Stream.subscribe(...)` to `Stream.observe(...)`

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
the sequence of steps and can not be told to stop generating values except via unsubscribe. Some sequences are
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


Alternative design: Only the "source" publisher can perform flow control. That is optionally accessible
from subscription via something like `@Nullable Subscription.getFlowControl()`

### React4j Integration

Interesting idea - enhance react4j so that can convert handlers into stream sources. Also bind props to output
streams. This is based on vue integration that uses similar thing inside template language ala
https://egghead.io/courses/build-async-vue-js-apps-with-rxjs - In react4j the approach is probably to create a
wrapper component? stream pipes in new props each time?

Another react4j integration is to add a "<Stream/>" component that takes a stream as a prop and has a render prop
that has output of stream as parameter - https://github.com/johnlindquist/react-streams/

Another possibility is to create field for subscription and let contain subscribe and dispose - template
language extracts it - ala angular

`Yolk` is an interesting framework. Essentially it is react-like in that it uses jsx and vdom/reconcilliation
but it differs as both props and event handlers are streams and streams are passed as jsx.

### Schedulers

* Immediate - directly invoke now
* Current - queue in current Execution and will invoke before exiting runtime and returning to invoking application/system code
* Mircotask - in next microtask execution. == Current if current execution in microtask
* Animation - in next animation frame
* Idle - in next idle frame
