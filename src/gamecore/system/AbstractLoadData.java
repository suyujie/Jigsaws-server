package gamecore.system;

import gamecore.util.FileUtil;

import java.util.ArrayList;
import java.util.List;

import server.node.system.StorageManager;

/**
 * 抽象加载数据
 */
public abstract class AbstractLoadData {

	// 根据部分文件名,得到最新的文件名
	public String getNewXmlName(String xmlName) {

		// 文件命名规范,例如:arm.xml arm_1.xml arm_2.xml head.xml head_2.xml

		List<String> xmlList = FileUtil.readFileNames(StorageManager.getInstance().dataPath, "xml");

		// 有 部分文件名的文件名
		List<String> xmlNameList = new ArrayList<String>();

		for (String name : xmlList) {
			if (name.startsWith(xmlName)) {
				xmlNameList.add(name);
			}
		}

		if (xmlNameList.size() == 1) {// 只有一个的时候
			return new StringBuffer(StorageManager.getInstance().dataPath).append(xmlNameList.get(0)).toString();
		} else {// 多个的时候,选择最大的版本号
			int version = 0;
			for (String xm : xmlList) {

				if (xm.contains("_")) {// 截取版本号
					xm = xm.replace(".xml", "").replace(".XML", "");
					int v = Integer.parseInt(xm.split("_")[1]);
					if (v > version) {// 保存最大的
						version = v;
					}
				}
			}

			if (version == 0) {
				return new StringBuffer(StorageManager.getInstance().dataPath).append(xmlName).append(".xml")
						.toString();
			} else {
				return new StringBuffer(StorageManager.getInstance().dataPath).append(xmlName).append("_")
						.append(version).append(".xml").toString();
			}
		}

	}
}
