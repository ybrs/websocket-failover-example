package nl.ybrs.eventserver;

import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

import java.util.Map;

public class WebsocketAdapterCreator implements WebSocketCreator {


    private final Map<String, Integer> rooms;

    public WebsocketAdapterCreator(Map<String, Integer> rooms)
    {
        this.rooms = rooms;
    }

    public Object createWebSocket(ServletUpgradeRequest request,
                                  ServletUpgradeResponse response)
    {
        return new EventSocket(rooms);
    }
}
