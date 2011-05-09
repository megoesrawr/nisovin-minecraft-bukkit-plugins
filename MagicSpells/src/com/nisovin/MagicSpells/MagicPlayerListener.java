package com.nisovin.MagicSpells;

import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class MagicPlayerListener extends PlayerListener {

	private MagicSpells plugin;
	
	public MagicPlayerListener(MagicSpells plugin) {
		this.plugin = plugin;
		
		plugin.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, this, Event.Priority.Monitor, plugin);
		plugin.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_QUIT, this, Event.Priority.Monitor, plugin);
		plugin.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, this, Event.Priority.Monitor, plugin);
		plugin.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_ANIMATION, this, Event.Priority.Monitor, plugin);
	}
	
	@Override
	public void onPlayerJoin(PlayerJoinEvent event) {
		Spellbook spellbook = new Spellbook(event.getPlayer(), plugin);
		MagicSpells.spellbooks.put(event.getPlayer().getName(), spellbook);
	}
	
	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
		MagicSpells.spellbooks.remove(event.getPlayer().getName());
	}
	
	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ItemStack inHand = event.getPlayer().getItemInHand();
			Spell spell = MagicSpells.spellbooks.get(event.getPlayer().getName()).nextSpell(inHand.getTypeId());
			if (spell != null) {
				spell.sendMessage(event.getPlayer(), spell.formatMessage(MagicSpells.strSpellChange, "%s", spell.getName()));
			}
		}
	}
	
	@Override
	public void onPlayerAnimation(PlayerAnimationEvent event) {
		if (event.getAnimationType() == PlayerAnimationType.ARM_SWING) {
			ItemStack inHand = event.getPlayer().getItemInHand();
			Spell spell = MagicSpells.spellbooks.get(event.getPlayer().getName()).getActiveSpell(inHand.getTypeId());
			if (spell != null && spell.canCastWithItem()) {
				spell.cast(event.getPlayer());
			}			
		}
	}
	
}
