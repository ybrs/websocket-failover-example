package nl.ybrs.eventserver;

import com.hazelcast.core.HazelcastInstance;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

import java.util.Map;

public class WebsocketAdapterCreator implements WebSocketCreator {
    private final HazelcastInstance hzInstance;

    public WebsocketAdapterCreator(HazelcastInstance hzInstance)
    {
        this.hzInstance = hzInstance;
    }

    public Object createWebSocket(ServletUpgradeRequest request,
                                  ServletUpgradeResponse response)
    {
        return new EventSocket(hzInstance);
    }
}
