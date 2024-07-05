# 麦瑞克设备连接文档

## 使用须知
- 此SDK只适配部分设备，需要适配更多设备，请联系麦瑞克商务。
- 此SDK只提供搜索，连接，运动数据回调等设备的功能，设备产生的运动数据请自行处理。
- 所有设备类型只允许同时连接一个设备。如跑步机已经连接了一台，再连接跑步机时需要先断开之前所连接的跑步机。
- 在使用SDK的搜索，连接设备前，请先确保所需权限以及蓝牙开关是否打开，设备是否唤醒且未被他人连接。

## 引入&配置SDK

麦瑞克为开发者提供了两种引入SDK方式：自动集成和手动集成。自动集成是指Android的maven依赖，手动集成是指在下载SDK后导入App工程。

#### maven自动集成（推荐）

* 在项目根目录`build.gradle`文件的`allprojects.repositories`块中，添加maven库地址配置。
```
 allprojects {
   repositories {
     maven {
       url 'https://maven.aliyun.com/repository/public'
     }
      maven {
             credentials {
                 username '66505af78138868a60f57f91'
                 password '31xhdA(s2-kl'
             }
             url 'https://packages.aliyun.com/maven/repository/2160068-snapshot-25MJti'
         }
       maven {
             credentials {
                 username '66505af78138868a60f57f91'
                 password '31xhdA(s2-kl'
             }
             url 'https://packages.aliyun.com/maven/repository/2160068-release-Q0kcTH'
         }
   }
 }
```
*   在`app/build.gradle`文件的`dependencies`块中添加SDK依赖。
```
dependencies {
      implementation 'com.mrk.device.small:mrkDeviceSmall:0.0.15'
}
```


#### 手动集成

