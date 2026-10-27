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

**Armor HUD** (own settings page, opened with the "Armor HUD..." button)
- Shows the armor you are wearing in a small vanilla-style widget next to the hotbar
- Warning icon and sound when a piece is about to break
- Position, side, style, orientation, durability display (bar / number / percentage) and more can be changed
- Optional key to toggle it (Options > Controls > Key Binds, unbound by default)

**Sword PvP** (own settings page, opened with the "Sword PvP..." button)
- **Low Health Alert**: plays a note block sound once when your health drops to the set number of hearts (0.5 - 10)
- **W-Tap Alert**: after you hit something, plays a note block sound as soon as you can really W-tap and hit again:
  your attack is fully charged **and** the target lost its damage immunity (its red hurt animation is over).
  No sound when the target died, despawned or is out of range
- Every alert has its own instrument (all 16 note block instruments) and note (0 - 24, like a note block),
  plus a shared volume and test buttons

Settings are saved to `config/liglps-pvp-overhaul.json`.

## Requirements
- Minecraft 1.21.11
- Fabric Loader 0.17.0+
- Fabric API

## Building
```
./gradlew build
```
The jar ends up in `build/libs/`.

## Credits
- **Armor HUD** is based on [uku's Armor HUD](https://github.com/uku3lig/armor-hud)
  by **BerdinskiyBear** and **uku**, used under the MIT License (see [THIRD_PARTY_LICENSES.md](THIRD_PARTY_LICENSES.md)).
  It works without ukulib.

## License
GPL-3.0, see [LICENSE](LICENSE). Parts based on third-party code keep their original license,
see [THIRD_PARTY_LICENSES.md](THIRD_PARTY_LICENSES.md).
