package fr.vga.virtual;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

public class VirtualProxyFailoverProducer {


    private static final Logger LOGGER = LoggerFactory.getLogger(VirtualProxyFailoverProducer.class);

    private String clientId;
    private Connection connection;
    private Session session;
    private MessageProducer messageProducer;

    public void create(String clientId, String topicName) throws JMSException {
        this.clientId = clientId;

        // create a Connection Factory
        ConnectionFactory connectionFactory =
                new ActiveMQConnectionFactory("failover:(tcp://localhost:61618)?initialReconnectDelay=10");

        // create a Connection
        connection = connectionFactory.createConnection();
        connection.setClientID(clientId);

        // create a Session
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // create the topic to which messages will be sent
//        Topic topic = session.createTopic(topicName);

        // create a MessageProducer for sending messages
        messageProducer = session.createProducer(new ActiveMQTopic(topicName));
    }

    public void closeConnection() throws JMSException {
        connection.close();
    }

    public void sendName(String text) throws JMSException {

        // create a JMS TextMessage
        TextMessage textMessage = session.createTextMessage(text);

        // send the message to the topic destination
        messageProducer.send(textMessage);

        LOGGER.debug(clientId + ": sent message with text='{}'", text);
    }


    public static void main(String[] args) throws JMSException, InterruptedException {
        VirtualProxyFailoverProducer producer = new VirtualProxyFailoverProducer();

        producer.create("vga-producer", "VirtualTopic.TEST");
        int i = 0;
        while (true) {
            producer.sendName("Message " + i);
            i++;
            Thread.sleep(5000);
        }
    }
}
