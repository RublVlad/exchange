package by.bsuir.exchange.pool.exception;

public class PoolDestructionException extends Exception {
    public PoolDestructionException(Throwable t){
        super(t);
    }
    public PoolDestructionException(String msg){ super(msg); }
}
