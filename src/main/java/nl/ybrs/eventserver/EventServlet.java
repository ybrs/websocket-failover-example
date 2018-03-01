package nl.ybrs.eventserver;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import java.util.Map;

@SuppressWarnings("serial")
public class EventServlet extends WebSocketServlet
{
    private final Map<String, Integer> rooms;

    public EventServlet(Map<String, Integer> rooms){
        this.rooms = rooms;
    }

    @Override
    public void configure(WebSocketServletFactory factory)
    {
//        factory.register(EventSocket.class);
        factory.setCreator(new WebsocketAdapterCreator(this.rooms));
    }
}
