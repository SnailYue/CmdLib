package com.ebanswers.cmdlibdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ebanswers.cmdlib.Command;
import com.ebanswers.cmdlib.callback.CmdListener;
import com.ebanswers.cmdlib.callback.SerialPortConnectedListener;
import com.ebanswers.cmdlib.exception.CommandException;
import com.ebanswers.cmdlib.exception.TRDException;

import java.net.ConnectException;
import java.util.concurrent.ConcurrentHashMap;

public class MainActivity extends AppCompatActivity {
    private volatile ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();

    private Button clickButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }

    public void initView() {
        clickButton = findViewById(R.id.bt_click);
        clickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPressSound();
            }
        });
    }

    public void initData() {
        Command.init(this);
        try {
            /**
             * 协议初始化
             */
            Command.getInstance().setProtocol(this, "protocol.json");
        } catch (TRDException e) {
            e.printStackTrace();
        }
    }

    public void setPressSound() {
        map.clear();
        map.put("STRILIZER_PRESS_SOUND", 1);
        sendCommands(map, false);
    }

    public void sendCommands(ConcurrentHashMap<String, Object> map, boolean isOpenPressSound) {
        this.map = map;
        try {
            Command.getInstance().control(map, null);
        } catch (ConnectException e) {
            e.printStackTrace();
        } catch (CommandException e) {
            e.printStackTrace();
        }
    }

    public void initListener(){
        Command.getInstance().setCmdListener(new CmdListener() {
            @Override
            public void readCmdData(byte[] bytes, int len) {

            }
        });
    }

    public void initConnectedStatusListener(){
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
    }
}
