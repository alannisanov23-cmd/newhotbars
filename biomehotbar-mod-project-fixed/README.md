# Biome Hotbar (Fabric, client-side, MC 26.3)

Hotbar background + particles that change with the biome you stand in (1.2s crossfade):

| Biome family | Look |
|---|---|
| Ocean / river / beach | rising bubbles + animated double-layer waves with foam crests |
| Forest / taiga | falling green, gold and orange leaves |
| Snowy / frozen / ice / groves | drifting snowflakes (plus-shaped) |
| Jungle | leaves + fireflies |
| Swamp / mangrove | fireflies + murky dust |
| Desert / badlands / savanna | wind-blown sand / red dust / dry pollen |
| Cherry grove | pink petals |
| Pale garden | grey leaves |
| Meadow / plains | pollen + flower petals |
| Mushroom fields | colourful spores |
| Windswept / peaks | wind streaks + snow |
| Lush caves / dripstone / deep dark | glow berries / water drips / pulsing sculk |
| Nether (wastes, crimson, warped, soul sand, basalt) | embers, spores, soul wisps, ash |
| The End | twinkling stars |

## Enchantment Outlines compatibility
Enchantment Outlines is a resource pack (core shaders + item models), so this mod avoids touching item rendering entirely:
- background + particles are drawn BEFORE the vanilla hotbar (items render on top, unchanged)
- the only things drawn after it are 1px border rows and 3px-high waves, outside the 16x16 item area
- no mixins, no shader/core-shader overrides, no item-render hooks

## Build
Generate a Fabric 26.3 project from https://fabricmc.net/develop/template/ (mod id `biomehotbar`, package `com.biomehotbar`,
Fabric API on), then replace `src/main` with this folder's `src/main`.
