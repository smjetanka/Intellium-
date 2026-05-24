package pl.dirtowiec.intellium.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import org.lwjgl.opengl.GL11;

public class IntelliumClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            System.out.println("[Intellium] Starting hardware optimizer...");

            try {
                String renderer = GL11.glGetString(GL11.GL_RENDERER);
                System.out.println("[Intellium] Detected GPU renderer: " + renderer);

                if (renderer != null && renderer.toLowerCase().contains("intel")) {
                    System.out.println("[Intellium] STATUS: Intel iGPU detected. Loading i3 profile...");
                } else {
                    System.out.println("[Intellium] STATUS: Non-Intel GPU detected. Waiting mode.");
                }
            } catch (Throwable t) {
                System.out.println("[Intellium] Hardware detection error: " + t.getMessage());
            }
        });
    }
}
