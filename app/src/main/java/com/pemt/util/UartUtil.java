package com.pemt.util;

import android.util.Log;

import com.pemt.jnahelper.CLibrary;

public class UartUtil {
    private static final String TAG = "UartUtil";
    public static final int FLOWCTRL_NO = 0;
    public static final int FLOWCTRL_HW = 1;
    public static final int FLOWCTRL_SW = 2;
    public static final int DATABITS_FIVE = 5;
    public static final int DATABITS_SIX = 6;
    public static final int DATABITS_SEVEN = 7;
    public static final int DATABITS_EIGHT = 8;
    public static final int STOPBITS_ONE = 1;
    public static final int STOPBITS_TWO = 2;
    private int mSerialFd;
    private String mDevice = "/dev/ttyMT1";
    private CLibrary.termios mSerialOptions;
    private int mOpenMode = CLibrary.O_RDWR | CLibrary.O_NOCTTY | CLibrary.O_NDELAY;

    public void serialDefaultConfig() {
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

    public void openSerial() {
        mSerialFd = CLibrary.INSTANCE.open(mDevice, mOpenMode);
        Log.i("goddess--fd=", mSerialFd + "");
        if (mSerialFd < 0) {
            throw new RuntimeException("open device fail!" + "***" + mSerialFd);
        }
    }
    public void setOpenMode(int openMode)
    {
        this.mOpenMode = openMode;
    }

    public void closeSerial() {
        CLibrary.INSTANCE.close(mSerialFd);
    }

    public void setBaudRate(int baudRate) {
        int speed_arr[] = {CLibrary.B0,CLibrary.B50,CLibrary.B75,CLibrary.B110,CLibrary.B134,CLibrary.B150,
                CLibrary.B200,CLibrary.B300,CLibrary.B600,CLibrary.B1200,CLibrary.B1800,CLibrary.B2400,
                CLibrary.B4800,CLibrary.B9600,CLibrary.B19200,CLibrary.B38400,CLibrary.B57600,CLibrary.B115200};
        int name_arr[] = {0,50,75,110,134,150,200,300,600,1200,1800,2400,4800,9600,19200,38400,57600,115200};
        //设置串口输入波特率和输出波特率
        for (int i = 0; i < speed_arr.length; i++) {
            if (baudRate == name_arr[i]) {
                CLibrary.INSTANCE.cfsetispeed(mSerialOptions, speed_arr[i]);
                CLibrary.INSTANCE.cfsetospeed(mSerialOptions, speed_arr[i]);
            }
        }
        setToEffective();
    }

    public void setDevice(String deviceName) {
        closeSerial();
        this.mDevice = deviceName;
        openSerial();
        setToEffective();
    }

    public void setDataBits(int dataBits) {
        switch (dataBits) {
            case DATABITS_FIVE:
                mSerialOptions.c_cflag |= CLibrary.CS5;
                break;
            case DATABITS_SIX:
                mSerialOptions.c_cflag |= CLibrary.CS6;
                break;
            case DATABITS_SEVEN:
                mSerialOptions.c_cflag |= CLibrary.CS7;
                break;
            case DATABITS_EIGHT:
                mSerialOptions.c_cflag |= CLibrary.CS8;
                break;
            default:
                mSerialOptions.c_cflag |= CLibrary.CS8;
        }
        setToEffective();
    }


    public int uartSend(String sendContent) {
        int len = 0;
        len = CLibrary.INSTANCE.write(mSerialFd, sendContent, sendContent.getBytes().length);//实际写入的长度
        if (len == sendContent.getBytes().length) {
            return len;
        } else {
            CLibrary.INSTANCE.tcflush(mSerialFd, CLibrary.TCOFLUSH);//TCOFLUSH刷新写入的数据但不传送
            return -1;
        }

    }

    public int uartReceive(char[] receivedData) {
        int ret = 0;
        long len = 0;
        CLibrary.fd_set fs_read = new CLibrary.fd_set();
        CLibrary.timeval tv_timeout = new CLibrary.timeval();

        CLibrary.INSTANCE.FD_ZERO(fs_read);
        CLibrary.INSTANCE.FD_SET(mSerialFd, fs_read);
        tv_timeout.tv_sec = 0;     // (10*20/115200+5);
        tv_timeout.tv_usec = 0;

        CLibrary.INSTANCE.FD_ZERO(fs_read);
        CLibrary.INSTANCE.FD_SET(mSerialFd, fs_read);
        //如果返回0，代表在描述符状态改变前已超过timeout时间,错误返回-1
        ret = CLibrary.INSTANCE.select(mSerialFd + 1, fs_read, null, null, tv_timeout);
        if (ret != 0) {
            len = CLibrary.INSTANCE.read(mSerialFd, receivedData, receivedData.length);
            Log.i(TAG, "len = " + len);
            if (-1 == len) {
                return -1;
            }
        } else {
            Log.i(TAG, "select fail!");
        }
        return 0;
    }

    public void setStopBits(int stopBits) {
        switch (stopBits) {
            case STOPBITS_ONE:
                mSerialOptions.c_cflag &= ~CLibrary.CSTOPB;
                break;
            case STOPBITS_TWO:
                mSerialOptions.c_cflag |= CLibrary.CSTOPB;
                break;
            default:
                mSerialOptions.c_cflag &= ~CLibrary.CSTOPB;
        }
        setToEffective();
    }

    public void setFlowControl(int flag) {
        switch (flag) {
            case FLOWCTRL_NO://不使用流控制
                mSerialOptions.c_cflag &= ~CLibrary.CRTSCTS;
                break;

            case FLOWCTRL_HW://使用硬件流控制
                mSerialOptions.c_cflag |= CLibrary.CRTSCTS;
                break;
            case FLOWCTRL_SW://使用软件流控制
                mSerialOptions.c_cflag |= CLibrary.IXON | CLibrary.IXOFF | CLibrary.IXANY;
                break;
            default://不使用流控制
                mSerialOptions.c_cflag &= ~CLibrary.CRTSCTS;
        }
        setToEffective();
    }

    public void setParity(char flag) {
        switch (flag) {
            case 'n':
            case 'N': //无奇偶校验位。
                mSerialOptions.c_cflag &= ~CLibrary.PARENB;
                mSerialOptions.c_iflag &= ~CLibrary.INPCK;
                break;
            case 'o':
            case 'O'://设置为奇校验
                mSerialOptions.c_cflag |= (CLibrary.PARODD | CLibrary.PARENB);
                mSerialOptions.c_iflag |= CLibrary.INPCK;
                break;
            case 'e':
            case 'E'://设置为偶校验
                mSerialOptions.c_cflag |= CLibrary.PARENB;
                mSerialOptions.c_cflag &= ~CLibrary.PARODD;
                mSerialOptions.c_iflag |= CLibrary.INPCK;
                break;
            case 's':
            case 'S': //设置为空格
                mSerialOptions.c_cflag &= ~CLibrary.PARENB;
                mSerialOptions.c_cflag &= ~CLibrary.CSTOPB;
                break;
            default://设置为奇校验
                mSerialOptions.c_cflag |= (CLibrary.PARODD | CLibrary.PARENB);
                mSerialOptions.c_iflag |= CLibrary.INPCK;
        }
        setToEffective();
    }

    public void setToEffective() {
        CLibrary.INSTANCE.tcflush(mSerialFd, CLibrary.TCIFLUSH);//溢出数据可以接收，但不读
        CLibrary.INSTANCE.tcsetattr(mSerialFd, CLibrary.TCSANOW, mSerialOptions);
    }

    public void setOutputMode(int outputMode) {
        mSerialOptions.c_oflag = outputMode;
        setToEffective();
    }
    public void setOutputMode(CLibrary.OutputMode outputMode) {
        switch (outputMode)
        {
            case OPOST:mSerialOptions.c_oflag = CLibrary.OPOST;;break;
            case OLCUC:mSerialOptions.c_oflag = CLibrary.OLCUC;;break;
            case ONLCR:mSerialOptions.c_oflag = CLibrary.ONLCR;;break;
            case ONOCR:mSerialOptions.c_oflag = CLibrary.ONOCR;;break;
            case OCRNL:mSerialOptions.c_oflag = CLibrary.OCRNL;;break;
            case ONLRET:mSerialOptions.c_oflag = CLibrary.ONLRET;;break;

        }
        setToEffective();
    }

    public void setClflag(int clflag) {
        mSerialOptions.c_lflag = clflag;
        setToEffective();
    }

    public void setClflag(CLibrary.Clflg clflag) {
        switch (clflag)
        {
            case ISIG:mSerialOptions.c_lflag = CLibrary.ISIG;break;
            case ICANON:mSerialOptions.c_lflag = CLibrary.ICANON;break;
            case ECHO:mSerialOptions.c_lflag = CLibrary.ECHO;break;
            case ECHOE:mSerialOptions.c_lflag = CLibrary.ECHOE;break;
            case ECHOK:mSerialOptions.c_lflag = CLibrary.ECHOK;break;
            case ECHONL:mSerialOptions.c_lflag = CLibrary.ECHONL;break;
            case ECHOPRT:mSerialOptions.c_lflag = CLibrary.ECHOPRT;break;
            case TOSTOP:mSerialOptions.c_lflag = CLibrary.TOSTOP;break;
        }
        setToEffective();
    }
}
