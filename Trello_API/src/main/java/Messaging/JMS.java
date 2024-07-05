package Messaging;

import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Singleton
@Startup
public class JMS {
    @Resource(mappedName = "java:jboss/exported/jms/queue/MyTrelloQueue")
    private Queue queue;

    @Inject
    private JMSContext Context;

    public Queue getQueue() {
		return queue;
	}

	public void setQueue(Queue queue) {
		this.queue = queue;
	}

	public JMSContext getContext() {
		return Context;
	}

	public void setContext(JMSContext context) {
		Context = context;
	}

	public void send(String message) {
        JMSProducer producer = Context.createProducer();
        producer.send(queue, message);
        System.out.println("Message sent: " + message);
    }

    public String receive() {
        JMSConsumer consumer = Context.createConsumer(queue);
        String message = consumer.receiveBody(String.class);
        System.out.println("Message received: " + message);
        return message;
    }
    
    
	
}