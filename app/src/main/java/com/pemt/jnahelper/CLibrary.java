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
    int O_RDWR = 00000002;
    int O_NOCTTY = 00000400;
    int O_NDELAY = 00004000;
    int B115200 = 0010002;
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

    class termios extends Structure
    {
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

    class fd_set extends Structure
    {
        public long fds_bits[] = new long[1024/8*8];
        @Override
        protected List getFieldOrder() {
            return Arrays.asList("fds_bits");
        }
    }

    class timeval extends Structure
    {
        public long tv_sec ;
        public long tv_usec;
        @Override
        protected List getFieldOrder() {
            return Arrays.asList("tv_sec","tv_usec");
        }
    }
    int open(String pathname, int flags);
    int tcgetattr(int fd, termios ts);
    int write(int fd, String content, int len);
    int tcsetattr(int fd, int optional_actions,
                  termios  ts);
    int cfsetospeed(termios  ts, int speed);
    int tcflush(int fd, int p2);
    int close(int fd);
    long read(int fd, char[] data, int dataLen);
    void FD_ZERO(fd_set fs);
    int select(int a,fd_set fs1,fd_set fs2,fd_set fs3,timeval tv);
    void FD_SET(int fd, fd_set set);
    boolean FD_ISSET(int fd, fd_set set);
}
