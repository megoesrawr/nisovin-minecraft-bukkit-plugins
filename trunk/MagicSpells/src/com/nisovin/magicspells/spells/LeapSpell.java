package com.nisovin.magicspells.spells;

import java.util.HashSet;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Vector;
import org.bukkit.util.config.Configuration;

import com.nisovin.magicspells.InstantSpell;

public class LeapSpell extends InstantSpell {
	
	private double forwardVelocity;
	private double upwardVelocity;
	private boolean cancelDamage;
	
	private HashSet<Player> jumping;

	public LeapSpell(Configuration config, String spellName) {
		super(config, spellName);
		
		forwardVelocity = getConfigInt("forward-velocity", 40) / 10D;
		upwardVelocity = getConfigInt("upward-velocity", 15) / 10D;
		cancelDamage = getConfigBoolean("cancel-damage", true);
		
		if (cancelDamage) {
			addListener(Event.Type.ENTITY_DAMAGE);
			jumping = new HashSet<Player>();
		}
	}

	@Override
	protected PostCastAction castSpell(Player player, SpellCastState state, float power, String[] args) {
		if (state == SpellCastState.NORMAL) {
			Vector v = player.getLocation().getDirection();
			v.setY(0).normalize().multiply(forwardVelocity*power).setY(upwardVelocity*power);
			player.setVelocity(v);
			if (cancelDamage) {
				jumping.add(player);
			}
		}
		
		return PostCastAction.HANDLE_NORMALLY;
	}
	
	@Override
	public void onEntityDamage(EntityDamageEvent event) {
		if (cancelDamage && event.getCause() == DamageCause.FALL && event.getEntity() instanceof Player && jumping.contains((Player)event.getEntity())) {
			event.setCancelled(true);
			jumping.remove((Player)event.getEntity());
		}
	}

}
