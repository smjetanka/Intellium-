package com.bartek.intellium;

public final class IntelliumProfile {
    public enum HardwareType {
        INTEGRATED_EFFICIENT,
        DISCRETE_PERFORMANCE
    }

    public static volatile float frustumMargin = -0.12F;
    public static volatile double maxEntityDistanceSq = 84.0D * 84.0D;
    public static volatile boolean enableAggressiveOcclusion = true;

    private IntelliumProfile() {
    }

    public static void initializeParameters(HardwareType type) {
        if (type == HardwareType.INTEGRATED_EFFICIENT) {
            frustumMargin = -0.20F;
            maxEntityDistanceSq = 72.0D * 72.0D;
            enableAggressiveOcclusion = true;
            return;
        }

        frustumMargin = 0.18F;
        maxEntityDistanceSq = 192.0D * 192.0D;
        enableAggressiveOcclusion = false;
    }
}
