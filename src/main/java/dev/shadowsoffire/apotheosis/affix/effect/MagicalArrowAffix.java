package dev.shadowsoffire.apotheosis.affix.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.shadowsoffire.apotheosis.Apoth.Affixes;
import dev.shadowsoffire.apotheosis.affix.Affix;
import dev.shadowsoffire.apotheosis.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.affix.AffixType;
import dev.shadowsoffire.apotheosis.loot.LootCategory;
import dev.shadowsoffire.apotheosis.loot.LootRarity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class MagicalArrowAffix extends Affix {

    public static final Codec<MagicalArrowAffix> CODEC = RecordCodecBuilder.create(inst -> inst
        .group(
            LootRarity.CODEC.fieldOf("min_rarity").forGetter(a -> a.minRarity))
        .apply(inst, MagicalArrowAffix::new));

    protected LootRarity minRarity;

    public MagicalArrowAffix(LootRarity minRarity) {
        super(AffixType.ABILITY);
        this.minRarity = minRarity;
    }

    @Override
    public boolean canApplyTo(ItemStack stack, LootCategory cat, LootRarity rarity) {
        return cat.isRanged() && rarity.isAtLeast(this.minRarity);
    }

    // EventPriority.HIGH
    public void onHurt(LivingHurtEvent e) {
        if (e.getSource().getDirectEntity() instanceof AbstractArrow arrow) {
            if (AffixHelper.getAffixes(arrow).containsKey(Affixes.MAGICAL)) {
                // e.getSource().setMagic(); TODO: Forge event needs updating with a setDamageSource method.
            }
        }
    }

    @Override
    public Codec<? extends Affix> getCodec() {
        return CODEC;
    }

}
