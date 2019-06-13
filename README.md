# CmdLib串口开发库
cmdlib是一个用于Android串口开发，通过简单的配置，
快速实现与硬件之间的通信的库。

## 快速接入
#### 配置文件
在main目录中新建assets文件夹，把协议以Json文件的格式放置在此文件夹中。

#### 库的初始化
```
    public void initData() {
         Command.init(this);
         try {
             /**
              * 协议初始化
              */
             Command.getInstance().setProtocol(this, "配置文件名.json");
         } catch (TRDException e) {
             e.printStackTrace();
         }
     }
```

#### 监听指定功能的状态变化
```
    statusCallback = new StatusCallback() {
          @Override
          public String getType() {
               //此return的字段根据需要监听状态的功能改进行修改，为null值监听全部的功能的变化
               return ConstansConfig.STEAMER_HEATING_PLATE;
          }

          @Override
          public void statueChanged(String type, int value) {
               //value值为对应的功能的值
          }
    };
    //绑定状态监听
    ViewUIManager.getInstance().bind(statusCallback);
```
#### 获取指定功能当前的状态
```
    Command.getInstance().getFunctionStatus("功能名")
```

#### 监听接收到的帧指令
```
    Command.getInstance().setCmdListener(new CmdListener() {
            @Override
            public void readCmdData(byte[] bytes, int len) {
                /**
                 * bytes为电控的上行帧指令，len为帧指令的长度
                 */
            }
        });
```

#### 监听串口的连接状态
```
    Command.getInstance().setOnSerialPortConnectedListener(new SerialPortConnectedListener() {
            @Override
            public void disConnected() {
                /**
                 * 串口通讯连接断开
                 */
            }

            @Override
            public void connected() {
                /**
                 * 串口通讯连接正常
                 */
            }
        });
```

#### 发送帧指令
```
    /**
     * 打开蜂鸣
     */
    public void setPressSound() {
        map.clear();
        map.put("PRESS_SOUND", 1);
        sendCommands(map);
    }

    public void sendCommands(ConcurrentHashMap<String, Object> map) {
        this.map = map;
        try {
            Command.getInstance().control(map, null);
        } catch (ConnectException e) {
            e.printStackTrace();
        } catch (CommandException e) {
            e.printStackTrace();
        }
    }
```
#### 定时器功能
```
    /**
     * 串口通信中可能会使用到计时器，故增加了计时器功能
     */
    WorkTimer.setTimer("功能名", new TimerTask.TimerTaskListener() {
        @Override
         public void onStart(final long totalTime, String type) {
            //计时器开始
         }

         @Override
         public void onTick(final long totalTime, String type) {
            //计时器倒计时剩余时间
         }

         @Override
         public void onFinish(String type) {
            //计时器倒计时完成
         }

         @Override
         public void onPause(long countdownTime, String type) {
            //计时器暂停
         }
     });
     //开始   start的参数为时间，单位为秒
     WorkTimer.getTimer("功能名").start(60*30);
     //结束
     WorkTimer.getTimer("功能名").stop();
     //暂停
     WorkTimer.getTimer("功能名").pause();
     //恢复计时
     WorkTimer.getTimer("功能名").resume();
     //是否正在运行状态
     WorkTimer.getTimer("功能名").isRun()
     //是否处于暂停状态
     WorkTimer.getTimer("功能名").isPause()
     //是否处于结束状态
     WorkTimer.getTimer("功能名").isFinish()
```
## 配置文件说明
#### Android串口通信部分说明
1.在Android串口开发过程中，与硬件之间的通信以特定的协议进行，通信使用十六进制。

2.串口通信涉及到位的运算，1byte = 8bit，请注意配置时的单位。

3.配置数据位功能集合时，由高位向低位计数。如某一个字节为：0x10 转换成二进制为：00010000

