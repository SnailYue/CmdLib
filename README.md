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
## 配置文件说明 ...
