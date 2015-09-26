package server.node;

import gamecore.servlet.ServletCapsule;
import gamecore.util.Clock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;

import server.node.system.ConfigManager;
import server.node.system.Root;

/**
 * 系统根，所有子系统引导类。
 */
public final class NodeServer {

	private final static Logger logger = LogManager.getLogger(NodeServer.class.getName());

	private final static NodeServer instance = new NodeServer();

	private Server server;

	private NodeServer() {
	}

	public static NodeServer getInstance() {
		return NodeServer.instance;
	}

	public void startup() {
		// 初始化服务器
		server = initServer();
		// 启动服务器
		try {
			System.out.println("server start...");
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("server start success");

		try {
			server.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// 关闭子系统
		Root.getInstance().shutdown();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			logger.error(e);
		}

		System.out.println("Server stopped");

		// 关闭时钟
		Clock.stop();

		System.exit(0);
	}

	public Server initServer() {

		// 创建 Jetty 服务器
		Server server = new Server();

		//servlet
		ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
		servletContextHandler.setContextPath("/");
		servletContextHandler.setClassLoader(Thread.currentThread().getContextClassLoader());
		ServletCapsule.getInstance().registerServlets("server.node.servlet", servletContextHandler);

		ContextHandlerCollection handlers = new ContextHandlerCollection();
		handlers.addHandler(servletContextHandler);

		server.setHandler(handlers);

		// 工作服务
		ServerConnector work = new ServerConnector(server);
		work.setPort(ConfigManager.getInstance().workPort);
		work.setAcceptQueueSize(ConfigManager.getInstance().workQueueSize);

		// 支付服务
		ServerConnector pay = new ServerConnector(server);
		pay.setPort(ConfigManager.getInstance().payPort);
		pay.setAcceptQueueSize(ConfigManager.getInstance().payQueueSize);

		// 管理服务
		ServerConnector management = new ServerConnector(server);
		management.setPort(ConfigManager.getInstance().mgmtPort);
		management.setAcceptQueueSize(ConfigManager.getInstance().mgmtQueueSize);

		// 设置连接器
		server.setConnectors(new Connector[] { work, pay, management });

		return server;
	}

	public void stopServer() {
		try {
			this.server.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
