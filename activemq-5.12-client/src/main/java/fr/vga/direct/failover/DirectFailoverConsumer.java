package fr.vga.direct.failover;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.io.*;
import java.util.zip.GZIPInputStream;

public class DirectFailoverConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirectFailoverConsumer.class);

    private String clientId;
    private Connection connection;
    private MessageConsumer messageConsumer;

    public void create(String clientId, String topicName) throws JMSException, IOException {
        this.clientId = clientId;


        // create a Connection Factory
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("failover:(tcp://localhost:61616,tcp://localhost:61617)?initialReconnectDelay=10");

        // create a Connection
        connection = connectionFactory.createConnection();
        connection.setClientID(clientId);

        // create a Session
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        // create the topic from which messages will be received
        Topic topic = session.createTopic(topicName);

        // create a MessageConsumer for receiving messages
        messageConsumer = session.createDurableSubscriber(topic, clientId + "-consumer");

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

    public static void main(String[] args) throws JMSException, InterruptedException, IOException {
        DirectFailoverConsumer consumer = new DirectFailoverConsumer();

        consumer.create("vga-consumer", "topic.foo.vga");
        while(true) {
            Thread.sleep(5000);
            consumer.getMessage(10, true);
        }
    }
}
