package test;

import java.util.List;

import gamecore.util.FileUtil;

public class TestFile {

	public static void main(String[] args) {

		String path = "C:\\Users\\sui\\Desktop\\JIgsaw_guanfang";

		List<String> names = FileUtil.readFileNames(path, "");

		String abc="<img><id>aaa</id><url>http://pic-10013504.file.myqcloud.com/aaa.jpg</url></img>";

		for (String name : names) {
			
			System.out.println(abc.replaceAll("aaa", name.replace(".jpg", "")));


		}

	}

}
