# cmdlib库使用文档

> #### 初始化
> 
> ```
> public void initProtocol(Context context) {
>           Command.init(context, true);
>           try {
>                Command.getInstance().setProtocalFromLocal(context, "json配置文件名.json");
>           } catch (TRDException e) {
>               e.printStackTrace();
>           }
>       }
> ```
> 
> #### Json配置文件说明
> 
> > ###### 1. 引入配置文件
> > 
> > 在main目录中新建assets文件夹，将新建的json格式的配置文件放在此文件键中
> > 
> > ###### 2. 配置文件的字段说明
> > 
> > ```
> > Protocol对象字段
> >       @SerializedName("Name")
> >       public String Name; //项目名称或厂商名称
> >       @SerializedName("Model")
> >       public String Model;// 模式 标准版或非标准版
> >       @SerializedName("Key")
> >       public String Key;// 项目的key  可不写
> >       @SerializedName("AllFunctions")
> >       public List<PTFunction> AllFunctions;// 全功能集合 上行帧与下行帧的集合
> >       @SerializedName("UpFunctions")
> >       public List<PTFunction> UpFunction;//上行帧的功能集合
> >       @SerializedName("DownFunctions")
> >       public List<PTFunction> DownFunction;//下行帧的功能集合
> >       @SerializedName("PCFrames")
> >       public List<PCFrames> PCFrames;
> >       @SerializedName("PTConfig")
> >       public PTConfig PTConfig;
> >       @SerializedName("PCloudControl")
> >       public PCloudControl PCloudControl;
> > ```
> > 
> > ```
> > PTFunction对象字段
> >       @SerializedName("Id")
> >       public int Id;
> >       @SerializedName("No")
> >       public int No;
> >       @SerializedName("Name")
> >       public String Name;
> >       @SerializedName("Title")
> >       public String Title;
> >       @SerializedName("Length")
> >       public int Length;
> >       @SerializedName("Cur_Value")
> >       public int Cur_Value;
> >       @SerializedName("Last_Value")
> >       public int Last_Value;
> >       @SerializedName("Cloud_Value")
> >       public int Value;
> >       @SerializedName("Values")
> >       public int[] Values;
> >       @SerializedName("Value_des")
> >       public List<PValueDes> Value_Des;
> >       @SerializedName("Value_Change")
> >       public boolean ValueChange;
> >       @SerializedName("No_Change_Notify")
> >       public boolean NoChangeNotify;
> > ```
> > 
> > ```
> > PCFrames对象字段
> >      @SerializedName("Pid")
> >      private int pId;
> >      @SerializedName("Length")
> >      private int length;
> >      @SerializedName("Ptype")
> >      private String pType;
> >      @SerializedName("CmdHead")
> >      private PCmdHead cmdHead;
> >      @SerializedName("CmdType")
> >      private List<PCmdType> cmdTypes;
> >      @SerializedName("CheckSum")
> >      private PCheckSum checkSum;
> >      @SerializedName("CmdFooter")
> >      private PCmdFooter cmdFooter;
> > ```
> > 
> > ```
> > PTConfig对象字段
> >      @SerializedName("SendHeart")
> >      private boolean sendHeart;
> >      @SerializedName("HeartFrequency")
> >      private int heartFrequency;
> >      @SerializedName("SupportAllSerial")
> >      private boolean supportAllSerial;
> >      @SerializedName("ResendInterval")
> >      private int resendInterval;
> >      @SerializedName("ResendTimes")
> >      private int resendTimes;
> >      @SerializedName("SupportSingleContorl")
> >      private boolean supportSingleContorl;
> >      @SerializedName("SendResponse")
> >      private boolean sendResponse;
> >      @SerializedName("IsReSend")
> >      private boolean IsReSend;
> >      @SerializedName("IsStandard")
> >      private boolean isStandard;
> >      @SerializedName("IsChangeStatusSelf")
> >      private boolean isChangeStatusSelf;
> >      @SerializedName("AnalyzeData")
> >      private boolean analyzeData;
> >      @SerializedName("SerialName")
> >      public String serialName = "/dev/ttyS1";
> >      @SerializedName("SerialBaudrate")
> >      public int serial_baudrate = 9600;
> >      @SerializedName("SerialCsize")
> >      public int serial_csize = 8;
> >      @SerializedName("SerialParity")
> >      public int serial_parity = -1;
> >      @SerializedName("SerialStopBits")
> >      public int serial_stopbits = 1;
> >      @SerializedName("SupportFrameType")
> >      public boolean supportFrameType;
> >      @SerializedName("IsSupportCloud")
> >      public boolean isSupportCloud;
> > ```
> > 
> > ```
> > 部分bean对象省略，详情查看源码
> > ```
> > 
> > ###### 3. 配置文件的编写
> > 
> > 详情查看森歌项目中的Json配置文件
> 
> #### 帧指令的发送与接收
> 
> > ###### 1. 帧指令的发送
> > 
> > ```
> > public void sendCommands(ConcurrentHashMap<String, Object> map, boolean isOpenPressSound) {
> >          this.map = map;
> >          try {
> >              Command.getInstance().control(map);
> >          } catch (ConnectException e) {
> >              e.printStackTrace();
> >          } catch (CommandException e) {
> >              e.printStackTrace();
> >          }
> >      }
> > ```
> > 
> > ###### 2. 订阅自定义数据帧
> > 
> > ```
> > statusCallback = new StatusCallback() {
> >           @Override
> >           public String getType() {
> >                //此return的字段根据需要监听状态的功能改进行修改，为空时为全部的功能状态监听
> >                return ConstansConfig.STEAMER_HEATING_PLATE;
> >           }
> > 
> >           @Override
> >           public void statueChanged(String type, int value) {
> >                //value值为对应的功能的值
> >           }
> >     };
> > ViewUIManager.getInstance().bind(statusCallback);
> > ```
> > 
> > ###### 3. 获取某个功能当前的状态
> > 
> > ```
> > Command.getInstance().getFunctionStatus("功能名")
> > ```

> > ###### 4. WorkTimer定时器
> >
> > ```
> > WorkTimer.setTimer("功能名", new TimerTask.TimerTaskListener() {
> >         @Override
>>          public void onStart(final long totalTime, String type) {
>>
>>          }
>>
>>          @Override
>>          public void onTick(final long totalTime, String type) {
>>
>>          }
>>
>>          @Override
>>          public void onFinish(String type) {
>>
>>          }
>>
>>          @Override
>>          public void onPause(long countdownTime, String type) {
>>
>>          }
>>      });
>>      //开始   start的参数为时间，单位为秒
>>      WorkTimer.getTimer("功能名").start(60*30);
>>      //结束
>>      WorkTimer.getTimer("功能名").stop();
>>      //暂停
>>      WorkTimer.getTimer("功能名").pause();
>>      //恢复计时
>>      WorkTimer.getTimer("功能名").resume();
>>      //是否正在运行状态
>>      WorkTimer.getTimer("功能名").isRun()
>>      //是否处于暂停状态
>>      WorkTimer.getTimer("功能名").isPause()
>>      //是否处于结束状态
>>      WorkTimer.getTimer("功能名").isFinish()
> > ```
