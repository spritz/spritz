package spritz.dom.util;

import elemental2.dom.Event;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsFunction;

//TODO: Remove this one it is present in Elemental2
@JsFunction
public interface OnerrorFn
{
  void onInvoke( @Nonnull final Event event );
}
