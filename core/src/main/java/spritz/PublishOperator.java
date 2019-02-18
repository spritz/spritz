package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;

final class PublishOperator<T>
  extends AbstractStream<T>
{
  @Nonnull
  private final Subject<T> _subject;

  PublishOperator( @Nonnull final Publisher<T> upstream, @Nonnull final Subject<T> subject )
  {
    super( upstream );
    _subject = Objects.requireNonNull( subject );
  }

  @Override
  protected void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    _subject.subscribe( new PassThroughSubscription<>( subscriber ) );
  }

  private static final class WorkerSubscription<T>
    implements Subscriber<T>
  {
    //TODO: Make EventEmitter?
    @Nonnull
    private final Subject<T> _subject;

    WorkerSubscription( @Nonnull final Subject<T> subject )
    {
      _subject = subject;
    }

    @Override
    public void onSubscribe( @Nonnull final Subscription subscription )
    {
    }

    @Override
    public void onNext( @Nonnull final T item )
    {
      _subject.next( item );
    }

    @Override
    public void onError( @Nonnull final Throwable error )
    {
      _subject.error( error );
    }

    @Override
    public void onComplete()
    {
      _subject.complete();
    }
  }
}
