package gamecore.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javolution.util.FastTable;

public class DataUtils {

	public static FastTable<Integer> string2FastTable(String str) {
		if (str == null) {
			return null;
		}
		String[] ss = str.split("\\|");
		FastTable<Integer> fastTable = new FastTable<>();
		for (int i = 0; i < ss.length; i++) {
			fastTable.add(Integer.parseInt(ss[i].trim()));
		}
		return fastTable;
	}

	public static int[] string2Array(String str) {
		if (str == null) {
			return null;
		}
		String[] ss = str.split("\\|");
		int[] fastTable = new int[ss.length];
		for (int i = 0; i < ss.length; i++) {
			fastTable[i] = Integer.parseInt(ss[i].trim());
		}
		return fastTable;
	}

	public static List<String> string2Array(String str, String split) {
		if (str == null) {
			return null;
		}
		String[] ss = str.split(split);
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < ss.length; i++) {
			if (ss[i] != null && ss[i].trim().length() > 0) {
				list.add(ss[i].trim());
			}
		}
		return list;
	}

	public static byte[] arrayConcat(byte[] bs, byte[] bs2) {
		byte[] result = Arrays.copyOf(bs, bs.length + bs2.length);
		System.arraycopy(bs2, 0, result, bs.length, bs2.length);
		return result;
	}

	public static <T> T[] concatAll(T[] first, T[]... rest) {
		int totalLength = first.length;
		for (T[] array : rest) {
			totalLength += array.length;
		}
		T[] result = Arrays.copyOf(first, totalLength);
		int offset = first.length;
		for (T[] array : rest) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		return result;
	}

	public static List<Integer> combinationIntegerArray(int min, int max, boolean shuffle) {
		List<Integer> list = new ArrayList<Integer>();
		while (max >= min) {
			list.add(min++);
		}

		if (shuffle) {
			Collections.shuffle(list);
		}

		return list;
	}

}
