# Smart AutoClicker for Minecraft 1.8.9 (Forge)

A feature-rich Forge mod for Minecraft 1.8.9 with configurable CPS, inventory clicking support, and intelligent projectile detection.

## Features

### Core Features
- ✅ **Left & Right Click AutoClickers**: Independent autoclickers for both mouse buttons
- ✅ **Configurable CPS**: Set any CPS from 1-100 using commands
- ✅ **Inventory Support**: Works in inventories, chests, and GUI screens
- ✅ **Configurable Hotkeys**: Change toggle keys in Minecraft's Controls menu
- ✅ **Smart Projectile Detection**: Automatically disables left-click for bows and throwables
- ✅ **Entity-Aware**: Won't left-click on projectile entities
- ✅ **Client-Side Only**: Works on any server

### Toggle Keybinds (Customizable in Controls)
- **R** - Toggle Left AutoClicker (default)
- **T** - Toggle Right AutoClicker (default)

### Commands
- `/lcps` - Show current left click CPS
- `/lcps <number>` - Set left click CPS (1-100)
- `/rcps` - Show current right click CPS
- `/rcps <number>` - Set right click CPS (1-100)

## Installation

### Prerequisites
1. Minecraft Java Edition 1.8.9
2. [Forge for 1.8.9](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.8.9.html) (Recommended: 11.15.1.2318)

### Steps
1. Install Forge for Minecraft 1.8.9
2. Build this mod (see Building section) or use the compiled JAR
3. Place the `autoclicker-1.0.0.jar` in your `.minecraft/mods` folder
4. Launch Minecraft with the Forge 1.8.9 profile

## Building from Source

### Prerequisites
- JDK 8 (Java 8)
- Internet connection (for Gradle dependencies)

### Project Structure
Create this structure before building:
```
autoclicker/
├── src/main/
│   ├── java/com/autoclicker/mod/
│   │   ├── AutoClickerMod.java
│   │   ├── CommandLCPS.java
│   │   └── CommandRCPS.java
│   └── resources/
│       └── mcmod.info
├── build.gradle
└── README.md
```

### Build Steps

1. **Setup the workspace**
   ```bash
   # On Windows:
   gradlew setupDecompWorkspace
   
   # On Mac/Linux:
   ./gradlew setupDecompWorkspace
   ```
   ⏱️ This takes 5-15 minutes on first run

2. **Build the mod**
   ```bash
   # On Windows:
   gradlew build
   
   # On Mac/Linux:
   ./gradlew build
   ```

3. **Find your mod**
   The compiled JAR will be in `build/libs/autoclicker-1.0.0.jar`

## Usage Guide

### Basic Usage

1. **Launch Minecraft** with Forge 1.8.9
2. **Join a world** or server
3. **Toggle Autoclickers**:
   - Press **R** for left-click autoclicker
   - Press **T** for right-click autoclicker
4. **Adjust CPS**:
   - `/lcps 15` - Set left click to 15 CPS
   - `/rcps 10` - Set right click to 10 CPS

### Status Messages

When you toggle, you'll see:
```
Left AutoClicker: Enabled (20 CPS)
Right AutoClicker: Disabled (20 CPS)
```

### What it Does

#### Left-Click Autoclicker
- Works in **game world** (mining, attacking, breaking blocks)
- Works in **inventories** (clicking items, moving items)
- **Automatically disables** when holding:
  - Bows
  - Snowballs
  - Eggs
  - Ender Pearls
  - Splash Potions
  - Fishing Rods
- **Automatically disables** when looking at projectile entities

#### Right-Click Autoclicker
- Works in **game world** (placing blocks, using items)
- Works in **inventories** (right-click actions)
- **Always active** when enabled (no projectile checks)

### Customizing Keybinds

1. Open Minecraft
2. Go to **Options** → **Controls**
3. Scroll to **AutoClicker** category
4. Click on the keybind you want to change
5. Press your desired key
6. Click **Done**

![Controls Menu Example]
```
AutoClicker Category:
  Toggle Left AutoClicker: R
  Toggle Right AutoClicker: T
```

## Configuration Examples

### Common Use Cases

**Fast Mining (15 CPS left-click)**
```
/lcps 15
[Press R to enable]
```

**Building (10 CPS right-click)**
```
/rcps 10
[Press T to enable]
```

**Inventory Sorting (12 CPS left-click)**
```
/lcps 12
[Press R to enable]
[Open inventory]
```

**PvP Practice (20 CPS both)**
```
/lcps 20
/rcps 20
[Press R and T to enable both]
```

## Advanced Features

### CPS Ranges
- **Low (1-8 CPS)**: Slow, deliberate clicking
- **Medium (9-15 CPS)**: Balanced for most tasks
- **High (16-25 CPS)**: Fast clicking for competitive play
- **Very High (26-100 CPS)**: Extreme speeds (may be flagged on anticheat)

### Inventory Clicking
The mod intelligently handles inventory clicks:
- Left-click: Pick up/place items
- Right-click: Split stacks, special actions
- Works in: Player inventory, chests, furnaces, crafting tables, etc.

