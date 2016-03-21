/**
 * copyright© www.pemt.com.cn
 * create time: 13-10-29
 */
package com.pemt.jnahelper;

import com.sun.jna.Library;

public interface CkUtilLibrary extends Library {

    CkUtilLibrary INSTANCE = (CkUtilLibrary) com.sun.jna.Native.loadLibrary("ckutil", CkUtilLibrary.class);
    int init_serial();
}
