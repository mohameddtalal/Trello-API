//package Service;
//import path.to.TimerEvent;
//import javax.annotation.PostConstruct;
//import javax.annotation.Resource;
//import javax.ejb.Startup;
//import javax.ejb.Timeout;
//import javax.ejb.Timer;
//
//import javax.ejb.TimerService;
//import javax.enterprise.event.Event;
//import javax.inject.Inject;
//import javax.inject.Singleton;
//
//@Startup
//@Singleton
//public class ProgrammaticAtFixedRateTimerBean {
//
//    @Inject
//    Event<TimerEvent> event;
//
//    @Resource
//    TimerService timerService;
//
//    @PostConstruct
//    public void initialize() {
//        timerService.createTimer(0, 1000, "Every second timer with no delay");
//    }
//
//    @Timeout
//    public void programmaticTimeout(Timer timer) {
//        // Instead of timer.getInfo().toString(), pass some information directly
//        event.fire(new TimerEvent("Some information"));
//    }
//}