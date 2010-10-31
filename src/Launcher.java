import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.thread.QueuedThreadPool;

public class Launcher {

  public static final int DEFAULT_JETTY_PORT = 8080;

  public static void main(String[] args) throws Exception {
    int jettyPort = Integer.getInteger("jetty.port", DEFAULT_JETTY_PORT);
    Server jetty = new Server();
    jetty.setThreadPool(new QueuedThreadPool(100));

    Connector connector = new SelectChannelConnector();
    connector.setPort(jettyPort);
    connector.setMaxIdleTime(30000);
    jetty.setConnectors(new Connector[]{connector});

    WebAppContext webapp = new WebAppContext();
    webapp.setContextPath("/");
    webapp.setWar("war");

    HandlerList handlers = new HandlerList();
    handlers.setHandlers(new Handler[]{webapp, new DefaultHandler()});
    jetty.setHandler(handlers);

    jetty.start();
  }
}
