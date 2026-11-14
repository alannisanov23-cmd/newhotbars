package com.biomehotbar;

/** One look per biome family. Colours are ARGB. */
enum Theme {
    //     main kind        2nd kind        panel top   panel bottom accent      waves rate palette...
    MEADOW(Kind.POLLEN,     Kind.PETAL,     0x30A8D860, 0x40387020, 0xFFB8E060, false, 6,  0xFFFFE680, 0xFFFFFFFF, 0xFFFF9AC8),
    FOREST(Kind.LEAF,       null,           0x302E6B2A, 0x40204A1C, 0xFF7DBB32, false, 6,  0xFF4C9A2A, 0xFF7DBB32, 0xFFC9A227, 0xFFD9731F, 0xFF8B4A1B),
    WATER(Kind.BUBBLE,      null,           0x403C8CE0, 0x600A3A8A, 0xFF6FD0FF, true,  7,  0xFFBFEFFF, 0xFF9FDFFF),
    SNOW(Kind.SNOWFLAKE,    null,           0x40D8F0FF, 0x409CC8E8, 0xFFE8F8FF, false, 9,  0xFFFFFFFF, 0xFFD8F0FF),
    JUNGLE(Kind.LEAF,       Kind.FIREFLY,   0x3040A020, 0x50106018, 0xFF50E040, false, 6,  0xFF2E9A1E, 0xFF60C830, 0xFFE0E040),
    SWAMP(Kind.FIREFLY,     Kind.DUST,      0x40506030, 0x50283A20, 0xFFA0C040, false, 4,  0xFFD8F060, 0xFF90C040, 0xFF6A7A40),
    DESERT(Kind.SAND,       null,           0x40F0D890, 0x50C8A050, 0xFFF0D080, false, 8,  0xFFE8C878, 0xFFD8B060),
    BADLANDS(Kind.DUST,     null,           0x40E08050, 0x50903820, 0xFFD9743A, false, 7,  0xFFD9743A, 0xFFB5502A, 0xFFE8A070),
    SAVANNA(Kind.DUST,      Kind.POLLEN,    0x40F0B040, 0x50A06020, 0xFFE8B050, false, 6,  0xFFE8B050, 0xFFFFD070),
    CHERRY(Kind.PETAL,      null,           0x40FFC0E0, 0x40D070A0, 0xFFFF9FC8, false, 7,  0xFFFFB7D5, 0xFFFF8FBF, 0xFFFFD6E8),
    PALE(Kind.LEAF,         Kind.DUST,      0x30C0C0B8, 0x40585850, 0xFFB0B0A8, false, 4,  0xFFB0B0A8, 0xFF8E9088, 0xFFD0D0C8),
    MUSHROOM(Kind.SPORE,    null,           0x40A040A0, 0x50503060, 0xFFE060E0, false, 7,  0xFFE060E0, 0xFFFF9090, 0xFFC0A0FF),
    MOUNTAIN(Kind.WIND,     Kind.SNOWFLAKE, 0x30B0C8E0, 0x40607080, 0xFFD0E0F0, false, 5,  0xFFFFFFFF, 0xFFD0E0F0),
    LUSH(Kind.GLOWBERRY,    Kind.SPORE,     0x4060C050, 0x50205030, 0xFF9AF060, false, 4,  0xFFFFD84A, 0xFF9AF060),
    DRIPSTONE(Kind.DRIP,    null,           0x40907050, 0x50403020, 0xFFB09070, false, 3,  0xFF9CC8F0),
    DEEP_DARK(Kind.SCULK,   null,           0x40001018, 0x600A2A30, 0xFF30D8E8, false, 4,  0xFF30D8E8, 0xFF1A8CA0),
    NETHER(Kind.EMBER,      null,           0x50C03010, 0x60601008, 0xFFFF7A1A, false, 9,  0xFFFF7A1A, 0xFFFFB030, 0xFFFF4010),
    SOUL(Kind.SOUL,         null,           0x4030A0B0, 0x60103840, 0xFF60E0F0, false, 5,  0xFF60E0F0, 0xFFA0F8FF),
    CRIMSON(Kind.SPORE,     Kind.EMBER,     0x50A01030, 0x60400818, 0xFFE03050, false, 7,  0xFFE03050, 0xFFFF6070),
    WARPED(Kind.SPORE,      null,           0x4020A090, 0x60083838, 0xFF30E0B0, false, 7,  0xFF30E0B0, 0xFF20A0A0),
    BASALT(Kind.ASH,        Kind.EMBER,     0x40505058, 0x60202024, 0xFF9A9A9A, false, 9,  0xFF8A8A8A, 0xFFB0B0B0, 0xFFFF7A1A),
    END(Kind.STAR,          null,           0x408040C0, 0x60100020, 0xFFC080FF, false, 6,  0xFFF0E0FF, 0xFFC080FF, 0xFFFFFFFF);

    final Kind kind, kind2;
    final int top, bottom, accent, rate;
    final boolean waves;
    final int[] palette;

    Theme(Kind kind, Kind kind2, int top, int bottom, int accent, boolean waves, int rate, int... palette) {
        this.kind = kind; this.kind2 = kind2; this.top = top; this.bottom = bottom;
        this.accent = accent; this.waves = waves; this.rate = rate; this.palette = palette;
    }
}
