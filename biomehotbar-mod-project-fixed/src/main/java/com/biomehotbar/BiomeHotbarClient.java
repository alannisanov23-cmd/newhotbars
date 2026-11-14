package com.biomehotbar;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.resources.Identifier;

public class BiomeHotbarClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HotbarRenderer r = new HotbarRenderer();
        // Behind the vanilla hotbar: tint + particles (items are drawn later, so they stay untouched).
        HudElementRegistry.attachElementBefore(VanillaHudElements.HOTBAR,
                Identifier.fromNamespaceAndPath("biomehotbar", "back"), r::renderBack);
        // After it: only the 1px border rows and bottom waves, which never overlap the 16x16 item area.
        HudElementRegistry.attachElementAfter(VanillaHudElements.HOTBAR,
                Identifier.fromNamespaceAndPath("biomehotbar", "front"), r::renderFront);
    }
}
