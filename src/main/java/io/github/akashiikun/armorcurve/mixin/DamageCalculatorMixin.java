package io.github.akashiikun.armorcurve.mixin;

import io.github.akashiikun.armorcurve.ArmorCurveMod;
import net.minecraft.entity.DamageUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Mixin(DamageUtil.class)
public class DamageCalculatorMixin {
    @Inject(cancellable = true, at = @At("HEAD"), method = "getDamageLeft(FFF)F")
    private static void getDamageLeft(float damage, float armor, float armorToughness, CallbackInfoReturnable<Float> info) {
        if (ArmorCurveMod.config == null || Arrays.stream(ArmorCurveMod.formulae).anyMatch(Objects::isNull)){
            Logger.getLogger("ArmorCurveMod").log(Level.WARNING, "armor formulae loaded incorrectly! Double check your config!");
            return;
        }
        BigDecimal ret = ArmorCurveMod.formulae[0].with("damage", new BigDecimal(damage)).and("armor", new BigDecimal(armor)).and("toughness", new BigDecimal(armorToughness)).eval();
        ret = ArmorCurveMod.formulae[1].with("damage", ret).and("armor", new BigDecimal(armor)).and("toughness", new BigDecimal(armorToughness)).eval();
//        float reduction = 1 + armor / 5;
//        float afterDamage = damage / reduction;
//
//        if (armorToughness > 0) {
//            float cap = 40 / armorToughness;
//            if (afterDamage > cap)
//                afterDamage -= (afterDamage - cap) / 2;
//        }
        info.setReturnValue(ret.floatValue());
    }

    @Inject(cancellable = true, at = @At("HEAD"), method = "getInflictedDamage(FF)F")
    private static void getInflictedDamage(float damage, float prot, CallbackInfoReturnable<Float> info) {
        if (ArmorCurveMod.config == null || Arrays.stream(ArmorCurveMod.formulae).allMatch(Objects::isNull)) return;
        BigDecimal ret = ArmorCurveMod.formulae[2].with("damage", new BigDecimal(damage)).and("enchant", new BigDecimal(prot)).eval();

        //float reduction = 1 + prot / 5;
        info.setReturnValue(ret.floatValue());
    }
}