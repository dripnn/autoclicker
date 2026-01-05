package com.autoclicker.mod;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class CommandRCPS extends CommandBase {
    
    @Override
    public String getCommandName() {
        return "rcps";
    }
    
    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/rcps <cps> - Set right click CPS (1-100)";
    }
    
    @Override
    public int getRequiredPermissionLevel() {
        return 0; // No permission required
    }
    
    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            // Show current CPS
            sender.addChatMessage(new ChatComponentText(
                EnumChatFormatting.GOLD + "Right Click CPS: " + 
                EnumChatFormatting.WHITE + AutoClickerMod.getRightCPS()
            ));
            return;
        }
        
        try {
            int cps = Integer.parseInt(args[0]);
            
            if (cps < 1 || cps > 100) {
                sender.addChatMessage(new ChatComponentText(
                    EnumChatFormatting.RED + "CPS must be between 1 and 100!"
                ));
                return;
            }
            
            AutoClickerMod.setRightCPS(cps);
            sender.addChatMessage(new ChatComponentText(
                EnumChatFormatting.GREEN + "Right Click CPS set to " + 
                EnumChatFormatting.WHITE + cps
            ));
            
        } catch (NumberFormatException e) {
            sender.addChatMessage(new ChatComponentText(
                EnumChatFormatting.RED + "Invalid number! Usage: /rcps <number>"
            ));
        }
    }
}
