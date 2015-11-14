package server.node.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

import gamecore.action.ActionPathSpec;
import gamecore.message.RequestJson;
import gamecore.message.ResponseJson;

/**
 * 获取一个图片地址
 * 
 * @author suiyujie
 */
@ActionPathSpec("201")
public class ReadImageUrlAction extends AbstractAction {

	private static final long serialVersionUID = 2561195241515014369L;

	private static final Logger logger = LogManager.getLogger(ReadImageUrlAction.class);

	public static List<String> imgUrls = new ArrayList<String>(Arrays.asList(

	"http://img8.cache.hxsd.com/game/2012/08/03/657350_1343960007_28.jpg",
//			"http://static.sporttery.cn/images/130517/18-13051G32G3-52.jpg",
//			"http://d01.res.meilishuo.net/pic/_o/41/7d/c4f4269540a4738091564c37b33c_567_733.c1.jpg",
//			"http://img1b.xgo-img.com.cn/pics/1538/1537562.jpg",
//			"http://h.hiphotos.baidu.com/image/pic/item/86d6277f9e2f0708c787381feb24b899a901f22b.jpg",
//			"http://b.hiphotos.baidu.com/image/pic/item/0e2442a7d933c895609c9554d01373f08202003a.jpg",
//			"http://b.hiphotos.baidu.com/image/pic/item/3b87e950352ac65c118f0c15fbf2b21192138ac9.jpg",
//
//	"http://f.hiphotos.baidu.com/image/pic/item/d058ccbf6c81800a7cd9ff18b23533fa828b47e3.jpg",
//			"http://d.hiphotos.baidu.com/image/pic/item/4034970a304e251fee04e8eea486c9177f3e5330.jpg",
//			"http://a.hiphotos.baidu.com/image/pic/item/c995d143ad4bd1137bd5613a58afa40f4bfb0599.jpg",
//
//	"http://ff.topit.me/f/b0/cd/114051713140ccdb0fo.jpg",
//			"http://img5.duitang.com/uploads/item/201408/06/20140806204930_H5FL3.thumb.700_0.jpeg",
//			"http://e.hiphotos.baidu.com/baike/c0%3Dbaike80%2C5%2C5%2C80%2C26/sign=8dad34a5c5cec3fd9f33af27b7e1bf5a/58ee3d6d55fbb2fb77a9917b494a20a44723dcdd.jpg",
//
	"http://img1d.xgo-img.com.cn/pics/1549/a1548844.jpg"

	));

	@Override
	public ResponseJson execute(RequestJson requestJson) {

		ResponseJson responseJson = new ResponseJson(requestJson.getCommandId(), true, null);

		JSONObject json = new JSONObject();

		json.put("url", gamecore.util.Utils.randomSelectOne(imgUrls));

		responseJson.setBody(json);

		return responseJson;
	}
}
