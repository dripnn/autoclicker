package com.autoclicker.mod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.inventory.Container;
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

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
    
    private static boolean debugMode = false;
    private static int inventoryClickAttempts = 0;
    private static int inventoryClickSuccesses = 0;
    
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
                
                // Show inventory stats if was enabled
                if (!leftClickerEnabled && inventoryClickAttempts > 0) {
                    mc.thePlayer.addChatMessage(
                        new ChatComponentText(EnumChatFormatting.YELLOW + 
                        "Inventory clicks: " + inventoryClickSuccesses + "/" + inventoryClickAttempts)
                    );
                    inventoryClickAttempts = 0;
                    inventoryClickSuccesses = 0;
                }
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
            // In inventory - try EVERY possible method
            inventoryClickAttempts++;
            boolean success = handleInventoryClick((GuiContainer) mc.currentScreen, mc);
            if (success) {
                inventoryClickSuccesses++;
            }
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
    
    private boolean handleInventoryClick(GuiContainer container, Minecraft mc) {
        int mouseX = Mouse.getX() * container.width / mc.displayWidth;
        int mouseY = container.height - Mouse.getY() * container.height / mc.displayHeight - 1;
        
        Slot targetSlot = null;
        Container inventoryContainer = null;
        
        // METHOD 1: Try to get inventorySlots field (Container)
        try {
            Field inventorySlotsField = GuiContainer.class.getDeclaredField("inventorySlots");
            inventorySlotsField.setAccessible(true);
            inventoryContainer = (Container) inventorySlotsField.get(container);
        } catch (Exception e) {
            // Try alternate field name
            try {
                Field field = GuiContainer.class.getDeclaredField("field_147002_h");
                field.setAccessible(true);
                inventoryContainer = (Container) field.get(container);
            } catch (Exception e2) {
                // Can't get container
            }
        }
        
        // METHOD 2: Try getSlotAtPosition method
        try {
            Method getSlotMethod = GuiContainer.class.getDeclaredMethod("getSlotAtPosition", int.class, int.class);
            getSlotMethod.setAccessible(true);
            targetSlot = (Slot) getSlotMethod.invoke(container, mouseX, mouseY);
        } catch (Exception e) {
            // Try obfuscated name
            try {
                Method getSlotMethod = GuiContainer.class.getDeclaredMethod("func_146975_c", int.class, int.class);
                getSlotMethod.setAccessible(true);
                targetSlot = (Slot) getSlotMethod.invoke(container, mouseX, mouseY);
            } catch (Exception e2) {
                // Method doesn't exist
            }
        }
        
        // METHOD 3: If getSlotAtPosition failed, try getting hoveredSlot field
        if (targetSlot == null) {
            try {
                Field hoveredSlotField = GuiContainer.class.getDeclaredField("hoveredSlot");
                hoveredSlotField.setAccessible(true);
                targetSlot = (Slot) hoveredSlotField.get(container);
            } catch (Exception e) {
                // Try obfuscated name
                try {
                    Field field = GuiContainer.class.getDeclaredField("field_147006_u");
                    field.setAccessible(true);
                    targetSlot = (Slot) field.get(container);
                } catch (Exception e2) {
                    // Try another obfuscated name
                    try {
                        Field field = GuiContainer.class.getDeclaredField("theSlot");
                        field.setAccessible(true);
                        targetSlot = (Slot) field.get(container);
                    } catch (Exception e3) {
                        // Can't find slot field
                    }
                }
            }
        }
        
        // METHOD 4: Manual slot detection by iterating through all slots
        if (targetSlot == null && inventoryContainer != null) {
            try {
                Field xPosField = GuiContainer.class.getDeclaredField("guiLeft");
                Field yPosField = GuiContainer.class.getDeclaredField("guiTop");
                xPosField.setAccessible(true);
                yPosField.setAccessible(true);
                int guiLeft = (Integer) xPosField.get(container);
                int guiTop = (Integer) yPosField.get(container);
                
                for (Object slotObj : inventoryContainer.inventorySlots) {
                    Slot slot = (Slot) slotObj;
                    int slotX = guiLeft + slot.xDisplayPosition;
                    int slotY = guiTop + slot.yDisplayPosition;
                    
                    if (mouseX >= slotX && mouseX < slotX + 16 && 
                        mouseY >= slotY && mouseY < slotY + 16) {
                        targetSlot = slot;
                        break;
                    }
                }
            } catch (Exception e) {
                // Manual detection failed
            }
        }
        
        // If we found a slot AND container, perform the click
        if (targetSlot != null && inventoryContainer != null) {
            try {
                // Use windowClick - the proper way
                mc.playerController.windowClick(
                    inventoryContainer.windowId,
                    targetSlot.slotNumber,
                    0, // left mouse button
                    0, // click mode: normal click
                    mc.thePlayer
                );
                return true;
            } catch (Exception e) {
                // Click failed
                return false;
            }
        }
        
        // METHOD 5: Last resort - try calling mouseClicked via reflection
        try {
            Method mouseClickedMethod = null;
            Class<?> currentClass = container.getClass();
            
            // Walk up class hierarchy to find mouseClicked
            while (currentClass != null && mouseClickedMethod == null) {
                try {
                    mouseClickedMethod = currentClass.getDeclaredMethod("mouseClicked", int.class, int.class, int.class);
                } catch (NoSuchMethodException e) {
                    // Try obfuscated name
                    try {
                        mouseClickedMethod = currentClass.getDeclaredMethod("func_73864_a", int.class, int.class, int.class);
                    } catch (NoSuchMethodException e2) {
                        currentClass = currentClass.getSuperclass();
                    }
                }
            }
            
            if (mouseClickedMethod != null) {
                mouseClickedMethod.setAccessible(true);
                mouseClickedMethod.invoke(container, mouseX, mouseY, 0);
                return true;
            }
        } catch (Exception e) {
            // Reflection failed
        }
        
        return false;
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
        
        // Perform right click
        if (mc.objectMouseOver != null) {
            if (mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                mc.playerController.onPlayerRightClick(
                    mc.thePlayer,
                    mc.theWorld,
                    mc.thePlayer.getHeldItem(),
                    mc.objectMouseOver.getBlockPos(),
                    mc.objectMouseOver.sideHit,
                    mc.objectMouseOver.hitVec
                );
            } else {
                mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
            }
        } else {
            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
        }
    }
    
    private boolean isHoldingProjectileWeapon(Minecraft mc) {
        ItemStack heldItem = mc.thePlayer.getHeldItem();
        
        if (heldItem == null) {
            return false;
        }
        
        Item item = heldItem.getItem();
        
        if (item instanceof ItemBow) {
            return true;
        }
        
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
        
        return objectMouseOver.entityHit instanceof EntityArrow ||
               objectMouseOver.entityHit instanceof EntityThrowable ||
               objectMouseOver.entityHit instanceof EntityFireball;
    }
    
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
