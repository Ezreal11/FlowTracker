package com.zzw.compat;

import com.intellij.openapi.application.ApplicationInfo;
//记录当前IDE的具体信息，最终会输出到本地log，例如版本号等
public class AppInfo {
    private static ApplicationInfo info = ApplicationInfo.getInstance();

    /**
     * such as IntelliJ IDEA
     * @return
     */
    public static String getVersionName() {
        return info.getVersionName();
    }

    /**
     * such as IC-183.4588.61
     * @return
     */
    public static String getApiVersion() {
        return info.getApiVersion();
    }

    /**
     * such as 2018.3.1.0.0
     * @return
     */
    public static String getStrictVersion() {
        return info.getStrictVersion();
    }

    /**
     * such as 2018.3.1
     * @return
     */
    public static String getFullVersion() {
        return info.getFullVersion();
    }

    /**
     * such as 2018
     * @return
     */
    public static String getMajorVersion() {
        return info.getMajorVersion();
    }

    /**
     * such as 3.1
     * @return
     */
    public static String getMinorVersion() {
        return info.getMinorVersion();
    }

    /**
     * such as 2018.3.1
     * @return
     */
    public static String getPlatformVersion() {
        String majorVersion = info.getMajorVersion();
        String minorVersion = info.getMinorVersion();
        return majorVersion + "." + minorVersion;
    }

    public static String getAllVersion() {
        StringBuilder builder = new StringBuilder();
        builder.append("VersionName:").append(info.getVersionName()).
                append("\napiVersion:").append(info.getApiVersion()).
                append("\nstrictVersion:").append(info.getStrictVersion()).
                append("\nfullVersion:").append(info.getFullVersion()).
                append("\nplatformVersion:").append(getPlatformVersion()).
                append("\nmajorVersion:").append(info.getMajorVersion()).
                append("\nminorVersion:").append(info.getMinorVersion()).
                append("\nmicroVersion:").append(info.getMicroVersion()).
                append("\npatchVersion:").append(info.getPatchVersion());
        return builder.toString();
    }
}
