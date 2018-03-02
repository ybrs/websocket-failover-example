package nl.ybrs.eventserver;

import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

class RoomMessageListener implements MessageListener {

    @Override
    public void onMessage(Message message) {
        System.out.println("Message received = " + message.getPublishingMember() + ": " + message.getMessageObject());
    }
}
