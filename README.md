# Origins (NeoForge)

**This mod is still under construction, some functions may not work properly.**

This mod is a full rewrite of the [Origins Mod](https://github.com/Apace100/origins-fabric) on NeoForge platform.

This mod provide an "origin" system. Each of them have special effects, and you can select them when join world or use
`Orb of Origin` items. Also, you can use datapacks to customize origins.

## FAQ

### Some powers missing?

NeoForge and Additional Entity Attributes provides some attributes which can replace them, just use AttributePowers
instead.

Also following powers are not implemented:

- `ModifyEnchantmentLevelPower`, `EdiableItemPower`: Original implementation are too complex, I will find better way to
  implement them later.

### Are datapacks for Fabric version capable with this mod?

Sadly not, Fabric version use their own logic to load datapacks but this mod load them with vanilla methods. They have
different data structure. Also, a lot of powers changed parameters, so you need extra changes to make them work.

You can follow [this guide](https://docs.iafenvoy.com/docs/mod/origins/guides/porting/) to port your datapack to this
mod. You can also try to use [Auto Converter](https://docs.iafenvoy.com/docs/mod/origins/guides/porting/converter) to
convert your datapack, it may not do everything but can save you a lot of time.

## Credit

Special thanks to the following developers for ideas and some code:

- Apace: Author of the [`Origins Mod`](https://github.com/Apace100/origins-fabric), open source under `MIT` license.
- EdwinMindcraft: Author of the [`Forge` port of `Origins Mod`](https://github.com/EdwinMindcraft/origins-forge), open
  source under `MIT` license.
- UltrusBot: Author of [`Alternate Origin GUI`](https://github.com/UltrusBot/AltOriginGui), a better choose origin
  screen, open source under `MIT` license.

## Discord

https://discord.gg/NDzz2upqAk
