package dev.shadowsoffire.apotheosis.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.shadowsoffire.apotheosis.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.socket.SocketHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    /**
     * Injection to {@link EnchantmentHelper#getDamageProtection(Iterable, DamageSource)}
     */
    @Inject(at = @At("RETURN"), method = "getDamageProtection(Ljava/lang/Iterable;Lnet/minecraft/world/damagesource/DamageSource;)I", cancellable = true)
    private static void apoth_getDamageProtection(Iterable<ItemStack> stacks, DamageSource source, CallbackInfoReturnable<Integer> cir) {
        int prot = cir.getReturnValueI();
        for (ItemStack s : stacks) {
            prot += SocketHelper.getGems(s).getDamageProtection(source);

            var affixes = AffixHelper.getAffixes(s);
            for (AffixInstance inst : affixes.values()) {
                prot += inst.getDamageProtection(source);
            }
        }
        cir.setReturnValue(prot);
    }

    /**
     * Injection to {@link EnchantmentHelper#getDamageBonus(ItemStack, MobType)
     */
    @Inject(at = @At("RETURN"), method = "getDamageBonus(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/MobType;)F", cancellable = true)
    private static void apoth_getDamageBonus(ItemStack stack, MobType type, CallbackInfoReturnable<Float> cir) {
        float dmg = cir.getReturnValueF();

        dmg += SocketHelper.getGems(stack).getDamageBonus(type);

        var affixes = AffixHelper.getAffixes(stack);
        for (AffixInstance inst : affixes.values()) {
            dmg += inst.getDamageBonus(type);
        }

        cir.setReturnValue(dmg);
    }

    /**
     * Injection to {@link EnchantmentHelper#doPostDamageEffects(LivingEntity, Entity)}
     */
    @Inject(at = @At("TAIL"), method = "doPostDamageEffects(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/Entity;)V")
    private static void apoth_doPostDamageEffects(LivingEntity user, Entity target, CallbackInfo ci) {
        if (user == null) return;
        for (ItemStack s : user.getAllSlots()) {
            SocketHelper.getGems(s).doPostAttack(user, target);

            var affixes = AffixHelper.getAffixes(s);
            for (AffixInstance inst : affixes.values()) {
                int old = target.invulnerableTime;
                target.invulnerableTime = 0;
                inst.doPostAttack(user, target);
                target.invulnerableTime = old;
            }
        }
    }

    /**
     * Injection to {@link EnchantmentHelper#doPostHurtEffects(LivingEntity, Entity)}
     */
    @Inject(at = @At("TAIL"), method = "doPostHurtEffects(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/Entity;)V")
    private static void apoth_doPostHurtEffects(LivingEntity user, Entity attacker, CallbackInfo ci) {
        if (user == null) return;
        for (ItemStack s : user.getAllSlots()) {
            SocketHelper.getGems(s).doPostHurt(user, attacker);

            var affixes = AffixHelper.getAffixes(s);
            for (AffixInstance inst : affixes.values()) {
                inst.doPostHurt(user, attacker);
            }
        }
    }

}
