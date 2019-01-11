package deimophobe.nightfall.monster.mob;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.common.collect.Sets;
import deimophobe.nightfall.*;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.player.PlayerManager;
import deimophobe.nightfall.common.player.cosmetic.Hat;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import deimophobe.nightfall.dwarf.kit.melee.Scepter;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import deimophobe.nightfall.skin.PlayerSkin;
import deimophobe.nightfall.skin.Skin;
import deimophobe.nightfall.skin.SkinManager;
import deimophobe.nightfall.util.ArmourSlot;
import deimophobe.nightfall.util.Hitscan;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

import java.util.*;

/**
 * Created by Deimophobe on 15/01/18.
 */
public class Doppelganger extends AbstractMob {
	
	private static final int INVIS_DURATION = 40*20;
	private static final String TEAM_PREFIX = "doppel";
	
	private boolean giveArrow = false;
	
	private final Dwarf target;
	private final ComplexCooldown unhider = new ComplexCooldown(INVIS_DURATION, null, this::unhide);
	private final Team fakeTeam;
	private PlayerDisguise disguise = null;
	private boolean hidden;
	
	protected Doppelganger(MonsterPlayer monster) {
		super(monster, MobType.DOPPELGANGER);
		target = Misc.getRandom(DwarfManager.getManager().getNonHeroDwarves());
		NightfallPlugin.logger().info("Spawned doppel with target: " + (target == null ? "(no target)" : target.getName()));
		setFakeWeapon();
		
		int id = getID((target != null ? target.getName() : "" ));
		this.fakeTeam = Game.getGame().getNewTeam(TEAM_PREFIX + id);
		
		if (target != null) {
			
			String targetName = target.getName();
			int endIndex = Math.min(3, targetName.length());
			fakeTeam.setPrefix(ChatColor.DARK_AQUA.toString() + target.getName().substring(0, endIndex));
			
			fakeTeam.setColor(ChatColor.DARK_AQUA);
			fakeTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
			fakeTeam.addEntry(getFakeName());
		}
	}
	
	private String getFakeName() {
		return ChatColor.DARK_AQUA + target.getName().substring(3);
	}
	
	@Override
	public void onSpawn(SpawnMethod spawnMethod) {
		//monster.getPlayer().setPlayerListName(ChatColor.DARK_RED + monster.getName());
		if (target != null) {
			Skin skin = new Skin(target.getPlayer());
			PlayerSkin playerSkin = new PlayerSkin(
					monster.getName(), skin, false,
					ChatColor.DARK_AQUA + target.getName() + ChatColor.DARK_RED + " (You)"
			);
			
			UUID uuid = UUID.randomUUID();
			WrappedGameProfile profile = new WrappedGameProfile(uuid, getFakeName());
			skin.applyToWrappedGameProfile(profile);
			
			disguise = new PlayerDisguise(profile);
			disguise.setDisplayedInTab(false);
			disguise.setViewSelfDisguise(false);
			disguise.setReplaceSounds(false);
			disguise.getWatcher().setArrowsSticking(0);
			disguise.getWatcher().setSprinting(false);
			DisguiseAPI.disguiseEntity(monster.getPlayer(), disguise);
			
			SkinManager.getManager().addSkinChange(monster, playerSkin);
		}
		
		super.onSpawn(spawnMethod);
		if (target != null) {
			Hat hat = PlayerManager.getManager().getCosmetics(target.getPlayer()).getHat();
			if (hat != null) {
				hat.putOn(monster.getPlayer());
			}
		}
		ArmourSlot.LEGS.equipArmour(monster, getItem("legs"));
		ArmourSlot.FEET.equipArmour(monster, getItem("boots"));
		
		hide();
		
		final String targetMsg;
		if (target == null) targetMsg = ChatColor.RED + "There are no dwarves to clone!";
		else targetMsg = ChatColor.GOLD + "Your clone: " + ChatColor.DARK_AQUA + target.getName();
		monster.sendMessage(targetMsg);
		monster.sendTitleMessage(targetMsg);
	}
	
	@Override
	protected void setupItems() {
		super.setupItems();
		String fakeItem = Misc.getRandom(FAKE_ITEMS);
		giveItem(fakeItem);
		
		if (giveArrow) giveItem("arrow");
		giveItem("unhider");
	}
	
	@Override
	public void update() {
		super.update();
		unhider.update();
		beamer.update();
		
		Location smokeLoc = monster.getLocation().add(0,1,0);
		for (MonsterPlayer player : MonsterManager.getManager().getGamePlayers()) {
			if (player == monster) continue;
			player.getPlayer().spawnParticle(Particle.SMOKE_LARGE, smokeLoc, 2, 0.3, 0.3, 0.3, 0.01);
		}
	}
	
