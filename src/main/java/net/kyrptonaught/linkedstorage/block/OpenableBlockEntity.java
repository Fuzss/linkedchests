package net.kyrptonaught.linkedstorage.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@EnvironmentInterfaces({@EnvironmentInterface(value = EnvType.CLIENT, itf = LidBlockEntity.class)})
public class OpenableBlockEntity extends BlockEntity implements LidBlockEntity {
    OpenableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Environment(EnvType.CLIENT)
    protected int countViewers() {
        return 0;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public float getOpenNess(float f) {
        return Mth.lerp(f, lastAnimationAngle, animationAngle);
    }

    private float animationAngle;
    private float lastAnimationAngle;

    @Environment(EnvType.CLIENT)
    public void clientTick() {
        int viewerCount = countViewers();
        lastAnimationAngle = animationAngle;
        if (viewerCount > 0 && animationAngle == 0.0F) playSound(SoundEvents.ENDER_CHEST_OPEN);
        if (viewerCount == 0 && animationAngle > 0.0F || viewerCount > 0 && animationAngle < 1.0F) {
            float float_2 = animationAngle;
            if (viewerCount > 0) animationAngle += 0.1F;
            else animationAngle -= 0.1F;
            animationAngle = Mth.clamp(animationAngle, 0, 1);
            if (animationAngle < 0.5F && float_2 >= 0.5F) playSound(SoundEvents.ENDER_CHEST_CLOSE);
        }
    }

    @Environment(EnvType.CLIENT)
    private void playSound(SoundEvent soundEvent) {
        double d = (double) this.worldPosition.getX() + 0.5D;
        double e = (double) this.worldPosition.getY() + 0.5D;
        double f = (double) this.worldPosition.getZ() + 0.5D;
        this.level.playLocalSound(d, e, f, soundEvent, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
    }
}
