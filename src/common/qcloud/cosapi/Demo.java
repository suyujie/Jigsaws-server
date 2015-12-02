package common.qcloud.cosapi;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import common.qcloud.cosapi.api.CosCloud;
import gamecore.util.Utils;

public class Demo {
	// 通过控制台获取AppId,SecretId,SecretKey
	public static final int APP_ID = 10013504;
	public static final String SECRET_ID = "AKIDvBjkLMmLYOEEot401lkJPy5pet3YwdlG";
	public static final String SECRET_KEY = "8ZcUkEQxkam9XKokFGe7QDTGdDghP83I";

	static List<String> bucketNames = new ArrayList<String>(Arrays.asList("pic1", "pic2"));

	public static String updateFile(String name, byte[] bytes) {

		long start = System.currentTimeMillis();
		String bucketName = Utils.randomSelectOne(bucketNames);

		CosCloud cos = new CosCloud(APP_ID, SECRET_ID, SECRET_KEY);
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
			String result = cos.uploadFile(bucketName, "/" + name, inputStream);
			long end = System.currentTimeMillis();
			System.out.println(result);
			System.out.println("总用时：" + (end - start) + "毫秒");

			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static void main(String[] args) {
		// 分片上传大文件时，应把CosCloud构造方法第4个超时时间参数设置得长些，默认为60秒
		CosCloud cos = new CosCloud(APP_ID, SECRET_ID, SECRET_KEY);
		try {
			String result = "";
			String bucketName = "111";
			long start = System.currentTimeMillis();
			result = cos.getFolderList(bucketName, "/", 20, "", 0, CosCloud.FolderPattern.Both);
			result = cos.createFolder(bucketName, "/sdk/");
			result = cos.uploadFile(bucketName, "/sdk/xx.txt", "c:\\script.txt");
			result = cos.updateFile(bucketName, "/sdk/xx.txt", "test file");
			result = cos.getFileStat(bucketName, "/sdk/xx.txt");
			result = cos.updateFolder(bucketName, "/sdk/", "test folder");
			result = cos.getFolderStat(bucketName, "/sdk/");
			result = cos.deleteFile(bucketName, "/sdk/xx.txt");
			result = cos.deleteFolder(bucketName, "/sdk/");
			// FileInputStream方式上传
			cos.deleteFile(bucketName, "/stream1.txt");
			File file = new File("c:\\script.txt");
			FileInputStream fileStream = new FileInputStream(file);
			result = cos.uploadFile(bucketName, "/stream1.txt", fileStream);
			// ByteArrayInputStream方式上传
			cos.deleteFile(bucketName, "/shitou.txt");
			ByteArrayInputStream inputStream = new ByteArrayInputStream("woshiyikexiaoxiaodeshitou".getBytes());
			result = cos.uploadFile(bucketName, "/shitou.txt", inputStream);
			cos.deleteFile(bucketName, "/CentOS-6.5-i386-bin-DVD1.iso");
			result = cos.sliceUploadFile(bucketName, "/CentOS-6.5-i386-bin-DVD1.iso",
					"E:\\QQDownload\\CentOS-6.5-i386-bin-DVD1.iso", 3 * 1024 * 1024);
			long end = System.currentTimeMillis();
			System.out.println(result);
			System.out.println("总用时：" + (end - start) + "毫秒");
			System.out.println("The End!");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
