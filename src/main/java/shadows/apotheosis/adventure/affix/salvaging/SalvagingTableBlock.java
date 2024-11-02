package shadows.apotheosis.adventure.affix.salvaging;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import shadows.placebo.container.ContainerUtil;
import shadows.placebo.container.SimplerMenuProvider;

public class SalvagingTableBlock extends Block implements EntityBlock {

	public SalvagingTableBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	public MenuProvider getMenuProvider(BlockState pState, Level pLevel, BlockPos pPos) {
		return new SimplerMenuProvider<>(pLevel, pPos, SalvagingMenu::new);
	}

	@Override
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
		return ContainerUtil.openGui(pPlayer, pPos, SalvagingMenu::new);
	}

	@Override
	public void appendHoverText(ItemStack stack, BlockGetter pLevel, List<Component> list, TooltipFlag flag) {
		list.add(new TranslatableComponent(this.getDescriptionId() + ".desc").withStyle(ChatFormatting.GRAY));
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new SalvagingTableTile(blockPos, blockState);
	}

	@Override
	@Deprecated
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() == this && newState.getBlock() == this) return;
		BlockEntity te = world.getBlockEntity(pos);
		if (te instanceof SalvagingTableTile salvTile) {
			for (int i = 0; i < salvTile.output.getSlots(); i++) {
				popResource(world, pos, salvTile.output.getStackInSlot(i));
			}
		}
		super.onRemove(state, world, pos, newState, isMoving);
	}
}