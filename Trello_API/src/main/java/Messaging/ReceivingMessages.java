package Messaging;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.*;
import java.io.Serializable;

@MessageDriven(name = "queue", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/myTrelloQueue"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue") })
public class ReceivingMessages implements MessageListener {
    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                System.out.println("Received message ( " + ((TextMessage) message).getText() + " )");
            } else if (message instanceof ObjectMessage) {
                Serializable object = ((ObjectMessage) message).getObject();
                System.out.println("Received message ( " + object + " )");
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
    
    

}