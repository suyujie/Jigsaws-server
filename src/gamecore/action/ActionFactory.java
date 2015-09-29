package gamecore.action;

import gamecore.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.Set;

import javolution.util.FastMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ActionFactory {

	private final static Logger logger = LogManager.getLogger(ActionFactory.class.getName());

	// 单例
	private static ActionFactory instance = new ActionFactory();
	private FastMap<String, IAction> actionMap = new FastMap<String, IAction>();
	private FastMap<String, IAction> managerActionMap = new FastMap<String, IAction>();

	public static ActionFactory getInstance() {
		return instance;
	}

	private ActionFactory() {
	}

	public int registerActions(String packageName) {
		Set<Class<?>> classes = ClassUtils.getClasses(packageName);
		Iterator<Class<?>> iter = classes.iterator();
		while (iter.hasNext()) {
			Class<?> cls = iter.next();
			Annotation[] anns = cls.getAnnotations();
			for (Annotation ann : anns) {
				if (ann instanceof ActionPathSpec) {
					try {
						IAction action = (IAction) cls.newInstance();
						ActionPathSpec path = (ActionPathSpec) ann;
						String key = path.value();

						this.actionMap.put(key, action);

						if (logger.isDebugEnabled()) {
							logger.info("register action:[" + key + "] " + cls.getName() + " succeeded");
						}

					} catch (Exception e) {
						logger.error("registerServlets", e);
					}
					break;
				}
			}
		}
		return actionMap.size();
	}

	public int registerManagerActions(String packageName) {
		Set<Class<?>> classes = ClassUtils.getClasses(packageName);
		Iterator<Class<?>> iter = classes.iterator();
		while (iter.hasNext()) {
			Class<?> cls = iter.next();
			Annotation[] anns = cls.getAnnotations();
			for (Annotation ann : anns) {
				if (ann instanceof ActionPathSpec) {
					try {
						IAction action = (IAction) cls.newInstance();
						ActionPathSpec path = (ActionPathSpec) ann;
						this.managerActionMap.put(path.value(), action);

						if (logger.isDebugEnabled()) {
							logger.info("register manager action: " + cls.getName() + " succeeded");
						}

					} catch (Exception e) {
						logger.error("registerManagerActions", e);
					}
					break;
				}
			}
		}
		return managerActionMap.size();
	}

	public IAction getAction(String commandId) {
		return actionMap.get(commandId);
	}

	public IAction getManagerAction(String commandId) {
		return managerActionMap.get(commandId);
	}

}
