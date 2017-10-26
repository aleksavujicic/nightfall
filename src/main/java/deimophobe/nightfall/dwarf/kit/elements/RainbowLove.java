package deimophobe.nightfall.dwarf.kit.elements;


import deimophobe.nightfall.Misc;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.kit.KitCooldownElement;
import deimophobe.nightfall.entity.GameEntity;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

class RainbowLove extends AbstractElement implements KitCooldownElement{

    private final ComplexCooldown cd = new ComplexCooldown(90*20);


    public RainbowLove(Dwarf dwarf) {
        super(dwarf);
    }

    @Override
    public float fractionComplete() {
        return 0;
    }

    @Override
    public void onDamageAttack (MonsterDamage damage) {
        dwarf.useMana(5);
        dwarf.heal(1);
    }

    @Override
    public void onKill (MonsterDamage damage) {
        dwarf.regenMana( 50);
        cd.reduceCooldown(3*20);
        if (dwarf.hasProc()) {
            dwarf.giveProc(ProcType.RAINBOWLOVE);
        }
    }

    public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace){
        if (Misc.isRightClick(action)){
            cd.tryUse();

            Location center = dwarf.getLocation();
            double radius = getRadius();
            for (GameEntity entity: DwarfManager.getManager().getDwarves())
                if (entity == dwarf)
                    if (center.distance(entity.getLocation()) <= radius){
                }
        }
        return false;
    }

    protected double getRadius(){return 13;}

    @Override
    public void onShift (boolean shift) {
        if (dwarf.tryUseMana(200)){
            dwarf.healMax();
            dwarf.getArmour().repair(1000);
        }
    }

    @Override
    public ItemStack getCooldownToggleItem() {
        return null;
    }

}
//    @Override
//    public void onDamageReceive (CustomDamageType RainbowLove){
//        if (dwarf.hasKitCooldownElement){
//            cd.reduceCooldown(2*60*20);
//        }
//    }