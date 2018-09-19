# rxs

[![Build Status](https://secure.travis-ci.org/realityforge/rxs.png?branch=master)](http://travis-ci.org/realityforge/rxs)
[<img src="https://img.shields.io/maven-central/v/org.realityforge.rxs/rxs.svg?label=latest%20release"/>](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.realityforge.rxs%22%20a%3A%22rxs%22)

Rxs contains some experiments with reactive streaming code.


## TODO

Must have publishers:
* `Interval(Duration)` - Every N time frame produce a value (monotonically increasing counter?)

Must have processors:

**Filtering Processors** (Remove items from stream)

* `Filter(Predicate)` - only pass next value if predicate returns true
* `Take(Count)` - take first "count" items (then unsubscribe from source?)
* `First` == `Take(1)`
* `Skip(Count)` - filter out the first "count" items
* `SkipUntil(Predicate)` - filter out all items until predicate returns true the first time.
* `TaskLast(Count)` -- take last "count" items. i.e. Wait to onComplete and send last Count items. Needs a buffer `Count` long.
* `Last` == `TaskLast(1)`

**Combination Processors** (Take 2 or more streams and combine)

* `append(Publishers)` - for each publisher wait till it produces onComplete, elide that signal and then
                       subscribe to next. (a.k.a `concat`)
*` prepend(Publishers)` == `append(reverse(Publishers))`
* `startWith(value)` == `prepend(of(value))`
* `merge(Publishers)` (a.k.a. `or(Publishers)`) - for each stream if it produces a value then pass on value. onComplete if all onComplete, onError if any onError
* `combineLates(Publishers)` - for each stream grab latest value and pass through a function and pass on result of function. onComplete if all onComplete, onError if any onError

-----

* Map - convert value from one type to another
* peek - perform an action on each value that goes by
