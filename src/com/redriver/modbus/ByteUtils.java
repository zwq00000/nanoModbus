package com.redriver.modbus;

/**
 * 字节处理工具
 * 整形转字节数组
 * 数组转换为 int、short 等
 */
 public final class ByteUtils {
    /**
     * int到byte[] 高位在前
     * @param i
     * @return
     */
    public static byte[] toByteArray(int i) {
        //由高位到低位
        return  new byte[]{
                (byte)((i >> 24) & 0xFF),
                (byte)((i >> 16) & 0xFF),
                (byte)((i >> 8) & 0xFF),
                (byte)(i & 0xFF)};
    }

    /**
     * int到byte[] 高位在前
     * @param s
     * @return
     */
    public static byte[] toByteArray(short s) {
        return new byte[]{(byte)((s & 0xFF00)>>8),(byte)(s & 0x00FF)};
    }

    /**
     * byte[]转int
     * @param bytes
     * @return
     */
    public static int bytesToInt(byte[] bytes) {
       return bytesToInt(bytes,0);
    }

    public static int bytesToInt(byte[] bytes,int positon) {
        if (bytes == null) {
            throw new IllegalArgumentException("字节数组参数不能为空");
        }
        if (bytes.length < 2) {
            throw new IllegalArgumentException("参数 bytes 长度必须大于等于 2");
        }
        return  ((bytes[positon] & 0xff)<<24
                | ((bytes[positon+1] & 0xff)<<16)
                | ((bytes[positon+2] & 0xff)<<8)
                | ((bytes[positon+3] & 0xff))
        );
    }

    /**
     * byte[]转 short
     * @param bytes
     * @return
     */
    public static short bytesToShort(byte[] bytes)  {
        return bytesToShort(bytes,0);
    }

    /**
     * byte[]转 short
     * @param bytes
     * @param positon 开始位置
     * @return
     */
    public static short bytesToShort(byte[] bytes,int positon) {
        if(bytes == null){
            throw new IllegalArgumentException("参数 bytes 不能为空");
        }
        if(positon<0){
            throw new IllegalArgumentException("参数 position 必须大于等于 0");
        }
        if(bytes.length<2){
            throw new IllegalArgumentException("参数 bytes 长度必须大于等于 2");
        }
        if(bytes.length<(positon+2)){
            throw new IllegalArgumentException("从开始位置到结束,长度必须大于等于 2");
        }
        return (short) ((bytes[positon] & 0xff)<<8 | ((bytes[positon+1] & 0xff)));
    }

    /**
     * 根据 status 条件设置 字节中指定的 位（Bit）
     * @param bits  需要设置的字节
     * @param offset    字节中待指定的位置 [0,7]
     * @param status    true 置 1，false 置 0
     * @return
     */
    public static final byte setBit(byte bits, int offset,boolean status) {
        if(status){
            return setBit(bits,offset);
        }else {
            return clearBit(bits,offset);
        }
    }

    /**
     * * 字节中指定 Bit 位置 置 1
     * @param bits
     * @param offset
     * @return
     */
    public static final byte setBit(byte bits, int offset) {
        if (offset < 0 && offset > 7) {
            throw new IndexOutOfBoundsException("offset 必须在[0,8)范围");
        }
        bits |= 0x1 << offset;
        return bits;
    }

    private static final byte[] ClearBits = new byte[]{(byte) 0xFE, (byte) 0xFD, (byte) 0xFB,
            (byte) 0xF7,(byte) 0xEF, (byte) 0xDF ,(byte) 0xBF ,0x7F };

    /**
     * 字节中指定 Bit 位置 置 0
     * @param bits
     * @param offset
     * @return
     */
    public static final byte clearBit(byte bits, int offset) {
        if (offset < 0 && offset > 7) {
            throw new IndexOutOfBoundsException("offset 必须在[0,8)范围");
        }
        bits &= ClearBits[offset];
        return bits;
    }

    private static final char[] HEXES = new char[]{'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

    /**
     * 字节转换为 16进制 字符串
     * @param value
     * @return
     */
    public static final String toHexString(byte value) {
        char[] chars = new char[2];
        chars[0] = HEXES[(value >> 4 & 0xf)];
        chars[1] = HEXES[value & 0xf];
        return new String(chars);
    }

    /**
     * 字节数组 转换为 16进制 字符串
     * @param values    待转换的字节数组，不可为空
     * @return  转换后的字符串，没有分隔符
     */
    public static final String toHexString(byte[] values) {
        return toHexString(values,values.length);
    }

    /**
     * 字节数组 转换为 16进制 字符串
     * @param values    待转换的字节数组，不可为空
     * @return  转换后的字符串，没有分隔符
     */
    public static final String toHexString(byte[] values,int length) {
        if(values == null){
            throw new NullPointerException("values is not been null");
        }
        if(length>values.length){
            length = values.length;
        }
        char[] chars = new char[length*2];
        for (int i=0;i<length;i++) {
            chars[2*i] = HEXES[(values[i] >> 4 & 0xf)];
            chars[2*i + 1] = HEXES[values[i] & 0xf];
        }
        return new String(chars);
    }

    /**
     * 数组合并
     * @param src       合并的第一个数组
     * @param srcLen    第一数组长度
     * @param target    第二数组
     * @return          合并后的结果
     */
    public static byte[] combine(byte[] src, int srcLen, byte[] target) {
        byte[] combined = new byte[srcLen + target.length];
        System.arraycopy(src,0,combined,0,srcLen);
        System.arraycopy(target,0,combined,srcLen,target.length);
        return combined;
    }

    /**
     * 向目标数组 赋值
     * @param dst       目标数组
     * @param offset    目标数组偏移量
     * @param values    赋值内容
     * @return
     */
    public static int setBytes(byte[] dst, int offset, byte[] values){
        if(dst.length< (offset+values.length)){
            throw new ArrayIndexOutOfBoundsException("offset + values.length 超出 src数组长度");
        }
        System.arraycopy(values,0,dst,offset,values.length);
        return offset+values.length;
    }

    public static int setBytes(byte[] src,int srcPos,short value){
        return setBytes(src,srcPos,toByteArray(value));
    }


    /**
     * 数组合并
     * @param src       合并的第一个数组
     * @param target    第二数组
     * @return          合并后的结果
     */
    public static byte[] combine(byte[] src, byte[] target) {
        if(src == null){
            return target;
        }
        if(target == null){
            return src;
        }
        byte[] combined = new byte[src.length + target.length];
        System.arraycopy(src,0,combined,0,src.length);
        System.arraycopy(target,0,combined,src.length,target.length);
        return combined;
    }
}
