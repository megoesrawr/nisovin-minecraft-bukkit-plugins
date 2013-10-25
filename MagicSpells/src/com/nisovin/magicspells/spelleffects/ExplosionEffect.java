package com.nisovin.magicspells.spelleffects;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

class ExplosionEffect extends SpellEffect {

	@Override
	public void loadFromString(String string) {
	}

	@Override
	public void loadFromConfig(ConfigurationSection config) {
	}

	@Override
	public void playEffectLocation(Location location) {
		location.getWorld().createExplosion(location, 0F);
	}

}