#### AllFunctions json数组,上行帧与下行帧数据位功能的集合(功能帧，不包含帧头，流水号，校验帧，帧尾，帧类型等，仅是数据位)，注：(字段名必须与示例中的字段一致，部分字段未标明，详情可查看源码)
```
"AllFunctions": [
    {
      "Id": 1,    //id
      "No": 1,    //数据位的编号，由高位向低位的顺序计数，即第8位 -> 第1位的顺序编号
      "Name": "DOOR",    //功能名
      "Title": "门信号",  //功能中文名
      "Values": [        //功能状态值的范围
        0,
        1
      ],
      "Length": 1,        //数据长度，单位bit  1byte = 8bit
      "Value_des": [      //功能状态值介绍
        {
          "data": 0,      //值为0，表示关
          "desc": "关"
        },
        {
          "data": 1,      //值为1，表示开
          "desc": "开"
        }
      ],
      "Cur_Value": 0,     //功能当前的值
      "Last_Value": 0,    //功能上一个状态的值
      "Cloud_Value": 0,   //功能云端控制的值，暂未使用，可忽略
      "Value_Change": true,   //值是否可变，为true时，当每一次发送Cur_Value的值会赋给Last_Value,功能当前的值会赋给Cur_Value,
      "No_Change_Notify": false  //状态没有发生改变时是否发送通知，通知的接收方式为文档的[监听接收到的帧指令]
    },
    {
      "Id": 2,
      "No": 2,
      "Name": "FIRE",
      "Title": "火信号",
      "Values": [
        0,
        1
      ],
      "Length": 1,
      "Value_des": [
        {
          "data": 0,
          "desc": "关"
        },
        {
          "data": 1,
          "desc": "开"
        }
      ],
      "Cur_Value": 0,
      "Last_Value": 0,
      "Cloud_Value": 0,
      "Value_Change": true,
      "No_Change_Notify": false
    }
]
```

#### UpFunctions json数组,上行帧(电控发送给屏端)数据位功能的集合(功能帧，不包含帧头，流水号，校验帧，帧尾，帧类型等，仅是数据位)，注：(字段名必须与示例中的字段一致，部分字段未标明，详情可查看源码)
```
"UpFunctions": [
    {
      "Id": 1,    //id
      "No": 1,    //数据位的编号，由高位向低位的顺序计数
      "Name": "DOOR",    //功能名
      "Title": "门信号",  //功能中文名
      "Values": [        //功能状态值的范围
        0,
        1
      ],
      "Length": 1,        //数据长度，单位bit  1byte = 8bit
      "Value_des": [      //功能状态值介绍
        {
          "data": 0,      //值为0，表示关
          "desc": "关"
        },
        {
          "data": 1,      //值为1，表示开
          "desc": "开"
        }
      ],
      "Cur_Value": 0,     //功能当前的值
      "Last_Value": 0,    //功能上一个状态的值
      "Cloud_Value": 0,   //功能云端控制的值，暂未使用，可忽略
      "Value_Change": true,   //值是否可变，为true时，当每一次发送Cur_Value的值会赋给Last_Value,功能当前的值会赋给Cur_Value,
      "No_Change_Notify": false  //状态没有发生改变时是否发送通知，通知的接收方式为文档的[监听接收到的帧指令]
    },
    {
      "Id": 2,
      "No": 2,
      "Name": "FIRE",
      "Title": "火信号",
      "Values": [
        0,
        1
      ],
      "Length": 1,
      "Value_des": [
        {
          "data": 0,
          "desc": "关"
        },
        {
          "data": 1,
          "desc": "开"
        }
      ],
      "Cur_Value": 0,
      "Last_Value": 0,
      "Cloud_Value": 0,
      "Value_Change": true,
      "No_Change_Notify": false
    }
]
```

