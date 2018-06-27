package fr.vga.direct.failover;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

public class DirectFailoverProducer {


    private static final Logger LOGGER = LoggerFactory.getLogger(DirectFailoverProducer.class);

    private String clientId;
    private Connection connection;
    private Session session;
    private MessageProducer messageProducer;

    public void create(String clientId, String topicName) throws JMSException {
        this.clientId = clientId;

        // create a Connection Factory
        ConnectionFactory connectionFactory =
                new ActiveMQConnectionFactory("failover:(tcp://localhost:61617,tcp://localhost:61616)?initialReconnectDelay=10");

        // create a Connection
        connection = connectionFactory.createConnection();
        connection.setClientID(clientId);

        // create a Session
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // create the topic to which messages will be sent
        Topic topic = session.createTopic(topicName);

        // create a MessageProducer for sending messages
        messageProducer = session.createProducer(topic);
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
        DirectFailoverProducer producer = new DirectFailoverProducer();

        producer.create("vga-producer", "topic.foo.vga");
        int i = 0;
        while (true) {
            producer.sendName("Message " + i);
            i++;
            Thread.sleep(5000);
        }
    }
}
