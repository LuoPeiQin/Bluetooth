

/*
 * Copyright (c) 2019. stag All rights reserved.
 */

package com.luo.bluetooth.protocol;

import android.util.Log;

import com.stag.bluetooth.BluetoothTask;
import com.stag.bluetooth.util.ByteUtils;

import java.nio.ByteBuffer;

public class WeiCeDeviceOperate {
    private static final String TAG = "DncpDeviceOperate";
    public static int ERASE_TIMEOUT = 3000;
    public static int ERASE_TRY_COUNT = 2;

    public static void getSn(final OnTimeoutResult<String> obtainResult, boolean callbackInMainThread) {
        BluetoothTask task = new BluetoothTask(new CustomPacket(WeiCeCode.GET_SN, WeiCeCode.EXPAND_CODE_READ),
                callbackInMainThread,
                new BluetoothTask.OnDataResultListener() {
                    @Override
                    public void onResult(boolean isTimeout, byte[] data) {
                        Log.i("lpq", "onResult: data = " + ByteUtils.toString(data));
                        String sn = null;
                        if (!isTimeout && data != null) {
                            ByteBuffer byteBuffer = ByteBuffer.wrap(data);
                            sn = new String(byteBuffer.array()).replaceAll("[^\u4E00-\u9FA5a-zA-Z0-9-_]+", "");
                        }
                        Log.i("lpq", "onResult: sn = " + sn);
                        obtainResult.onResult(isTimeout, sn);
                    }
                });
        task.setTimeout(ERASE_TIMEOUT);
        task.send();
    }


//    public String getTypeBySync() {
//        byte[] data = new BluetoothTask(new DncpPacket(DeviceInfoCode.DSCP_CMD_DII_GET_TYPE, address)).sendBySync2();
//        String type = null;
//        if (data != null) {
//            ByteBuffer byteBuffer = ByteBuffer.wrap(data);
//            type = new String(byteBuffer.array()).replaceAll("[^\u4E00-\u9FA5a-zA-Z0-9-_]+", "");
//        }
//        return type;
//    }


//    public void setType(String type, final OnTimeoutResult<Boolean> obtainResult, boolean callbackInMainThread) {
//        new BluetoothTask(new DncpPacket(DeviceInfoCode.DSCP_CMD_DII_SET_TYPE,
//                ByteUtils.wrapData(type.getBytes(), 16),
//                address),
//                callbackInMainThread,
//                obtainResult == null ? null : new BluetoothTask.OnDataResultListener() {
//                    @Override
//                    public void onResult(boolean isTimeout, byte[] data) {
//                        Boolean res = Boolean.FALSE;
//                        if (!isTimeout && data != null) {
//                            res = ByteUtils.bytesToShort(data) == DSCPCode.DSCP_OK;
//                        }
//                        obtainResult.onResult(isTimeout, res);
//                    }
//                }).send();
//    }
//
//
//    public void getModelNumber(@NonNull final OnTimeoutResult<String> obtainResult, boolean callbackInMainThread) {
//        new BluetoothTask(new DncpPacket(DeviceInfoCode.DSCP_CMD_DII_GET_MODEL, address),
//                callbackInMainThread,
//                new BluetoothTask.OnDataResultListener() {
//                    @Override
//                    public void onResult(boolean isTimeout, byte[] data) {
//                        String modelNumber = null;
//                        if (!isTimeout && data != null) {
//                            ByteBuffer byteBuffer = ByteBuffer.wrap(data);
//                            modelNumber = new String(byteBuffer.array()).replaceAll("[^\u4E00-\u9FA5a-zA-Z0-9-_]+", "");
//                        }
//                        obtainResult.onResult(isTimeout, modelNumber);
//                    }
//                }).send();
//    }
//
//
//    public void setModelNumber(String model, final OnTimeoutResult<Boolean> obtainResult, boolean callbackInMainThread) {
//        new BluetoothTask(new DncpPacket(DeviceInfoCode.DSCP_CMD_DII_SET_MODEL,
//                ByteUtils.wrapData(model.getBytes(), 16),
//                address),
//                callbackInMainThread,
//                obtainResult == null ? null : new BluetoothTask.OnDataResultListener() {
//                    @Override
//                    public void onResult(boolean isTimeout, byte[] data) {
//                        Boolean res = Boolean.FALSE;
//                        if (!isTimeout && data != null) {
//                            res = ByteUtils.bytesToShort(data) == DSCPCode.DSCP_OK;
//                        }
//                        obtainResult.onResult(isTimeout, res);
//                    }
//                }).send();
//    }
//
//
//    public void getManufacturer(@NonNull final OnTimeoutResult<String> obtainResult, boolean callbackInMainThread) {
//        new BluetoothTask(new DncpPacket(DeviceInfoCode.DSCP_CMD_DII_GET_MANUFACTURER, address),
//                callbackInMainThread,
//                new BluetoothTask.OnDataResultListener() {
//                    @Override
//                    public void onResult(boolean isTimeout, byte[] data) {
//                        String manufacturer = null;
//                        if (!isTimeout && data != null) {
//                            ByteBuffer byteBuffer = ByteBuffer.wrap(data);
//                            manufacturer = new String(byteBuffer.array()).replace("\0", "");
//                            //manufacturer = new String(byteBuffer.array()).replaceAll("[^\u4E00-\u9FA5a-zA-Z0-9-_]+", "");
//                        }
//                        obtainResult.onResult(isTimeout, manufacturer);
//                    }
//                }).send();
//    }
//
//
//    public void setManufacturer(String manufacturer, final OnTimeoutResult<Boolean> obtainResult, boolean callbackInMainThread) {
//        new BluetoothTask(new DncpPacket(DeviceInfoCode.DSCP_CMD_DII_SET_MANUFACTURER,
//                ByteUtils.wrapData(manufacturer.getBytes(), 20),
//                address),
//                callbackInMainThread,
//                obtainResult == null ? null : new BluetoothTask.OnDataResultListener() {
//                    @Override
//                    public void onResult(boolean isTimeout, byte[] data) {
//                        Boolean res = Boolean.FALSE;
//                        if (!isTimeout && data != null) {
//                            res = ByteUtils.bytesToShort(data) == DSCPCode.DSCP_OK;
//                        }
//                        obtainResult.onResult(isTimeout, res);
//                    }
//                }).send();
//    }
//
//
//    public void getHardWareVersion(@NonNull final OnTimeoutResult<Version> obtainResult, boolean callbackInMainThread) {
//        new BluetoothTask(new DncpPacket(DeviceInfoCode.DSCP_CMD_DII_GET_HARDWARE_VERSION, address),
//                callbackInMainThread,
//                new BluetoothTask.OnDataResultListener() {
//                    @Override
//                    public void onResult(boolean isTimeout, byte[] data) {
////                        Log.e("软件/硬件版本号", "硬件版本号：" + (data == null ? "是个空" : StringUtils.byteArrayToHexString(data)));
//
//                        Version version = null;
//                        if (!isTimeout && data != null) {
//                            version = Version.getVersion(data);
//                        }
//                        obtainResult.onResult(isTimeout, version);
//                    }
//                }).send();
//    }
//
//
//    public void getSoftWareVersion(@NonNull final OnTimeoutResult<Version> obtainResult, boolean callbackInMainThread) {
//        new BluetoothTask(new DncpPacket(DeviceInfoCode.DSCP_CMD_DII_GET_SOFT_VERSION, address),
//                callbackInMainThread,
//                new BluetoothTask.OnDataResultListener() {
//                    @Override
//                    public void onResult(boolean isTimeout, byte[] data) {
//                        Version version = null;
//                        if (!isTimeout && data != null) {
//                            version = Version.getVersion(data);
//                        }
//                        obtainResult.onResult(isTimeout, version);
//                    }
//                }).send();
//    }
//
//
//    public Version getSoftWareVersionBySync() {
//        BluetoothTask task = new BluetoothTask(new DncpPacket(DeviceInfoCode.DSCP_CMD_DII_GET_SOFT_VERSION, address));
//        task.setTimeout(1500);
//        task.setTryCount(2);
//        byte[] data = task.sendBySync2();
//        Version version = null;
//        if (data != null) {
//            version = Version.getVersion(data);
//        }
//        return version;
//    }
//
//
//    public void getSerialNumber(@NonNull final OnTimeoutResult<String> obtainResult, boolean callbackInMainThread) {
//        new BluetoothTask(new DncpPacket(DeviceInfoCode.DSCP_CMD_DII_GET_SN, address),
//                callbackInMainThread,
//                new BluetoothTask.OnDataResultListener() {
//                    @Override
//                    public void onResult(boolean isTimeout, byte[] data) {
//                        String serialNumber = null;
//                        if (!isTimeout && data != null) {
//                            ByteBuffer byteBuffer = ByteBuffer.wrap(data);
//                            serialNumber = new String(byteBuffer.array()).replaceAll("[^\u4E00-\u9FA5a-zA-Z0-9-_]+", "");
//                        }
//                        obtainResult.onResult(isTimeout, serialNumber);
//                    }
//                }).send();
//    }
//
//
//    public void getRunMode(@NonNull final OnTimeoutResult<DeviceRunMode> obtainResult, boolean callbackInMainThread) {
//        new BluetoothTask(new DncpPacket(DeviceUpdateCode.DSCP_CMD_DUI_GET_RUN_MODE, address),
//                callbackInMainThread,
//                new BluetoothTask.OnDataResultListener() {
//                    @Override
//                    public void onResult(boolean isTimeout, byte[] data) {
//                        DeviceRunMode mode = null;
//                        if (!isTimeout && data != null) {
//                            mode = DeviceRunMode.getDeviceRunMode(data[0]);
//                        }
//                        obtainResult.onResult(isTimeout, mode);
//                    }
//                }).send();
//    }
//
//
//    public DeviceRunMode getRunModeBySync() {
//        byte[] data = new BluetoothTask(new DncpPacket(DeviceUpdateCode.DSCP_CMD_DUI_GET_RUN_MODE, address))
//                .setTimeout(500)
//                .sendBySync2();
//        DeviceRunMode mode = null;
//        if (data != null) {
//            mode = DeviceRunMode.getDeviceRunMode(data[0]);
//        }
//        return mode;
//    }
//
//
//    public void getVersion(@NonNull final OnTimeoutResult<Version> obtainResult, boolean callbackInMainThread) {
//        new BluetoothTask(new DncpPacket(DeviceUpdateCode.DSCP_CMD_DUI_GET_VERSION, address),
//                callbackInMainThread,
//                new BluetoothTask.OnDataResultListener() {
//                    @Override
//                    public void onResult(boolean isTimeout, byte[] data) {
//                        Version version = null;
//                        if (!isTimeout && data != null) {
//                            version = Version.getVersion(data);
//                        }
//                        obtainResult.onResult(isTimeout, version);
//                    }
//                }).send();
//    }
//
//
//    public void getMaxFragmentSize(@NonNull final OnTimeoutResult<Short> obtainResult, boolean callbackInMainThread) {
//        new BluetoothTask(new DncpPacket(DeviceUpdateCode.DSCP_CMD_DUI_GET_MAX_FRAGMENT_SIZE, address),
//                callbackInMainThread,
//                new BluetoothTask.OnDataResultListener() {
//                    @Override
//                    public void onResult(boolean isTimeout, byte[] data) {
//                        Short res = null;
//                        if (!isTimeout && data != null) {
//                            res = Short.valueOf(ByteUtils.bytesToShort(data));
//                        }
//                        obtainResult.onResult(isTimeout, res);
//                    }
//                }).send();
//    }
//
//
//    public short getMaxFragmentSizeBySync() {
//        byte[] data = new BluetoothTask(new DncpPacket(DeviceUpdateCode.DSCP_CMD_DUI_GET_MAX_FRAGMENT_SIZE, address)).sendBySync2();
//        short res = 0;
//        if (data != null) {
//            res = ByteUtils.bytesToShort(data);
//        }
//        return res;
//    }
//
//
//    public void enterUpdater() {
//        new BluetoothTask(new DncpPacket(DeviceUpdateCode.DSCP_CMD_DUI_ENTER_UPDATER, address)).send();
//    }
//
//
//    public void enterApp() {
//        new BluetoothTask(new DncpPacket(DeviceUpdateCode.DSCP_CMD_DUI_ENTER_APPLICATION, address)).send();
//    }
//
//
//    public void erase(int address, int size, final OnTimeoutResult<Boolean> obtainResult, boolean callbackInMainThread) {
//        byte[] data = ByteUtils.wrapData(ByteUtils.combineByteArray(ByteUtils.intToBytes(address), ByteUtils.intToBytes(size)), 8);
//        new BluetoothTask(new DncpPacket(DeviceUpdateCode.DSCP_CMD_DUI_ERASE, data, this.address),
//                callbackInMainThread,
//                obtainResult == null ? null : new BluetoothTask.OnDataResultListener() {
//                    @Override
//                    public void onResult(boolean isTimeout, byte[] data) {
//                        Boolean res = Boolean.FALSE;
//                        if (!isTimeout && data != null) {
//                            res = ByteUtils.bytesToShort(data) == DSCPCode.DSCP_OK;
//                        }
//                        obtainResult.onResult(isTimeout, res);
//                    }
//                }).setTimeout(ERASE_TIMEOUT).setTryCount(ERASE_TRY_COUNT).send();
//    }
//
//
//    public void writeProgram(int address, int seq, int length, byte[] data,
//                             final OnTimeoutResult<WriteProgramResult> obtainResult, boolean callbackInMainThread) {
//        byte[] temp = ByteUtils.wrapData(ByteUtils.combineByteArray(ByteUtils.intToBytes(address),
//                ByteUtils.shortToBytes((short) (length & 0xffff)), ByteUtils.shortToBytes((short) (seq & 0xffff)), data), length + 8);
//        BluetoothTask task = new BluetoothTask(new DncpPacket(DeviceUpdateCode.DSCP_CMD_DUI_WRITE_PROGRAM, temp, this.address),
//                callbackInMainThread,
//                obtainResult == null ? null : new BluetoothTask.OnDataResultListener() {
//                    @Override
//                    public void onResult(boolean isTimeout, byte[] data) {
//                        WriteProgramResult res = null;
//                        if (!isTimeout && data != null) {
//                            res = WriteProgramResult.getWriteProgramResult(data);
//                        }
//                        obtainResult.onResult(isTimeout, res);
//                    }
//                });
//        task.setTimeout(2000);
//        task.setTryCount(1);
//        task.send();
//    }
//
//    public WriteProgramResult writeProgramBySync(int address, int seq, int length, byte[] data) {
//        byte[] temp = ByteUtils.wrapData(ByteUtils.combineByteArray(ByteUtils.intToBytes(address),
//                ByteUtils.shortToBytes((short) (length & 0xffff)), ByteUtils.shortToBytes((short) (seq & 0xffff)), data), length + 8);
//        BluetoothTask task = new BluetoothTask(new DncpPacket(DeviceUpdateCode.DSCP_CMD_DUI_WRITE_PROGRAM, temp, this.address));
//        task.setTimeout(2000);
//        task.setTryCount(1);
//        byte[] d = task.sendBySync2();
//        WriteProgramResult res = null;
//        if (d != null) {
//            res = WriteProgramResult.getWriteProgramResult(d);
//        }
//        return res;
//    }
//
//
//    public void checkIntegrity(int checksum, @NonNull final OnTimeoutResult<Boolean> obtainResult, boolean callbackInMainThread) {
//        new BluetoothTask(new DncpPacket(DeviceUpdateCode.DSCP_CMD_DUI_CHECK_INTEGRITY, ByteUtils.intToBytes2SmallDian(checksum), address),
//                callbackInMainThread,
//                new BluetoothTask.OnDataResultListener() {
//                    @Override
//                    public void onResult(boolean isTimeout, byte[] data) {
//                        Boolean res = Boolean.FALSE;
//                        if (!isTimeout && data != null) {
//                            res = ByteUtils.bytes2ToInt2(data) == DSCPCode.DSCP_OK;
//                        }
//                        obtainResult.onResult(isTimeout, res);
//                    }
//                }).send();
//    }

}