#### DownFunctions json数组,下行帧(屏端发送给电控)数据位功能的集合(功能帧，不包含帧头，流水号，校验帧，帧尾，帧类型等，仅是数据位)，注：(字段名必须与示例中的字段一致，部分字段未标明，详情可查看源码)
```
"DownFunctions": [
    {
      "Id": 1,    //id
      "No": 1,    //数据位的编号，由高位向低位的顺序计数
      "Name": "DOOR",    //功能名
      "Title": "门信号",  //功能中文名
      "Values": [        //功能状态值的范围
        0,
        1
      ],
      "Length": 1,        //数据长度，单位bit  1byte = 8bit
      "Value_des": [      //功能状态值介绍
        {
          "data": 0,      //值为0，表示关
          "desc": "关"
        },
        {
          "data": 1,      //值为1，表示开
          "desc": "开"
        }
      ],
      "Cur_Value": 0,     //功能当前的值
      "Last_Value": 0,    //功能上一个状态的值
      "Cloud_Value": 0,   //功能云端控制的值，暂未使用，可忽略
      "Value_Change": true,   //值是否可变，为true时，当每一次发送Cur_Value的值会赋给Last_Value,功能当前的值会赋给Cur_Value,
      "No_Change_Notify": false  //状态没有发生改变时是否发送通知，通知的接收方式为文档的[监听接收到的帧指令]
    },
    {
      "Id": 2,
      "No": 2,
      "Name": "FIRE",
      "Title": "火信号",
      "Values": [
        0,
        1
      ],
      "Length": 1,
      "Value_des": [
        {
          "data": 0,
          "desc": "关"
        },
        {
          "data": 1,
          "desc": "开"
        }
      ],
      "Cur_Value": 0,
      "Last_Value": 0,
      "Cloud_Value": 0,
      "Value_Change": true,
      "No_Change_Notify": false
    }
]
```

#### 完整帧(包含帧头，帧类型，数据位，流水号，校验方式，帧尾)指令的配置，注：(字段名必须与示例中的字段一致，部分字段未标明，详情可查看源码)
```
"PCFrames": [
    {
      "Pid": 1,    //id，即帧的第1个byte
      "Length": 1, //帧的长度，单位为byte
      "Ptype": "CmdHead",   //帧头位
      "CmdHead": {          //帧头值的集合
        "Send": "AA",       //主动发动下行帧帧头的值
        "Response": "AA"    //被动回复帧帧头的值
      }
    },
    {
      "Pid": 2,  //id
      "Length": 1,
      "Ptype": "CmdLength",  //完整帧的长度位
      "PCmdLength": {
        "DownCmdLength": 25, //下行帧的长度  单位byte
        "UpCmdLength": 20    //上行帧的长度  单位byte
      }
    },
    {
      "Pid": 3,
      "Length": 21,     //下行帧数据功能的长度 单位byte
      "Ptype": "CmdDownData" // 下行帧数据位
    },
    {
      "Pid": 4,
      "Length": 16,    //上行帧数据功能的长度 单位byte
      "Ptype": "CmdUpData"  // 上行帧数据位
    },
    {
      "Pid": 5,
      "Length": 1,
      "Ptype": "CheckSum", //校验位
      "CheckSum": {
        "UpStartPid": 1,   //上行帧校验位的起始位
        "UpEndPid": 18,    //上行帧校验位的结束位
        "DownStartPid": 1, //下行帧校验位的起始位
        "DownEndPid": 23,  //下行帧校验位的结束位
        "Type": "CRC8"     //校验方式
      }
    },
    {
      "Pid": 6,
      "Length": 1,
      "Ptype": "CmdFooter", //帧尾
      "CmdFooter": {
        "Send": "55",       //主动发送帧帧尾
        "Response": "55"    //被动回复帧帧尾
      }
    }
  ]
```

#### 协议的相关配置，注：(字段名必须与示例中的字段名一致，部分字段未标明，详情可查看源码)
```
"PTConfig": {
    "SendHeart": true,  //是否发送心跳帧
    "HeartFrequency": 1000,  //心跳帧的频率  单位毫秒
    "SupportAllSerial": true, //是否支持全指令，当前只支持全指令
    "ResendInterval": 0,  //重发间隔
    "ResendTimes": 0,     //重发次数(不计主动发送的那一次)
    "SupportSingleContorl": false, //是否支持单指令控制，暂不支持
    "SendResponse": true,
    "IsStandard": true,  //是否为标准协议，可不配置
    "AnalyzeData": true, //是否解析帧指令
    "IsReSend": false,   //是否重发
    "IsChangeStatusSelf": true,  //是否改变自身状态值
    "SerialName": "/dev/ttyS0",  //串口名
    "SerialBaudrate": "9600",  //串口波特率
    "SerialCsize": 8,     //串口数据位
    "SerialParity": -1,   //校验方式
    "SerialStopBits": 1,  //串口停止位
    "SupportFrameType": false,  //是否是多类型帧
    "IsSupportCloud": false   //是否支持云端控制，暂不支持
  }
```

#### 配置示例:
app->main->assets->protocol.json