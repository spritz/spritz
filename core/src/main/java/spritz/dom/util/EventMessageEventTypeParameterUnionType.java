package spritz.dom.util;

import elemental2.core.ArrayBuffer;
import elemental2.dom.Blob;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

// TODO: Remove with next version of Elemental2
@SuppressWarnings( { "RedundantCast", "ConstantConditions" } )
@JsType( isNative = true, name = "?", namespace = JsPackage.GLOBAL )
public interface EventMessageEventTypeParameterUnionType
{
  @JsOverlay
  static EventMessageEventTypeParameterUnionType of( Object o )
  {
    return Js.cast( o );
  }

  @JsOverlay
  default ArrayBuffer asArrayBuffer()
  {
    return Js.cast( this );
  }

  @JsOverlay
  default Blob asBlob()
  {
    return Js.cast( this );
  }

  @JsOverlay
  default String asString()
  {
    return Js.asString( this );
  }

  @JsOverlay
  default boolean isArrayBuffer()
  {
    return (Object) this instanceof ArrayBuffer;
  }

  @JsOverlay
  default boolean isBlob()
  {
    return (Object) this instanceof Blob;
  }

  @JsOverlay
  default boolean isString()
  {
    return (Object) this instanceof String;
  }
}
