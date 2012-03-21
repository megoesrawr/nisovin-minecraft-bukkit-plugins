package com.nisovin.yapp.menu;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;

import com.nisovin.yapp.Group;
import com.nisovin.yapp.MainPlugin;
import com.nisovin.yapp.PermissionContainer;

public class HasGroup extends MenuPrompt {

	@Override
	public String getPromptText(ConversationContext context) {
		return Menu.TEXT_COLOR + "Please type the group you want to check:";
	}

	@Override
	public Prompt accept(ConversationContext context, String input) {
		input = input.trim();

		// get group
		Group g = MainPlugin.getGroup(input);
		if (g == null) {
			context.getForWhom().sendRawMessage(MainPlugin.ERROR_COLOR + "That group does not exist");
			return this;
		}
		
		// get other stuff
		PermissionContainer obj = getObject(context);
		String world = getWorld(context);
		String type = getType(context);		
		
		if (obj.inGroup(world, g, false)) {
			context.getForWhom().sendRawMessage(MainPlugin.TEXT_COLOR + "The " + type + " " + MainPlugin.HIGHLIGHT_COLOR + obj.getName() + ChatColor.GREEN + " directly inherits " + MainPlugin.TEXT_COLOR + "the group " + MainPlugin.HIGHLIGHT_COLOR + g.getName());
		} else if (obj.inGroup(world, g, true)) {
			context.getForWhom().sendRawMessage(MainPlugin.TEXT_COLOR + "The " + type + " " + MainPlugin.HIGHLIGHT_COLOR + obj.getName() + ChatColor.GREEN + " indirectly inherits " + MainPlugin.TEXT_COLOR + "the group " + MainPlugin.HIGHLIGHT_COLOR + g.getName());
		} else {
			context.getForWhom().sendRawMessage(MainPlugin.TEXT_COLOR + "The " + type + " " + MainPlugin.HIGHLIGHT_COLOR + obj.getName() + ChatColor.RED + " does not inherit " + MainPlugin.TEXT_COLOR + "the group " + MainPlugin.HIGHLIGHT_COLOR + g.getName());
		}
		return Menu.MODIFY_OPTIONS;
	}

	@Override
	public Prompt getPreviousPrompt(ConversationContext context) {
		return Menu.MODIFY_OPTIONS;
	}

}
