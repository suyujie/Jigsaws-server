package server.node.system.lang;

import gamecore.system.AbstractLoadData;

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Properties;

import javolution.util.FastMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class LangLoadData extends AbstractLoadData {

	public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

	private final static Logger logger = LogManager.getLogger(LangLoadData.class.getName());

	private static final LangLoadData instance = new LangLoadData();

	public FastMap<String, String> i18nMsg_zh_CN = new FastMap<String, String>();
	public FastMap<String, String> i18nMsg_zh_TW = new FastMap<String, String>();
	public FastMap<String, String> i18nMsg_en_US = new FastMap<String, String>();
	public FastMap<String, String> i18nMsg_es_ES = new FastMap<String, String>();

	public LangLoadData() {
	}

	public static LangLoadData getInstance() {
		return LangLoadData.instance;
	}

	public boolean readData() {

		boolean b = load_zh_CN();
		b = b & load_zh_TW();
		b = b & load_en_US();
		b = b & load_es_ES();

		logger.info("i18nMsg_zh_CN  " + i18nMsg_zh_CN.size());
		logger.info("i18nMsg_zh_TW  " + i18nMsg_zh_TW.size());
		logger.info("i18nMsg_en_US  " + i18nMsg_en_US.size());
		logger.info("i18nMsg_es_ES  " + i18nMsg_es_ES.size());

		return b;
	}

	public boolean load_zh_CN() {

		Properties pps = new Properties();
		try {
			pps.load(new FileInputStream(("languageServerChinese")));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		Enumeration<?> e = pps.propertyNames();//得到配置文件的名字
		while (e.hasMoreElements()) {
			String k = e.nextElement().toString();
			String v = null;
			try {
				v = new String(pps.getProperty(k).getBytes("ISO-8859-1"), DEFAULT_CHARSET);
			} catch (UnsupportedEncodingException e1) {
				logger.error(e1);
			}
			i18nMsg_zh_CN.put(k, v);
		}
		return true;
	}

	public boolean load_zh_TW() {

		Properties pps = new Properties();
		try {
			pps.load(new FileInputStream(getNewXmlName("languageServerFanChinese")));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		Enumeration<?> e = pps.propertyNames();//得到配置文件的名字
		while (e.hasMoreElements()) {
			String k = e.nextElement().toString();
			String v = null;
			try {
				v = new String(pps.getProperty(k).getBytes("ISO-8859-1"), DEFAULT_CHARSET);
			} catch (UnsupportedEncodingException e1) {
				logger.error(e1);
			}
			i18nMsg_zh_TW.put(k, v);
		}
		return true;
	}

	public boolean load_en_US() {
		Properties pps = new Properties();
		try {
			pps.load(new FileInputStream(getNewXmlName("languageServerEnglish")));
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		Enumeration<?> e = pps.propertyNames();//得到配置文件的名字
		while (e.hasMoreElements()) {
			String k = e.nextElement().toString();
			String v = null;
			try {
				v = new String(pps.getProperty(k).getBytes("ISO-8859-1"), DEFAULT_CHARSET);
			} catch (UnsupportedEncodingException e1) {
				logger.error(e1);
			}
			i18nMsg_en_US.put(k, v);
		}
		return true;
	}

	public boolean load_es_ES() {
		Properties pps = new Properties();
		try {
			pps.load(new FileInputStream(getNewXmlName("languageServerSpanish")));
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		Enumeration<?> e = pps.propertyNames();//得到配置文件的名字
		while (e.hasMoreElements()) {
			String k = e.nextElement().toString();
			String v = null;
			try {
				v = new String(pps.getProperty(k).getBytes("ISO-8859-1"), DEFAULT_CHARSET);
			} catch (UnsupportedEncodingException e1) {
				logger.error(e1);
			}
			i18nMsg_es_ES.put(k, v);
		}
		return true;
	}

}
