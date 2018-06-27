package fr.vga.virtual;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

public class VirtualProxyFailoverConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(VirtualProxyFailoverConsumer.class);

    private String clientId;
    private Connection connection;
    private MessageConsumer messageConsumer;

    public void create(String clientId, String topicName) throws JMSException {
        this.clientId = clientId;

        // create a Connection Factory
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("failover:(tcp://localhost:61618)?initialReconnectDelay=10");

        // create a Connection
        connection = connectionFactory.createConnection();
        connection.setClientID(clientId);

        // create a Session
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        Queue queueA = session.createQueue("Consumer." + clientId + "." + topicName);
        messageConsumer = session.createConsumer(queueA);

        // start the connection in order to receive messages
        connection.start();
    }

    public void closeConnection() throws JMSException {
        connection.close();
    }

    public String getMessage(int timeout, boolean acknowledge) throws JMSException {

        // read a message from the topic destination
        Message message = messageConsumer.receive(timeout);

        // check if a message was received
        if (message != null) {
            // cast the message to the correct type
            TextMessage textMessage = (TextMessage) message;

            // retrieve the message content
            String text = textMessage.getText();
            LOGGER.debug(clientId + ": received message with text='{}'", text);

            if (acknowledge) {
                // acknowledge the successful processing of the message
                message.acknowledge();
                LOGGER.debug(clientId + ": message acknowledged");
            } else {
                LOGGER.debug(clientId + ": message not acknowledged");
            }
            return text;
        } else {
            LOGGER.debug(clientId + ": no message received");
        }
        return "";
    }

    public static void main(String[] args) throws JMSException, InterruptedException {
        VirtualProxyFailoverConsumer consumer = new VirtualProxyFailoverConsumer();

        consumer.create("virtual-consumer", "VirtualTopic.TEST");
        while(true) {
            Thread.sleep(5000);
            consumer.getMessage(10, true);
        }
    }
}
