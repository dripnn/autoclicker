# How to Build the AutoClicker Mod - Step by Step Guide

## What You Need

1. **Java Development Kit (JDK) 8**
   - Download from: https://adoptium.net/temurin/releases/?version=8
   - Choose your operating system (Windows/Mac/Linux)
   - Install it and remember where you installed it

2. **The mod source files** (provided in the zip)

3. **Internet connection** (for Gradle to download dependencies)

## Step-by-Step Instructions

### Windows Users

1. **Install Java 8**
   - Download and install JDK 8 from the link above
   - After installation, open Command Prompt and type: `java -version`
   - You should see "java version 1.8.x"

2. **Extract the mod files**
   - Extract the provided `autoclicker-source.zip` to a folder
   - Example: `C:\Users\YourName\Desktop\autoclicker`

3. **Open Command Prompt in the mod folder**
   - Navigate to the folder in File Explorer
   - Hold Shift and Right-click in the folder
   - Choose "Open command window here" or "Open PowerShell window here"

4. **Run these commands one at a time:**
   ```
   gradlew setupDecompWorkspace
   ```
   - This takes 5-15 minutes. Wait for it to finish!
   - You'll see "BUILD SUCCESSFUL" when done

   Then run:
   ```
   gradlew build
   ```
   - This takes 2-5 minutes
   - You'll see "BUILD SUCCESSFUL" when done

5. **Find your mod file**
   - Go to `build\libs\` folder
   - You'll find `autoclicker-1.0.0.jar`
   - This is your mod! Copy it to your `.minecraft\mods` folder

### Mac/Linux Users

1. **Install Java 8**
   - Download and install JDK 8 from the link above
   - Open Terminal and type: `java -version`
   - You should see "java version 1.8.x"

2. **Extract the mod files**
   - Extract the provided source files to a folder
   - Example: `~/Desktop/autoclicker`

3. **Open Terminal in the mod folder**
   ```bash
   cd ~/Desktop/autoclicker
   ```

4. **Make gradlew executable and run setup:**
   ```bash
   chmod +x gradlew
   ./gradlew setupDecompWorkspace
   ```
   - This takes 5-15 minutes. Wait for it to finish!

   Then run:
   ```bash
   ./gradlew build
   ```
   - This takes 2-5 minutes

5. **Find your mod file**
   ```bash
   ls build/libs/
   ```
   - You'll see `autoclicker-1.0.0.jar`
   - Copy it to `~/.minecraft/mods/` folder

## Troubleshooting

### "java is not recognized" or "java: command not found"
- Java is not installed or not in your PATH
- Reinstall Java 8 and make sure to check "Add to PATH" during installation
- Or manually add Java to your PATH

### "Could not find ForgeGradle"
- Make sure you have internet connection
- Gradle needs to download files from the internet
- Try running with `--refresh-dependencies`: `gradlew setupDecompWorkspace --refresh-dependencies`

### "setupDecompWorkspace" takes forever
- This is normal! First time takes 10-15 minutes
- Gradle is downloading Minecraft source code and decompiling it
- Let it finish - you'll only need to do this once

### Build fails with errors
- Make sure you're using Java 8, not Java 11, 17, or newer
- Check that all source files are in the correct folders
- Delete `.gradle` folder and try again

### Still having issues?
- Check that your folder structure looks exactly like this:
```
autoclicker/
├── build.gradle
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradlew (or gradlew.bat on Windows)
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── autoclicker/
        │           └── mod/
        │               ├── AutoClickerMod.java
        │               ├── CommandLCPS.java
        │               └── CommandRCPS.java
        └── resources/
            └── mcmod.info
```

## Alternative: Use Pre-built Version

If building is too complicated, you can:
1. Ask someone to build it for you
2. Use online build services
3. Find a pre-compiled version

But building yourself ensures you have the latest version and can make modifications!

## After Building

Once you have `autoclicker-1.0.0.jar`:

1. **Install Forge 1.8.9** if you haven't already
   - Download from: https://files.minecraftforge.net/net/minecraftforge/forge/index_1.8.9.html
   - Run the installer and choose "Install client"

2. **Copy the mod to your mods folder**
   - Windows: `%appdata%\.minecraft\mods\`
   - Mac: `~/Library/Application Support/minecraft/mods/`
   - Linux: `~/.minecraft/mods/`

3. **Launch Minecraft**
   - Use the "Forge 1.8.9" profile
   - Join a world
   - Press R to toggle left autoclicker
   - Use `/lcps 15` to set CPS

Enjoy! 🎮
