package server.node.system.jigsaw;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import gamecore.system.AbstractLoadData;
import gamecore.util.Utils;

public class JigsawLoadData extends AbstractLoadData {

	private static Logger logger = LogManager.getLogger(JigsawLoadData.class.getName());

	private static JigsawLoadData instance = null;

	// 官方图片
	private List<Long> ids_guanfang = new ArrayList<Long>();
	// 官方图片
	private HashMap<Long, Jigsaw> image_guanfang = new HashMap<Long, Jigsaw>();

	public static JigsawLoadData getInstance() {
		if (instance == null) {
			instance = new JigsawLoadData();
		}
		return instance;

	}

	public boolean readData() {

		File file = new File(this.getClass().getResource("/").getPath() + "xml/guanfang_img.xml");

		if (!file.exists()) {
			logger.error("NOT FOUND    xml/guanfang_img.xml    file!");
			return false;
		}

		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(file);

			Element imgs = doc.getRootElement().element("imgs");
			if (null != imgs) {
				@SuppressWarnings("unchecked")
				List<Element> imgList = imgs.elements("img");

				if (null != imgList) {
					for (Element el : imgList) {
						Long id = Long.parseLong(el.element("id").getTextTrim());
						String url = el.element("url").getTextTrim();
						Jigsaw gameImage = new Jigsaw(id, null, url, 0, 0, true);
						this.image_guanfang.put(id, gameImage);
						this.ids_guanfang.add(id);
						System.out.println(id + "  " + gameImage.getUrl());
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public List<Long> readRandomList(int num) {
		return (List<Long>) Utils.randomSelect(ids_guanfang, num);
	}

	public Jigsaw readGameImage(Long id) {
		return image_guanfang.get(id);
	}

}