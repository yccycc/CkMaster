/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_pemt_util_UartUtil */

#ifndef _Included_com_pemt_util_UartUtil
#define _Included_com_pemt_util_UartUtil
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_pemt_util_UartUtil
 * Method:    initUart
 * Signature: (Ljava/lang/String;II)I
 */
JNIEXPORT jint JNICALL Java_com_pemt_util_UartUtil_initUart
  (JNIEnv *, jobject, jstring, jint, jint);

/*
 * Class:     com_pemt_util_UartUtil
 * Method:    uartSend
 * Signature: (Ljava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_com_pemt_util_UartUtil_uartSend
  (JNIEnv *, jobject, jstring, jint);

/*
 * Class:     com_pemt_util_UartUtil
 * Method:    uartRecv
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_pemt_util_UartUtil_uartRecv
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_pemt_util_UartUtil
 * Method:    test
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_pemt_util_UartUtil_test
  (JNIEnv *, jobject);

/*
 * Class:     com_pemt_util_UartUtil
 * Method:    closeUart
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_pemt_util_UartUtil_closeUart
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif
