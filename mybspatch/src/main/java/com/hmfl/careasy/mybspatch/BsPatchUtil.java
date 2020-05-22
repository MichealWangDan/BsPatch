package com.hmfl.careasy.mybspatch;

/**
 * author : MICHEAL
 * date : 2020/5/20 10:36
 * description :
 */
public class BsPatchUtil {
    static {
        System.loadLibrary("bsdiff-lib");
    }
    /**
     * @param oldApkPath 旧apk文件路径
     * @param newApkPath 新apk文件路径
     * @param patchPath  生成的差分包的存储路径
     */
    public static native void patch(String oldApkPath, String patchPath, String newApkPath);
}
