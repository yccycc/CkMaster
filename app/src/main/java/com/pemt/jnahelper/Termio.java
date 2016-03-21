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
public class Termio extends Structure {
    public short c_iflag;
    public short c_oflag;
    public short c_cflag;
    public short c_lflag;
    public byte c_line;
    public byte[] c_cc = new byte[8];

    public Termio() {
        super();
    }

    /**
     * @param c_cc C type : unsigned char[8]
     */
    public Termio(short c_iflag, short c_oflag, short c_cflag, short c_lflag, byte c_line, byte c_cc[]) {
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

    public static class ByReference extends Termio implements Structure.ByReference {
    }

    public static class ByValue extends Termio implements Structure.ByValue {
    }
}
