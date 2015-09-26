package gamecore.action;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** 
 * action上下文路径描述。
 * @author suiyujie
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ActionPathSpec {
	String value();
}
