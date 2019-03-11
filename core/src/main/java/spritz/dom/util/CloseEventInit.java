package spritz.dom.util;

import elemental2.dom.EventInit;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

//TODO: Remove this one it is present in Elemental2
@JsType( isNative = true, namespace = JsPackage.GLOBAL )
public interface CloseEventInit
  extends EventInit
{
  @JsOverlay
  static CloseEventInit create()
  {
    return Js.uncheckedCast( JsPropertyMap.of() );
  }

  @JsProperty
  int getCode();

  @JsProperty
  String getReason();

  @JsProperty
  boolean isWasClean();

  @JsProperty
  void setCode( int code );

  @JsProperty
  void setReason( String reason );

  @JsProperty
  void setWasClean( boolean wasClean );
}
