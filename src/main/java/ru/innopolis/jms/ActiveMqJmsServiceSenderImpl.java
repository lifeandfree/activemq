package ru.innopolis.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ActiveMqJmsServiceSenderImpl implements MessageProducerService {

    private static final Logger logger = LogManager.getLogger(ActiveMqJmsServiceSenderImpl.class.getName());

    private ConnectionFactory connectionFactory;
    private final PropertyHandler propertyHandler;
    private ConcurrentLinkedQueue<TransactionQueueRecord> transactionsQueue;

    public ActiveMqJmsServiceSenderImpl(PropertyHandler propertyHandler) {
        logger.debug("init MessageProducerService");
        this.propertyHandler = propertyHandler;
        this.connectionFactory = connectionFactory();
        this.transactionsQueue = new ConcurrentLinkedQueue<>();
    }

    private ConnectionFactory connectionFactory() {
        logger.debug("create connectionFactory");

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(propertyHandler.getPropertyByKey(Constants.JMS_ACTIVEMQ_URL));
        connectionFactory.setUserName(propertyHandler.getPropertyByKey(Constants.JMS_ACTIVEMQ_USERNAME));
        connectionFactory.setPassword(propertyHandler.getPropertyByKey(Constants.JMS_ACTIVEMQ_PASSWORD));
        connectionFactory.setTrustedPackages(Arrays.asList("ru.innopolis"));
        return connectionFactory;
    }

    @Override
    public void launchProcessing() {
        logger.info("launchProcessing..");

        Runnable processing = () -> {

            logger.info("launchProcessing: new thread for processing is launched");

            while (true) {
                while (!transactionsQueue.isEmpty()) {
                    logger.debug("send transaction");

                    sendTransaction(transactionsQueue.poll()); // TODO
                }
                try {
                    Long sleepTime = Long.parseLong(
                            propertyHandler.getPropertyByKey(Constants.MESSAGE_BROKER_SLEEP));
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    logger.error(e);
                }
            }
        };

        new Thread(processing).start();
    }

    private void sendTransaction(TransactionQueueRecord transactionQueueRecord) {

        logger.debug("sendTransaction: " + transactionQueueRecord.getMessageBrokerMessage() +
                ". To topic: " + transactionQueueRecord.getTopic());

        try {
            logger.trace("Create connection");
            Connection connection = connectionFactory.createConnection();
            logger.trace("Start connection");
            connection.start();

            logger.trace("Create session");
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination destination = session.createQueue(transactionQueueRecord.getTopic());
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            TextMessage message = session.createTextMessage(TransactionGson.instance().toJson(transactionQueueRecord.getMessageBrokerMessage()));
            logger.debug("set property " + transactionQueueRecord.getMessageBrokerMessage().getClass());
            message.setStringProperty("_type", transactionQueueRecord.getMessageBrokerMessage().getClass().toString().split(" ")[1]);

            producer.send(message);

            logger.trace("Session close");
            session.close();
            logger.trace("Connection close");
            connection.close();

        } catch (JMSException e) {
            logger.error(e);
        }
    }

    /**
     * Передать сообщение брокеру
     *
     * @param topic
     * @param messageBrokerMessage
     */
    @Override
    public void addMessage(String topic, MessageBrokerMessage messageBrokerMessage) {
        logger.info("addTransactionMessage " + messageBrokerMessage + " to " + topic);
        TransactionQueueRecord transactionQueueRecord = new TransactionQueueRecord(topic, messageBrokerMessage);
        transactionsQueue.add(transactionQueueRecord);
    }
}
