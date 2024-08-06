package me.sebastian420.PandaTNTQueue.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

import java.util.List;
import java.util.Set;

public class LithiumPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return "";
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        try {
            boolean shouldApply;
            if (mixinClassName.equals("me.sebastian420.PandaTNTQueue.mixin.LithiumExplosionMixin")) {
                shouldApply = isClassPresent("me.jellysquid.mods.lithium.mixin.world.explosions.block_raycast.ExplosionMixin");
            } else if (mixinClassName.equals("me.sebastian420.PandaTNTQueue.mixin.ExplosionQueue")) {
                shouldApply = !isClassPresent("me.jellysquid.mods.lithium.mixin.world.explosions.block_raycast.ExplosionMixin");
            } else {
                shouldApply = true;
            }
            return shouldApply;
        } catch (Exception e) {
            // In case of any error, return true to apply the mixin by default
            return true;
        }
    }

    private boolean isClassPresent(String className) {
        try {
            return MixinService.getService().getClassProvider().findClass(className, false) != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }


    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return List.of();
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
