package com.autoclicker.mod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

@Mod(modid = AutoClickerMod.MODID, version = AutoClickerMod.VERSION, name = AutoClickerMod.NAME, clientSideOnly = true)
public class AutoClickerMod {
    
    public static final String MODID = "autoclicker";
    public static final String VERSION = "1.0.0";
    public static final String NAME = "Smart AutoClicker";
    
    private static KeyBinding toggleLeftKey;
    private static KeyBinding toggleRightKey;
    
    private static boolean leftClickerEnabled = false;
    private static boolean rightClickerEnabled = false;
    
    private static int leftClickDelay = 0;
    private static int rightClickDelay = 0;
    
    private static int leftCPS = 20;
    private static int rightCPS = 20;
    
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // Register keybindings (will show up in Minecraft controls menu)
        toggleLeftKey = new KeyBinding("Toggle Left AutoClicker", Keyboard.KEY_R, "AutoClicker");
        toggleRightKey = new KeyBinding("Toggle Right AutoClicker", Keyboard.KEY_T, "AutoClicker");
        
        ClientRegistry.registerKeyBinding(toggleLeftKey);
        ClientRegistry.registerKeyBinding(toggleRightKey);
        
        // Register this class for events
        MinecraftForge.EVENT_BUS.register(this);
        
        // Register commands
        ClientCommandHandler.instance.registerCommand(new CommandLCPS());
        ClientCommandHandler.instance.registerCommand(new CommandRCPS());
    }
    
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        
        Minecraft mc = Minecraft.getMinecraft();
        
        // Check if toggle keys were pressed
        if (toggleLeftKey.isPressed()) {
            leftClickerEnabled = !leftClickerEnabled;
            if (mc.thePlayer != null) {
                String status = leftClickerEnabled ? 
                    EnumChatFormatting.GREEN + "Enabled" : 
                    EnumChatFormatting.RED + "Disabled";
                mc.thePlayer.addChatMessage(
                    new ChatComponentText("Left AutoClicker: " + status + 
                    EnumChatFormatting.GRAY + " (" + leftCPS + " CPS)")
                );
            }
        }
        
        if (toggleRightKey.isPressed()) {
            rightClickerEnabled = !rightClickerEnabled;
            if (mc.thePlayer != null) {
                String status = rightClickerEnabled ? 
                    EnumChatFormatting.GREEN + "Enabled" : 
                    EnumChatFormatting.RED + "Disabled";
                mc.thePlayer.addChatMessage(
                    new ChatComponentText("Right AutoClicker: " + status + 
                    EnumChatFormatting.GRAY + " (" + rightCPS + " CPS)")
                );
            }
        }
        
        // Run autoclicker logic
        if (mc.thePlayer != null) {
            if (leftClickerEnabled) {
                tickLeftClicker(mc);
            }
            if (rightClickerEnabled) {
                tickRightClicker(mc);
            }
        }
    }
    
    private void tickLeftClicker(Minecraft mc) {
        // Check if holding a projectile weapon (don't auto-click with projectiles)
        if (mc.currentScreen == null && isHoldingProjectileWeapon(mc)) {
            return;
        }
        
        // Check if looking at a projectile entity
        if (mc.currentScreen == null && isLookingAtProjectile(mc)) {
            return;
        }
        
        // Handle click timing
        if (leftClickDelay > 0) {
            leftClickDelay--;
            return;
        }
        
        // Perform left click
        int tickDelay = Math.max(1, 20 / leftCPS);
        leftClickDelay = tickDelay;
        
        // Check if in inventory or in game
        if (mc.currentScreen != null) {
            // In inventory - simulate inventory click
            try {
                // Click the slot under the mouse
                mc.currentScreen.handleMouseInput();
            } catch (Exception e) {
                // Ignore errors
            }
        } else {
            // In game - normal attack
            if (mc.objectMouseOver != null) {
                if (mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                    mc.playerController.attackEntity(mc.thePlayer, mc.objectMouseOver.entityHit);
                } else if (mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    mc.playerController.clickBlock(
                        mc.objectMouseOver.getBlockPos(), 
                        mc.objectMouseOver.sideHit
                    );
                }
                mc.thePlayer.swingItem();
            } else {
                mc.thePlayer.swingItem();
            }
        }
    }
    
    private void tickRightClicker(Minecraft mc) {
        // Handle click timing
        if (rightClickDelay > 0) {
            rightClickDelay--;
            return;
        }
        
        // Perform right click
        int tickDelay = Math.max(1, 20 / rightCPS);
        rightClickDelay = tickDelay;
        
        // Check if in inventory or in game
        if (mc.currentScreen != null) {
            // In inventory - simulate right click
            try {
                mc.currentScreen.handleMouseInput();
            } catch (Exception e) {
                // Ignore errors
            }
        } else {
            // In game - normal right click
            mc.rightClickMouse();
        }
    }
    
    private boolean isHoldingProjectileWeapon(Minecraft mc) {
        ItemStack heldItem = mc.thePlayer.getHeldItem();
        
        if (heldItem == null) {
            return false;
        }
        
        Item item = heldItem.getItem();
        
        // Check for bow
        if (item instanceof ItemBow) {
            return true;
        }
        
        // Check for throwable items by item ID
        String itemName = Item.itemRegistry.getNameForObject(item).toString();
        
        return itemName.contains("snowball") ||
               itemName.contains("egg") ||
               itemName.contains("ender_pearl") ||
               itemName.contains("potion") ||
               itemName.contains("fishing_rod");
    }
    
    private boolean isLookingAtProjectile(Minecraft mc) {
        MovingObjectPosition objectMouseOver = mc.objectMouseOver;
        
        if (objectMouseOver == null || 
            objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) {
            return false;
        }
        
        // Check if the entity is a projectile
        return objectMouseOver.entityHit instanceof EntityArrow ||
               objectMouseOver.entityHit instanceof EntityThrowable ||
               objectMouseOver.entityHit instanceof EntityFireball;
    }
    
    // Getters and setters for commands
    public static void setLeftCPS(int cps) {
        leftCPS = Math.max(1, Math.min(100, cps)); // Clamp between 1-100
    }
    
    public static void setRightCPS(int cps) {
        rightCPS = Math.max(1, Math.min(100, cps)); // Clamp between 1-100
    }
    
    public static int getLeftCPS() {
        return leftCPS;
    }
    
    public static int getRightCPS() {
        return rightCPS;
    }
}
