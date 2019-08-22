# Bluetooth
## 该库的特点

1. 内部集成了多种蓝牙芯片的操作，能兼容几乎市面上所有的蓝牙设备；
2. 支持低功耗蓝牙和传统蓝牙；
3. 支持设置低功耗蓝牙的高速传输模式；
4. 实现了蓝牙的重发机制；
5. 实现了蓝牙的同异步发送数据；
6. 实现了协议基类封装，开发者可以快速扩展自己的协议；

**注意：部分Android6.0以上的手机需要定位权限才能正常使用蓝牙功能**

## 库地址
```
implementation 'com.stag:bluetooth:1.0.2'
```

## 蓝牙基本操作相关接口说明

> **调用的类为com.stag.bluetooth.BluetoothControl.java**

### 获取单例对象

```java
BluetoothController mController = BluetoothController.getController(this);
```

### 注册蓝牙状态监听

```java
mController.registerBluetoothStateChangeListener(new OnBluetoothStateChangeListener() {
            @Override
            public void onBluetoothOpen() {
                LogUtils.i(TAG + "lpq", "onBluetoothOpen: 蓝牙打开");
            }

            @Override
            public void onBluetoothClose() {
                LogUtils.i(TAG + "lpq", "onBluetoothClose: 蓝牙关闭");
            }
        });
```

### 注册蓝牙连接状态变化监听

```java
mController.registerConnectStateChangeListener(new OnBluetoothConnectStateChangeListener() {
            @Override
            public void onBluetoothConnect(BluetoothDevice device, boolean isSuccess) {
                if (isSuccess) {
                    LogUtils.i(TAG + "lpq", "onBluetoothConnect: 蓝牙已连接");
                } else {
                    LogUtils.i(TAG + "lpq", "onBluetoothConnect: 蓝牙连接失败");
                }
            }

            @Override
            public void onBluetoothDisconnect(BluetoothDevice device) {
                LogUtils.i(TAG + "lpq", "onBluetoothDisconnect: 蓝牙已断开");
            }
        });
```

### 切换蓝牙类型

```java
mController.setBluetoothType(BluetoothType.BLE); // 低功耗蓝牙
mController.setBluetoothType(BluetoothType.TRADITION); // 传统蓝牙
```

### 搜索蓝牙

```java
mController.startScan(new OnBluetoothScanListener() {
            @Override
            public void onBluetoothScanFindDevice(BluetoothDevice device, int rssi) {
                LogUtils.i(TAG + "lpq", "onBluetoothScanFindDevice: " + device.getAddress());
            }

            @Override
            public void onBluetoothScanFinish() {
                LogUtils.i(TAG + "lpq", "onBluetoothScanFinish: ");
            }
        });
```

### 停止搜索蓝牙

```java
mController.stopScan();
```

### 连接蓝牙与断开蓝牙连接

```java
mController.connect("0C:B2:B7:3E:23:60");	//连接蓝牙，参数为蓝牙MAC地址
mController.disconnect();	//断开连接
```

### 低功耗蓝牙时开启高速模式

```java
mController.setBleHighSpeedMode(true);
```

不一定100%成功，由蓝牙设备与App设备蓝牙的最低MTU决定。

### 取消注册监听回调

```java
	@Override
    protected void onDestroy() {
        super.onDestroy();
        if (mController != null) {
            mController.unregisterBluetoothStateChangeListener();
            mController.unregisterConnectStateChangeListener();
        }
    }
```

## 蓝牙收发数据重点说明

### 方式一：继承蓝牙内置协议

```java
public abstract class Protocol<E extends Packet, T extends OnEventListener> {
    public final static int BLE_MAX_SEND_INTERVAL = 500;
    protected Context mContext;
    private T mEventListener;
    private Object mData;
    private int mMaxBleSendInterval = BLE_MAX_SEND_INTERVAL;

    /**
     * 协议所特有的主动事件监听
     */
    protected Protocol(Context context) {
        this(context, null);
    }

    /**
     * 协议所特有的主动事件监听
     */
    protected Protocol(Context context, T listener) {
        mContext = context.getApplicationContext();
        mEventListener = listener;
    }

    /**
     * 发送包处理成最终要发送的字节数据
     */
    public abstract byte[] packetToBytes(E packet);

    /**
     * 解析收到的字节处理成结果
     */
    public abstract ParseResult parse(byte[] data);

    /**
     * 获取协议类型
     * */
    public abstract int getType();

    /**
     * 是否设置了主动事件监听
     * */
    protected boolean haveSetEventListener(){
        return mEventListener!=null;
    }
	······
}
```

#### 内置协议简要说明

上述抽象类中屏蔽了部分内容，我们主要看几个重点：

1. **packetToBytes** 和 **parse**是用户层面发送数据的最终端和接收数据的最初端，您可以根据自己蓝牙协议的需要来重写方法，创建自己的协议类；
2. **getType**是用来支持App需要同时支持多个蓝牙协议的情况的；
3. **mEventListener**用于接收蓝牙设备主动上报的一些状态；

#### 设置蓝牙传输协议

> 该方法是与蓝牙设备操作相关的方法，而且必须在连接蓝牙设备之前设置

```java
mController.setProtocol(new Protocol(this, this));
```

#### 创建异步发送数据任务

```java
BluetoothTask task = new BluetoothTask(new Packet(cmd, data), new BluetoothTask.OnDataResultListener() {
            @Override
            public void onResult(boolean isTimeout, byte[] data) {
                //数据接收回调，异步时使用
            }
        });       
task.setTimeout(2000);	//设置超时时间
task.setTryCount(1);	//设置重发次数
task.send(); 	//异步发送数据
```

#### 创建同步发送数据任务

```java
BluetoothTask task = new BluetoothTask(new Packet(cmd, data));
task.setTimeout(2000);	//设置超时时间
task.setTryCount(1);	//设置重发次数
byte[] result = task.sendBySync2();	//同步发送数据
```

### 方式二：原始数据收发

如果方式一没有办法满足你的要求，那么我也提供方式二来供你选择

#### 设置原始数据收发监听

```java
    mController.registerTransmitListener(new OnBluetoothTransmitListener() {
        @Override
        public void onBluetoothSendData(byte[] data) {
            // 发送数据
        }

        @Override
        public void onBluetoothRecvData(byte[] data) {
            // 接收数据
        }
    });
```

#### 发送数据

```java
mController.sendData(new byte[]{});
```