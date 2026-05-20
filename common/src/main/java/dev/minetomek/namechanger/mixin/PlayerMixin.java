package dev.minetomek.namechanger.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends Avatar {

	protected PlayerMixin(EntityType<? extends LivingEntity> type, Level level) {
		super(type, level);
	}

	@Inject(method = "getName", at = @At("HEAD"), cancellable = true)
	void namechanger$returnCustomNameIfAvailable(CallbackInfoReturnable<Component> cir) {
		Component component = this.getCustomName();
		if(component != null) {
			cir.setReturnValue(namechanger$removeClickEvents(component));
		}
	}

	@Overwrite
	public @NonNull String getScoreboardName() {
		return getName().getString();
	}

	@Unique
	private static Component namechanger$removeClickEvents(Component component) {
		MutableComponent mutableComponent = component.plainCopy().setStyle(component.getStyle().withClickEvent(null));

		for (Component sibling : component.getSiblings()) {
			mutableComponent.append(namechanger$removeClickEvents(sibling));
		}

		return mutableComponent;
	}
}