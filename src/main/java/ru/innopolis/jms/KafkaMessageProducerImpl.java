package ru.innopolis.jms;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.innopolis.utils.PropertyHandler;
import ru.innopolis.utils.TransactionGson;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;

public class KafkaMessageProducerImpl implements MessageProducerService {
    private static final Logger logger = LogManager.getLogger(KafkaMessageProducerImpl.class.getName());

    private final PropertyHandler propertyHandler;
    private Producer producer;
    private ConcurrentLinkedDeque<TransactionQueueRecord> transactionsDeque;


    public KafkaMessageProducerImpl(PropertyHandler propertyHandler) {
        logger.info("KafkaMessageProducerImpl initialization..");
        this.propertyHandler = propertyHandler;
        this.producer = buildKafkaProducer();
        this.transactionsDeque = new ConcurrentLinkedDeque<>();
    }

    private Producer buildKafkaProducer() {
        logger.info("buildKafkaProducer..");
        Properties configProperties = new Properties();
        String jaasFormatTemplate = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s\" password=\"%s\";";
        String jaasConfigString = String.format(jaasFormatTemplate,
                propertyHandler.getPropertyByKey(Constants.KAFKA_SASL_USERNAME),
                propertyHandler.getPropertyByKey(Constants.KAFKA_SASL_PASSWORD));
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, propertyHandler.getPropertyByKey(Constants.KAFKA_BOOTSTRAP_ADDRESS));
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        configProperties.put("security.protocol", "SASL_PLAINTEXT");
        configProperties.put("sasl.mechanism", "PLAIN");
        configProperties.put("sasl.jaas.config", jaasConfigString);

        return new KafkaProducer(configProperties);
    }

    @Override
    public void launchProcessing() {
        logger.info("launchProcessing..");
        Runnable processing = () -> {
            logger.info("launchProcessing: new thread for processing is launched");
            while (true) {
                while (!transactionsDeque.isEmpty()) {
                    sendTransaction(transactionsDeque.poll());
                }
                try {
                    // проверять очередь будем раз в MESSAGE_BROKER_SLEEP миллисекунд
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
        String tjm = TransactionGson.instance().toJson(transactionQueueRecord.getMessageBrokerMessage());
        logger.debug("sendTransaction " + tjm);
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(transactionQueueRecord.getTopic(),
                UUID.randomUUID().toString(), tjm);

        producer.send(producerRecord, (metadata, exception) -> {
            if (exception == null) {
                logger.debug("Transaction sent to topic: " + metadata.topic());
            } else {
                logger.error("Exception to topic: " + metadata.topic() + " is: " + exception);
                transactionsDeque.addFirst(transactionQueueRecord);
            }
        });
    }

    @Override
    public void addMessage(String topic, MessageBrokerMessage messageBrokerMessage) {
        logger.info("addTransactionMessage..");
        TransactionQueueRecord transactionQueueRecord = new TransactionQueueRecord(topic, messageBrokerMessage);
        transactionsDeque.add(transactionQueueRecord);
    }
}
