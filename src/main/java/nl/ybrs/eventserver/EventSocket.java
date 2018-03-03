package nl.ybrs.eventserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ITopic;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import java.io.IOException;
import java.util.Map;

public class EventSocket extends WebSocketAdapter
{
    private final IMap<String, RoomState> rooms;
    private final HazelcastInstance hzInstance;
    private RoomMessageListener listener;
    private ITopic<Object> topic;
    private String messageListenerName;
    private String roomname;

    public EventSocket(HazelcastInstance hzInstance){
        this.hzInstance = hzInstance;
        this.rooms = hzInstance.getMap("rooms");
        System.out.println(rooms);
    }



    public void sendRoomState(){
        RoomState rs = this.rooms.get(this.roomname);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            this.getSession().getRemote().sendString(objectMapper.writeValueAsString(rs));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void joinRoom(String roomname){
        this.roomname = "room1";
        this.topic = hzInstance.getTopic(this.roomname);
        this.listener = new RoomMessageListener(this);
        this.messageListenerName = topic.addMessageListener(this.listener);
        topic.publish("user joined " + this.roomname);
        this.sendRoomState();

        rooms.addEntryListener(new RoomMapEntryListener(this), this.roomname, false);
    }

    @Override
    public void onWebSocketConnect(Session sess)
    {
        super.onWebSocketConnect(sess);
        System.out.println("Socket Connected: " + sess);
    }

    public void incrScore(){
        rooms.lock(this.roomname);
        RoomState rs = this.rooms.get(this.roomname);
        rs.incrScore();
        rooms.put(this.roomname, rs);
        rooms.unlock(this.roomname);
        topic.publish("STATE_UPDATE");
    }

    @Override
    public void onWebSocketText(String message)
    {
        super.onWebSocketText(message);
        System.out.println("Received TEXT message: " + message);
        if (message.equals("JOIN_ROOM")){
            this.joinRoom("room1"); // for simplicity sake, we are always joining room1
        }

        if (message.equals("INCR_SCORE")){
            if (this.roomname == null){
                return; // should give error
            }
            this.incrScore();
        }
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason)
    {
        super.onWebSocketClose(statusCode,reason);
        topic.removeMessageListener(this.messageListenerName);
        System.out.println("Socket Closed: [" + statusCode + "] " + reason);
    }

    @Override
    public void onWebSocketError(Throwable cause)
    {
        super.onWebSocketError(cause);
        cause.printStackTrace(System.err);
    }
}

