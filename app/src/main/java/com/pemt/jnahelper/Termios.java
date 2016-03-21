/**
 * copyright© www.pemt.com.cn
 * create time: 14-3-25
 */
package com.pemt.jnahelper;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

/**
 * @author hocking
 */
public class Termios extends Structure {

    /**
     * ioctl type
     */
    public static final int TCGETS = 0x5401;
    public static final int TCSANOW = 0x5402;
    public static final int TCSADRAIN = 0x5403;
    public static final int TCSAFLUSH = 0x5404;

    /**
     * baudrate
     */
    public static final int BAUDRATE[] = {50, 75, 110, 134, 150, 200, 300, 600, 1200, 1800, 2400, 4800, 9600, 19200,
            38400, 57600, 115200, 230400, 460800, 500000, 576000, 921600, 1000000, 1152000, 1500000, 2000000, 2500000,
            3000000, 3500000, 4000000};
    public static final int BAUDRATE_VALUE[] = {0000001, 0000002, 0000003, 0000004, 0000005, 0000006, 0000007, 0000010,
            0000011, 0000012, 0000013, 0000014, 0000015, 0000016, 0000017, 0010001, 0010002, 0010003, 0010004, 0010005
            , 0010006, 0010007, 0010010, 0010011, 0010012, 0010013, 0010014, 0010015, 0010016, 0010017};

    public int c_iflag;
    public int c_oflag;
    public int c_cflag;
    public int c_lflag;
    public byte c_line;
    public byte[] c_cc = new byte[19];

    public Termios() {
        super();
    }

    public Termios(int c_iflag, int c_oflag, int c_cflag, int c_lflag, byte c_line, byte c_cc[]) {
        super();
        this.c_iflag = c_iflag;
        this.c_oflag = c_oflag;
        this.c_cflag = c_cflag;
        this.c_lflag = c_lflag;
        this.c_line = c_line;
        if ((c_cc.length != this.c_cc.length))
            throw new IllegalArgumentException("Wrong array size !");
        this.c_cc = c_cc;
    }

    protected List<?> getFieldOrder() {
        return Arrays.asList("c_iflag", "c_oflag", "c_cflag", "c_lflag", "c_line", "c_cc");
    }

    public static class ByReference extends Termios implements Structure.ByReference {
    }

    public static class ByValue extends Termios implements Structure.ByValue {
    }
}
