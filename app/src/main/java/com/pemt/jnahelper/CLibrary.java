/**
 * copyright© www.pemt.com.cn
 * create time: 13-10-29
 */
package com.pemt.jnahelper;

import com.sun.jna.Library;
import com.sun.jna.Structure;

import java.util.ArrayList;
import java.util.List;

/**
 * linux c api
 *
 * @author hocking
 */
public interface CLibrary extends Library {

    CLibrary INSTANCE = (CLibrary) com.sun.jna.Native.loadLibrary("c", CLibrary.class);
    //ycc@pemt
    int O_RDWR = 2;
    int O_NOCTTY = 256;
    int O_NDELAY = 2048;
    int B115200 = 4098;
    int CS8 = 48;
    int CLOCAL = 2048;
    int CREAD = 128;
    int CSIZE = 48;
    int CRTSCTS = -2147483648;
    int CSTOPB = 64;
    int IGNPAR = 4;
    int TCIFLUSH = 0;
    int TCSANOW = 0;
    int NCCS = 19;
    int TCOFLUSH = 1;
    int IXON = 0002000;
    int IXOFF = 0010000;
    int IXANY = 0004000;
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
            return new ArrayList();
        }
    }

    class fd_set extends Structure
    {
        public long fds_bits[] = new long[1024/8*8];
        @Override
        protected List getFieldOrder() {
            return new ArrayList();
        }
    }

    class timeval extends Structure
    {
        public long tv_sec ;
        public long tv_usec;
        @Override
        protected List getFieldOrder() {
            return new ArrayList();
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
    void memset(fd_set fs,int c, int n);
    void FD_ZERO(fd_set fs);
    int select(int a,fd_set fs,fd_set fs2,fd_set fs3,timeval tv);
    void FD_SET(int fd, fd_set set);
    boolean FD_ISSET(int fd, fd_set set);
}
