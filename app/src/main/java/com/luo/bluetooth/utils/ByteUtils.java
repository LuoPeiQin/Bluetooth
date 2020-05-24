package com.luo.bluetooth.utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import lombok.NonNull;

/**
 * Created by Administrator on 2016/7/25 0025.
 */
public class ByteUtils {
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        StringBuilder r = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            r.append(hexArray[(b >> 4) & 0xF]);
            r.append(hexArray[(b & 0xF)]);
        }
        return r.toString();

    }

    /**
     * 缺失部分有效数字
     * UUID型转成4字节byte 大端模式，UUID如果不足处于后补零，有效数字在前
     *
     * @param u
     * @return
     */
    public static byte[] uuidToByte5BigEndian2(UUID u) {
        long l = u.getMostSignificantBits();
        byte[] b = new byte[5];
        b[4] = (byte) ((l >> 24) & 0xf000000fL);
        b[3] = (byte) ((l >> 32) & 0xff00000000L);
        b[2] = (byte) ((l >> 40) & 0xff0000000000L);
        b[1] = (byte) ((l >> 48) & 0xff000000000000L);
        b[0] = (byte) ((l >> 56) & 0xff00000000000000L);
        return b;
    }

    /**
     * 4字节byte转化成UUID 大端模式，UUID如果不足处于后补零，有效数字在前
     *
     * @param b
     * @return
     */
    public static UUID byte5ToUuidBigEndian2(byte[] b) {
        long l = ((long) b[4] << 24) & 0xff000000L;
        l |= ((long) b[3] << 32) & 0xff00000000L;
        l |= ((long) b[2] << 40) & 0xff0000000000L;
        l |= ((long) b[1] << 48) & 0xff000000000000L;
        l |= ((long) b[0] << 56) & 0xff00000000000000L;
        return new UUID(l, 0);
    }

    /**
     * 缺失部分有效数字
     * UUID型转成4字节byte 大端模式
     *
     * @param u
     * @return
     */
    public static byte[] uuidToByte5BigEndian(UUID u) {
        long l = u.getLeastSignificantBits();
        byte[] b = new byte[5];
        b[4] = (byte) (l & 0xffL);
        b[3] = (byte) ((l >> 8) & 0xff00L);
        b[2] = (byte) ((l >> 16) & 0xff0000L);
        b[1] = (byte) ((l >> 24) & 0xff000000L);
        b[0] = (byte) ((l >> 32) & 0xff00000000L);
        return b;
    }

    /**
     * 4字节byte转化成UUID 大端模式
     *
     * @param b
     * @return
     */
    public static UUID byte5ToUuidBigEndian(byte[] b) {
        long l = b[4] & 0xffL;
        l |= (b[3] << 8) & 0xff00L;
        l |= (b[2] << 16) & 0xff0000L;
        l |= (b[1] << 24) & 0xff000000L;
        l |= (b[0] << 32) & 0xff00000000L;
        return new UUID(0, l);
    }

    /**
     * 大端模式将3个字节的Byte转成int型
     *
     * @param b
     * @return
     */
    public static long byte3ToLongBigEndian(byte[] b) {
        long i = (long) b[2] & 0xffL;
        i |= (((long) b[1] << 8) & 0xff00L);
        i |= (((long) b[1] << 16) & 0xff0000L);
        return i;
    }

    /**
     * 大端模式将int型转成3个字节的Byte
     *
     * @param i
     * @return
     */
    public static byte[] longToByte3BigEndian(long i) {
        byte[] res = new byte[3];
        res[2] = (byte) (i & 0xffL);
        res[1] = (byte) ((i >> 8) & 0xffL);
        res[0] = (byte) ((i >> 16) & 0xffL);
        return res;
    }

    /**
     * 比较两个byte数组
     *
     * @param first
     * @param second
     * @return
     */
    public static boolean compareBytes(byte[] first, byte[] second) {
        for (int i = 0; i < (first.length < second.length ? first.length : second.length); i++) {
            if (first[i] != second[i]) {
                return false;
            }
        }
        return true && first.length == second.length;
    }

    /**
     * 缺失部分有效数字
     * UUID型转成4字节byte 大端模式
     *
     * @param u
     * @return
     */
    public static byte[] uuidToByte4BigEndian(UUID u) {
        long l = u.getLeastSignificantBits();
        byte[] b = new byte[4];
        b[3] = (byte) (l & 0xffL);
        b[2] = (byte) ((l >> 8) & 0xff00L);
        b[1] = (byte) ((l >> 16) & 0xff0000L);
        b[0] = (byte) ((l >> 24) & 0xff000000L);
        return b;
    }

    /**
     * 4字节byte转化成UUID 大端模式
     *
     * @param b
     * @return
     */
    public static UUID byte4ToUuidBigEndian(byte[] b) {
        long l = (long) b[3] & 0xffL;
        l |= ((long) b[2] << 8) & 0xff00L;
        l |= ((long) b[1] << 16) & 0xff0000L;
        l |= ((long) b[0] << 24) & 0xff000000L;
        return new UUID(0, l);
    }


    /**
     * 4字节byte转化成UUID 大端模式
     *
     * @param b
     * @return
     */
    public static UUID byte4ToUuidBigEndian2(byte[] b) {
        long l = ((long) b[3] << 32) & 0xff00000000L;
        l |= ((long) b[2] << 40) & 0xff0000000000L;
        l |= ((long) b[1] << 48) & 0xff000000000000L;
        l |= ((long) b[0] << 56) & 0xff00000000000000L;
        return new UUID(l, 0);
    }

    /**
     * 缺失部分有效数字
     * long型转成4字节byte 大端模式
     *
     * @param l
     * @return
     */
    public static byte[] longToByte4BigEndian(long l) {
        byte[] b = new byte[4];
        b[3] = (byte) (l & 0xffL);
        b[2] = (byte) ((l >> 8) & 0xff00L);
        b[1] = (byte) ((l >> 16) & 0xff0000L);
        b[0] = (byte) ((l >> 24) & 0xff000000L);
        return b;
    }

    /**
     * 4字节byte转化成Long 大端模式
     *
     * @param b
     * @return
     */
    public static long byte4ToLongBigEndian(byte[] b) {
        long l = (long) b[3] & 0xffL;
        l |= ((long) b[2] << 8) & 0xff00L;
        l |= ((long) b[1] << 16) & 0xff0000L;
        l |= ((long) b[0] << 24) & 0xff000000L;
        return l;
    }

    /**
     * 16个字节转化UUID 大端模式
     *
     * @param b
     * @return
     */
    public static UUID byte16ToUuidBigEndian(byte[] b) {
        return new UUID(byte8ToLongBigEndian(subBytes(b, 0, 8)), byte8ToLongBigEndian(subBytes(b, 8, 8)));
    }

    /**
     * UUID转化成16个字节 大端模式
     *
     * @param uuid
     * @return
     */
    public static byte[] uuidToByte16BigEndian(UUID uuid) {
        long l = uuid.getLeastSignificantBits();
        long l2 = uuid.getMostSignificantBits();
        return combineByteArray(longToByte8BigEndian(l2), longToByte8BigEndian(l));
    }

    /**
     * 8个字节转化UUID 大端模式
     *
     * @param b
     * @return
     */
    public static UUID byte8ToUuidBigEndian(byte[] b) {
        return new UUID(0, byte8ToLongBigEndian(b));
    }

    /**
     * UUID转化成8个字节 大端模式
     *
     * @param uuid
     * @return
     */
    public static byte[] uuidToByte8BigEndian(UUID uuid) {
        long l = uuid.getLeastSignificantBits();
        return longToByte8BigEndian(l);
    }

    /**
     * 8个字节转化成long型 大端模式
     *
     * @param b
     * @return
     */
    public static long byte8ToLongBigEndian(byte[] b) {
        long l = (long) b[7] & 0xffL;
        l |= ((long) b[6] << 8) & 0xff00L;
        l |= ((long) b[5] << 16) & 0xff0000L;
        l |= ((long) b[4] << 24) & 0xff000000L;
        l |= ((long) b[3] << 32) & 0xff00000000L;
        l |= ((long) b[2] << 40) & 0xff0000000000L;
        l |= ((long) b[1] << 48) & 0xff000000000000L;
        l |= ((long) b[0] << 56) & 0xff00000000000000L;
        return l;
    }

    /**
     * long型转成8个字节 大端模式
     *
     * @param l
     * @return
     */
    public static byte[] longToByte8BigEndian(long l) {
        byte[] b = new byte[8];
        b[7] = (byte) (l & 0xffL);
        b[6] = (byte) ((l >> 8) & 0xffL);
        b[5] = (byte) ((l >> 16) & 0xffL);
        b[4] = (byte) ((l >> 24) & 0xffL);
        b[3] = (byte) ((l >> 32) & 0xffL);
        b[2] = (byte) ((l >> 40) & 0xffL);
        b[1] = (byte) ((l >> 48) & 0xffL);
        b[0] = (byte) ((l >> 56) & 0xffL);
        return b;
    }

    /**
     * 六个字节的byte转化成UUID，有效数字在前，于后补零
     *
     * @param b
     * @return
     */
    public static UUID byte6ToUuidBigEndian2(byte[] b) {
        long l = ((long) b[5] << 16) & 0xff0000L;
        l |= ((long) b[4] << 24) & 0xff000000L;
        l |= ((long) b[3] << 32) & 0xff00000000L;
        l |= ((long) b[2] << 40) & 0xff0000000000L;
        l |= ((long) b[1] << 48) & 0xff000000000000L;
        l |= ((long) b[0] << 56) & 0xff00000000000000L;
        return new UUID(l, 0);
    }

    /**
     * 将UUID转成六个字节的Byte，有效数字在前，于后补零
     *
     * @param uuid
     * @return
     */
    public static byte[] uuidToByte6BigEndian2(UUID uuid) {
        long l = uuid.getMostSignificantBits();
        byte[] b = new byte[6];
        b[5] = (byte) ((l >> 16) & 0xffL);
        b[4] = (byte) ((l >> 24) & 0xffL);
        b[3] = (byte) ((l >> 32) & 0xffL);
        b[2] = (byte) ((l >> 40) & 0xffL);
        b[1] = (byte) ((l >> 48) & 0xffL);
        b[0] = (byte) ((l >> 56) & 0xffL);
        return b;
    }

    /**
     * 六个字节的byte转化成UUID
     *
     * @param b
     * @return
     */
    public static UUID byte6ToUuidBigEndian(byte[] b) {
        long l = (long) b[5] & 0xffL;
        l |= ((long) b[4] << 8) & 0xff00L;
        l |= ((long) b[3] << 16) & 0xff0000L;
        l |= ((long) b[2] << 24) & 0xff000000L;
        l |= ((long) b[1] << 32) & 0xff00000000L;
        l |= ((long) b[0] << 40) & 0xff0000000000L;
        return new UUID(0, l);
    }

    /**
     * 将UUID转成六个字节的Byte
     *
     * @param uuid
     * @return
     */
    public static byte[] uuidToByte6BigEndian(UUID uuid) {
        long l = uuid.getLeastSignificantBits();
        byte[] b = new byte[6];
        b[5] = (byte) (l & 0xffL);
        b[4] = (byte) ((l >> 8) & 0xffL);
        b[3] = (byte) ((l >> 16) & 0xffL);
        b[2] = (byte) ((l >> 24) & 0xffL);
        b[1] = (byte) ((l >> 32) & 0xffL);
        b[0] = (byte) ((l >> 40) & 0xffL);
        return b;
    }

    /**
     * 大端模式将4个字节的Byte转成int型
     *
     * @param b
     * @return
     */
    public static int byte4ToIntBigEndian(byte[] b) {
        int i = b[3] & 0xff;
        i |= ((b[2] << 8) & 0xff00);
        i |= ((b[1] << 16) & 0xff0000);
        i |= ((b[0] << 24) & 0xff000000);
        return i;
    }

    /**
     * 大端模式将int型转成4个字节的Byte
     *
     * @param i
     * @return
     */
    public static byte[] intToByte4BigEndian(int i) {
        byte[] res = new byte[4];
        res[3] = (byte) (i & 0xff);
        res[2] = (byte) ((i >> 8) & 0xff);
        res[1] = (byte) ((i >> 16) & 0xff);
        res[0] = (byte) ((i >> 24) & 0xff);
        return res;
    }

    /**
     * 大端模式将3个字节的Byte转成int型
     *
     * @param b
     * @return
     */
    public static int byte3ToIntBigEndian(byte[] b) {
        int i = b[2] & 0xff;
        i |= ((b[1] << 8) & 0xff00);
        i |= ((b[1] << 16) & 0xff0000);
        return i;
    }

    /**
     * 大端模式将int型转成3个字节的Byte
     *
     * @param i
     * @return
     */
    public static byte[] intToByte3BigEndian(int i) {
        byte[] res = new byte[3];
        res[2] = (byte) (i & 0xff);
        res[1] = (byte) ((i >> 8) & 0xff);
        res[0] = (byte) ((i >> 16) & 0xff);
        return res;
    }

    public static String toString(byte[] data) {
        return toString(data, " ");
    }

    public static String toString(byte[] data, String interval) {
        if (data == null) {
            return null;
        }
        String iStr = interval == null ? "" : interval;
        StringBuilder buffer = new StringBuilder();
        for (byte b : data) {
            String d = Integer.toHexString(b & 0xff);
            if (d.length() > 1) {
                buffer.append(d).append(iStr);
            } else {
                buffer.append("0").append(d).append(iStr);
            }
        }
        return buffer.toString();
    }

    public static byte[] subBytes(byte[] data, int start, int len) {
        byte[] res = new byte[len];
        for (int i = 0; i < len; i++) {
            res[i] = data[start + i];
        }
        return res;
    }

    public static byte[] subBytes(byte[] data, int start) {
        int len = data.length - start;
        return subBytes(data, start, len);
    }

    /**
     * 将字节数组转化成Long
     *
     * @param b
     * @return
     */
    public static long bytesToLong(byte[] b) {
        long l = ((long) b[0] << 56) & 0xFF00000000000000L;
        // 如果不强制转换为long，那么默认会当作int，导致最高32位丢失
        l |= ((long) b[1] << 48) & 0xFF000000000000L;
        l |= ((long) b[2] << 40) & 0xFF0000000000L;
        l |= ((long) b[3] << 32) & 0xFF00000000L;
        l |= ((long) b[4] << 24) & 0xFF000000L;
        l |= ((long) b[5] << 16) & 0xFF0000L;
        l |= ((long) b[6] << 8) & 0xFF00L;
        l |= b[7] & 0xFFL;
        return l;
    }

    /**
     * 将Long转化成字节数组
     *
     * @param l
     * @return
     */
    public static byte[] longToBytes(long l) {
        byte[] b = new byte[8];
        b[0] = (byte) (l >>> 56);
        b[1] = (byte) (l >>> 48);
        b[2] = (byte) (l >>> 40);
        b[3] = (byte) (l >>> 32);
        b[4] = (byte) (l >>> 24);
        b[5] = (byte) (l >>> 16);
        b[6] = (byte) (l >>> 8);
        b[7] = (byte) (l);
        return b;
    }

    /**
     * 合并Byte数组
     *
     * @param bytes
     * @return
     */
    public static byte[] combineByteArray(byte[]... bytes) {
        int len = 0;
        for (byte[] bs : bytes) {
            if (bs != null) {
                len += bs.length;
            }
        }
        byte[] res = new byte[len];
        int pos = 0;
        for (byte[] bs : bytes) {
            if (bs != null) {
                for (byte b : bs) {
                    res[pos++] = b;
                }
            }
        }
        return res;
    }

    /**
     * 将UUID转化成Byte数组
     *
     * @param uuid
     * @return
     */
    public static byte[] uuidToBytes(UUID uuid) {
        return combineByteArray(longToBytes(uuid.getMostSignificantBits()), longToBytes(uuid.getLeastSignificantBits()));
    }

    /**
     * 将Byte数组转化成UUID
     *
     * @param data
     * @return
     */
    public static UUID bytesToUuid(byte[] data) {
        if (data == null || data.length < 16)
            return null;
        byte[] bMost = subBytes(data, 0, 8);
        byte[] bLeast = subBytes(data, 8, 8);
        return new UUID(bytesToLong(bMost), bytesToLong(bLeast));
    }

    /**
     * 将Int转化为Byte数组
     *
     * @param i
     * @return
     */
    public static byte[] intToBytes2(int i) {
        byte[] res = new byte[2];
        res[1] = (byte) (i & 0xFF);
        res[0] = (byte) (i >> 8 & 0xFF);
        return res;
    }

    public static byte[] intToBytes2SmallDian(int i) {
        byte[] res = new byte[2];
        res[0] = (byte) (i & 0xFF);
        res[1] = (byte) (i >> 8 & 0xFF);
        return res;
    }

    /**
     * 将Byte数组转化为int,取第一二个字节
     *
     * @param b
     * @return
     */
    public static int bytes2ToInt(byte[] b) {
        int c = (int) ((b[0] << 8) & 0xFF00L);
        c |= (int) (b[1] & 0xFFL);
        return c;
    }

    /**
     * 将Byte数组转化为int,取第一二个字节,小端模式
     *
     * @param b
     * @return
     */
    public static int bytesS2ToInt(byte[] b) {
        int c = (int) ((b[1] << 8) & 0xFF00L);
        c |= (int) (b[0] & 0xFFL);
        return c;
    }

    /**
     * 将Byte数组转化为int,取第一三个字节
     *
     * @param b
     * @return
     */
    public static int bytes2ToInt2(byte[] b) {
        int c = (int) ((b[1] << 16) & 0xFFFFFF00L);
        c |= (int) (b[0]);
        return c;
    }

    /**
     * 将Byte数组转化为int
     *
     * @param b
     * @return
     */
    public static int bytesToIntBigEndian(byte[] b) {
        int res = (int) ((b[0] << 24) & 0xFF000000L);
        res |= (b[1] << 16 & 0xFF0000L);
        res |= (b[2] << 8 & 0xFF00L);
        res |= (b[3] & 0xFFL);
        return res;
    }

    /**
     * 将Byte数组转化为int
     *
     * @param b
     * @return
     */
    public static int bytesToIntSmallEndian(byte[] b) {
        int res = (int) ((b[3] << 24) & 0xFF000000L);
        res |= (b[2] << 16 & 0xFF0000L);
        res |= (b[1] << 8 & 0xFF00L);
        res |= (b[0] & 0xFFL);
        return res;
    }

    /**
     * 将Int转化成Byte数组
     *
     * @param i
     * @return
     */
    public static byte[] intToBytesBigEndian(int i) {
        byte[] res = new byte[4];
        res[0] = (byte) ((i >> 24) & 0xFFL);
        res[1] = (byte) ((i >> 16) & 0xFFL);
        res[2] = (byte) ((i >> 8) & 0xFFL);
        res[3] = (byte) (i & 0xFFL);
        return res;
    }

    /**
     * 将Byte数组转化为int
     *
     * @param b
     * @return
     */
    public static int bytesToInt(byte[] b) {
        int res = (int) ((b[3] << 24) & 0xFF000000L);
        res |= (b[2] << 16 & 0xFF0000L);
        res |= (b[1] << 8 & 0xFF00L);
        res |= (b[0] & 0xFFL);
        return res;
    }

    /**
     * 将Int转化成Byte数组
     *
     * @param i
     * @return
     */
    public static byte[] intToBytes(int i) {
        byte[] res = new byte[4];
        res[3] = (byte) ((i >> 24) & 0xFFL);
        res[2] = (byte) ((i >> 16) & 0xFFL);
        res[1] = (byte) ((i >> 8) & 0xFFL);
        res[0] = (byte) (i & 0xFFL);
        return res;
    }

    /**
     * 将Byte数组转化成short
     *
     * @param b
     * @return
     */
    public static short bytesToShort(byte[] b) {
        short res = (short) ((b[1] << 8) & 0xFF00l);
        res |= (b[0] & 0xFFl);
        return res;
    }

    /**
     * 将Short转化为Byte数组
     *
     * @param i
     * @return
     */
    public static byte[] shortToBytes(short i) {
        byte[] res = new byte[2];
        res[1] = (byte) ((i >> 8) & 0xFFL);
        res[0] = (byte) (i & 0xFFL);
        return res;
    }

    /**
     * 大端序
     * 将Byte数组转化成short
     *
     * @param b
     * @return
     */
    public static short bytesToShortBigEndian(byte[] b) {
        short res = (short) ((b[0] << 8) & 0xFF00L);
        res |= (b[1] & 0xFFL);
        return res;
    }

    /**
     * 大端序
     * 将Short转化为Byte数组
     *
     * @param i
     * @return
     */
    public static byte[] shortToBytesBigEndian(short i) {
        byte[] res = new byte[2];
        res[0] = (byte) ((i >> 8) & 0xFFL);
        res[1] = (byte) (i & 0xFFL);
        return res;
    }

    /**
     * 填充数据至长度为N的数组
     *
     * @param data
     * @param len
     * @return
     */
    public static byte[] wrapData(byte[] data, int len) {
        byte[] res = new byte[len];
        for (int i = 0; i < len; i++) {
            if (i < data.length) {
                res[i] = data[i];
            } else {
                res[i] = 0;
            }
        }
        return res;
    }

    /**
     * 取出一字节中每一位的数值
     *
     * @param data
     * @return
     */
    public static byte[] getByteBits(byte data) {
        byte[] result = new byte[8];
        for (int i = 0; i < 8; i++) {
            result[i] = (byte) (data >> i & 0x01);
        }
        return result;
    }

    public static byte[] getBytesWithBuffer(ByteBuffer buffer) {
        if (buffer == null)
            return null;
        byte[] bytes = new byte[buffer.position()];
        for (int i = 0; i < buffer.position(); i++)
            bytes[i] = buffer.array()[i];
        return bytes;
    }

    public static boolean equal(byte[] data1, byte[] data2) {
        return compare(data1, data2) == 0;
    }

    public static int compare(byte[] bytes1, byte[] bytes2) {
        if (bytes1.length > bytes2.length) {
            return 1;
        }
        if (bytes1.length < bytes2.length) {
            return -1;
        }

        for (int i = 0; i < bytes1.length; i++) {
            int result = (bytes1[i] & 0xFF) - (bytes2[i] & 0xFF);
            if (result > 0) {
                return 1;
            }
            if (result < 0) {
                return -1;
            }
        }
        return 0;

    }

    public static byte getXor(byte[] data) {
        return getXor(data, 0, data.length);
    }

    public static byte getXor(byte[] data, int start, int len) {
        byte xor = 0;
        for (int i = start; i < len + start; i++)
            xor ^= data[i];
        return xor;
    }

    /**
     * bytes 转Ascii码字符串
     */
    public static String bytesToAscii(byte[] bytes, int offset, int dateLen) {
        if ((bytes == null) || (bytes.length == 0) || (offset < 0) || (dateLen <= 0)) {
            return null;
        }
        if ((offset >= bytes.length) || (bytes.length - offset < dateLen)) {
            return null;
        }

        String asciiStr = null;
        byte[] data = new byte[dateLen];
        System.arraycopy(bytes, offset, data, 0, dateLen);
        asciiStr = new String(data, StandardCharsets.US_ASCII);
        return asciiStr;
    }

    public static String bytesToAscii(byte[] bytes, int dateLen) {
        return bytesToAscii(bytes, 0, dateLen);
    }

    public static String bytesToAscii(byte[] bytes) {
        return bytesToAscii(bytes, 0, bytes.length);
    }

    public static byte[] asciiToBytes(String string) {
        if (string == null) {
            return null;
        }
        return string.getBytes(StandardCharsets.US_ASCII);
    }

    /**
     * 16 进制字符串转bytes数据
     */
    public static byte[] hexStringToBytes(@NonNull String hexString) {
        byte[] result = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length() / 2; ++i) {
            String temp = hexString.substring(2 * i, 2 * (i + 1));
            result[i] = (byte) Integer.parseInt(temp, 16);
        }
        return result;
    }

    public static String addSpaceToString(String origin, int strLength) {
        if (origin == null) {
            origin = "";
        }
        String result = origin;
        if (origin.length() < strLength) {
            StringBuilder sb = new StringBuilder();
            sb.append(origin);// 左补0
            while (sb.length() < strLength) {
                sb.append(" ");
            }
            result = sb.toString();
        }
        return result;
    }
}
