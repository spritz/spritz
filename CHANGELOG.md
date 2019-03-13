# Change Log

### Unreleased

### [v0.04](https://github.com/spritz/spritz/tree/v0.04) (2019-03-12)
[Full Changelog](https://github.com/spritz/spritz/compare/v0.03...v0.04)

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

### [v0.03](https://github.com/spritz/spritz/tree/v0.03) (2019-03-09)
[Full Changelog](https://github.com/spritz/spritz/compare/v0.02...v0.03)

### [v0.02](https://github.com/spritz/spritz/tree/v0.02) (2019-03-01)
[Full Changelog](https://github.com/spritz/spritz/compare/v0.01...v0.02)

* Upgrade the `org.realityforge.braincheck` artifact to version `1.13.0`.
* Upgrade the `org.realityforge.com.google.jsinterop` artifact to version `1.0.0-b2-e6d791f`.
* Upgrade the `org.realityforge.com.google.elemental2` artifacts to version `1.0.0-b19-fb227e3`.

### [v0.01](https://github.com/spritz/spritz/tree/v0.01) (2019-17-01)
[Full Changelog](https://github.com/spritz/spritz/compare/f59605d9ede6d537d7b7d6286b2f5e34c6d246f8...v0.01)

 ‎🎉	Initial release ‎🎉
