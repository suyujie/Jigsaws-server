package gamecore.util;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

/**
 * 文件操作工具类。
 */
public class FileUtil {

	private static final Logger logger = Logger.getLogger(FileUtil.class);

	public static BufferedReader br;
	public static FileInputStream fis;
	public static PrintStream ps;

	/**
	 * 把文件内容读到一个字符串列表中。
	 */
	public static List<String> readLines(File file) throws IOException {
		final List<String> lines = new ArrayList<String>();

		read(file, new LineHandler() {
			@Override
			public void handle(String line) {
				lines.add(line);
			}
		});

		return lines;
	}

	/**
	 * 把文件内容读到一个字符串中。
	 */
	public static String readString(File file) throws IOException {
		final StringBuilder buf = new StringBuilder();

		read(file, new LineHandler() {
			@Override
			public void handle(String line) {
				buf.append(line).append("\n");
			}
		});

		return buf.toString();
	}

	public static void read(File file, LineHandler lineHandler) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				lineHandler.handle(line);
			}
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				logger.error("Can not close BufferedReader.", e);
			}
		}
	}

	public static interface LineHandler {
		public void handle(String line);
	}

	public static List<String> readFileNames(String dir, String suffix) {
		List<String> names = new ArrayList<>();

		File parent = new File(dir);

		File[] fs = parent.listFiles();

		for (File file : fs) {
			if (file.getName().toUpperCase().endsWith(suffix.toUpperCase())) {
				names.add(file.getName());
			}
		}

		return names;
	}

	public static String readFileSuffix(File file) {
		String name = file.getName();
		int dot = name.lastIndexOf('.');
		if ((dot > -1) && (dot < (name.length() - 1))) {
			return name.substring(dot + 1);
		}
		return null;
	}

	public static byte[] readImg(String imgName) {
		File file = new File(imgName);

		try {
			BufferedImage img = ImageIO.read(file);
			ByteArrayOutputStream buf = new ByteArrayOutputStream((int) file.length());
			ImageIO.write(img, readFileSuffix(file), buf);
			return buf.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static byte[] readFile(String filePath) {

		InputStream is = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {
			is = new FileInputStream(filePath);// pathStr 文件路径
			byte[] b = new byte[1024];
			int n;
			while ((n = is.read(b)) != -1) {
				out.write(b, 0, n);
			}// end while
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return out.toByteArray();

	}

	/**
	 * 以文件流的方式复制文件 该方法经过测试，支持中文处理，并且可以复制多种类型，比如txt，xml，jpg，doc等多种格式
	 * 
	 * @param src
	 * @param dest
	 * @throws IOException
	 */
	public static void copyFile(String src, String dest) throws IOException {
		FileInputStream in = new FileInputStream(src);
		File file = new File(dest);
		if (!file.exists())
			file.createNewFile();
		FileOutputStream out = new FileOutputStream(file);
		int c;
		byte buffer[] = new byte[1024];
		while ((c = in.read(buffer)) != -1) {
			for (int i = 0; i < c; i++)
				out.write(buffer[i]);
		}
		in.close();
		out.close();
	}

	public static File writeFile(String pathAndName, byte[] b) {
		BufferedOutputStream stream = null;
		File file = null;
		try {
			file = new File(pathAndName);
			FileOutputStream fstream = new FileOutputStream(file);
			stream = new BufferedOutputStream(fstream);
			stream.write(b);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return file;
	}

	/**
	 * 利用PrintStream写文件
	 */
	public static void writeFileByPrintStream(String filePath, String msg) {
		try {
			FileOutputStream out = new FileOutputStream(filePath);
			ps = new PrintStream(out);
			ps.println(msg);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 利用StringBuffer写文件 该方法可以设定使用何种编码，有效解决中文问题。
	 * 
	 * @throws IOException
	 */
	public static void writeFileByStringBuffer(String filePath, String msg) throws IOException {
		File file = new File(filePath);
		if (!file.exists())
			file.createNewFile();
		FileOutputStream out = new FileOutputStream(file, true);
		StringBuffer sb = new StringBuffer();
		sb.append(msg);
		out.write(sb.toString().getBytes("utf-8"));
		out.close();
	}

	/**
	 * FileWriter 写入文件
	 * 
	 * @param filePath
	 * @param msg
	 * @param cover
	 * @throws IOException
	 */
	public static void writeFile(String filePath, String msg, boolean append) throws IOException {
		File file = new File(filePath);
		if (!file.exists())
			file.createNewFile();

		writeFile(file, msg, append);
	}

	public static void writeFile(File file, String msg, boolean append) throws IOException {
		FileWriter fw = null;
		fw = new FileWriter(file, append);
		fw.write(msg + "\n");
		fw.close();
	}

	/**
	 * 文件重命名
	 * 
	 * @param path
	 * @param oldname
	 * @param newname
	 */
	public static void renameFile(String path, String oldname, String newname) {
		if (!oldname.equals(newname)) {// 新的文件名和以前文件名不同时,才有必要进行重命名
			File oldfile = new File(path + "/" + oldname);
			File newfile = new File(path + "/" + newname);
			oldfile.renameTo(newfile);
		}
	}

	/**
	 * 转移文件目录 转移文件目录不等同于复制文件，复制文件是复制后两个目录都存在该文件，而转移文件目录则是转移后，只有新目录中存在该文件。
	 * 
	 * @param filename
	 * @param oldpath
	 * @param newpath
	 * @param cover
	 */
	public static void changeDirectory(String filename, String oldpath, String newpath, boolean cover) {
		if (!oldpath.equals(newpath)) {
			File oldfile = new File(oldpath + "/" + filename);
			File newfile = new File(newpath + "/" + filename);
			if (newfile.exists()) {// 若在待转移目录下，已经存在待转移文件
				if (cover)// 覆盖
					oldfile.renameTo(newfile);
			} else {
				oldfile.renameTo(newfile);
			}
		}
	}

	/**
	 * 利用FileInputStream读取文件
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static String readFileByFileInputStream(String path) throws IOException {
		File file = new File(path);
		if (!file.exists() || file.isDirectory())
			throw new FileNotFoundException();
		fis = new FileInputStream(file);
		byte[] buf = new byte[1024];
		StringBuffer sb = new StringBuffer();
		while ((fis.read(buf)) != -1) {
			sb.append(new String(buf));
			buf = new byte[1024];// 重新生成，避免和上次读取的数据重复
		}
		return sb.toString();
	}

	/**
	 * 利用BufferedReader读取 在IO操作， 利用BufferedReader和BufferedWriter效率会更高一点
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static String readFileByBufferedReader(String path) throws IOException {
		File file = new File(path);
		if (!file.exists() || file.isDirectory())
			throw new FileNotFoundException();
		br = new BufferedReader(new FileReader(file));
		String temp = null;
		StringBuffer sb = new StringBuffer();
		temp = br.readLine();
		while (temp != null) {
			sb.append(temp + " ");
			temp = br.readLine();
		}
		return sb.toString();
	}

	/**
	 * 利用BufferedReader读取 在IO操作， 利用BufferedReader和BufferedWriter效率会更高一点
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static List<String> readFileByBufferedReaderAsList(String path) throws IOException {
		List<String> result = new ArrayList<>();
		File file = new File(path);
		if (!file.exists() || file.isDirectory())
			throw new FileNotFoundException();
		br = new BufferedReader(new FileReader(file));
		String temp = null;
		temp = br.readLine();
		while (temp != null) {
			temp = br.readLine();
			result.add(temp);
		}
		return result;
	}

	/**
	 * 创建文件夹
	 * 
	 * @param path
	 */
	public static File createDir(String path) {
		File dir = new File(path);
		if (!dir.exists())
			dir.mkdirs();
		return dir;
	}

	/**
	 * 创建新文件
	 * 
	 * @param path
	 * @param filename
	 * @throws IOException
	 */
	public static File createFile(String path, String filename) throws IOException {
		File file = new File(path + File.separatorChar + filename);
		if (!file.exists())
			file.createNewFile();
		return file;
	}

	/**
	 * 删除文件(目录) 删除文件
	 * 
	 * @param path
	 * @param filename
	 */
	public static void delFile(String path, String filename) {
		File file = new File(path + "/" + filename);
		if (file.exists() && file.isFile())
			file.delete();
	}

	public static File createPathAndFile(String path, String filename) throws Exception {
		createDir(path);
		File file = createFile(path, filename);
		return file;
	}

	/**
	 * 删除目录 要利用File类的delete()方法删除目录时， 必须保证该目录下没有文件或者子目录，否则删除失败，因此在实际应用中，我们要删除目录，
	 * 必须利用递归删除该目录下的所有子目录和文件，然后再删除该目录。
	 * 
	 * @param path
	 */
	public static void delDir(String path) {
		File dir = new File(path);
		if (dir.exists()) {
			File[] tmp = dir.listFiles();
			for (int i = 0; i < tmp.length; i++) {
				if (tmp[i].isDirectory()) {
					delDir(path + "/" + tmp[i].getName());
				} else {
					tmp[i].delete();
				}
			}
			dir.delete();
		}
	}
}
