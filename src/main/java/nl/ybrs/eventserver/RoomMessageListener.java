package nl.ybrs.eventserver;

import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

import java.io.IOException;

class RoomMessageListener implements MessageListener {
    private final EventSocket socket;

    public RoomMessageListener(EventSocket socket) {
        this.socket = socket;
    }

    @Override
    public void onMessage(Message message) {
        System.out.println("Message received = " + message.getPublishingMember() + ": " + message.getMessageObject());
        try {
            this.socket.getSession().getRemote().sendString(message.getMessageObject().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
