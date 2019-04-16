/**
 * This file provides the @defines for spritz configuration options.
 * See SpritzConfig.java for details.
 */
goog.provide('spritz');

/** @define {string} */
spritz.environment = goog.define('spritz.environment', 'production');

/** @define {string} */
spritz.enable_names = goog.define('spritz.enable_names', 'false');

/** @define {string} */
spritz.validate_subscriptions = goog.define('spritz.validate_subscriptions', 'false');

/** @define {string} */
spritz.purge_tasks_when_runaway_detected = goog.define('spritz.purge_tasks_when_runaway_detected', 'true');

/** @define {string} */
spritz.enable_uncaught_error_handlers = goog.define('spritz.enable_uncaught_error_handlers', 'false');

/** @define {string} */
spritz.logger = goog.define('spritz.logger', 'none');
