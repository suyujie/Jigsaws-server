package gamecore.util;

import org.apache.commons.codec.binary.Hex;
import org.apache.xmlbeans.impl.util.Base64;

/**
 * 字符串操作工具类。
 */
public class StringUtil {
    
    public static String toHexString(byte[] data) {
        return Hex.encodeHexString(data);
    }
    
    public static byte[] decodeHexString(String str) {
        try {
            return Hex.decodeHex(str.toCharArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    
    public static String toBase64String(byte[] data) {
        return new String(Base64.encode(data));
    }
    
    public static byte[] decodeBase64String(String str) {
        return Base64.decode(str.getBytes());
    }
    
    /**
     * 将字节数组转换成容易阅读的字符串。
     */
    public static String toDebugString(byte[] data) {
        StringBuilder buf = new StringBuilder("\n");
        buf.append("          0  1  2  3  4  5  6  7  8  9  A  B  C  D  E  F              ASCII\n");
        buf.append("---------------------------------------------------------------------------\n");
        
        StringBuilder subBuf = new StringBuilder();
        int lineNum = 0;
        for (int i = 0; i < data.length; i++) {
            if (i % 16 == 0) {
                // 打印16进制行号
                buf.append(String.format("%06X | ", lineNum));
                lineNum += 16;
            }
            
            // 打印16进制数字
            byte b = data[i];
            buf.append(String.format("%02X", 0xff & b)).append(' ');
            
            // 打印ASCII字符
            char c = (char) b;
            if (c >= 32 && c < 127) {
                subBuf.append(c); // 可打印字符
            } else {
                subBuf.append('.'); // 不可打印字符
            }
            
            // 每行打印16个字节
            if ((i + 1) % 16 == 0 || i == data.length - 1) {
                // 最后一行可能不足16个字节，补齐
                for (int j = subBuf.length(); j < 16; j++) {
                    buf.append("   ");
                }
                
                buf.append("| ").append(subBuf);
                subBuf.setLength(0);
                buf.append('\n');
            }
        }
        
        buf.append("---------------------------------------------------------------------------");
        return buf.toString();
    }
    
}
