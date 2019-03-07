package spritz;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class Subjects
{
  private Subjects()
  {
  }

  @Nonnull
  public static <T> EventEmitter<T> create()
  {
    return create( null );
  }

  @Nonnull
  public static <T> EventEmitter<T> create( @Nullable final String name )
  {
    return new Subject<>( name );
  }

  @Nonnull
  public static <T> EventEmitter<T> createCurrentValue( @Nonnull final T initialValue )
  {
    return createCurrentValue( null, initialValue );
  }

  @Nonnull
  public static <T> EventEmitter<T> createCurrentValue( @Nullable final String name, @Nonnull final T initialValue )
  {
    return new CurrentValueSubject<>( name, initialValue );
  }

  @Nonnull
  public static <T> EventEmitter<T> createReplayWithMaxAge( final int maxAge )
  {
    return createReplayWithMaxAge( null, maxAge );
  }

  @Nonnull
  public static <T> EventEmitter<T> createReplayWithMaxAge( @Nullable final String name, final int maxAge )
  {
    return createReplay( name, ReplaySubject.DEFAULT_VALUE, maxAge );
  }

  @Nonnull
  public static <T> EventEmitter<T> createReplayWithMaxSize( final int maxSize )
  {
    return createReplayWithMaxSize( null, maxSize );
  }

  @Nonnull
  public static <T> EventEmitter<T> createReplayWithMaxSize( @Nullable final String name, final int maxSize )
  {
    return createReplay( name, maxSize, ReplaySubject.DEFAULT_VALUE );
  }

  @Nonnull
  public static <T> EventEmitter<T> createReplay( final int maxSize, final int maxAge )
  {
    return createReplay( null, maxSize, maxAge );
  }

  @Nonnull
  public static <T> EventEmitter<T> createReplay( @Nullable final String name, final int maxSize, final int maxAge )
  {
    return new ReplaySubject<>( name, maxSize, maxAge );
  }

  @Nonnull
  public static <T> EventEmitter<T> createReplay()
  {
    return createReplay( null );
  }

  @Nonnull
  public static <T> EventEmitter<T> createReplay( @Nullable final String name )
  {
    return createReplay( name, ReplaySubject.DEFAULT_VALUE, ReplaySubject.DEFAULT_VALUE );
  }

/*
- [ ] Create `Subject.createReplay()`

*/

}
