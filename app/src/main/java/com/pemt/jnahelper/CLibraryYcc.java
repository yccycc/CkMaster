/**
 * copyright© www.pemt.com.cn
 * create time: 13-10-29
 */
package com.pemt.jnahelper;

import com.sun.jna.Library;

public interface CLibraryYcc extends Library {

    CLibraryYcc INSTANCE = (CLibraryYcc) com.sun.jna.Native.loadLibrary("ckutil", CLibraryYcc.class);
    int yccadd(int a,int b);
}
