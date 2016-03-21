package com.pemt.util;

import com.pemt.jnahelper.CLibrary;

public class ComUtil {
    private String mDevName = "/dev/tty";
    private int mBaudrate = CLibrary.B115200;
    private int mTransferDataLen = CLibrary.CS8;
    private int mSerialFd = 0;

    public void initCom(String devName,int baudrate,int dataBits)
    {
        this.mDevName = devName;
        this.mBaudrate = baudrate;
        this.mTransferDataLen = dataBits;
        CLibrary.termios options = new CLibrary.termios();
        CLibrary.INSTANCE.tcgetattr(mSerialFd, options);
        options.c_cflag |= (CLibrary.CLOCAL | CLibrary.CREAD);//设置控制模式状态，本地连接，接收使能
        options.c_cflag &= ~CLibrary.CSIZE;//字符长度，设置数据位之前一定要屏掉这个位
        options.c_cflag &= ~CLibrary.CRTSCTS;//无硬件流控
        options.c_cflag |= mTransferDataLen;//8位数据长度
        options.c_cflag &= ~CLibrary.CSTOPB;//1位停止位
        options.c_iflag |= CLibrary.IGNPAR;//无奇偶检验位
        options.c_oflag = 0; //输出模式
        options.c_lflag = 0; //不激活终端模式
       // cfsetospeed(options, mBaudrate);//设置波特率
        /**3. 设置新属性，TCSANOW：所有改变立即生效*/
       // tcflush(mSerialFd, CLibrary.TCIFLUSH);//溢出数据可以接收，但不读
       // tcsetattr(mSerialFd, CLibrary.TCSANOW, options);
    }
}
