//package Service;
//
//import java.util.concurrent.atomic.AtomicBoolean;
//import javax.ejb.Lock;
//import javax.ejb.LockType;
//import javax.ejb.Singleton;
//
//@Singleton
//public class WorkerBean {
//
//    private AtomicBoolean busy = new AtomicBoolean(false);
//
//    @Lock(LockType.READ)
//    public void doTimerWork() throws InterruptedException {
//        if (!busy.compareAndSet(false, true)) {
//            return;
//        }
//        try {
//            Thread.sleep(20000L);
//        } finally {
//            busy.set(false);
//        }
//    }
//}