package ru.innopolis.jms;

public class TransactionQueueRecord {
    private String topic;
    private MessageBrokerMessage messageBrokerMessage;

    public TransactionQueueRecord(String topic, MessageBrokerMessage messageBrokerMessage) {
        this.topic = topic;
        this.messageBrokerMessage = messageBrokerMessage;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public MessageBrokerMessage getMessageBrokerMessage() {
        return messageBrokerMessage;
    }

    public void setMessageBrokerMessage(MessageBrokerMessage messageBrokerMessage) {
        this.messageBrokerMessage = messageBrokerMessage;
    }
}