*   [下载此sdk](https://gitee.com/williamOsChina/mrk-device-demo/blob/main/app/libs/)，在Android Studio的项目工程libs目录中拷入相关组件aar包，右键Android Studio的项目工程 —> 选择Open Module Settings —> 在 Project Structure弹出框中 —> 选择 Dependencies选项卡 —> 点击左下方“＋” —> 选择组件包类型 —> 引入相应的包。
    
```
repositories{
    flatDir{
        dirs 'libs'
}}

dependencies {
    implementation fileTree(include:['*.jar'], dir:'libs')
    implementation (name:'MrkDevice_x.x.x', ext:'aar')
    implementation 'com.android.support:appcompat-v7:23.1.0'
    implementation "org.jetbrains.kotlin:kotlin-reflect:1.3.21"
    implementation "org.jetbrains.kotlin:kotlin-stdlib:1.3.21"
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.1.0"
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.1.0"
    implementation 'com.alibaba:fastjson:1.2.83'
}
```


## 使用说明
#### 权限声明

使用过程中需要您打开蓝牙，打开定位开关，需确保开启和授权必要的权限， 精准定位权限和附近的设备权限 。可以查看官方蓝牙权限文档，文档地址：[Google开发者网站关于Bluetooth permissions说明](https://developer.android.com/guide/topics/connectivity/bluetooth/permissions)。

权限说明:
- Android 6 至 Android 11 需要动态获取 ACCESS\_FINE\_LOCATION 和 ACCESS\_COARSE\_LOCATION 权限。 
- Android 10 至 Android 11 蓝牙扫描需要开启 定位开关。 
- Android 12 及以上 需要动态获取 BLUETOOTH\_CONNECT 和BLUETOOTH\_SCAN 权限。
为了搜索与连接设备。需要向您申请以下权限：
```
<uses-permission android:name="android.permission.INTERNET" />
  <!--    蓝牙所需权限-->
  <uses-permission
android:name="android.permission.ACCESS_FINE_LOCATION"
tools:node="replace" />
  <uses-permission
android:name="android.permission.ACCESS_COARSE_LOCATION"
tools:node="replace" />

  <uses-permission
android:name="android.permission.BLUETOOTH"
android:maxSdkVersion="30" />
  <uses-permission
android:name="android.permission.BLUETOOTH_ADMIN"
android:maxSdkVersion="30" />
  <uses-permission
android:name="android.permission.BLUETOOTH_SCAN"
android:usesPermissionFlags="neverForLocation" />
  <uses-permission android:name="android.permission.BLUETOOTH_ADVERTISE" />
  <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
  <!--    蓝牙所需权限-->
```
代码示例
```
 public String[] getBLEPermissions() {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        return new String[]{
          Manifest.permission.BLUETOOTH_SCAN,
          Manifest.permission.BLUETOOTH_ADVERTISE,
          Manifest.permission.BLUETOOTH_CONNECT,
          Manifest.permission.ACCESS_FINE_LOCATION // 添加这行确保所有情况下蓝牙扫描都能正常工作
        };
      } else {
        return new String[]{
          Manifest.permission.ACCESS_FINE_LOCATION,
          Manifest.permission.ACCESS_COARSE_LOCATION
        };
      }
    }
```


#### SDK初始化
在您应用的启动入口调用SDK的初始化代码：`application`为你项目的Application，`isDebug`表示是否是调试模式，调试模式下会打印关键日志。日志TAG为：`BluetoothManager`。
```
MrkDeviceManger.INSTANCE.init(application,isDebug);
```

#### 监听所有设备连接状态
```
 MrkDeviceManger.INSTANCE.registerBluetoothStateListener(new BluetoothStatusListener() {
           @Override
           public void onBluetoothStatus(BluetoothEnum bluetoothEnum) {
               //蓝牙开关状态
           }
       }).registerDeviceListener(this, new DeviceListener() {
           @Override
           public void onConnectStatus(boolean isAutoReconnect, DeviceMangerBean bean) {
               switch (bean.getConnectEnum()) {
                   case ON:
                       //连接成功
                       break;
                   case OFF:
                       //断开连接
                       break;
                   case ING:
                       //连接中
                       break;
                   case ERROR:
                       //连接失败
                       break;
                   default:
                       break;
               }
           }
       });
```


#### 开始设备搜索
- 设备开始搜索，会回调搜索开始`BluetoothEnum.START`。 
- 设备搜索中，会回调搜索中`BluetoothEnum.ING`。 
- 搜索超时时间为10秒，10秒以后会回调搜索结束`BluetoothEnum.STOP` 调用搜索结束或点击连接设备也会回调。
```
MrkDeviceManger.INSTANCE.startSearch(this, new BluetoothSearchListener() {
            @Override
            public void onSearchStatus(BluetoothEnum bluetoothEnum) {
                //设备搜索状态
            }
            @Override
            public void onSearchDevice(DeviceSearchBean bean) {
               //搜索到的设备对象
            }
        });
```
#### 停止设备搜索
SDK内部会在设备连接的时候也会同步调用此方法。
```
MrkDeviceManger.INSTANCE.stopSearch();
```

#### 设备连接
- 连接超时时间为5秒，5秒以后还未连接会回调未连接`DeviceConnectEnum.OFF`。
- 此方法会自动断开已连接的同类型设备再连接。

**方法1：通过搜索对象连接。**
把搜索到的设备对象`DeviceSearchBean`传入，即可连接设备。
```
MrkDeviceManger.INSTANCE.create(context, DeviceSearchBean).connect();
```

**方法2：通过必要参数连接。**
把搜索到的设备对象`DeviceSearchBean`保存`mac`mac地址，`productId`产品Id，`bluetoothName`蓝牙广播名， `modelId`型号id， `uniqueModelIdentify` 设备特征值。通过这些参数去连接。
```
MrkDeviceManger.INSTANCE.create(context,mac,productId,bluetoothName,modelId,uniqueModelIdentify).connect();
```

#### 断开设备连接
把需要断开连接的`mac`地址传入，即可断开连接设备。`mac`地址在搜索到的设备对象跟连接成功后返回的对象里面有。
```
MrkDeviceManger.INSTANCE.disConnect(mac) 
```

#### 判断设备是否连接
`params`可以是设备大类  也可以mac地址。
```
MrkDeviceManger.INSTANCE.isConnect(params) 
```

#### 获取所有已经连接的设备对象
```
MrkDeviceManger.INSTANCE.getConnectList()
```

#### 清空数据
`mac`可传入null或者设备的mac地址，传入null则表示清除掉当前所有的连接设备的数据，传入mac地址，则清除此mac地址设备的数据。
```
MrkDeviceManger.INSTANCE.clear(Context，mac) 
```

#### 通过设备大类id返回对象的设备名称
`productId`设备大类id
```
MrkDeviceManger.INSTANCE.getTypeName(productId) 
```
```
fun getTypeName(productId: String): String {
          return when (productId) {
              DeviceConstants.D_BICYCLE -> "动感单车"
              DeviceConstants.D_TREADMILL -> "跑步机"
              DeviceConstants.D_OTHER -> "其他"
              DeviceConstants.D_ROW -> "划船机"
              DeviceConstants.D_TECHNOGYM -> "椭圆机"
              DeviceConstants.D_FASCIA_GUN -> "筋膜枪"
              DeviceConstants.D_SKIPPING -> "跳绳"
              DeviceConstants.D_POWER -> "力量站"
              DeviceConstants.D_HULA_HOOP -> "呼啦圈"
              DeviceConstants.D_HEART -> "心率带"
              DeviceConstants.D_FAT_SCALE -> "体脂秤"
              DeviceConstants.D_PHYLLISRODS -> "飞力士棒"
              DeviceConstants.D_SCALE_LF -> "乐福体脂秤"
              DeviceConstants.D_SCALE_WL -> "沃莱体脂秤"
              DeviceConstants.D_GAME_PAD -> "游戏手柄"
              else -> {
                  "未知设备"
              }
          }
      }
```


#### 控制设备
- 设备连接成功后，即可控制设备。
- 有多种创建控制类的方式，请根据需求选择。

**1.通过设备大类创建**
```
deviceControl = MrkDeviceManger.INSTANCE.create(this, productId)//设备大类ID，
  .setOnDeviceListener(deviceListener)//设置设备状态监听
  .registerDevice();//注册设备状态
```

**2.通过设备的mac地址创建**
```
deviceControl = MrkDeviceManger.INSTANCE.create(this, mac)//设备mac地址
  .setOnDeviceListener(deviceListener)//设置设备状态监听
  .registerDevice();//注册设备状态
```

**3.通过搜索对象创建**
```
deviceControl = MrkDeviceManger.INSTANCE.create(this, DeviceSearchBean)//搜索对象
  .setOnDeviceListener(deviceListener)//设置设备状态监听
  .registerDevice();//注册设备状态
```


**开启自动重连**
- 特定情况下，设备会断开连接。如长时间未使用设备，电源断开等。
- 开启此功能后，非人为主动断开即会触发设备重连。
- 此功能建议在运动模式下开启。

```
//开启一次自动重连
deviceControl.autoConnect()

//开启一直自动重连
deviceControl.autoConnectAlways()
```

**关闭自动重连**
```
deviceControl.unAutoConnect()
```

**发送控制指令**
- 控制指令，SDK内部有做设备最大最小指令限制。如设备最大阻力15，下发了16，SDK内部会直接发送设备的最大阻力。坡度，速度亦是如此。
```
 //发送设备控制指令 阻力(如果发错了，设备不会响应)
 //resistance 阻力
 deviceControl.sendCommandResistance(resistance: Int)
 
 //发送设备控制指令 坡度(如果发错了，设备不会响应)
 //slope 坡度
 deviceControl.sendCommandSlope(slope: Int)
 
 //发送跑步机控制指令 速度与坡度（不论是发送速度还是坡度都要使用此方法，请不要使用单独的sendCommandSlope）
 deviceControl.sendCommandTreadmill(speed: Int, slope: Int) 
```
**根据麦瑞克提供的课程教案发送教案**
```
 {
   "id": "1604735072183595009",
   "name": "拉伸",
   "describeInfo": "",
   "kcal": 10.0,
   "time": 2,
   "beginTime": 1350,
   "endTime": 1470,
   "sustainTime": 120,
   "courseLinks": [
       {
           "id": "1604735072187789313",
           "catalogueId": "1604735072183595009",
           "name": "股四头肌-臀大肌-腘绳肌",
           "crux": "",
           "beginTime": 1350,
           "sustainTime": 120,
           "endTime": 1470,
           "minNum": 0.0,
           "maxNum": 0.0,
           "adviseNum": 0,
           "slopeNum": 0,
           "distance": 0.0,
           "kcal": 10.0,
           "isExistFlame": 0
       }
   ],
   "isContinue": 0,
   "maxNum": 0.0,
   "speedDesc": "最高踏频(rpm)"
 }
```
- 当设备为：动感单车，椭圆机，划船机时（阻力对应以上教案中的`adviseNum`,坡度对应以上教案中的`slopeNum`）
```
//动感单车，椭圆机，划船机发送阻力
deviceControl.sendCommandResistance(adviseNum)
//动感单车，椭圆机，划船机发送坡度
deviceControl.sendCommandSlope(slopeNum)
```
- 当设备为：跑步机时（速度对应以上教案中的 `maxNum`,坡度对应以上教案中的 `adviseNum`）
```
deviceControl.sendCommandTreadmill(maxNum, adviseNum) 
```

**其他控制**
```
//当前设备连接
deviceControl.connect()

//当前设备断开连接
deviceControl.disConnect()

//设备开始(只有跑步机可用)
deviceControl.deviceStart()

//设备暂停(只有跑步机可用)
deviceControl.devicePause()

//设备数据清除
deviceControl.clearData()

//是否开启设备数据回调返回
deviceControl.setNotifyData(true)

//发送设备控制指令 阻力(如果发错了，设备不会响应)
//resistance 阻力
deviceControl.sendCommandResistance(resistance: Int)

//发送设备控制指令 坡度(如果发错了，设备不会响应)
//slope 坡度
deviceControl.sendCommandSlope(slope: Int)

//发送跑步机控制指令速度与坡度（在发送控制指令前，先确定跑步机是否启动，不论是发送速度还是坡度都要使用此方法，请不要使用单独的sendCommandSlope）
deviceControl.sendCommandTreadmill(speed: Int, slope: Int) 
```


## 对象参数说明
#### 蓝牙状态`BluetoothEnum`
```
 /**
 * @param OPEN  蓝牙开关打开
 * @param CLOSE   蓝牙开关关闭
 * @param START  开始搜索
 * @param STOP   停止搜索
 * @param ING   搜索中
 */
enum class BluetoothEnum {
    OPEN, CLOSE, START, STOP, ING
}
```

#### 设备连接状态 `DeviceConnectEnum`
```
 /**
 * 设备连接状态
 * @param ON 设备连接成功
 * @param OFF 设备未连接
 * @param ING 设备连接中
 * @param ERROR 设备连接错误(接口没请求通)
 */
enum class DeviceConnectEnum {
    ON, OFF, ING, ERROR
}
```

#### 跑步机设备状态  `DeviceTreadmillEnum`
```
 /**
 * @param START  跑步机 设备开始 运行中
 * @param PAUSE  跑步机 设备暂停
 * @param STOP  跑步机 设备停止
 * @param COUNT_TIME  跑步机 启动中
 * @param SLOW_DOWN  跑步机 减速中
 * @param DISABLE 故障
 * @param WAIT 待机
 */
enum class DeviceTreadmillEnum {
    START, PAUSE, STOP, COUNT_TIME, SLOW_DOWN, WAIT, DISABLE
}
```


#### 整个设备对象  `DeviceMangerBean` 
```
data class DeviceMangerBean(
    var connectBean: DeviceGoConnectBean,//当前连接的对象
    var deviceOtaBean: DeviceOtaBean? = null,//当前设备的ota对象
    var deviceDetails: DeviceDetailsBean? = null,//当前设备的设备详情(需要先连接)
    var connectEnum: DeviceConnectEnum = DeviceConnectEnum.OFF, //当前设备的连接状态
    var deviceFunction: BaseDeviceFunction? = null,//当前设备的设备控制类
    var unitDistance: Int = -1//设备单位：1-公制，2-英制	 1
)
```


#### 搜索对象  `DeviceSearchBean` 
```
data class DeviceSearchBean(
    val modelId: String = "", //型号id 1630452448293478401
    val originModelId: String = "", //起源型号id(老型号ID) 1630452448293478401
    val modelName: String = "", //型号名称 S06单车
    val bluetoothName: String = "", //蓝牙广播名 MRK-S06-07BD
    val mac: String = "", //mac地址	 24:00:0C:A0:DA:27
    val modelDescription: String = "",  //型号描述
    val productId: String = "", //产品Id	 1
    val cover: String = "", //型号封面	 https://static.merach.com/other/20230228/24843ca5b8cd4b9ebba0106cb667f161_1560x840.png
    val communicationProtocol: Int = 0, //通信协议	1:麦瑞克,2:FTMS,3:智健,4:柏群,5:FTMS+智健
    val isOta: Int = 0, //是否支持ota	 1
    val isMerit: Int = 0, //是否merit设备	 1
    val characteristicValue: String="",//唯一型号确认json
    val isTrust: Int = 0,//是否可以信任的数据，0不可信任，1可信任，当数据不可信任时不展示具体信息 1
    var connectEnum: DeviceConnectEnum? //自定义的连接状态
)
```

#### 设备详情  `DeviceDetailsBean` 
```
data class DeviceDetailsBean(
    val productId: String = "", //产品Id	  // 1
    val modelId: String = "", //型号id	 1744914228939124742
    val modelName: String = "",//型号名称
    val cover: String = "", //型号封面	 https://static.merach.com/other/20240201/c70321dd6842484f8855d369449e4e19_1200x900.jpg
    val communicationProtocol: Int = 0, //通信协议	1:麦瑞克,2:FTMS,3:智健,4:柏群,5:FTMS+智健 3
    val isOta: Int = 0, //是否支持ota,不推荐使用，建议使用productModelTsl中的isOta 「已废弃」 1
    val otaProtocol: Int = 0, //ota协议	1:泰凌威 智建设备；2:博通; 3:DFU 4:新向远 5富芮坤 6 凌思威	  0
    val versionEigenValue: Int = 0, //版本特征值	 2
    val deviceUserRelId: String = "", //用户设备关联id	 1752946454528196610
    val deviceAlias: String = "",//设备别名
    var bluetoothName: String = "", //蓝牙广播名	 MRK-S12-07BD
    var mac: String = "", //mac地址	 24:00:0C:A0:DA:27
    val productName: String = "",//产品名称
    val productType: Int = 0, //产品分类	 0
    val helpCenter: String = "",//帮助中心
    val productManual: String = "",//产品说明
    val isMerit: Int = 0, //是否merit设备,不推荐使用，建议使用productModelTsl中的isElectromagneticControl 1
    val deviceId: String = "", //设备id	 0
    val firmwareVersion: String = "",//当前固件版本
    val isBind: Int = 0, //是否绑定操作	 0
    val productModelTsl: ProductModelTsl = ProductModelTsl()
 ) {
     data class ProductModelTsl(
         val isOta: Int = 0, //是否支持ota：1是0否	 1
         val isClean: Int = 0, //是否支持清理运动数据：1是0否	 1
         val isElectromagneticControl: Int = 0, //是否是电磁控设备 1是0否	 1
         val versionEigenValue: Int = 0, //版本特征值	 2
         val controlResistance: Int = 0, //是否控制阻力，1是0否	 0
         val minResistance: Int = 0, //最小阻力	 0
         val maxResistance: Int = 0, //最大阻力	 0
         val controlSpeed: Int = 0, //是否控制速度，1是0否	 0
         val minSpeed: Int = 0, //最小速度	 0
         val maxSpeed: Int = 0, //最大速度	 0
         val controlSlope: Int = 0, //是否控制坡度，1是0否	 0
         val minSlope: Int = 0, //最小坡度	 0
         val maxSlope: Int = 0, //最大坡度	 0
         val controlGear: Int = 0, //是否可调节档位，1是0否	 0
         val minGear: Int = 0, //最小档位	 0
         val maxGear: Int = 0, //最大档位	 0
         val isSupportResistanceEcho: Int = 0, //是否支持阻力回显	 1
         val guide: String = "",//引导信息
         val electrode: Int = 0, //电极信息	 0
         val unitVal: Int = 0, //设备单位：1-公制，2-英制	 1
         val isSupportSlopeEcho: Int = 0, //是否支持坡度回显：0-否，1-是	 0
         val torqueMap: HashMap<String, Double> = HashMap()//扭矩
     )
}
```


#### 设备数据回调  `DeviceTrainBO` 
```
data class DeviceTrainBO(
 var dataType: Int = 0, //智健协议 车表类协议区分是 瞬时数据/累计数据  默认为0都包含  1；瞬时数据：2； 累计数据 3 辅助通道
 var linkId: Long = 0, //小节ID
 var speed: Float = 0f, //速度
 var avgSpeed: Float = 0f, //平均速度
 var distance: Int = 0, //距离：米
 var spm: Int = 0, //踏频/桨频
 var count: Int = 0, //总踏数/总桨数/总个数/总圈数
 var avgSpm: Float = 0f, //平均踏频/平均桨频
 var drag: Int = 0, //阻力
 var power: Float = 0f, //功率
 var avgPower: Float = 0f, //平均功率
 var energy: Float = 0f, //消耗：kcal
 var rateKcal: Float = 0f, //消耗：kcal
 var deviceTime: Long = 0,//设备时长：秒
 var deviceRate: Int = 0, //设备心率
 var rate: Int = 0,//心率设备心率
 var gradient: Int = 0, //坡度
 var grade: Int = 0, //挡位
 var electric: Int = 0, //电量
 var timestamp: Long = 0,
 var status: Int = -1, //设备状态用来规避x1彩屏跑步机暂停也会给数据;
 var model: Int = 0,
 var deviceTimestamp: Long = 0,//设备收到数据的时间戳
 var direction: Int = -1,//摇摆方向 00H：直线 01H：左 02H：右
 var press: Int = -1,//00H：存在按压信号
 var originalData: String = "",
 var mac: String = "",//设备mac地址
 var type: String = "",//设备类型
 var name: String = "", //蓝牙名称k60,
 var unitDistance: Int = -1,//设备类型0：公制，1：英制
) 
```


## 文件下载
- SDK：[https://gitee.com/williamOsChina/mrk-device-demo/blob/main/app/libs/](https://gitee.com/williamOsChina/mrk-device-demo/blob/main/app/libs/)
- Demo源码：[https://gitee.com/williamOsChina/mrk-device-demo](https://gitee.com/williamOsChina/mrk-device-demo)
- DemoApk：[https://www.pgyer.com/967QYXBh](https://www.pgyer.com/967QYXBh)





## 常见问题
#### 设备搜索不到。
1.请确认该设备是否在与麦瑞克合作的范围以内。
2.请检查必要权限是否授予，检查蓝牙开关是否打开，检查设备是否唤醒且未被他人连接，检查设备是过远。

#### 设备连接不上
1.请参考【设备搜索不到】的解决方式。
2.如以上操作还是连接不上，请前往系统蓝牙界面，点击“取消匹配”后，重新搜索连接。

#### 设备中途断开连接
1.请检查设备电源或者电池是否正常。
2.部分设备有人在模式，即设备在正常运行中。如设备没有在运行中，即未产生相应的运动数据，根据设备不同会在特定的时间里自动断开。