# Notes: uku's Armor HUD

Reference notes for a possible armor HUD feature in this mod.

- Mod: "uku's Armor HUD" (Modrinth id `ukus-armor-hud`), client-side, Fabric + NeoForge
- Source: https://github.com/uku3lig/armor-hud (branch `1.21.11` = version 0.10.2 for MC 1.21.11, default branch is MC 26.x)
- Fork of BerdinskiyBear's Armor HUD, **MIT license** (MIT code can be reused in GPL-3.0 code, but the MIT copyright notice has to be kept)
- Depends on uku's library `ukulib` (config screen, toggle keybind)

## What it looks like

A small vanilla-style bar that shows the 4 armor pieces you are wearing, by default directly **left of the hotbar**
(on the side of the offhand slot). No text or numbers by default, only the items with their normal durability bars.

- Each slot is 20 px wide, the whole widget is 22 px high: width = `22 + (slots - 1) * 20`
  (same slot grid as the vanilla hotbar, 4 slots = 82 x 22 px)
- Background is cut out of the vanilla `hud/hotbar` sprite (182 x 22): the left part for the slots plus the
  last 3 px of the hotbar as the right border, so it looks like a short piece of hotbar
- Items are drawn with the vanilla hotbar slot renderer at `slotX + 3, slotY + 3` (16 x 16 item, durability bar, enchant glint)
- Order is helmet, chestplate, leggings, boots (left to right), can be reversed
- Empty slots (if they are shown) get the vanilla empty armor slot icons from the inventory
  (`container/slot/helmet` etc.)
- **Low durability warning**: an 8 x 8 red round icon (`warn.png`, red with light pink highlights) drawn centered
  above the slot. It jitters up/down by a random amount each frame (`warningBobIntensity`, default 3 px)
- **Break warning sound**: when a worn armor piece loses durability while it is already in the warning range,
  a custom sound `armor_breaking` (category "player") is played

### Styles (`style`)
- `HOTBAR` (default): piece of the hotbar texture, square corners
- `ROUNDED_CORNERS`: middle from the hotbar, left/right 3 px edges from the offhand slot sprite (`hud/hotbar_offhand_left`, 29 x 24)
- `ROUNDED`: every slot built from the offhand slot sprite (each slot looks like a rounded offhand slot)
- `NONE`: no background, only the items

### Orientation
- `HORIZONTAL` (default) or `VERTICAL` (the background is rotated by 90 degrees, slots go downwards)

## How it works

### Position (`anchor`, `side`, `offsetX`, `offsetY`)
- `HOTBAR` (default): next to the hotbar. x = `screenWidth / 2 +- (98 + extra)`, y = `screenHeight - 22 - offsetY`.
  98 = half hotbar width (91) + 7 px gap
- `TOP_CENTER`, `TOP` (top corner), `VERT_CENTER` (middle of the left/right edge), `BOTTOM` (bottom corner)
- `side`: `LEFT` (default) or `RIGHT`
- `offsetX` / `offsetY`: pixels, positive = away from the anchor

### Offhand slot / attack indicator (`offhandSlotBehavior`, only with anchor `HOTBAR`)
- `ADHERE` (default): moves 29 px further out when the offhand slot is visible on that side, or 23 px when the
  attack indicator is set to "hotbar" and currently showing on that side
- `ALWAYS_IGNORE`: never moves
- `ALWAYS_LEAVE_SPACE`: always leaves that space

### Which slots are shown (`widgetShown`)
- `NOT_EMPTY` (default): only worn pieces, nothing at all when no armor is worn
- `ALWAYS`: always 4 slots
- `IF_ANY_PRESENT`: all 4 slots as soon as one piece is worn
- `DAMAGED_PIECES`: only pieces that are in the warning range

### Durability
- `durabilityDisplay`: `BAR` (default, only the vanilla bar), `NUMERIC` (remaining durability as a number),
  `PERCENTAGE` (e.g. `37%`, empty when undamaged). Text is drawn above the slot (below it when anchored at the top),
  coloured with the item's durability bar colour
- Warning range: remaining durability `<= minDurabilityValue` (default 20) **or** remaining percentage
  `<= minDurabilityPercentage` (default 0.1 = 10 %)
- Newer versions (26.x) can also show durability text / warning for the offhand item (`offHandDurability`, default on)
  and the main hand item (`mainHandDurability`, default off, outlined text over the selected hotbar slot)

### Pushing other HUD elements out of the way
- `pushBossbars`: with `TOP_CENTER`, boss bars are moved down below the widget
- `pushStatusEffectIcons`: with `TOP` + `RIGHT`, the effect icons are moved down
- `pushSubtitles`: with `BOTTOM` + `RIGHT`, subtitles are moved up

### Other
- `enabled` master switch, plus a toggle keybind (unbound by default)
- `reversed`, `iconsShown`, `warningShown`, `playBreakSound`
- HUD opacity can follow other mods (compat layer), e.g. Bedrockify's screen safe area

### 1.21.11 implementation details
- Rendering: `@Inject(method = "renderItemHotbar", at = @At("TAIL"))` in `Gui`, uses the shadowed
  `Gui.renderSlot(GuiGraphics, x, y, DeltaTracker, Player, ItemStack, seed)` and the sprites
  `Gui.HOTBAR_SPRITE` / `Gui.HOTBAR_OFFHAND_LEFT_SPRITE`
- Drawing the background: `graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, spriteW, spriteH, u, v, x, y, w, h, color)`
- Armor items: `player.getItemBySlot(slot)` for the armor slots of `InventoryMenu.SLOT_IDS`
  (HEAD, CHEST, LEGS, FEET)
- Effect icons: `@ModifyVariable` on the y variable in `Gui.renderEffects`; boss bars: `BossHealthOverlay.render`;
  subtitles: `SubtitleOverlay.render`
- Break sound: `Minecraft.tick` RETURN, compares the damage value of every slot with the last tick
- Config: JSON via ukulib, default values listed above
