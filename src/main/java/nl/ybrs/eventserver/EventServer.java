package nl.ybrs.eventserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.config.Config;
import com.hazelcast.core.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class EventServer
{

    @SuppressWarnings("serial")
    public static class ServersServlet extends HttpServlet {

        private final IMap<String, Integer> servers;

        public ServersServlet(IMap<String, Integer> servers) {
            this.servers = servers;
        }

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            ObjectMapper objectMapper = new ObjectMapper();
            response.getWriter().println(objectMapper.writeValueAsString(this.servers));
        }
    }

    public static void main(String[] args) {
        Config cfg = new Config();
        HazelcastInstance instance = Hazelcast.newHazelcastInstance(cfg);
        IMap<String, RoomState> rooms = instance.getMap("rooms");
        rooms.putIfAbsent("room1", new RoomState("room1"));

        IMap<String, Integer> servers = instance.getMap("servers");

        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(Integer.parseInt(args[0]));
        server.addConnector(connector);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        context.setWelcomeFiles(new String[] { "client.html" });

        ServletHolder holderEvents = new ServletHolder("ws-events", new EventServlet(instance));
        context.addServlet(holderEvents, "/events/*");

        ServletHolder holder = new ServletHolder(new ServersServlet(servers));
        context.addServlet(holder, "/servers/");

        ServletHolder staticHolder = new ServletHolder(new DefaultServlet());
        staticHolder.setInitParameter("pathInfoOnly", "true");
        staticHolder.setInitParameter("resourceBase", "./public");
        staticHolder.setInitParameter("dirAllowed","true");
        context.addServlet(staticHolder, "/*");

        ServletHolder holderPwd = new ServletHolder("default", DefaultServlet.class);
        holderPwd.setInitParameter("dirAllowed","true");
        context.addServlet(holderPwd,"/");

        server.setHandler(context);


        try {
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Integer p = servers.putIfAbsent("ws://localhost:" + args[0] + "/events/", 1, 3, TimeUnit.SECONDS);
                }
            }, 1 * 1000, 1 * 1000);

            server.start();
            server.dump(System.err);
            server.join();
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }

    }
}
