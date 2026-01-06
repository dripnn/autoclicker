package com.autoclicker.mod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.inventory.Slot;
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
import org.lwjgl.input.Mouse;

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
        // Register keybindings
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
        // Only autoclick if left mouse button is being held down
        if (!mc.gameSettings.keyBindAttack.isKeyDown()) {
            return;
        }
        
        // Handle click timing
        if (leftClickDelay > 0) {
            leftClickDelay--;
            return;
        }
        
        // Set next click delay
        int tickDelay = Math.max(1, 20 / leftCPS);
        leftClickDelay = tickDelay;
        
        // Check if in inventory or in game
        if (mc.currentScreen != null && mc.currentScreen instanceof GuiContainer) {
            // In inventory - handle inventory clicking
            handleInventoryClick((GuiContainer) mc.currentScreen);
        } else if (mc.currentScreen == null) {
            // In game - check for projectile weapons first
            if (isHoldingProjectileWeapon(mc)) {
                return;
            }
            
            if (isLookingAtProjectile(mc)) {
                return;
            }
            
            // Perform game left click
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
    
    private void handleInventoryClick(GuiContainer container) {
        int mouseX = Mouse.getX() * container.width / Minecraft.getMinecraft().displayWidth;
        int mouseY = container.height - Mouse.getY() * container.height / Minecraft.getMinecraft().displayHeight - 1;
        
        try {
            // Just call mouseClicked directly - simulate a left click at current mouse position
            // Use reflection to access the protected method
            java.lang.reflect.Method mouseClicked = container.getClass()
                .getDeclaredMethod("mouseClicked", int.class, int.class, int.class);
            mouseClicked.setAccessible(true);
            mouseClicked.invoke(container, mouseX, mouseY, 0); // 0 = left click
        } catch (NoSuchMethodException e) {
            // Method doesn't exist in this class, try superclass
            try {
                java.lang.reflect.Method mouseClicked = GuiContainer.class
                    .getDeclaredMethod("mouseClicked", int.class, int.class, int.class);
                mouseClicked.setAccessible(true);
                mouseClicked.invoke(container, mouseX, mouseY, 0);
            } catch (Exception ex) {
                // Silently fail
            }
        } catch (Exception e) {
            // Silently fail
        }
    }
    
    private void tickRightClicker(Minecraft mc) {
        // Only autoclick if right mouse button is being held down
        if (!mc.gameSettings.keyBindUseItem.isKeyDown()) {
            return;
        }
        
        // Don't work in GUIs
        if (mc.currentScreen != null) {
            return;
        }
        
        // Check if holding a projectile weapon
        if (isHoldingProjectileWeapon(mc)) {
            return;
        }
        
        // Handle click timing
        if (rightClickDelay > 0) {
            rightClickDelay--;
            return;
        }
        
        // Set next click delay
        int tickDelay = Math.max(1, 20 / rightCPS);
        rightClickDelay = tickDelay;
        
        // Perform right click - place blocks or use items
        if (mc.objectMouseOver != null) {
            if (mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                // Right click on block (place blocks, open doors, etc.)
                mc.playerController.onPlayerRightClick(
                    mc.thePlayer,
                    mc.theWorld,
                    mc.thePlayer.getHeldItem(),
                    mc.objectMouseOver.getBlockPos(),
                    mc.objectMouseOver.sideHit,
                    mc.objectMouseOver.hitVec
                );
            } else {
                // Right click in air (use item like shield)
                mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
            }
        } else {
            // No target - just use item
            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
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
        
        // Check for throwable items
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
        leftCPS = Math.max(1, Math.min(100, cps));
    }
    
    public static void setRightCPS(int cps) {
        rightCPS = Math.max(1, Math.min(100, cps));
    }
    
    public static int getLeftCPS() {
        return leftCPS;
    }
    
    public static int getRightCPS() {
        return rightCPS;
    }
}
