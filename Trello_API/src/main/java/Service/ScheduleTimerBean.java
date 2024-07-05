//package Service;
//
//import java.awt.desktop.AppEvent;
//import java.util.Timer;
//
//import javax.ejb.Schedule;
//import javax.ejb.Startup;
//import javax.enterprise.event.Event;
//import javax.inject.Inject;
//import javax.inject.Singleton;
//
//
//@Startup
//@Singleton
//public class ScheduleTimerBean {
//
//    @Inject
//    Event<AppEvent> event;
//
//    @Schedule(hour = "*", minute = "*", second = "*/5", info = "Every 5 seconds timer")
//    public void automaticallyScheduled(Timer timer) {
//        fireEvent(timer);
//    }
//
//
//    private void fireEvent(String info ) {
//        event.fire(new AppEvent());
//    }
//}