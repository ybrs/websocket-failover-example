package nl.ybrs.eventserver;

import com.hazelcast.core.HazelcastInstance;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import java.util.Map;

@SuppressWarnings("serial")
public class EventServlet extends WebSocketServlet
{
    private final HazelcastInstance hzInstance;


    public EventServlet(HazelcastInstance instance){
        this.hzInstance = instance;
    }

    @Override
    public void configure(WebSocketServletFactory factory)
    {
        factory.setCreator(new WebsocketAdapterCreator(hzInstance));
    }
}
