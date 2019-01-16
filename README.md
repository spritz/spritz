# streak

[![Build Status](https://secure.travis-ci.org/realityforge-experiments/streak.png?branch=master)](http://travis-ci.org/realityforge-experiments/streak)
[<img src="https://img.shields.io/maven-central/v/org.realityforge.streak/streak.svg?label=latest%20release"/>](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.realityforge.streak%22%20a%3A%22streak%22)

Streak is a browser based, reactive event streaming library that is best used when coordinating events.
For more information about Streak, please see the [Website](https://realityforge.github.io/streak). For
the source code and project support, please visit the [GitHub project](https://github.com/realityforge/streak).

# Contributing

Streak was released as open source so others could benefit from the project. We are thankful for any
contributions from the community. A [Code of Conduct](CODE_OF_CONDUCT.md) has been put in place and
a [Contributing](CONTRIBUTING.md) document is under development.

# License

Streak is licensed under [Apache License, Version 2.0](LICENSE).

# Credit

* The toolkit began life as an attempt to build an implementation of the
  [Microprofile Reactive Streams](https://github.com/eclipse/microprofile-reactive-streams) API as a learning
  exercise with the ultimate aim of transitioning to using [RxJava](https://github.com/ReactiveX/RxJava) in the
  browser via [rxjava-gwt](https://github.com/intendia-oss/rxjava-gwt). I found that [RxJs](https://rxjs-dev.firebaseapp.com/api)
  seemed better suited for the browser context and the toolkit tended to gravitate more to the [RxJs](https://rxjs-dev.firebaseapp.com/api)
  way of doing things. Neither toolkit was easy to optimize to reduce code size and thus Streak was born.
  Streak continues to steal, recombine, remix and mashup the best ideas from the various reactive event streaming
  libraries.

* Other frameworks that influenced or are influencing the development of Streak include;
  - [ReactiveX](http://reactivex.io)
  - [Most](https://mostcore.readthedocs.io/en/latest/concepts.html)
  - [Bacon](http://baconjs.github.io/api2.html)
  - [Reactor](https://projectreactor.io/)
  - [Callbag](https://github.com/callbag/callbag)
