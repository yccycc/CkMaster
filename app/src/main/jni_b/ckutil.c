#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h> //文件控制定义  
#include <termios.h>//终端控制定义  
#include <errno.h>
#include "ckutil.h"
char* device="/dev/ttyUSB0";

int serial_fd = 0;

//打开串口并初始化设置  
int init_serial(char* device,int baud_rate,int transfer_data_len)
{
    serial_fd = open(device, O_RDWR | O_NOCTTY | O_NDELAY);
    if (serial_fd < 0) {
        perror("open");
        return -1;
    }

    //串口主要设置结构体termios <termios.h>  
    struct termios options;

    /**1. tcgetattr函数用于获取与终端相关的参数。 
    *参数fd为终端的文件描述符，返回的结果保存在termios结构体中 
    */
    tcgetattr(serial_fd, &options);
    /**2. 修改所获得的参数*/
    options.c_cflag |= (CLOCAL | CREAD);//设置控制模式状态，本地连接，接收使能  
    options.c_cflag &= ~CSIZE;//字符长度，设置数据位之前一定要屏掉这个位  
    options.c_cflag &= ~CRTSCTS;//无硬件流控  
    options.c_cflag |= CS8;//8位数据长度
    options.c_cflag &= ~CSTOPB;//1位停止位  
    options.c_iflag |= IGNPAR;//无奇偶检验位  
    options.c_oflag = 0; //输出模式  
    options.c_lflag = 0; //不激活终端模式  
    cfsetospeed(&options, B115200);//设置波特率

    /**3. 设置新属性，TCSANOW：所有改变立即生效*/
    tcflush(serial_fd, TCIFLUSH);//溢出数据可以接收，但不读  
    tcsetattr(serial_fd, TCSANOW, &options);


    //test-bb
    options.c_cflag &= ~CRTSCTS;
    //
    options.c_cflag |= CRTSCTS;
    //
    options.c_cflag |= IXON | IXOFF | IXANY;

   // NCCS
//    PARODD
    //test-ee


    return 0;
}

/** 
*串口发送数据 
*@fd:串口描述符 
*@data:待发送数据 
*@datalen:数据长度
*/
int uart_send(char *data, int datalen)
{
    int len = 0;
    len = write(serial_fd, data, datalen);//实际写入的长度
    if(len == datalen) {
        return len;
    } else {
        tcflush(serial_fd, TCOFLUSH);//TCOFLUSH刷新写入的数据但不传送
        return -1;
    }

    return 0;
}

/**
*串口接收数据
*要求启动后，在pc端发送ascii文件
*/
int uart_recv(char *data, int datalen)
{
    int len=0, ret = 0;
    fd_set fs_read;
    struct timeval tv_timeout;

    FD_ZERO(&fs_read);
    FD_SET(serial_fd, &fs_read);
    tv_timeout.tv_sec  =2;     // (10*20/115200+5);
    tv_timeout.tv_usec = 0;

    while(FD_ISSET(serial_fd,&fs_read))
    {

        FD_ZERO(&fs_read);
        FD_SET(serial_fd,&fs_read);
        ret = select(serial_fd+1, &fs_read, NULL, NULL, &tv_timeout);
        printf("ret = %d\n", ret);
        //如果返回0，代表在描述符状态改变前已超过timeout时间,错误返回-1

        if(FD_ISSET(serial_fd, &fs_read)) {
            len = read(serial_fd, data, datalen);
            printf("len = %d\n", len);
            if(-1==len) {
                return -1;
            }
        } else {
            perror("select");
        }
    }
    return 0;
}
void uart_close()
{
    close(serial_fd);
}


