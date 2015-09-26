package gamecore.servlet;

import gamecore.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/** Servlet 简单维护器的封装，用于管理 Servlet 。
 */
public final class ServletCapsule {

	private static Logger logger = LogManager.getLogger("ServletCapsule");

	private final static ServletCapsule instance = new ServletCapsule();

	private List<ServletHolder> holders;

	private ServletCapsule() {
		this.holders = new ArrayList<ServletHolder>();
	}

	public static ServletCapsule getInstance() {
		return ServletCapsule.instance;
	}

	public List<ServletHolder> getHolders() {
		return this.holders;
	}

	public void registerServlets(String packageName, ServletContextHandler context) {
		// 遍历指定包下的所有类文件
		Set<Class<?>> classes = ClassUtils.getClasses(packageName);
		Iterator<Class<?>> iter = classes.iterator();
		while (iter.hasNext()) {
			Class<?> cls = iter.next();
			Annotation[] anns = cls.getAnnotations();
			for (Annotation ann : anns) {
				if (ann instanceof WebServlet) {
					try {
						ServletHolder servlet = new ServletHolder((HttpServlet) cls.newInstance());
						WebServlet path = (WebServlet) ann;
						context.addServlet(servlet, path.value()[0]);

						this.holders.add(servlet);

						if (logger.isDebugEnabled()) {
							logger.info("register servlet: " + cls.getName() + " succeeded");
						}
					} catch (InstantiationException ie) {
						logger.error("registerServlets", ie);
					} catch (IllegalAccessException iae) {
						logger.error("registerServlets", iae);
					} catch (Exception e) {
						logger.error("registerServlets", e);
					}
					break;
				}
			}
		}
	}
}
