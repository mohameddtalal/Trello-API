//package Service;
//
//import javax.ejb.EJB;
//import javax.ejb.Lock;
//import javax.ejb.LockType;
//import javax.ejb.Schedule;
//import javax.ejb.Singleton;
//
//@Singleton
//public class FixedTimerBean {
//
//    @EJB
//    private WorkerBean workerBean;
//
//    @Lock(LockType.READ)
//    @Schedule(second = "*/5", minute = "*", hour = "*", persistent = false)
//    public void atSchedule() throws InterruptedException {
//        workerBean.doTimerWork();
//    }
//}
