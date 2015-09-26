package server.node.system.lang;

import gamecore.system.AbstractSystem;

import common.language.LangType;

/**
 * 公告消息系统
 */
public final class LangSystem extends AbstractSystem {

	@Override
	public boolean startup() {

		System.out.println("LangSystem start..");

		//读取公共信息,去web读取json信息来获得
		boolean b = LangLoadData.getInstance().readData();

		System.out.println("LangSystem start..ok");

		return b;
	}

	@Override
	public void shutdown() {
	}

	public String getMessage(LangType lang, String key, String... args) {
		String msg = null;
		switch (lang) {
		case zh_CN:
			msg = LangLoadData.getInstance().i18nMsg_zh_CN.get(key);
			break;
		case zh_TW:
			msg = LangLoadData.getInstance().i18nMsg_zh_TW.get(key);
			break;
		case en_US:
			msg = LangLoadData.getInstance().i18nMsg_en_US.get(key);
			break;
		case es_ES:
			msg = LangLoadData.getInstance().i18nMsg_es_ES.get(key);
			break;
		default:
			msg = LangLoadData.getInstance().i18nMsg_en_US.get(key);
			break;
		}
		if (msg == null) {
			return key;
		}
		if (args != null && args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				if (args[i] != null) {
					msg = msg.replace("{" + i + "}", args[i]);
				}
			}
		}
		return msg;
	}
}
