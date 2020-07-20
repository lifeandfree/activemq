package ru.innopolis.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.innopolis.jms.Constants;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class PropertyHandlerImpl implements PropertyHandler {

    private static final Logger logger = LogManager.getLogger(PropertyHandlerImpl.class.getName());

    private Map<String, String> properties;
    private LoadPropertyHandler loadPropertyHandler;

    public PropertyHandlerImpl() {
        this.properties = new ConcurrentHashMap<>();
        this.loadPropertyHandler = new LoadPropertyHandlerImpl();
        loadProperty();
        logger.debug("PropertyHandlerImpl constructed");
    }

    public void loadProperty() {
        logger.debug("load property");

        String propertyFileName = "jms-serv.properties";
        Properties appProps = loadPropertyHandler.loadPropertyFile(propertyFileName);

        loadPropertyHandler.setProperty(this.properties, appProps, Constants.MESSAGE_BROKER_TJM);
        loadPropertyHandler.setProperty(this.properties, appProps, Constants.MESSAGE_BROKER_SLEEP);

        loadPropertyHandler.setProperty(this.properties, appProps, Constants.JMS_ACTIVEMQ_URL);
        loadPropertyHandler.setProperty(this.properties, appProps, Constants.JMS_ACTIVEMQ_USERNAME);
        loadPropertyHandler.setProperty(this.properties, appProps, Constants.JMS_ACTIVEMQ_PASSWORD);

        loadPropertyHandler.setProperty(this.properties, appProps, Constants.KAFKA_BOOTSTRAP_ADDRESS);
        loadPropertyHandler.setProperty(this.properties, appProps, Constants.KAFKA_SASL_USERNAME);
        loadPropertyHandler.setProperty(this.properties, appProps, Constants.KAFKA_SASL_PASSWORD);
    }

    /**
     * Получить property по key
     *
     * @return
     */
    @Override
    public String getPropertyByKey(String key) {
        if (!properties.containsKey(key)) {
            return null;
        }
        return properties.get(key);
    }
}
