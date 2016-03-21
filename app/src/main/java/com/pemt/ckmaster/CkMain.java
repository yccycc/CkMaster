package com.pemt.ckmaster;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.pemt.jnahelper.CLibraryYcc;
import com.pemt.util.UartUtil;
import com.pemt.util.UartUtil_1;


public class CkMain extends Activity {
    private Button mWriteBtn;
    private EditText mWriteEt;
    private UartUtil_1 mUUartUtil;
    private EditText mDevNameEt;
    private EditText mBaudrateEt;
    private EditText mDataLenEt;
    private EditText mScreenEt;
    private Button mUartSet;
    private Handler mMainHandler = new Handler();
    private Thread mRsvThread;
    private Spinner mBaudrateSpinner;
    private Spinner mDataLenSpinner;
    static {
      //  System.loadLibrary("ckutil");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ck_main);
        mWriteBtn = (Button) findViewById(R.id.write);
        mWriteEt = (EditText) findViewById(R.id.write_et);
        mDevNameEt = (EditText) findViewById(R.id.dev_name_et);
        mBaudrateEt = (EditText) findViewById(R.id.baudrate_et);
        mDataLenEt = (EditText) findViewById(R.id.data_len_et);
        mUartSet = (Button) findViewById(R.id.uart_set);
        mScreenEt = (EditText) findViewById(R.id.screen_et);
        mBaudrateSpinner = (Spinner) findViewById(R.id.baudrate_spinner);
        mDataLenSpinner = (Spinner) findViewById(R.id.data_len_spinner);
        final String baudRateList[]=new String[]{"110", "300", "600", "1200",
                "2400", "4800", "9600","14400","19200","38400","56000",
                "57600","115200","128000","230400","256000"
        };
        final String dataLenList[]=new String[]{"5","6","7","8"};
        ArrayAdapter<String> baudRateAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, baudRateList);
        ArrayAdapter<String> dataLenAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,dataLenList);
        mBaudrateSpinner.setAdapter(baudRateAdapter);
        mDataLenSpinner.setAdapter(dataLenAdapter);
        mBaudrateSpinner.setSelection(12);
        mDataLenSpinner.setSelection(3);
        mUUartUtil = new UartUtil_1();
        mUartSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String devName = mDevNameEt.getText().toString();
                devName = (devName.equals("")?"/dev/tty":devName);
                int baudrate = Integer.parseInt(mBaudrateSpinner.getSelectedItem().toString());
                int dataLen = Integer.parseInt(mDataLenSpinner.getSelectedItem().toString());
             //   mUUartUtil.initUart(devName, baudrate, dataLen);
//                if(mRsvThread.isAlive()) {
//                    mRsvThread.interrupt();
//                }
//                mRsvThread.start();
                //new UartUtil().openSerial();
               Log.i("goddes", CLibraryYcc.INSTANCE.yccadd(100, 111) + "$");
               // CkUtilLibrary.INSTANCE.init_serial();
            }
        });
        mWriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sendContent = mWriteEt.getText().toString();
              //  mUUartUtil.uartSend(sendContent, sendContent.getBytes().length);
            }
        });
        class ReceiceRunnable implements Runnable{

            @Override
            public void run() {
                for(;;){
                   // final String rsvData = mUUartUtil.uartRecv(10);
                    mMainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                        //    mScreenEt.append(rsvData+"\n");
                        }
                    });
                }
            }
        }
        mRsvThread = new Thread(new ReceiceRunnable());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if(mRsvThread.isAlive())
//        {
//            mRsvThread.interrupt();
//        }
        //mUUartUtil.closeUart();
        new UartUtil().closeSerial();
    }
}
