package com.bartek.intellium;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public final class IntelliumCore implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("intellium");

    public static volatile boolean sodiumLoaded;
    public static volatile IntelliumProfile.HardwareType activeHardwareType = IntelliumProfile.HardwareType.INTEGRATED_EFFICIENT;

    @Override
    public void onInitializeClient() {
        sodiumLoaded = FabricLoader.getInstance().isModLoaded("sodium");
        IntelliumProfile.initializeParameters(activeHardwareType);
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> detectHardwareAndApplyProfile());

        LOGGER.info("Intellium initialized. sodiumLoaded={}", sodiumLoaded);
    }

    private static void detectHardwareAndApplyProfile() {
        String renderer;
        try {
            renderer = GL11.glGetString(GL11.GL_RENDERER);
        } catch (RuntimeException ex) {
            renderer = null;
        }
        if (renderer == null || renderer.isEmpty()) {
            activeHardwareType = IntelliumProfile.HardwareType.INTEGRATED_EFFICIENT;
            IntelliumProfile.initializeParameters(activeHardwareType);
            LOGGER.info("OpenGL renderer string not available. Default profile applied: {}", activeHardwareType);
            return;
        }

        String normalized = renderer.toUpperCase(Locale.ROOT);
        IntelliumProfile.HardwareType detected;

        if (normalized.contains("INTEL")) {
            if (normalized.contains("ARC") || normalized.contains("DG1") || normalized.contains("DG2")) {
                detected = IntelliumProfile.HardwareType.DISCRETE_PERFORMANCE;
            } else if (normalized.contains("UHD") || normalized.contains("IRIS") || normalized.contains("HD GRAPHICS")) {
                detected = IntelliumProfile.HardwareType.INTEGRATED_EFFICIENT;
            } else {
                detected = IntelliumProfile.HardwareType.INTEGRATED_EFFICIENT;
            }
        } else {
            detected = IntelliumProfile.HardwareType.DISCRETE_PERFORMANCE;
        }

        activeHardwareType = detected;
        IntelliumProfile.initializeParameters(detected);
        LOGGER.info(
                "Renderer='{}', profile={}, frustumMargin={}, maxEntityDistanceSq={}, aggressiveOcclusion={}",
                renderer,
                detected,
                IntelliumProfile.frustumMargin,
                IntelliumProfile.maxEntityDistanceSq,
                IntelliumProfile.enableAggressiveOcclusion
        );
    }
}
