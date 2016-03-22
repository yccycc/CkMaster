#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h> //文件控制定义  
#include <termios.h>//终端控制定义  
#include <errno.h>
char* device="/dev/ttyUSB0";

int serial_fd = 0;

struct termios options;

//default config with fd--ycc1
void defaultConfigWithFd()
{
    //串口主要设置结构体termios <termios.h>
    //struct termios options;

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
    cfsetospeed(&options, B300);//设置波特率
	cfsetispeed(&options, B300);

    /**3. 设置新属性，TCSANOW：所有改变立即生效*/
    tcflush(serial_fd, TCIFLUSH);//溢出数据可以接收，但不读
    tcsetattr(serial_fd, TCSANOW, &options);
}
//open serial--ycc1
int openSerial(char* device)
{
    serial_fd = open(device, O_RDWR | O_NOCTTY | O_NDELAY);
    if (serial_fd < 0) {
        perror("open err");
        return -1;
    }
    return serial_fd;
}

//close
void uart_close()
{
    close(serial_fd);
}

//打开串口并初始化设置  
int init_serial(char* device)
{
    if (openSerial(device) < 0) {
        perror("open error");
        return -1;
    }
    defaultConfigWithFd();
    return 0;
}
void make_set_effect()
{
	tcflush(serial_fd, TCIFLUSH);//溢出数据可以接收，但不读
    tcsetattr(serial_fd, TCSANOW, &options);
}
//***ycc --bb
//set baudrate
void set_baudrate(int speed) {
    int i;
    int speed_arr[] = {B38400, B19200, B9600, B4800, B2400, B1200, B300,
                       B38400, B19200, B9600, B4800, B2400, B1200, B300};
    int name_arr[] = {38400, 19200, 9600, 4800, 2400, 1200, 300, 38400, 19200, 9600, 4800, 2400,
                      1200, 300};
    //设置串口输入波特率和输出波特率
    for (i = 0; i < sizeof(speed_arr) / sizeof(int); i++) {
        if (speed == name_arr[i]) {
            cfsetispeed(&options, speed_arr[i]);
            cfsetospeed(&options, speed_arr[i]);
            printf("set speed = %d\n",speed);
        }
    }
	make_set_effect();
}
//set device
void set_device(char* device)
{
	uart_close();
	serial_fd = open(device, O_RDWR | O_NOCTTY | O_NDELAY);
}
//set data bits
int set_data_bits(int databits)
{
    //设置数据位
    options.c_cflag &= ~CSIZE; //屏蔽其他标志位
    switch (databits)
    {
        case 5    :
            options.c_cflag |= CS5;
            break;
        case 6    :
            options.c_cflag |= CS6;
            break;
        case 7    :
            options.c_cflag |= CS7;
            break;
        case 8:
            options.c_cflag |= CS8;
            break;
        default:
            fprintf(stderr,"Unsupported data size/n");
            return -1;
    }
}
//set flow control
void set_flow_ctrl(int flow_ctrl)
{
    //设置数据流控制
    switch(flow_ctrl)
    {

        case 0 ://不使用流控制
            options.c_cflag &= ~CRTSCTS;
            break;

        case 1 ://使用硬件流控制
            options.c_cflag |= CRTSCTS;
            break;
        case 2 ://使用软件流控制
            options.c_cflag |= IXON | IXOFF | IXANY;
            break;
    }
}
//set parity bits
void set_parity_bits(int parity)
{
    //设置校验位
    switch (parity)
    {
        case 'n':
        case 'N': //无奇偶校验位。
            options.c_cflag &= ~PARENB;
            options.c_iflag &= ~INPCK;
            break;
        case 'o':
        case 'O'://设置为奇校验    
            options.c_cflag |= (PARODD | PARENB);
            options.c_iflag |= INPCK;
            break;
        case 'e':
        case 'E'://设置为偶校验  
            options.c_cflag |= PARENB;
            options.c_cflag &= ~PARODD;
            options.c_iflag |= INPCK;
            break;
        case 's':
        case 'S': //设置为空格 
            options.c_cflag &= ~PARENB;
            options.c_cflag &= ~CSTOPB;
            break;
        default:
            fprintf(stderr,"Unsupported parity/n");
    }
}
//set stop bits
void set_stop_bits(int stopbits)
{
    // 设置停止位 
    switch (stopbits)
    {
        case 1:
            options.c_cflag &= ~CSTOPB;
            break;
        case 2:
            options.c_cflag |= CSTOPB;
            break;
        default:
            fprintf(stderr,"Unsupported stop bits/n");
    }
}
//***ycc --ee

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
int uart_recv_select_while(char *data, int datalen)
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
            printf("len = %d;data=%s(sleep 2 sec)\n", len,data);
            if(-1==len) {
                return -1;
            }
        } else {
            perror("select");
        }
    }
    return 0;
}
//test1 
int ycc_uart_recv_while_noselect(char *data, int datalen)
{
    int len=0, ret = 0;
    while(1)
    {
        len = read(serial_fd, data, datalen);
        printf("len = %d;data=%s(sleep 2 sec)\n", len,data);
        sleep(1);
        if(-1==len) {
            return -1;
        }
    }
    return 0;
}
//test2
int ycc2_uart_recv_noselect_nowhile(char *data, int datalen)
{
    int len=0, ret = 0;
   
	len = read(serial_fd, data, datalen);
	printf("len = %d;data=%s(sleep 2 sec)\n", len,data);
	//sleep(2);
	if(-1==len) {
		return -1;
	}
    return 0;
}
//test3
int ycc3_uart_recv_use_select_no_while(char *data, int datalen)
{
    int len=0, ret = 0;
    fd_set fs_read;
    struct timeval tv_timeout;

    FD_ZERO(&fs_read);
    FD_SET(serial_fd, &fs_read);
    tv_timeout.tv_sec  =2;     // (10*20/115200+5);
    tv_timeout.tv_usec = 0;

	ret = select(serial_fd+1, &fs_read, NULL, NULL, &tv_timeout);
	printf("ret = %d\n", ret);
	//如果返回0，代表在描述符状态改变前已超过timeout时间,错误返回-1

	if(FD_ISSET(serial_fd, &fs_read)) {
		len = read(serial_fd, data, datalen);
		printf("len = %d;data=%s(sleep 2 sec)\n", len,data);
		//sleep(2);
		if(-1==len) {
			printf("len = %d;data=%s\n", len,data);
			return -1;
		}
	} else {
		perror("select");
	}
    return 0;
}

