package net.kyrptonaught.linkedstorage.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.linkedstorage.LinkedStorageMod;
import net.kyrptonaught.linkedstorage.block.StorageBlock;
import net.kyrptonaught.linkedstorage.inventory.LinkedContainer;
import net.kyrptonaught.linkedstorage.util.DyeChannel;
import net.kyrptonaught.linkedstorage.util.LinkedInventoryHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import java.util.List;

public class StorageItem extends Item {
    public StorageItem(Properties item$Settings_1) {
        super(item$Settings_1);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(LinkedStorageMod.MOD_ID, "storageitem"), this);

    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!context.getLevel().isClientSide) {
            Player playerEntity = context.getPlayer();
            if (playerEntity.isShiftKeyDown() && context.getLevel().getBlockState(context.getClickedPos()).getBlock() instanceof StorageBlock) {
                DyeChannel channel = LinkedInventoryHelper.getBlockChannel(context.getLevel(), context.getClickedPos());
                LinkedInventoryHelper.setItemChannel(channel, context.getItemInHand());
            } else use(context.getLevel(), context.getPlayer(), context.getHand());
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player playerEntity, InteractionHand hand) {
        ItemStack stack = playerEntity.getItemInHand(hand);
        if (!world.isClientSide) {
            playerEntity.openMenu(LinkedContainer.createScreenHandlerFactory(LinkedInventoryHelper.getItemChannel(stack)));
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }


    @Environment(EnvType.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag options) {
        DyeChannel channel = LinkedInventoryHelper.getItemChannel(stack);
        for (Component text : channel.getCleanName()) {
            tooltip.add(((MutableComponent) text).withStyle(ChatFormatting.GRAY));
        }
    }
}
