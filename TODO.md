## TODO

This document is essentially a list of shorthand notes describing work yet to completed.
Unfortunately it is not complete enough for other people to pick work off the list and
complete as there is too much un-said.

### High Priority Tasks

* Reorganize project into several sub-modules similar to Arez/React4j.
  - Add `spritz/elemental2` with Elemental2 dependencies and Elemental2 specific sources and sinks.
    Ensure that they are included in javadocs.

* Add some operators that queue tasks on `VirtualProcessorUnit`. These tasks include:
  - [ ] `subscribeOn(Scheduler)` - perform subscribe on different scheduler
  - [ ] `cancelOn(Scheduler)` - perform cancel on different scheduler
  - [ ] `observeOn(Scheduler)` - if signal or item emitted and current scheduler is not specified scheduler or if the specified scheduler has a non-zero queue then enqueue item/signal, schedule scheduler to run next tick if not scheduled.

* Complete `VirtualProcessorUnit` implementation so that it supports more than ASP.

* Enhance `Scheduler` so that it can queue tasks to different VPUs when timeouts occur. This will also
  suspending and/or resuming VPUs based on activations from tasks.

* Build different VPUs in Elemental2 module such as:
  - Primary (a.k.a `setTimeout(mycallback,0)` on web)
  - Microtask - via promise microtask
  - Idle `requestIdleCallback( mycallback )`
  - Animation `requestAnimationFrame( mycallback )`
  - AfterFrame (i.e. `requestAnimationFrame( () -> setTimeout( mycallback, 0 ))`. See Arez TODO.

  Each VPU has a task queue and a strategy for selecting items off queue each activation. i.e. TaskQueue can
  be prioritized or not. Activation can drain queue or tun till deadline.

* Add annotation and enhance processor so that schedulers are included in the documentation.

* Build a testing VPU/Scheduler based on ideas in [reactor-by-example](https://www.infoq.com/articles/reactor-by-example) article.

### Other Tasks

* Ensure tests all verify that predicates can accept supertypes of T

* Ensure that after every error, complete or cancel there are no timers left associated with stream.
  Probably can do this for every by enqueuing streams which can be cancelled and then checking that
  there are no uncancelled timer tasks on test complete.

* Ensure that cancel after `onComplete` or `onError` is a noop.

* Ensure tests verify that every stream source supports being disposed in `onNext(item)` invocation.

* Every callable/supplier/predicate/runnable/function passed to an operator should be tested to ensure
  errors are caught and emitted as error signals.

* Ensure tests all verify that take control streams gracefully handle completion and error signals.

* Ensure tests verify scenario where stream has a `takeWhile( v -> false )` to make sure we correctly
  separate out onSubscribe from initial delivery of elements.

* Stream should mark whether they can ever emit errors or completions. This could be done statically at
  compile time, dynamically at runtime (in development mode), or dynamically at runtime using spies or ...
  This would allow us to detect poorly constructed stream like adding onError on streams that produce no
  errors or reduce on a stream that never completes.

* Each operator should supply N different marble diagram inputs. These inputs are picked up and used to
  test the operators. Some diagrams are also tagged so that a documentation tool runs over them and produces
  diagrams that can be included in javadocs and other documentation. Develop a custom javadoc plugin that
  includes marble diagrams in the generated javadocs based on the same tags. Each diagram can potentially
  have a label and a description as well as annotations inside the diagram. An even better regime would be
  to allow example code for each example. The example code is checked to ensure it matches the expected
  marble diagram and can be formatted and places in the documentation.

* Use consistent terminology throughout the javadocs and code.
  - Use `item` or `element` to refer to the data elements that are passed downstream. At the moment the
    code uses both terms but one should be selected and the other purged. Consider renaming `onNext` to
    `onItem` or `onElement` to align with the selected terminology.
  - Use `emit` rather than `notify` or `send` to describe passing items or signals downstream.
  - Use the term `signal` rather than `notification` or `event` or `message` when referring to invoking
    the `onError` or `onComplete` methods in downstream. It is unclear if there is a consistent term for
    passing the data element downstream.
  - downstream and upstream seem reasonably useful descriptions defining which direction data is flowing.
    However we don't have a good way to describe distinguish between each step or stage and the complete
    stream definition nor the stream executable data (which is really a chain of subscriptions). `Stage`
    seems like a useful term but the codebase has no consistent terminology at this point.

* Scheduler should use a linked list to contain tasks or some other similar mechanism as there is likely
  to be many tasks that are cancelled in flight and need to have resources releases to avoid being unnecessary
  memory pressure.

Below are the old TODO notes:

---

## Links

* [ReactiveX Scheduler](http://reactivex.io/documentation/scheduler.html)
* [Callbag Operators](https://github.com/callbag/callbag/wiki)

## To Implement

**Elemental2 Sources**

- [ ] `fromEvent(EventSource)` - Create events from DOM/JS event source.
- [ ] `fromPromise(Promise)` - This conversion is also built into several other higher-order operations (i.e. `switchMap()` so that if you map to to promise it will convert to promise).
- [ ] `fetch()` - Fetch/http controller that must make use of `AbortController`.
- [ ] `webSocket()` - WebSocket source.

**Elemental2 Sinks**

- [ ] `toPromise()` - Extract the first element from stream and return it or just return null on completion, error on error.

**Arez Sinks**

- [ ] `toComputableValue()` - Create an `arez.ComputableValue` instance from stream. It is unclear whether it should re-subscribe or dispose on error or completion signals. Otherwise it just provides the computable value.

**Sources**

- [ ] `at(Time,Item)` == `periodic(Time).first().mapTo(Item)` - Create a Stream containing a single event at a specific time.

**Filtering Operators**

- [ ] `skipRepeats()` == `dropConsecutiveDuplicates()`
- [ ] `skipAfter()` == `takeUntil()`
- [ ] `skipWhile()` == `dropUntil()`

- [ ] `skipLast(Count)` or `dropLast(Count)` - Drops the last `Count` items emitted by stream, and emits the remaining items.
- [ ] `singleOrError()` - Emits the only item emitted by the stream, or signals a error if the stream completes after emitting 0 or more than 1 items.
- [ ] `singleOrDefault(DefaultValue)` - Emits the only item emitted by the stream, or emits `DefaultValue` if the stream completes after emitting 0 or more than 1 items.

- [ ] `reduce((accumulator, item) => {...function...}, initialValue) == scan((accumulator, item) => {...function...}, initialValue).lastOrDefault(initialValue)` - Similar to scan except only final value is emitted.

- [ ] `FilterByControlStreams(OnControlStream, OffControlStream)` - allow elements to pass after `OnControlStream` emits an element but before `OffControlStream` emits an element. Complete if both streams complete. Error if either completes.
- [ ] `TakeUntil(ControlStream)` a.k.a. `FilterByControlStreams(ControlStream.mapTo(true).startWith(true).take(2), Spritz.empty())` - take until `ControlStream` emits an element or completes.
- [ ] `SkipUntil(ControlStream)` a.k.a. `FilterByControlStreams(Spritz.empty(),ControlStream.first())` - skip until `ControlStream` emits an element or completes.

**Combination Operators** (Take 2 or more streams and combine) (a.k.a vertical merging operations as it combines values across streams)

- [ ] `combineLatest(Streams)` == `combine(Streams)` - for each stream grab latest value and pass through a function and pass on result of function this happens anytime an item is received on any stream. onComplete if all onComplete, onError if any onError
- [ ] `withLatestFrom(MasterStream,Streams)` a.k.a. `snapshot(MasterStream,Streams)` - for a primary stream, any time an item appears combine it with latest from other streams using function to produce new item. onComplete if all onComplete, onError if any onError
      a.k.a. For each event in a sampler Stream, apply a function to combine its value with the most recent event value in another Stream. The resulting Stream will contain the same number of events as the sampler Stream.
- [ ] `zip(Streams)` - select N-th value of each stream and combine them using a function. onComplete if all onComplete, onError if any onError
- [ ] `firstEmitting(Stream...)` or `selectFirstEmitting(Stream...)` or `race(Stream...)` - wait for first publisher to emit a value, select publisher and then cancel other publishers and pass on signals from selected publisher
- [ ] `withItems(Array[])` - Replace each event value with the array item at the respective index. Cancel or loop if reach end of array

**Accumulating Operators** (Takes 1 or more values from a single streams and combine) (a.k.a horizontal merging operations as it combines values within streams)

- [ ] `bufferByCount` - wait for Count items and then emit them as an array. onComplete send may remaining?
- [ ] `bufferByTime` - wait for time buffering items.
- [ ] `bufferByPredicate` - use predicate to determine when to emit - predicate passed each item.
- [ ] `bufferBySignal` - Another stream signals when to open and/or close buffering operation.
- [ ] `toList()` - Return all the values in stream as a list.

- [ ] `loop()` - Accumulate results using a feedback loop that emits one value and feeds back another to be used in the next iteration. It allows you to maintain and update a “state” (a.k.a. feedback, a.k.a. seed for the next iteration) while emitting a different value. In contrast, scan feeds back and produces the same value.

- [ ] `sampleBy(ControlStream)` - When `ControlStream` emits then produce a sample.

**HigherOrder Operators**

All the windowing functions take an input stream that they cut up into segments where each segment is a new stream.

- [ ] `window(ControlStream)` - Create an inner stream each time next occurs on control stream and forward all `onNext` calls onto inner stream.
- [ ] `windowByTime(WindowTime)` - Create a new inner stream every `WindowTime` time.
- [ ] `windowByCount(Count)` - Create a new inner stream every `Count` items.
- [ ] `window(OnControlStream,OffControlStream)` - Create a new inner stream every starting on signal from `OnControlStream` and then ending when signal occurs on `OffControlStream`.

Other

- [ ] `groupBy(GroupByFunction)` - Create an inner stream based on group returned by function. Stream can be concurrent.

-----

- [ ] `delayBy(DelayFunction) where DelayTime DelayFunction(Item)` - delay each item by variable time returned by delay function. This involves buffering them for a time and may result in reordered messages.
- [ ] `delay(DelayTime) == delayBy(_ -> DelayTime) ` - delay each item by DelayTime. This involves buffering them by a fixed time.
- [ ] `delaySubscriptionBy(DelayFunction) where DelayTime DelayFunction(Subscription)` - delay subscription of upstream by variable time returned by delay function.
- [ ] `delaySubscription(DelayTime) == delaySubscriptionBy(_ -> DelayTime)` - delay subscription of upstream by DelayTime
- [ ] `sort()` - buffer all items until onComplete then apply some sorting

**Control Flow**

- [ ] `catch((error, downstream) -> myFunction)` - If `onError` then call callback. Often this will replace onError signal with onComplete or do something like omit error and re-subscribe
- [ ] `retry(RetryCount)` - The retry operator is an `onError` operator that simply resubscribes on error and starts again and it will do it `RetryCount` times or infinite times if not specified. If ceases to retry then pass onError signal down.
- [ ] `retryWhen(RetryCount, RetryWhenFunction)` - Like retry but a function indicates when should start retry.
- [ ] `retryWithExponentialBackoff(RetryCount)` - A call to `retryWhen` with some simple parameters .
- [ ] `repeat(RepeatCount)` - Replace `onComplete` with subscription to stream again a `RepeatCount` number of times.
- [ ] `recoverWith(StreamFromErrorFn)` - Recover from a stream failure by calling a function to create a new Stream.

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

### Documentation

Steal some documentation terminology for timeline notation from https://mostcore.readthedocs.io/en/latest/notation.html#timeline-notation

Steal some general documentation concepts from https://mostcore.readthedocs.io/en/latest/concepts.html

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

### Sample Applications

Once done build a few sample apps.

* [TodoMVC](https://github.com/briancavalier/mostcore-todomvc)
* [rxsnake](https://github.com/ibaca/rxsnake-gwt) (Implementation described on [blog](http://philipnilsson.github.io/badness/)).
  Maybe style like in [react-game-snake](https://github.com/avin/react-game-snake). Could also look to
  [react-snake](https://github.com/taming-the-state-in-react/react-snake)
