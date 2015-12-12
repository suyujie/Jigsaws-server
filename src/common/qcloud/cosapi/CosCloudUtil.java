package common.qcloud.cosapi;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

import common.qcloud.cosapi.api.CosCloud;
import gamecore.util.Utils;
import server.node.system.StorageManager;

public class CosCloudUtil {

	static List<String> bucketNames = new ArrayList<String>(Arrays.asList("pic1", "pic2"));

	public static String readBucketName() {
		return Utils.randomSelectOne(bucketNames);
	}

	public static String updateFile(String bucketName, String name, byte[] bytes) {

		QCloudStorageBean cosBean = StorageManager.getInstance().qCloudStorages.get(1);

		CosCloud cos = new CosCloud(cosBean.getAppId(), cosBean.getSecretId(), cosBean.getSecretKey());
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
			String result = cos.uploadFile(bucketName, "/" + name, inputStream);

			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String deleteFile(String bucketName, String name) {

		QCloudStorageBean cosBean = StorageManager.getInstance().qCloudStorages.get(1);

		CosCloud cos = new CosCloud(cosBean.getAppId(), cosBean.getSecretId(), cosBean.getSecretKey());
		String result = null;
		try {
			result = cos.deleteFile(bucketName, name);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public static JSONObject readFolderList(String bucketName, int num, String context) {
		QCloudStorageBean cosBean = StorageManager.getInstance().qCloudStorages.get(1);
		CosCloud cos = new CosCloud(cosBean.getAppId(), cosBean.getSecretId(), cosBean.getSecretKey());

		String result = null;
		try {
			result = cos.getFolderList(bucketName, "/", num, context, 0, CosCloud.FolderPattern.Both);
			JSONObject resultJson = JSONObject.parseObject(result);
			return resultJson;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}

	public static void main(String[] args) {

		StorageManager.getInstance().load();

		// 分片上传大文件时，应把CosCloud构造方法第4个超时时间参数设置得长些，默认为60秒
		QCloudStorageBean cosBean = StorageManager.getInstance().qCloudStorages.get(1);

		CosCloud cos = new CosCloud(cosBean.getAppId(), cosBean.getSecretId(), cosBean.getSecretKey());
		try {
			String result = "";
			String bucketName = "pic";
			long start = System.currentTimeMillis();
			
//			JSONObject resultJson = readFolderList(bucketName, 20, "");
//			
//			
//			System.out.println(resultJson.getIntValue("code"));
//			System.out.println(resultJson.getJSONObject("data"));
//			System.out.println(resultJson.getJSONObject("data").getString("context"));
//			System.out.println(resultJson.getJSONObject("data").getBooleanValue("has_more"));
//			System.out.println(resultJson.getJSONObject("data").getJSONArray("infos").size());
			
			
			// result = cos.getFolderList(bucketName, "/", 20, "", 0,
			// CosCloud.FolderPattern.Both);
			// result = cos.createFolder(bucketName, "/sdk/");
			// result = cos.uploadFile(bucketName, "/sdk/xx.txt",
			// "c:\\script.txt");
			// result = cos.updateFile(bucketName, "/sdk/xx.txt", "test file");
			// result = cos.getFileStat(bucketName, "/sdk/xx.txt");
			// result = cos.updateFolder(bucketName, "/sdk/", "test folder");
			// result = cos.getFolderStat(bucketName, "/sdk/");
			// result = cos.deleteFile(bucketName, "/sdk/xx.txt");
			// result = cos.deleteFolder(bucketName, "/sdk/");
			// FileInputStream方式上传
			// cos.deleteFile(bucketName, "/stream1.txt");
			// File file = new File("c:\\script.txt");
			// FileInputStream fileStream = new FileInputStream(file);
			// result = cos.uploadFile(bucketName, "/stream1.txt", fileStream);
			// // ByteArrayInputStream方式上传
			// cos.deleteFile(bucketName, "/shitou.txt");
			// ByteArrayInputStream inputStream = new
			// ByteArrayInputStream("woshiyikexiaoxiaodeshitou".getBytes());
			// result = cos.uploadFile(bucketName, "/shitou.txt", inputStream);
			// cos.deleteFile(bucketName, "/CentOS-6.5-i386-bin-DVD1.iso");
			// result = cos.sliceUploadFile(bucketName,
			// "/CentOS-6.5-i386-bin-DVD1.iso",
			// "E:\\QQDownload\\CentOS-6.5-i386-bin-DVD1.iso", 3 * 1024 * 1024);
			long end = System.currentTimeMillis();
			System.out.println(result);
			System.out.println("总用时：" + (end - start) + "毫秒");
			System.out.println("The End!");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
