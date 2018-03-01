package nl.ybrs.eventserver;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import java.io.IOException;
import java.util.Map;

public class EventSocket extends WebSocketAdapter
{
    private final Map<String, Integer> rooms;

    public EventSocket(Map<String, Integer> rooms){
        this.rooms = rooms;
        System.out.println(rooms);
    }

    @Override
    public void onWebSocketConnect(Session sess)
    {
        super.onWebSocketConnect(sess);
        Integer value = this.rooms.get("room1");
        this.rooms.put("room1", value+1);
        System.out.println("Socket Connected: " + sess);
        try {
            this.getSession().getRemote().sendString("current value:" + this.rooms.get("room1").toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWebSocketText(String message)
    {
        super.onWebSocketText(message);
        System.out.println("Received TEXT message: " + message);
        try {
            this.getSession().getRemote().sendString(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason)
    {
        super.onWebSocketClose(statusCode,reason);
        System.out.println("Socket Closed: [" + statusCode + "] " + reason);
    }

    @Override
    public void onWebSocketError(Throwable cause)
    {
        super.onWebSocketError(cause);
        cause.printStackTrace(System.err);
    }
}

