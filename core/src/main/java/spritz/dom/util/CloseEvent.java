package spritz.dom.util;

import elemental2.dom.Event;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

//TODO: Remove this one it is present in Elemental2
@JsType( isNative = true, namespace = JsPackage.GLOBAL )
public class CloseEvent
  extends Event
{
  public int code;
  public String reason;
  public boolean wasClean;

  public CloseEvent( String type, CloseEventInit init )
  {
    // This call is only here for java compilation purpose.
    super( type, init );
  }

  public CloseEvent( String type )
  {
    // This call is only here for java compilation purpose.
    super( type, null );
  }
}
