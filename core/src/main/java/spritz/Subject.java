package spritz;

public interface Subject<T>
  extends EventEmitter<T>, Publisher<T>
{
}
