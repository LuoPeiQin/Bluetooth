/**
 * Copyright (C), 2007-2022, 未来穿戴有限公司
 * FileName: WeiCeCode
 * Author: lpq
 * Date: 2022/3/11 16:27
 * Description: 用一句话描述下
 */
package com.luo.bluetooth.protocol;

/**
 *
 * @ProjectName: Bluetooth
 * @Package: com.luo.bluetooth.protocol
 * @ClassName: WeiCeCode
 * @Description: 用一句话描述下
 * @Author: lpq
 * @CreateDate: 2022/3/11 16:27
 */
public class WeiCeCode {
    /**
     * ExpandCode
     */
    public static final byte EXPAND_CODE_READ = 0x55;
    public static final byte EXPAND_CODE_WRITE = 0x66;
    public static final byte EXPAND_CODE_READ_RESPONSE = (byte) 0xAA;
    public static final byte EXPAND_CODE_WRITE_RESPONSE = (byte) 0x99;

    /**
     * 获取SN
     */
    public static final int GET_SN = 0x77;

}
