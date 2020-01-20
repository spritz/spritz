# Change Log

### Unreleased

* Upgrade the `com.google.guava` artifact to version `27.1-jre`.
* Upgrade the `com.google.truth` artifact to version `0.44`.
* Upgrade the `com.google.testing.compile` artifact to version `0.18`.
* Upgrade the `com.squareup` artifact to version `1.12.0`.
* Upgrade the `org.realityforge.gir` artifact to version `0.11`.
* Upgrade the `org.realityforge.gwt.symbolmap` artifact to version `0.09`.
* Upgrade the `org.realityforge.javax.annotation` artifact to version `1.0.1`.
* Upgrade the `org.realityforge.com.google.elemental2` artifacts to version `2.27`.
* Upgrade the `au.com.stocksoftware.idea.codestyle` artifact to version `1.14`.
* Upgrade the `org.realityforge.braincheck` artifact to version `1.25.0`.
* Add `JDepend` tests that enforce dependencies between packages.
* Use capabilities in `braincheck` to start recording and verifying all invariant failures in `diagnostic_messages.json`.
* Remove the `jul` (a.k.a. `java.util.logging`) strategy available when configuring the `SpritzLogger` via the compile-time property `spritz.logger`. This strategy was never used in practice.
* Rework the way `SpritzLogger` is implemented to consolidate the JRE and javascript based console loggers into the class `ConsoleLogger`. The involved renaming the `console_js` value to `console` for the compile-time property `spritz.logger`.
* Stop recording source location where diagnostic messages are generated.

### [v0.09](https://github.com/spritz/spritz/tree/v0.09) (2019-06-22) · [Full Changelog](https://github.com/spritz/spritz/compare/v0.08...v0.09)

* Upgrade the `org.realityforge.gir` artifact to version `0.10`.
* Upgrade the `org.realityforge.revapi.diff` artifact to version `0.08`.
* Upgrade the `org.realityforge.com.google.elemental2` artifacts to version `2.24`.
* Remove `{@inheritDoc}` as it only explicitly indicates that the default behaviour at the expense of significant visual clutter.

### [v0.08](https://github.com/spritz/spritz/tree/v0.08) (2019-04-16) · [Full Changelog](https://github.com/spritz/spritz/compare/v0.07...v0.08)

* Upgrade the `org.realityforge.braincheck` artifact to version `1.16.0`.

### [v0.07](https://github.com/spritz/spritz/tree/v0.07) (2019-04-16) · [Full Changelog](https://github.com/spritz/spritz/compare/v0.06...v0.07)

* Upgrade the `org.realityforge.com.google.elemental2` artifacts to version `1.0.0-b21-6a027d2`.
* Upgrade the `org.realityforge.braincheck` artifact to version `1.15.0`.
* Upgrade the `org.realityforge.gir` artifact to version `0.08`.
* **\[core\]** Add `SpritzConfig.native.js` so that spritz will pick up compile time constants when compiled
  using J2CL.
* Improve the way defines are handled in closure by assigning the results of `goog.define` to a module local variable.

### [v0.06](https://github.com/spritz/spritz/tree/v0.06) (2019-03-18) · [Full Changelog](https://github.com/spritz/spritz/compare/v0.05...v0.06)

* Upgrade the `org.realityforge.com.google.elemental2` artifacts to version `1.0.0-b20-bfe6e22`.

### [v0.05](https://github.com/spritz/spritz/tree/v0.05) (2019-03-13) · [Full Changelog](https://github.com/spritz/spritz/compare/v0.04...v0.05)

* Ensure that the Spritz TodoMVC is building as part fo build and release process.
* Remove the requirement that all `Subscriber` methods, `Stream.subscribe(...)` and `Subscription.cancel()`
  method be invoked within the context of a VPU. This adds some unnecessary overhead and code complexity with
  no benefit when run on the target platform (i.e. Javascript).
* Expose the `VirtualProcessorUnit.queue(Runnable)` method to enable queuing tasks directly on the VPU.
* Refactor the `Stream.subscribe(...)` and `Stream.forEach(...)` methods to return the `Subscription`. This
  makes it easy for the component subscribing to the `Stream`, to cancel the subscription if/when the component
  no longer needs the stream to execute.

### [v0.04](https://github.com/spritz/spritz/tree/v0.04) (2019-03-12) · [Full Changelog](https://github.com/spritz/spritz/compare/v0.03...v0.04)

* Convert `Subscription` from an interface to a class and move common behaviour out of `AbstractSubscription`
  and into new `Subscription` class.
* Introduce the concept of a `Hub` that is both an `EventEmitter` and a `Stream` except the types
  of items that the stream emits and the types of items put into `EventEmitter` need not be identical.
  Refactor `Subject` to extend `Hub` with the additional restriction that the types align.
* Expose `ConnectableStream.isConnected()` helper method.
* Expose `EventEmitter.isNotDone()` helper method.
* Expose `Scheduler.isVirtualProcessorUnitActivated()` helper method.
* Add `Stream.subscribe(EventEmitter)` that routes events from a stream to an `EventEmitter`.
* Add `Stream.webSocket(...)` method that creates a `Hub` that is responsible for managing bidrectional
  communication over a `WebSocket`.
* If `Subscription.cancel()` is invoked when a VPU is not active then activate VPU.

### [v0.03](https://github.com/spritz/spritz/tree/v0.03) (2019-03-09) · [Full Changelog](https://github.com/spritz/spritz/compare/v0.02...v0.03)

* Remove the `spritz.internal.*` packages and move all classes into the `spritz` package with reduced access
  levels as appropriate.
* Add tracking of API changes for the `core` artifact so that breaking changes only occur when
  explicitly acknowledged. API changes are tracked in reports generated per-release in the
  `api-test/src/test/resources/fixtures` directory.
* Initial implementation of Subjects. These are a combination of an `EventEmitter` and a `Stream` that can
  be used to share a stream execution between multiple subscribers. The simple subject as well as a `replaySubject`
  and a `currentValueSubject` (a.k.a. Behavior Subject) have been implemented.
* Add a `ConnectableStream` that can be used to trigger a `Subject` to subscribe to and/or unsubscribe
  from upstream stages.
* Ensure that all methods on a Stream are invoked within the context of a VPU.
* Add a `DirectExecutor` that invokes a task in the context of the caller.
* Add basic javadocs to `EventEmitter` to reflect expected behaviour.

### [v0.02](https://github.com/spritz/spritz/tree/v0.02) (2019-03-01) · [Full Changelog](https://github.com/spritz/spritz/compare/v0.01...v0.02)

* Upgrade the `org.realityforge.braincheck` artifact to version `1.13.0`.
* Upgrade the `org.realityforge.com.google.jsinterop` artifact to version `1.0.0-b2-e6d791f`.
* Upgrade the `org.realityforge.com.google.elemental2` artifacts to version `1.0.0-b19-fb227e3`.

### [v0.01](https://github.com/spritz/spritz/tree/v0.01) (2019-17-01) · [Full Changelog](https://github.com/spritz/spritz/compare/f59605d9ede6d537d7b7d6286b2f5e34c6d246f8...v0.01)

 ‎🎉	Initial release ‎🎉
