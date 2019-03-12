package spritz.dom;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class WebSocketConfig
{
  @Nonnull
  private final String _url;
  @Nullable
  private final String[] _protocols;
  @Nullable
  private final String _binaryType;

  public WebSocketConfig( @Nonnull final String url )
  {
    this( url, null, null );
  }

  public WebSocketConfig( @Nonnull final String url,
                          @Nullable final String[] protocols,
                          @Nullable final String binaryType )
  {
    _url = Objects.requireNonNull( url );
    _protocols = protocols;
    _binaryType = binaryType;
  }

  @Nonnull
  public String getUrl()
  {
    return _url;
  }

  @Nullable
  public String[] getProtocols()
  {
    return _protocols;
  }

  @Nullable
  public String getBinaryType()
  {
    return _binaryType;
  }
}
