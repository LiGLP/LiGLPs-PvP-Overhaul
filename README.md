# LiGLP's PvP Overhaul

A client-side Fabric mod for Minecraft **1.21.11**.

Press **Esc** and click the skin button left of **Options...** to open the settings menu.

## Features

**General**
- **Clear Water / Clear Lava / Clear Powder Snow**: removes the fog (and the water / frozen screen overlays)
- **Fullbright**: everything is fully lit
- **Fire View**: hides the fire on your screen while you are burning

**Crystal category** (toggle all at once with the category button, or one by one)
- **Small Totem & Totem Pop**: smaller held totem, smaller totem pop animation, also smaller food
- **No Ender Pearl SFX**: mutes the ender pearl throw and teleport sounds
- **No Crystal Explosion VFX**: hides explosion particles
- **Lower Shield**: renders your shield lower in first person

Settings are saved to `config/liglps-pvp-overhaul.json`.

## Requirements
- Minecraft 1.21.11
- Fabric Loader 0.20.1+
- Fabric API

## Building
```
./gradlew build
```
The jar ends up in `build/libs/`.

## License
GPL-3.0, see [LICENSE](LICENSE).