### Projectile Safety
When holding a bow or throwable item, the left autoclicker automatically disables to prevent:
- Wasting arrows
- Throwing items unintentionally
- Awkward bow charging

You can still manually left-click when this happens.

## Troubleshooting

### Build Issues

**"Could not find ForgeGradle"**
- Ensure internet connection
- Run `gradlew --refresh-dependencies`

**"Java version mismatch"**
- You MUST use Java 8 for Minecraft 1.8.9
- Download JDK 8: [Oracle](https://www.oracle.com/java/technologies/javase/javase8-archive-downloads.html) or [OpenJDK 8](https://adoptium.net/temurin/releases/?version=8)
- Set `JAVA_HOME` environment variable

**"setupDecompWorkspace failed"**
```bash
# Delete cache and retry:
rmdir /s .gradle     # Windows
rm -rf .gradle       # Mac/Linux
gradlew setupDecompWorkspace --refresh-dependencies
```

### Runtime Issues

**Mod doesn't load**
- Check Forge version is 1.8.9 (not 1.8, 1.8.8, 1.9, etc.)
- Verify JAR is in `.minecraft/mods` folder
- Check logs: `.minecraft/logs/fml-client-latest.log`

**Commands don't work**
- Make sure you're typing `/lcps` or `/rcps` (with slash)
- Check for typos in the command
- Try `/lcps` with no arguments to see current CPS

**Autoclicker not clicking**
- Verify you've toggled it on (press R or T)
- Check you set a reasonable CPS (1-100)
- For left-click: make sure you're not holding a bow/throwable

**Hotkeys don't work**
- Check Controls menu for conflicts
- Make sure another mod isn't using the same key
- Try rebinding to a different key

**Too fast/slow**
- Adjust CPS: `/lcps 15` or `/rcps 10`
- Remember: CPS = Clicks Per Second
- Lower CPS = slower clicking

**Inventory clicking not working**
- Make sure you're in an inventory screen
- Try adjusting CPS (some GUIs need lower CPS)
- Check that the autoclicker is enabled

### Server Compatibility

**Kicked for "Unfair Advantage"**
- Some servers ban autoclickers
- Lower your CPS to more human levels (8-16)
- Disable when not needed
- Check server rules before using

## Important Notes & Warnings

### ⚠️ Server Rules
**ALWAYS check server rules before using autoclickers!**
- Many servers explicitly ban autoclickers
- Using them may result in bans
- PvP servers are especially strict
- When in doubt, ask staff

### ⚠️ Anticheat Systems
High CPS may trigger anticheat:
- Stay under 20 CPS on most servers
- 12-16 CPS is safer for competitive servers
- 8-12 CPS is most "human-like"

### ⚠️ Fair Play
This mod is intended for:
- ✅ Mining and resource gathering
- ✅ Building and placing blocks
- ✅ Inventory management
- ✅ AFK activities
- ❌ NOT for unfair PvP advantages
- ❌ NOT for griefing or trolling

### ⚠️ Performance
Very high CPS (50+) may cause:
- Client lag/stuttering
- Server lag (kicked for spam)
- Mouse input conflicts

Recommended: Stay under 25 CPS for best performance

## Technical Details

- **Minecraft Version**: 1.8.9
- **Forge Version**: 11.15.1.2318-1.8.9 (or compatible)
- **Java Version**: 8
- **Mod Type**: Client-side only
- **Default CPS**: 20 for both left and right
- **CPS Range**: 1-100
- **Tick Rate**: 20 ticks per second (Minecraft standard)

## FAQ

**Q: Does this work on servers?**
A: Yes, it's client-side only. However, many servers ban autoclickers.

**Q: Can I use both autoclickers at once?**
A: Yes! Toggle both with R and T.

**Q: What's a good CPS for mining?**
A: 12-16 CPS is efficient without being suspicious.

**Q: Will this get me banned?**
A: It depends on the server. Always check rules first.

**Q: Can I change the default CPS?**
A: Use `/lcps` and `/rcps` commands. Settings reset when you restart the game.

**Q: Does this work in creative mode?**
A: Yes, in both survival and creative.

**Q: Can I make it click faster than 100 CPS?**
A: The mod caps at 100 CPS for stability. This is already very fast.

**Q: Why does left-click stop working with a bow?**
A: Safety feature to prevent wasting arrows. This is intentional.

## Changelog

### Version 1.0.0
- Initial release
- Left and right click autoclickers
- Configurable CPS (1-100)
- Inventory clicking support
- Configurable keybinds in Controls menu
- Smart projectile detection
- Commands: /lcps and /rcps

## License

MIT License - Free to use, modify, and distribute

## Credits

Created for enhanced Minecraft gameplay on version 1.8.9!

## Support

Having issues? Check:
1. ✅ This README's troubleshooting section
2. ✅ Forge logs (`.minecraft/logs/fml-client-latest.log`)
3. ✅ That you're using Minecraft 1.8.9 with Forge
4. ✅ That all files are in the correct structure
5. ✅ That you're using Java 8

---

**Enjoy smart autoclicking in Minecraft 1.8.9!** 🎮
