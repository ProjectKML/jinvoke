package com.projectkml.jinvoke;

public enum Platform {
    Windows,
    MacOS,
    Linux,
    BSD;

    public static Platform getCurrent() {
        final String osName = System.getProperty("os.name").toLowerCase();
        if(osName.startsWith("windows")) return Windows;
        if(osName.startsWith("mac os")) return MacOS;
        if(osName.startsWith("linux")) return Linux;
        if(osName.startsWith("bsd")) return BSD; //TODO: check
        return MacOS;
    }
}
