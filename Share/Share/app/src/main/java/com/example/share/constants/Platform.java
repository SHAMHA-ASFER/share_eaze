package com.example.share.constants;
public enum Platform {
    ARDUINO("arduino"),
    MOBILE("mobile"),
    DESKTOP("desktop"),
    UNKNOWN("unkown");

    private final String platform;
    private Platform(String plat) {
        this.platform = plat;
    }

    public String getPlatform() {
        return platform;
    }

    public static Platform fromString(String str) {
        for (Platform plat: Platform.values()) {
            if (plat.platform.equalsIgnoreCase(str)) {
                return plat;
            }
        }
        return Platform.UNKNOWN;
    }
}

