#include "com_pemt_util_UartUtil.h"
#include "ckutil.h"
#include "../../../../../../AppData/Local/Android/sdk/ndk-bundle/platforms/android-23/arch-arm/usr/include/string.h"
jstring strToJstring(JNIEnv *env, const char *pStr) {
    int strLen = strlen(pStr);
    jclass jstrObj = (*env)->FindClass(env, "java/lang/String");
    jmethodID methodId = (*env)->GetMethodID(env, jstrObj, "<init>", "([BLjava/lang/String;)V");
    jbyteArray byteArray = (*env)->NewByteArray(env, strLen);
    jstring encode = (*env)->NewStringUTF(env, "utf-8");

    (*env)->SetByteArrayRegion(env, byteArray, 0, strLen, (jbyte *) pStr);

    return (jstring) (*env)->NewObject(env, jstrObj, methodId, byteArray, encode);
}

char *jstringTostr(JNIEnv *env, jstring jstr) {
    char *pStr = NULL;

    jclass jstrObj = (*env)->FindClass(env, "java/lang/String");
    jstring encode = (*env)->NewStringUTF(env, "utf-8");
    jmethodID methodId = (*env)->GetMethodID(env, jstrObj, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray byteArray = (jbyteArray) (*env)->CallObjectMethod(env, jstr, methodId, encode);
    jsize strLen = (*env)->GetArrayLength(env, byteArray);
    jbyte *jBuf = (*env)->GetByteArrayElements(env, byteArray, JNI_FALSE);

    if (jBuf > 0) {
        pStr = (char *) malloc(strLen + 1);
        if (!pStr) {
            return NULL;
        }
        memcpy(pStr, jBuf, strLen);
        pStr[strLen] = 0;
    }
    (*env)->ReleaseByteArrayElements(env, byteArray, jBuf, 0);
    return pStr;
}
JNIEXPORT jint JNICALL Java_com_pemt_util_UartUtil_initUart
        (JNIEnv *jniEnv, jobject jobject1, jstring jstring1, jint jint1, jint jint2) {
    return init_serial(jstring1, jint1, jint2);
}

JNIEXPORT jint JNICALL Java_com_pemt_util_UartUtil_uartSend
        (JNIEnv * jniEnv, jobject jobject1, jstring jstring1, jint jint1) {
    return uart_send(jstringTostr(jniEnv, jstring1), jint1);
}

JNIEXPORT jstring JNICALL Java_com_pemt_util_UartUtil_uartRecv
        (JNIEnv *jniEnv, jobject jobject1,jint jint1) {
    char data[jint1];
    uart_recv(data, jint1);
    return strToJstring(jniEnv, data);
}

JNIEXPORT void JNICALL Java_com_pemt_util_UartUtil_test
        (JNIEnv *jniEnv, jobject jobject1) {

}

JNIEXPORT void JNICALL Java_com_pemt_util_UartUtil_closeUart
        (JNIEnv * jniEnv, jobject jobject1)
{
    uart_close();
}