//test4---right---safe_read
int uart_rsv(char *data, int datalen)
{
    int len=0, ret = 0;
    fd_set fs_read;
    struct timeval tv_timeout;

    FD_ZERO(&fs_read);
    FD_SET(serial_fd, &fs_read);
    tv_timeout.tv_sec  =0;     // (10*20/115200+5);
    tv_timeout.tv_usec = 0;

	ret = select(serial_fd+1, &fs_read, NULL, NULL, &tv_timeout);
	printf("ret = %d\n", ret);
	//如果返回0，代表在描述符状态改变前已超过timeout时间,错误返回-1

	if(ret) {
		len = read(serial_fd, data, datalen);
		printf("len = %d;data=%s\n", len,data);
		//sleep(2);
		if(-1==len) {
			printf("len = %d;data=%s\n", len,data);
			return -1;
		}
	} else {
		printf("read data over\n");
		return -1;
	}
    return 0;
}

void testRsv()
{
    char buf1[3];
	uart_rsv(buf1, sizeof(buf1));
    //uart_recv_select_while(buf1, sizeof(buf1));
	//ycc_uart_recv_while_noselect(buf1, sizeof(buf1));
	//ycc2_uart_recv_noselect_nowhile(buf1, sizeof(buf1));
	//ycc3_uart_recv_use_select_no_while(buf1, sizeof(buf1));
    printf("@@@finally-uart-receive=%s\n", buf1);
}
int main(int argc, char **argv)
{
	int i =0;
    //rsv
    init_serial("/dev/pts/4");
    set_baudrate(300);
	//testRsv();
    while (1){
	    printf("rsv-bb**%d\n",++i);
        testRsv();
		printf("rsv-over\n\n");
    } 
    return 0;
}



