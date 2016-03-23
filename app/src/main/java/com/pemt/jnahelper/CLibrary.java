/**
 * copyright© www.pemt.com.cn
 * create time: 13-10-29
 */
package com.pemt.jnahelper;

import com.sun.jna.Library;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public interface CLibrary extends Library {

    CLibrary INSTANCE = (CLibrary) com.sun.jna.Native.loadLibrary("c", CLibrary.class);
    //ycc@pemt
    int CLOCAL = 0004000;
    int CREAD = 0000200;
    int CSIZE = 0000060;
    int CRTSCTS = 020000000000;
    int CSTOPB = 0000100;
    int IGNPAR = 0000004;
    int TCIFLUSH = 0;
    int TCSANOW = 0;
    int NCCS = 19;
    int TCOFLUSH = 1;
    int IXON = 0002000;
    int IXOFF = 0010000;
    int IXANY = 0004000;
    int CS5 = 0000000;
    int CS6 = 0000020;
    int CS7 = 0000040;
    int CS8 = 0000060;
    int PARENB = 0000400;
    int INPCK = 0000020;
    int PARODD = 0001000;
    //baud rate
    int B0 = 0000000;
    int B50 = 0000001;
    int B75 = 0000002;
    int B110 = 0000003;
    int B134 = 0000004;
    int B150 = 0000005;
    int B200 = 0000006;
    int B300 = 0000007;
    int B600 = 0000010;
    int B1200 = 0000011;
    int B1800 = 0000012;
    int B2400 = 0000013;
    int B4800 = 0000014;
    int B9600 = 0000015;
    int B19200 = 0000016;
    int B57600 = 0010001;
    int B38400 = 0000017;
    int B115200 = 0010002;

    //OutputMode
    int OPOST = 0000001;
    int OLCUC = 0000002;
    int ONLCR = 0000004;
    int ONOCR = 0000020;
    int OCRNL = 0000010;
    int ONLRET = 0000040;
    enum OutputMode
    {
        OPOST,OLCUC,ONLCR,ONOCR,OCRNL,ONLRET
    }

    //clflg
    int ISIG = 0000001;
    int ICANON = 0000002;
    int ECHO = 0000010;
    int ECHOE = 0000020;
    int ECHOK = 0000040;
    int ECHONL = 0000100;
    int ECHOPRT = 0002000;
    int TOSTOP = 0000400;
    enum Clflg
    {
        ISIG,ICANON,ECHO,ECHOE,ECHOK,ECHONL,ECHOPRT,TOSTOP
    }

    //open flags
    int O_APPEND = 00002000;
    int O_CLOEXEC = 02000000;
    int O_CREAT = 00000100;
    int O_DIRECT = 0200000;
    int O_DIRECTORY = 040000;
    int O_EXCL = 00000200;
    int O_LARGEFILE = 0400000;
    int O_NOATIME = 01000000;
    int O_NOCTTY = 00000400;
    int O_NOFOLLOW = 0100000;
    int O_NONBLOCK = 00004000;
    int O_TRUNC = 00001000;
    int O_RDWR = 00000002;
    int O_NDELAY = 00004000;

    enum OpenMode{
        O_APPEND,O_CLOEXEC,O_CREAT,O_DIRECT,O_DIRECTORY,O_EXCL,O_LARGEFILE,O_NOATIME,O_NOCTTY
        ,O_NOFOLLOW,O_NONBLOCK,O_TRUNC,O_RDWR,O_NDELAY
    }
    class termios extends Structure {
        public int c_iflag;
        public int c_oflag;
        public int c_lflag;
        public int c_cflag;
        public char c_line;
        public char c_cc[] = new char[NCCS];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList("c_iflag", "c_oflag", "c_cflag", "c_lflag", "c_line", "c_cc");
        }
    }

    class fd_set extends Structure {
        public long fds_bits[] = new long[1024 / 8 * 8];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList("fds_bits");
        }
    }

    class timeval extends Structure {
        public long tv_sec;
        public long tv_usec;

        @Override
        protected List getFieldOrder() {
            return Arrays.asList("tv_sec", "tv_usec");
        }
    }

    int open(String pathname, int flags);

    int tcgetattr(int fd, termios ts);

    int write(int fd, String content, int len);

    int tcsetattr(int fd, int optional_actions,
                  termios ts);

    int cfsetospeed(termios ts, int speed);

    int cfsetispeed(termios ts, int speed);

    int tcflush(int fd, int p2);

    int close(int fd);

    long read(int fd, char[] data, int dataLen);

    void FD_ZERO(fd_set fs);

    int select(int a, fd_set fs1, fd_set fs2, fd_set fs3, timeval tv);

    void FD_SET(int fd, fd_set set);

    boolean FD_ISSET(int fd, fd_set set);
}
