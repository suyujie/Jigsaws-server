package gamecore.io;


/**
 * 这个类往GameOutput中写入，或者从GameInput里读出一个正整数n（0 ~ 268435455）。
 * 
 * 如果n位于区间 [   0 , 2 ^7-1] 则占用一个字节
 * 如果n位于区间 [2^ 7 , 2^14-1] 则占用两个字节
 * 如果n位于区间 [2^14 , 2^21-1] 则占用三个字节
 * 如果n位于区间 [2^21 , 2^28-1] 则占用四个字节
 * 
 */
public class SmartUInt {
    
    /**
     * 往GameOutput中写入一个SmartUInt，可能会写入1到4个字节。
     * @param in
     * @param n 
     */
    public static void put(GameOutput out, int n) {
        if (n < 0 || n > 268435455) {
            throw new IllegalArgumentException("n < 0 || n >= 268435456 :" + n);
        }
        
        while (n > 127) {
            out.put((byte) (n | 0x80));
            n = n >> 7;
        }
        out.put((byte) (n & 0x7F));
    }
    
    /**
     * 从GameInput中读出一个SmartUInt。
     * @param out
     * @return 
     */
    public static int get(GameInput in) {
        int n = 0;
        
        for (int b = in.get(), i = 0; true; b = in.get(), i++) {
            n = n | ((b & 0x7F) << (7 * i));
            if (b >> 7 == 0) {
                break;
            }
        }
        
        return n;
    }
    
}
