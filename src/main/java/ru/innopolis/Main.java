package ru.innopolis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.innopolis.jms.ActiveMqJmsServiceSenderImpl;
import ru.innopolis.jms.Constants;
import ru.innopolis.jms.KafkaMessageProducerImpl;
import ru.innopolis.jms.MessageBrokerMessage;
import ru.innopolis.jms.MessageProducerService;
import ru.innopolis.jms.MessageSample;
import ru.innopolis.utils.PropertyHandler;
import ru.innopolis.utils.PropertyHandlerImpl;

/**
 * Main.
 *
 * @author Ilya_Sukhachev
 */
public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class.getName());

    public static void main(String[] args) throws InterruptedException {

        PropertyHandler propertyHandler = new PropertyHandlerImpl();
        MessageProducerService messageProducerService = new KafkaMessageProducerImpl(propertyHandler);
        messageProducerService.launchProcessing();

        for (int i = 1; i <= 25; i++) {
            MessageBrokerMessage messageBrokerMessage = new MessageSample();
            logger.info("Send message {}", messageBrokerMessage);
            messageProducerService.addMessage(propertyHandler.getPropertyByKey(Constants.MESSAGE_BROKER_TJM), messageBrokerMessage);
            Thread.sleep(3000);
        }


    }
}
