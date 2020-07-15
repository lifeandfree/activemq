package ru.innopolis.jms;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class PropertyHandlerImpl implements PropertyHandler {

    private static final Logger logger = LogManager.getLogger(PropertyHandlerImpl.class.getName());

    private Map<String, String> properties;
    private LoadPropertyHandler loadPropertyHandler;

    public PropertyHandlerImpl() {
//        public PropertyHandlerImpl(PropertyDecryptor propertyDecryptor) throws IOException {
        this.properties = new ConcurrentHashMap<>();
        this.loadPropertyHandler = new LoadPropertyHandlerImpl();
        loadProperty();
        logger.debug("PropertyHandlerImpl constructed");
    }

    public PropertyHandlerImpl(Function<Map<String, String>, Map<String, String>> function) {
        this.properties = new ConcurrentHashMap<>();
        this.properties = function.apply(this.properties);
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
            //throw new PropertyNotExists("Property " + key + " is not found");
        }
        return properties.get(key);
    }
}
