package com.pemt.util;

import android.util.Log;

import com.pemt.jnahelper.CLibrary;

public class UartUtil {
    private static final String TAG = "UartUtil";
    private int mSerialFd;
    private String mDevice = "/dev/tty";
    private CLibrary.termios mSerialOptions;
    public void openSerial()
    {
        mSerialFd = CLibrary.INSTANCE.open(mDevice, CLibrary.O_RDWR | CLibrary.O_NOCTTY | CLibrary.O_NDELAY);
        if (mSerialFd < 0) {
            throw new RuntimeException("open device fail!");
        }

        mSerialOptions = new CLibrary.termios();

        CLibrary.INSTANCE.tcgetattr(mSerialFd, mSerialOptions);
        mSerialOptions.c_cflag |= (CLibrary.CLOCAL | CLibrary.CREAD);//设置控制模式状态，本地连接，接收使能
        mSerialOptions.c_cflag &= ~CLibrary.CSIZE;//字符长度，设置数据位之前一定要屏掉这个位
        mSerialOptions.c_cflag &= ~CLibrary.CRTSCTS;//无硬件流控
        mSerialOptions.c_cflag |= CLibrary.CS8;//8位数据长度
        mSerialOptions.c_cflag &= ~CLibrary.CSTOPB;//1位停止位
        mSerialOptions.c_iflag |= CLibrary.IGNPAR;//无奇偶检验位
        mSerialOptions.c_oflag = 0; //输出模式
        mSerialOptions.c_lflag = 0; //不激活终端模式

        CLibrary.INSTANCE.tcflush(mSerialFd, CLibrary.TCIFLUSH);//溢出数据可以接收，但不读
        CLibrary.INSTANCE.tcsetattr(mSerialFd, CLibrary.TCSANOW, mSerialOptions);
    }

    public void closeSerial()
    {
        CLibrary.INSTANCE.close(mSerialFd);
    }
    public void setBaudRate(int baudRate)
    {
        CLibrary.INSTANCE.cfsetospeed(mSerialOptions, baudRate);//设置波特率
        setToEffective();
    }
    public void setDevice(String deviceName)
    {
        closeSerial();
        mSerialFd = CLibrary.INSTANCE.open(deviceName, CLibrary.O_RDWR | CLibrary.O_NOCTTY | CLibrary.O_NDELAY);
        setToEffective();
    }
    public void setDataBits(int dataBits)
    {
        mSerialOptions.c_cflag |= dataBits;//8位数据长度
        setToEffective();
    }
    public void setToEffective()
    {
        CLibrary.INSTANCE.tcflush(mSerialFd, CLibrary.TCIFLUSH);//溢出数据可以接收，但不读
        CLibrary.INSTANCE.tcsetattr(mSerialFd, CLibrary.TCSANOW, mSerialOptions);
    }

    public int uartSend(String sendContent)
    {
        int len = 0;
        len = CLibrary.INSTANCE.write(mSerialFd, sendContent, sendContent.getBytes().length);//实际写入的长度
        if(len == sendContent.getBytes().length) {
            return len;
        } else {
            CLibrary.INSTANCE.tcflush(mSerialFd, CLibrary.TCOFLUSH);//TCOFLUSH刷新写入的数据但不传送
            return -1;
        }

    }

    public int uartReceive(char[] receivedData)
    {
        int  ret = 0;
        long len=0;
        CLibrary.fd_set fs_read = new CLibrary.fd_set();
        CLibrary.timeval tv_timeout = new CLibrary.timeval();

        CLibrary.INSTANCE.FD_ZERO(fs_read);
        CLibrary.INSTANCE.FD_SET(mSerialFd, fs_read);
        tv_timeout.tv_sec  =2;     // (10*20/115200+5);
        tv_timeout.tv_usec = 0;

        while(CLibrary.INSTANCE.FD_ISSET(mSerialFd, fs_read))
        {

            CLibrary.INSTANCE.FD_ZERO(fs_read);
            CLibrary.INSTANCE.FD_SET(mSerialFd, fs_read);
            ret = CLibrary.INSTANCE.select(mSerialFd+1, fs_read, null, null, tv_timeout);
            Log.i(TAG,"ret="+ret);
            //如果返回0，代表在描述符状态改变前已超过timeout时间,错误返回-1

            if(CLibrary.INSTANCE.FD_ISSET(mSerialFd, fs_read)) {
            len = CLibrary.INSTANCE.read(mSerialFd, receivedData, receivedData.length);
                Log.i(TAG, "len = " + len);
            if(-1==len) {
                return -1;
            }
        } else {
                Log.i(TAG, "select fail!");
        }
        }
        return 0;
    }
    //add--not sure
    public void setCtrlmode(int mode)
    {
        mSerialOptions.c_cflag |= mode;
        setToEffective();
    }
    public void setCharLen(int charLen)
    {
        mSerialOptions.c_cflag &= charLen;
        setToEffective();
    }
    public void setHardwareStreamCtrl(int hwc)
    {
        mSerialOptions.c_cflag &= hwc;
        setToEffective();
    }
    public void setStopBits(int stopBits)
    {
        mSerialOptions.c_cflag &= stopBits;
        setToEffective();
    }

    public void setCheckMode(int checkMode)
    {
        mSerialOptions.c_iflag |= checkMode;
        setToEffective();
    }

    public void setOutputMode(int outputMode)
    {
        mSerialOptions.c_oflag = outputMode;
        setToEffective();
    }

    public void setTeminalMode(int teminalMode)
    {
        mSerialOptions.c_lflag = teminalMode;
        setToEffective();
    }
    //add 0321
    public void setFlowControl(int flag)
    {
        switch(flag)
        {
            case 0 ://不使用流控制
                mSerialOptions.c_cflag &= ~CLibrary.CRTSCTS;
                break;

            case 1 ://使用硬件流控制
                mSerialOptions.c_cflag |= CLibrary.CRTSCTS;
                break;
            case 2 ://使用软件流控制
                mSerialOptions.c_cflag |= CLibrary.IXON | CLibrary.IXOFF | CLibrary.IXANY;
                break;
        }
    }
}
