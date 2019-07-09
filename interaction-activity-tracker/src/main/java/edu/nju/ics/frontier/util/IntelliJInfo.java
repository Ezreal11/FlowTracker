package edu.nju.ics.frontier.util;

import com.intellij.openapi.application.ApplicationInfo;

public class IntelliJInfo {
    private static ApplicationInfo info = ApplicationInfo.getInstance();

    /**
     * version name of IntelliJ IDEA, such as IntelliJ IDEA
     * @return version name  of IntelliJ IDEA
     */
    public static String getVersionName() {
        return info.getVersionName();
    }

    /**
     * API version of IntelliJ IDEA, such as IC-183.4588.61
     * @return API version of IntelliJ IDEA
     */
    public static String getApiVersion() {
        return info.getApiVersion();
    }

    /**
     * strict version of IntelliJ IDEA, such as 2018.3.1.0.0
     * @return strict version of IntelliJ IDEA
     */
    public static String getStrictVersion() {
        return info.getStrictVersion();
    }

    /**
     * full version  of IntelliJ IDEA, such as 2018.3.1
     * @return full version of IntelliJ IDEA
     */
    public static String getFullVersion() {
        return info.getFullVersion();
    }

    /**
     * major version of IntelliJ IDEA, such as 2018
     * @return major version of IntelliJ IDEA
     */
    public static String getMajorVersion() {
        return info.getMajorVersion();
    }

    /**
     * minor version of IntelliJ IDEA, such as 3.1
     * @return minor version of IntelliJ IDEA
     */
    public static String getMinorVersion() {
        return info.getMinorVersion();
    }

    /**
     * platform version of IntelliJ IDEA, such as 2018.3.1
     * @return platform version of IntelliJ IDEA
     */
    public static String getPlatformVersion() {
        String majorVersion = info.getMajorVersion();
        String minorVersion = info.getMinorVersion();
        return majorVersion + "." + minorVersion;
    }

    /**
     * all version of IntelliJ IDEA
     * @return all version of IntelliJ IDEA
     */
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
