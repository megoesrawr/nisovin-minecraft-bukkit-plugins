package com.nisovin.MagicSpells.Spells;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

import com.nisovin.MagicSpells.CommandSpell;
import com.nisovin.MagicSpells.MagicSpells;
import com.nisovin.MagicSpells.Spell;
import com.nisovin.MagicSpells.Spellbook;

public class TeachSpell extends CommandSpell {

	private static final String SPELL_NAME = "teach";

	private String strUsage;
	private String strNoTarget;
	private String strNoSpell;
	private String strCantLearn;
	private String strCastTarget;
	
	public static void load(Configuration config) {
		load(config, SPELL_NAME);
	}
	
	public static void load(Configuration config, String spellName) {
		if (config.getBoolean("spells." + spellName + ".enabled", true)) {
			MagicSpells.spells.put(spellName, new TeachSpell(config, spellName));
		}
	}
	
	public TeachSpell(Configuration config, String spellName) {
		super(config, spellName);
		
		strUsage = config.getString("spells." + spellName + ".str-usage", "Usage: /cast teach <target> <spell>");
		strNoTarget = config.getString("spells." + spellName + ".str-no-target", "No such player.");
		strNoSpell = config.getString("spells." + spellName + ".str-no-spell", "You do not know a spell by that name.");
		strCantLearn = config.getString("spells." + spellName + ".str-cant-learn", "That person cannot learn that spell.");
		strCastTarget = config.getString("spells." + spellName + ".str-cast-target", "%a has taught you the %s spell.");
	}
	
	@Override
	protected boolean castSpell(Player player, SpellCastState state, String[] args) {
		if (state == SpellCastState.NORMAL) {
			if (args.length != 2) {
				// fail: missing args
				sendMessage(player, strUsage);
				return true;
			} else {
				List<Player> players = MagicSpells.plugin.getServer().matchPlayer(args[0]);
				if (players.size() != 1) {
					// fail: no player match
					sendMessage(player, strNoTarget);
					return true;
				} else {
					Spell spell = MagicSpells.spellNames.get(args[1]);
					if (spell == null) {
						// fail: no spell match
						sendMessage(player, strNoSpell);
						return true;
					} else {
						Spellbook spellbook = MagicSpells.getSpellbook(player);
						if (spellbook == null || !spellbook.hasSpell(spell)) {
							// fail: player doesn't have spell
							sendMessage(player, strNoSpell);
							return true;
						} else {
							// yay! can learn!
							Spellbook targetSpellbook = MagicSpells.getSpellbook(players.get(0));
							if (targetSpellbook == null) {
								// fail: no spellbook for some reason
								sendMessage(player, strCantLearn);
								return true;
							} else {
								targetSpellbook.addSpell(spell);
								targetSpellbook.save();
								sendMessage(players.get(0), formatMessage(strCastTarget, "%a", player.getName(), "%s", spell.getName(), "%t", players.get(0).getName()));
								sendMessage(player, formatMessage(strCastSelf, "%a", player.getName(), "%s", spell.getName(), "%t", players.get(0).getName()));
								setCooldown(player);
								removeReagents(player);
							}
						}
					}
				}
			}
		}
		return false;
	}

}