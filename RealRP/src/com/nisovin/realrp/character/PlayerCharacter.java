package com.nisovin.realrp.character;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;
import org.getspout.spoutapi.SpoutManager;

import com.nisovin.realrp.RealRP;

public class PlayerCharacter implements GameCharacter {

	private static HashMap<Player,PlayerCharacter> characters;
	
	private Configuration config;
	
	private Player player;
	private String firstName;
	private String lastName;
	private String prefixTitle;
	private String postfixTitle;
	private String subTitle;
	
	private int age;
	private Sex sex;
	private String description;
	private ArrayList<CharacterNote> notes;
	private HashMap<Player,CharacterNote> newNotes;
	
	private String chatName;
	private String emoteName;
	private String nameplate;
	
	public static PlayerCharacter get(Player player) {
		if (characters == null) {
			characters = new HashMap<Player,PlayerCharacter>();
		}
		
		PlayerCharacter character = characters.get(player);
		if (character != null) {
			return character;
		} else {
			File file = new File(RealRP.getPlugin().getDataFolder(), "players" + File.separator + player.getName().toLowerCase() + ".yml");
			if (file.exists()) {
				character = new PlayerCharacter(player, file);
				characters.put(player, character);
				return character;
			} else {
				return null;
			}
		}
	}
	
	public PlayerCharacter(Player player) {
		this(player, new File(RealRP.getPlugin().getDataFolder(), "players" + File.separator + player.getName().toLowerCase() + ".yml"));
	}
	
	public PlayerCharacter(Player player, File file) {
		this.player = player;
		
		config = new Configuration(file);
		config.load();
		
		firstName = config.getString("first-name", player.getName());
		lastName = config.getString("last-name", "");
		prefixTitle = config.getString("prefix-title", "");
		postfixTitle = config.getString("postfix-title", "");
		subTitle = config.getString("sub-title", "");
		age = config.getInt("age", 25);
		String sx = config.getString("sex", "u");
		if (sx.equalsIgnoreCase("m")) {
			sex = Sex.Male;
		} else if (sx.equalsIgnoreCase("f")) {
			sex = Sex.Female;
		} else {
			sex = Sex.Unknown;
		}
		description = config.getString("description", "");
		
		notes = new ArrayList<CharacterNote>();
		Map<String,ConfigurationNode> noteNodes = config.getNodes("notes");
		if (noteNodes != null) {
			for (String key : noteNodes.keySet()) {
				ConfigurationNode node = noteNodes.get(key);
				Long time = Long.parseLong(key);
				String by = node.getString("by");
				String text = node.getString("note");
				CharacterNote note = new CharacterNote(time, by, text);
				notes.add(note);
			}
		}
		
		newNotes = new HashMap<Player,CharacterNote>();
		
		setUpNames();
	}
	
	public PlayerCharacter(Player player, String firstName, String lastName, int age, Sex sex, String description) {
		characters.put(player, this);
		
		config = new Configuration(new File(RealRP.getPlugin().getDataFolder(), "players" + File.separator + player.getName().toLowerCase() + ".yml"));
		
		this.player = player;
		this.firstName = firstName;
		this.lastName = lastName;
		this.prefixTitle = "";
		this.postfixTitle = "";
		this.subTitle = "";
		this.age = age;
		this.sex = sex;
		this.description = description;
		this.notes = new ArrayList<CharacterNote>();
		this.newNotes = new HashMap<Player,CharacterNote>();
		
		setUpNames();
	}
	
	public void sendMessage(String message, String... replacements) {
		String[] msgs = message.split("\n");
		for (String msg : msgs) {
			player.sendMessage(msg);
		}
	}
	
	public void setUpNames() {
		chatName = generateName("chat");
		emoteName = generateName("emote");
		nameplate = generateName("nameplate");		
		player.setDisplayName(getChatName());
		SpoutManager.getAppearanceManager().setGlobalTitle(player, getNameplate());
	}
	
	private String generateName(String type) {
		String name = "";
		if (!firstName.isEmpty() && player.hasPermission("realrp.names." + type + ".first")) {
			name = firstName;
		}
		if (!lastName.isEmpty() && player.hasPermission("realrp.names." + type + ".last")) {
			name += " " + lastName;
			name.trim();
		}
		if (!prefixTitle.isEmpty() && player.hasPermission("realrp.names." + type + ".prefix")) {
			name = prefixTitle + " " + name;
		}
		if (!postfixTitle.isEmpty() && player.hasPermission("realrp.names." + type + ".postfix")) {
			name += " " + postfixTitle;
		}
		return name.trim();
	}

	@Override
	public String getChatName() {
		return chatName;
	}

	@Override
	public String getEmoteName() {
		return emoteName;
	}

	@Override
	public String getNameplate() {
		return nameplate;
	}
	
	public Sex getSex() {
		return sex;
	}
	
	public void save() {
		config.setProperty("first-name", firstName);
		config.setProperty("last-name", lastName);
		config.setProperty("prefix-title", prefixTitle);
		config.setProperty("postfix-title", postfixTitle);
		config.setProperty("sub-title", subTitle);
		config.setProperty("age", age);
		if (sex == Sex.Male) {
			config.setProperty("sex", "m");
		} else if (sex == Sex.Female) {
			config.setProperty("sex", "f");
		} else {
			config.setProperty("sex", "u");
		}
		config.setProperty("description", description);		
		for (CharacterNote note : notes) {
			note.store(config);
		}
		config.save();
	}
	
	public void startNote(Player by) {
		CharacterNote note = new CharacterNote(by.getName());
		newNotes.put(by, note);
	}
	
	public boolean addNoteText(Player by, String text) {
		CharacterNote note = newNotes.get(by);
		
		if (note == null) {
			return false;
		}
		
		note.addText(text);
		return true;
	}
	
	public boolean saveNote(Player by) {
		CharacterNote note = newNotes.get(by);
		
		if (note == null) {
			return false;
		}
		
		notes.add(note);
		newNotes.remove(note);
		save();
		return true;
	}
	
	public enum Sex {
		Male, Female, Unknown
	}
	
	public class CharacterNote implements Comparable<CharacterNote> {
		private Long time;
		private String by;
		private String note;
		
		public CharacterNote(String by) {
			this.time = System.currentTimeMillis();
			this.by = by;
			this.note = "";
		}
		
		public CharacterNote(Long time, String by, String note) {
			this.time = time;
			this.by = by;
			this.note = note;
		}
		
		public void addText(String text) {
			note += text.trim() + " ";
		}
		
		public void store(Configuration config) {
			config.setProperty("notes." + time + ".by", by);
			config.setProperty("notes." + time + ".note", note);
		}
		
		@Override
		public int compareTo(CharacterNote n) {
			return this.time.compareTo(n.time);
		}
	}
	
}
