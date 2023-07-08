package fuzs.thinair.world.item;

import fuzs.thinair.ThinAir;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class RespiratorArmorMaterial implements ArmorMaterial {
    public static final ArmorMaterial INSTANCE = new RespiratorArmorMaterial();

    @Override
    public int getDurabilityForSlot(EquipmentSlot pSlot) {
        return 64;
    }

    @Override
    public int getDefenseForSlot(EquipmentSlot pSlot) {
        return 1;
    }

    @Override
    public int getEnchantmentValue() {
        return 15;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_GENERIC;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(Items.CHARCOAL);
    }

    @Override
    public String getName() {
        return ThinAir.id("respirator").toString();
    }

    @Override
    public float getToughness() {
        return 0;
    }

    @Override
    public float getKnockbackResistance() {
        return 0;
    }
}
