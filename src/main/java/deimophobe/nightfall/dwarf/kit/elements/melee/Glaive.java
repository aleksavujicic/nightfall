package deimophobe.nightfall.dwarf.kit.elements.melee;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.KitCooldownElement;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.entity.MonsterEntity;
import deimophobe.nightfall.items.CustomItem;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by ED{Kegoir} and Div on 23/11/17
 */

public class Glaive extends AbstractAOEHitter implements KitCooldownElement {
    private final double aoeRadius = 3;
    private final int basicAttackDamage = 15;//Might be changed when testing abilities, OG value is 15
    private final int maxCD = 1*20;//Broken AF right now
    private final int abilityDuration = 3*20;//Might be changed when testing abilities, OG value is 1*20 (1 second)
    private final ComplexCooldown cd = new ComplexCooldown(maxCD, this::TestAbility);//WILL NEED TO CHANGE ONCE ABILITY IS DECIDED
    //Ability variables below

    //End of Ability variables

    public Glaive (Dwarf dwarf){super(dwarf);}
    private final static CustomItem ITEM = DwarvenItems.getItem("melee", "glaive");

    @Override
    public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
        super.update(quartSec, halfSec, sec, doubleSec, quadSec);
        cd.update();
    }
    @Override
    public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace){
        super.onUse(action, clickedBlock, blockFace);
        if(Misc.isRightClick(action)){
            cd.tryUse();
        }
        return false;
    }

    private void TestAbility(){TempAbility();}//Test abilities here. Must give basicAttackDamage a value based on ability.

    private void TempAbility(){//For use while abilities are being decided
        dwarf.givePotionEffect(PotionEffectType.INVISIBILITY,abilityDuration,1,false,false,true);
        dwarf.givePotionEffect(PotionEffectType.SPEED, abilityDuration,3,false,false,true);
    }
    //Possible Abilities (Being built and tested) (Only one of these will be used for Glaive, but I(ED) might keep some for other weapons
    private void ChangeStance(){}//Will change stance to do more damage to AIs, less damage to PlayerMobs, and vice versa

    private void AltHit(){}//Will be a different attack with a very fast cooldown(.5 to 1.5 seconds) dealing more damage to playermobs or AIs, and less damage to other

    private void PowerAttack(){}//Will have a slight delay (1 to 2 seconds) before hitting for a lot of damage(maybe an unrolling proc?)

    private void ChargeAttack(){}//Will slow player and charge up damage while held down (up to maybe 5 seconds), dealing charged damage when released

    private void FlurryOfBlows(){}//Will make a few aoe slashes or precise stabs in front of player, while slowing player down


    @Override
    protected double getDamageToMonster(MonsterEntity entity){return basicAttackDamage;}//Maybe change this to be more effective against AIs

    @Override public CustomItem getItem(){return ITEM;}
    @Override public KitGiveType getGiveType() { return KitGiveType.SWORD; }
    @Override public ItemStack getCooldownToggleItem() {
        return getItem().createItemStack();
    }
    @Override public float fractionComplete() {
        return cd.fractionComplete();
    }
    @Override protected double getRadius(MonsterEntity entity) {
        return aoeRadius;
    }
}
