package com.biomehotbar;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

final class HotbarRenderer {
    private static final int W = 182, H = 22, MAX = 90;
    private static final float FADE_SECONDS = 1.2f;

    private final Random rng = new Random();
    private final List<Particle> parts = new ArrayList<>();
    private Theme theme = Theme.MEADOW, prev = Theme.MEADOW;
    private float blend = 1f, spawnAcc = 0f, clock = 0f, sinceCheck = 99f;
    private long lastNanos = System.nanoTime();

    private static final class Particle {
        Kind kind; float x, y, vx, vy, sway, freq, age, life, phase, baseA; int color, size;
    }

    // ---------- layers ----------
    void renderBack(GuiGraphics g, DeltaTracker dt) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.player == null || mc.player.isSpectator()) return;
        float d = tick(mc);
        int x0 = mc.getWindow().getGuiScaledWidth() / 2 - 91, y0 = mc.getWindow().getGuiScaledHeight() - 22;

        g.enableScissor(x0, y0, x0 + W, y0 + H);
        if (blend < 1f) g.fillGradient(x0, y0, x0 + W, y0 + H, scale(prev.top, 1f - blend), scale(prev.bottom, 1f - blend));
        g.fillGradient(x0, y0, x0 + W, y0 + H, scale(theme.top, blend), scale(theme.bottom, blend));

        updateParticles(d);
        for (Particle p : parts) draw(g, p, x0, y0);
        g.disableScissor();
    }

    void renderFront(GuiGraphics g, DeltaTracker dt) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.player == null || mc.player.isSpectator()) return;
        int x0 = mc.getWindow().getGuiScaledWidth() / 2 - 91, y0 = mc.getWindow().getGuiScaledHeight() - 22;
        int accent = lerp(prev.accent, theme.accent, blend);

        // pulsing accent on top/bottom border rows
        float pulse = 0.55f + 0.25f * (float) Math.sin(clock * 2f);
        g.fill(x0 + 1, y0, x0 + W - 1, y0 + 1, scale(accent, pulse));
        g.fill(x0 + 1, y0 + H - 1, x0 + W - 1, y0 + H, scale(accent, pulse * 0.7f));

        // waves along the bottom for water biomes (fade in/out with the theme blend)
        float wa = (theme.waves ? blend : 0f) + (prev.waves ? 1f - blend : 0f);
        wa = Math.min(1f, wa);
        if (wa > 0.01f) {
            for (int x = 1; x < W - 1; x++) {
                float h1 = 2.2f + 1.3f * (float) Math.sin(x * 0.17f + clock * 2.6f);
                float h2 = 1.6f + 1.0f * (float) Math.sin(x * 0.23f - clock * 2.0f + 1.5f);
                int top2 = y0 + H - (int) Math.ceil(h2);
                int top1 = y0 + H - (int) Math.ceil(h1);
                g.fill(x0 + x, top2, x0 + x + 1, y0 + H, scale(0xFF5FC0F0, 0.35f * wa));
                g.fill(x0 + x, top1, x0 + x + 1, y0 + H, scale(0xFF2F7FD0, 0.45f * wa));
                if (h1 > 3.0f) g.fill(x0 + x, top1, x0 + x + 1, top1 + 1, scale(0xFFFFFFFF, 0.55f * wa)); // foam crest
            }
        }
    }

    // ---------- state ----------
    private float tick(Minecraft mc) {
        long now = System.nanoTime();
        float d = Math.min(0.1f, (now - lastNanos) / 1e9f);
        lastNanos = now; clock += d;
        blend = Math.min(1f, blend + d / FADE_SECONDS);
        sinceCheck += d;
        if (sinceCheck > 0.5f) {
            sinceCheck = 0f;
            Theme t = BiomeResolver.current(mc);
            if (t != theme) { prev = theme; theme = t; blend = 0f; }
        }
        return d;
    }

    private void updateParticles(float d) {
        spawnAcc += theme.rate * d * (0.6f + 0.4f * blend);
        while (spawnAcc >= 1f && parts.size() < MAX) {
            spawnAcc -= 1f;
            Kind k = (theme.kind2 != null && rng.nextFloat() < 0.3f) ? theme.kind2 : theme.kind;
            parts.add(spawn(k));
        }
        spawnAcc = Math.min(spawnAcc, 1f);
        for (int i = parts.size() - 1; i >= 0; i--) {
            Particle p = parts.get(i);
            p.age += d;
            p.x += (p.vx + p.sway * (float) Math.sin(p.age * p.freq + p.phase)) * d;
            p.y += p.vy * d;
            if (p.age >= p.life || p.x < -6 || p.x > W + 6) parts.remove(i);
        }
    }

    private Particle spawn(Kind k) {
        Particle p = new Particle();
        p.kind = k; p.phase = rng.nextFloat() * 6.28f; p.baseA = 0.85f;
        p.color = theme.palette[rng.nextInt(theme.palette.length)];
        p.size = 1; p.freq = 2f; p.sway = 0f;
        p.x = rng.nextFloat() * W; p.y = rng.nextFloat() * H;
        float travel = H + 6;
        switch (k) {
            case BUBBLE -> { p.y = H + 1; p.vy = -(9 + rng.nextFloat() * 13); p.sway = 6; p.freq = 4; p.size = 2 + rng.nextInt(3); p.life = travel / -p.vy; p.baseA = 0.8f; }
            case LEAF -> { p.y = -3; p.vy = 7 + rng.nextFloat() * 6; p.vx = -2 + rng.nextFloat() * 4; p.sway = 10; p.freq = 2.2f; p.life = travel / p.vy; }
            case PETAL -> { p.y = -3; p.vy = 5 + rng.nextFloat() * 5; p.vx = 1 + rng.nextFloat() * 4; p.sway = 8; p.freq = 1.8f; p.life = travel / p.vy; }
            case SNOWFLAKE -> { p.y = -3; p.vy = 6 + rng.nextFloat() * 6; p.sway = 4; p.freq = 1.4f; p.size = rng.nextInt(3) == 0 ? 3 : 1; p.life = travel / p.vy; p.color = 0xFFFFFFFF; }
            case SAND, DUST, ASH -> { p.x = -3; p.vx = 14 + rng.nextFloat() * 16; p.vy = 0; p.sway = 5; p.freq = 3; p.life = (W + 6) / p.vx; p.baseA = 0.7f; }
            case WIND -> { p.x = -5; p.vx = 60 + rng.nextFloat() * 40; p.size = 4; p.life = (W + 10) / p.vx; p.baseA = 0.45f; }
            case POLLEN, SPORE -> { p.vy = -(2 + rng.nextFloat() * 4); p.vx = -2 + rng.nextFloat() * 4; p.sway = 3; p.freq = 1.5f; p.life = 3 + rng.nextFloat() * 3; p.baseA = 0.7f; }
            case EMBER -> { p.y = H + 1; p.vy = -(10 + rng.nextFloat() * 14); p.sway = 5; p.freq = 5; p.life = travel / -p.vy; }
            case SOUL -> { p.y = H + 1; p.vy = -(6 + rng.nextFloat() * 6); p.sway = 7; p.freq = 2.5f; p.life = travel / -p.vy; p.baseA = 0.75f; }
            case STAR -> { p.vx = 1.5f; p.life = 3 + rng.nextFloat() * 3; p.size = rng.nextInt(4) == 0 ? 3 : 1; }
            case DRIP -> { p.y = -3; p.vy = 30 + rng.nextFloat() * 14; p.life = travel / p.vy; }
            case SCULK -> { p.vy = -(1.5f + rng.nextFloat() * 2); p.life = 3 + rng.nextFloat() * 2; p.size = 2; p.baseA = 0.65f; }
            case GLOWBERRY -> { p.y = -3; p.vy = 3 + rng.nextFloat() * 3; p.sway = 2; p.freq = 1.2f; p.size = 2; p.life = travel / p.vy; }
            case FIREFLY -> { p.vx = -3 + rng.nextFloat() * 6; p.vy = -2 + rng.nextFloat() * 4; p.sway = 6; p.freq = 1.7f; p.life = 4 + rng.nextFloat() * 3; }
        }
        return p;
    }

    // ---------- drawing ----------
    private void draw(GuiGraphics g, Particle p, int ox, int oy) {
        float fin = Math.min(1f, p.age / 0.4f), fout = Math.min(1f, (p.life - p.age) / 0.5f);
        float a = p.baseA * Math.max(0f, Math.min(fin, fout)) * (0.35f + 0.65f * blend);
        int x = ox + Math.round(p.x), y = oy + Math.round(p.y);
        switch (p.kind) {
            case BUBBLE -> {
                int s = p.size, c = scale(p.color, a);
                if (s < 3) { g.fill(x, y, x + s, y + s, scale(p.color, a * 0.6f)); }
                else {
                    g.fill(x + 1, y, x + s - 1, y + 1, c);             // top
                    g.fill(x + 1, y + s - 1, x + s - 1, y + s, c);     // bottom
                    g.fill(x, y + 1, x + 1, y + s - 1, c);             // left
                    g.fill(x + s - 1, y + 1, x + s, y + s - 1, c);     // right
                }
                g.fill(x + 1, y + 1, x + 2, y + 2, scale(0xFFFFFFFF, a)); // shine
            }
            case LEAF, PETAL -> {
                boolean flip = ((int) (p.age * 3 + p.phase)) % 2 == 0;
                int c = scale(p.color, a), dark = scale(darker(p.color), a);
                if (flip) { g.fill(x, y, x + 2, y + 1, c); g.fill(x + 1, y + 1, x + 3, y + 2, c); g.fill(x + 3, y + 2, x + 4, y + 3, dark); }
                else      { g.fill(x + 1, y, x + 3, y + 1, c); g.fill(x, y + 1, x + 2, y + 2, c); g.fill(x - 1, y + 2, x, y + 3, dark); }
            }
            case SNOWFLAKE -> {
                int c = scale(p.color, a);
                if (p.size == 1) g.fill(x, y, x + 1, y + 1, c);
                else { g.fill(x + 1, y, x + 2, y + 3, c); g.fill(x, y + 1, x + 3, y + 2, c);
                       int dim = scale(p.color, a * 0.5f);
                       g.fill(x, y, x + 1, y + 1, dim); g.fill(x + 2, y, x + 3, y + 1, dim);
                       g.fill(x, y + 2, x + 1, y + 3, dim); g.fill(x + 2, y + 2, x + 3, y + 3, dim); }
            }
            case SAND, DUST, ASH, POLLEN, SPORE -> g.fill(x, y, x + (p.kind == Kind.SAND ? 2 : 1), y + 1, scale(p.color, a));
            case WIND -> g.fill(x, y, x + p.size, y + 1, scale(p.color, a));
            case EMBER -> {
                float heat = 1f - p.age / p.life;
                int c = lerp(0xFF601008, p.color, heat);
                float flick = 0.7f + 0.3f * (float) Math.sin(p.age * 25 + p.phase);
                g.fill(x, y, x + 1, y + 1, scale(c, a * flick));
            }
            case SOUL -> {
                g.fill(x, y, x + 1, y + 2, scale(p.color, a));
                g.fill(x, y + 2, x + 1, y + 3, scale(p.color, a * 0.4f));
            }
            case STAR -> {
                float tw = 0.4f + 0.6f * (0.5f + 0.5f * (float) Math.sin(p.age * 5 + p.phase));
                int c = scale(p.color, a * tw);
                if (p.size == 1) g.fill(x, y, x + 1, y + 1, c);
                else { g.fill(x + 1, y, x + 2, y + 3, c); g.fill(x, y + 1, x + 3, y + 2, c); }
            }
            case DRIP -> { g.fill(x, y, x + 1, y + 2, scale(p.color, a)); g.fill(x, y, x + 1, y + 1, scale(0xFFFFFFFF, a * 0.6f)); }
            case SCULK -> {
                float pulse = 0.5f + 0.5f * (float) Math.sin(p.age * 4 + p.phase);
                g.fill(x, y, x + 2, y + 2, scale(p.color, a * pulse));
                g.fill(x - 1, y, x, y + 2, scale(p.color, a * pulse * 0.3f)); g.fill(x + 2, y, x + 3, y + 2, scale(p.color, a * pulse * 0.3f));
            }
            case GLOWBERRY -> { g.fill(x, y, x + 2, y + 2, scale(p.color, a)); g.fill(x, y - 1, x + 1, y, scale(0xFF3A7A2A, a)); }
            case FIREFLY -> {
                float blink = Math.max(0f, (float) Math.sin(p.age * 3 + p.phase));
                g.fill(x - 1, y - 1, x + 2, y + 2, scale(p.color, a * blink * 0.25f));
                g.fill(x, y, x + 1, y + 1, scale(p.color, a * blink));
            }
        }
    }

    // ---------- colour helpers ----------
    private static int scale(int argb, float f) {
        int a = Math.max(0, Math.min(255, Math.round(((argb >>> 24) & 0xFF) * f)));
        return (a << 24) | (argb & 0xFFFFFF);
    }
    private static int darker(int c) {
        return (c & 0xFF000000) | (((c >> 16 & 0xFF) * 6 / 10) << 16) | (((c >> 8 & 0xFF) * 6 / 10) << 8) | ((c & 0xFF) * 6 / 10);
    }
    private static int lerp(int a, int b, float t) {
        int r = 0;
        for (int s = 24; s >= 0; s -= 8) {
            int ca = (a >>> s) & 0xFF, cb = (b >>> s) & 0xFF;
            r |= (Math.round(ca + (cb - ca) * t) & 0xFF) << s;
        }
        return r;
    }
}