	private static final Hitscan scepterHitscan = Scepter.copyOfHitscanBuilder().build();
	private final Cooldown beamer = new ComplexCooldown(Scepter.ZAP_CD, () -> {
		scepterHitscan.fire(monster, Scepter.RANGE);
		Scepter.playZapSound(monster);
	});
	
	@Override
	public void onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {
		super.onUse(click, clickedBlock, blockFace);
		if (isPlayerHoldingItem("unhider")) {
			monster.useHeldItem();
			unhide();
		} else if (click.isLeftClick() && isPlayerHoldingItem("scepter")) {
			if (!hidden) beamer.tryUse();
		}
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		if (!damage.isCancelled()) unhide();
	}
	
	@Override
	public void onDamageReceive(MonsterDamage damage) {
		super.onDamageReceive(damage);
		if (!damage.isCancelled()) unhide();
	}
	
	@Override
	public boolean onBlockBreak(Block block, boolean didBreak) {
		didBreak = super.onBlockBreak(block, didBreak);
		if (didBreak) unhide();
		return didBreak;
	}
	
	@Override
	public void onShift(boolean sneaking) {
		super.onShift(sneaking);
		monster.removeFire();
	}
	
	@Override
	public void onDeath(boolean silent) {
		super.onDeath(silent);
		SkinManager.getManager().removeSkinChange(monster);
	}
	
	@Override
	public double getShrineWeight() {
		if (hidden) return 0;
		else return super.getShrineWeight();
	}
	
	private void hide() {
		hidden = true;
		monster.givePotionEffect(PotionEffectType.SPEED, INVIS_DURATION, 3, true, false, true);
		monster.givePotionEffect(PotionEffectType.JUMP, INVIS_DURATION, 3, true, false, true);
		giveSpawnProtection(INVIS_DURATION, true, false);
		if (disguise != null) {
			disguise.getWatcher().setInvisible(true);
			ItemStack air = new ItemStack(Material.AIR);
			disguise.getWatcher().setArmor(new ItemStack[]{air, air, air, air});
			disguise.getWatcher().setItemInMainHand(air);
		}
		
		unhider.reset();
	}
	
	private void unhide() {
		if (!hidden) return;
		
		removeSpawnProtection();
		monster.removePotionEffect(PotionEffectType.SPEED);
		monster.removePotionEffect(PotionEffectType.JUMP);
		if (disguise != null) {
			disguise.getWatcher().setInvisible(false);
			disguise.getWatcher().setArmor(new ItemStack[]{null, null, null, null});
			disguise.getWatcher().setItemInMainHand(null);
		}
		
		hidden = false;
		
		removeItem("unhider");
	}
	
	
	private static final Map<KitPieceType, String> DWARF_WEAPONS = new LinkedHashMap<>();
	static {
		DWARF_WEAPONS.put(KitPieceType.RUNESWORD, null);
		DWARF_WEAPONS.put(KitPieceType.BLOOD_AXE, "axe");
		DWARF_WEAPONS.put(KitPieceType.DAGGER, "dagger");
		DWARF_WEAPONS.put(KitPieceType.HAMMER, "hammer");
		DWARF_WEAPONS.put(KitPieceType.SCEPTER, "scepter");
		DWARF_WEAPONS.put(KitPieceType.RAPIER, "rapier");
		DWARF_WEAPONS.put(KitPieceType.SOUL_BLADE, "soulblade");
		DWARF_WEAPONS.put(KitPieceType.VOLCANIC, "gauntlet");
	}
	private void setFakeWeapon() {
		if (target == null) return;
		
		for (Map.Entry<KitPieceType, String> entry : DWARF_WEAPONS.entrySet()) {
			if (target.hasKitPiece(entry.getKey())) {
				String itemKey = entry.getValue();
				if (itemKey != null) {
					setWeapon(itemKey);
					if (itemKey.equals("gauntlet")) giveArrow = true;
				}
				return;
			}
		}
	}
	
	private static final Set<String> FAKE_ITEMS = Sets.newHashSet(
			"cobble", "torch", "mortar", "lumber-axe", "shovel"
	);
	
	
	private static final Map<String, Integer> nameToIDMap = new HashMap<>();
	private static int getID(String name) {
		if (nameToIDMap.containsKey(name)) {
			return nameToIDMap.get(name);
		} else {
			int nextID = nameToIDMap.size();
			nameToIDMap.put(name, nextID);
			return nextID;
		}
	}
	
	@Override
	protected void displayDeathAnimation() {
		monster.getWorld().spawnParticle(Particle.CLOUD, monster.getEyeLocation().subtract(0, 0.5, 0), 20, 0.5, 0.5, 0.5, 0.01);
		dropFakeWeapon();
		dropFakeItem("armour");
		dropFakeItem("legs");
		dropFakeItem("boots");
	}
}
