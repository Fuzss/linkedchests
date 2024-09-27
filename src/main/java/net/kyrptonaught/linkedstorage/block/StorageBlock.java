package net.kyrptonaught.linkedstorage.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.kyrptonaught.linkedstorage.LinkedStorageMod;
import net.kyrptonaught.linkedstorage.network.OpenStoragePacket;
import net.kyrptonaught.linkedstorage.network.SetDyePacket;
import net.kyrptonaught.linkedstorage.util.DyeChannel;
import net.kyrptonaught.linkedstorage.util.LinkedInventoryHelper;
import net.kyrptonaught.linkedstorage.util.PlayerDyeChannel;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class StorageBlock extends HorizontalDirectionalBlock implements EntityBlock, WorldlyContainerHolder {
    public static BlockEntityType<StorageBlockEntity> blockEntity;

    public StorageBlock(Properties block$Settings_1) {
        super(block$Settings_1);
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(LinkedStorageMod.MOD_ID, "storageblock"), this);
        blockEntity = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, LinkedStorageMod.MOD_ID + ":storageblock", FabricBlockEntityTypeBuilder.create(StorageBlockEntity::new, this).build(null));
        BlockItem item = new BlockItem(this, new Item.Properties());

        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(LinkedStorageMod.MOD_ID, "storageblock"), item);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    private boolean didHitButton(VoxelShape button, BlockPos pos, Vec3 hit) {
        return button.bounds().inflate(.001).move(pos.getX(), pos.getY(), pos.getZ()).contains(hit);
    }

    @Environment(EnvType.CLIENT)
    private boolean checkButons(BlockState state, BlockPos pos, BlockHitResult hit) {
        VoxelShape[] buttons = BUTTONS;
        if (state.getValue(FACING).equals(Direction.EAST) || state.getValue(FACING).equals(Direction.WEST))
            buttons = BUTTONSEW;
        for (int i = 0; i < buttons.length; i++)
            if (didHitButton(buttons[i], pos, hit.getLocation())) {
                if (state.getValue(FACING).equals(Direction.NORTH) || state.getValue(FACING).equals(Direction.EAST)) {
                    SetDyePacket.sendPacket(2 - i, pos);
                    return true;
                } else {
                    SetDyePacket.sendPacket(i, pos);
                    return true;
                }
            }
        return false;
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getMainHandItem();
        DyeChannel channel = LinkedInventoryHelper.getBlockChannel(world, pos);
        if (stack.getItem().equals(Items.DIAMOND)) {
            if (channel instanceof PlayerDyeChannel) {
                channel = new DyeChannel(channel.dyeChannel.clone());
                LinkedInventoryHelper.setBlockChannel(channel, world, pos);
                if (!player.isCreative()) stack.grow(1);
            } else {
                LinkedInventoryHelper.setBlockChannel(channel.toPlayerDyeChannel(player.getUUID()), world, pos);
                if (!player.isCreative()) stack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }

        if (world.isClientSide)
            if (stack.getItem() instanceof DyeItem) {
                if (!checkButons(state, pos, hit))
                    OpenStoragePacket.sendPacket(pos);
            } else {
                OpenStoragePacket.sendPacket(pos);
            }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState blockState_1, LivingEntity livingEntity_1, ItemStack stack) {
        if (!world.isClientSide()) {
            LinkedInventoryHelper.setBlockChannel(LinkedInventoryHelper.getItemChannel(stack), world, pos);
        }
    }

    @Override
    public WorldlyContainer getContainer(BlockState state, LevelAccessor world, BlockPos pos) {
        return ((StorageBlockEntity) world.getBlockEntity(pos)).getLinkedInventory();
    }

    @Environment(EnvType.CLIENT)
    public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
        DyeChannel dyechannel = LinkedInventoryHelper.getBlockChannel((Level) world, pos);
        ItemStack stack = new ItemStack(this);
        LinkedInventoryHelper.setItemChannel(dyechannel, stack);
        return stack;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    public RenderShape getRenderShape(BlockState blockState_1) {
        return RenderShape.MODEL;
    }

    private final VoxelShape[] BUTTONS = new VoxelShape[]{Block.box(4, 14, 6, 6, 15, 10),
            Block.box(7, 14, 6, 9, 15, 10),
            Block.box(10, 14, 6, 12, 15, 10)};
    private final VoxelShape SHAPE = Shapes.or(Block.box(1, 0, 1, 15, 14, 15), BUTTONS);
    private final VoxelShape[] BUTTONSEW = new VoxelShape[]{Block.box(6, 14, 4, 10, 15, 6),
            Block.box(6, 14, 7, 10, 15, 9),
            Block.box(6, 14, 10, 10, 15, 12)};
    private final VoxelShape SHAPEEW = Shapes.or(Block.box(1, 0, 1, 15, 14, 15), BUTTONSEW);

    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        if (state.getValue(FACING).equals(Direction.EAST) || state.getValue(FACING).equals(Direction.WEST))
            return SHAPEEW;
        return SHAPE;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromContainer(getContainer(state, world, pos));
    }


    @Override
    @Environment(EnvType.CLIENT)
    public void appendHoverText(ItemStack stack, BlockGetter view, List<Component> tooltip, TooltipFlag options) {
        DyeChannel channel = LinkedInventoryHelper.getItemChannel(stack);
        for (Component text : channel.getCleanName()) {
            tooltip.add(((MutableComponent) text).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StorageBlockEntity(blockEntity, pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return world.isClientSide & type == blockEntity ? (world1, pos, state1, blockEntity) -> ((OpenableBlockEntity) blockEntity).clientTick() : null;
    }
}
