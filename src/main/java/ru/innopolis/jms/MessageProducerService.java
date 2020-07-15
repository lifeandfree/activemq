package ru.innopolis.jms;

public interface MessageProducerService {

    /**
     * Запустить процесс обработки сообщений.
     * (Запускает отдельный поток, где вытаскивает сообщения из очереди и отправляет их Брокеру)
     */
    void launchProcessing();

    /**
     * Передать сообщение брокеру
     *
     * @param topic
     * @param messageBrokerMessage
     */
    void addMessage(String topic, MessageBrokerMessage messageBrokerMessage);
}
