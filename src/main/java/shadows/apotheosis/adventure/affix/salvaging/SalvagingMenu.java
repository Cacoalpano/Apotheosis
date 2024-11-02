package shadows.apotheosis.adventure.affix.salvaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.google.common.base.Predicates;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import shadows.apotheosis.Apoth;
import shadows.placebo.cap.InternalItemHandler;
import shadows.placebo.container.BlockEntityContainer;
import shadows.placebo.container.FilteredSlot;
import shadows.placebo.container.PlaceboContainerMenu;

import javax.annotation.Nullable;

public class SalvagingMenu extends BlockEntityContainer<SalvagingTableTile> {

	protected final Player player;
	protected final InternalItemHandler invSlots = new InternalItemHandler(15);
	protected Runnable updateButtons;

	public SalvagingMenu(int id, Inventory inv, BlockPos pos) {
		super(Apoth.Menus.SALVAGE, id, inv, pos);
		this.player = inv.player;
		for (int i = 0; i < 15; i++) {
			this.addSlot(new FilteredSlot(this.invSlots, i, 8 + i % 5 * 18, 17 + i / 5 * 18, s -> findMatch(this.level, s) != null) {
				@Override
				public void setChanged() {
					super.setChanged();
					if (SalvagingMenu.this.updateButtons != null) SalvagingMenu.this.updateButtons.run();
				}

				@Override
				public int getMaxStackSize() {
					return 1;
				}

				@Override
				public int getMaxStackSize(ItemStack stack) {
					return 1;
				}
			});
		}

		for (int i = 0; i < 6; i++) {
			this.addSlot(new FilteredSlot(this.tile.output, i, 134 + i % 2 * 18, 17 + i / 2 * 18, Predicates.alwaysFalse()) {
				@Override
				public void setChanged() {
					super.setChanged();
					if (SalvagingMenu.this.updateButtons != null) SalvagingMenu.this.updateButtons.run();
				}
			});
		}

		this.addPlayerSlots(inv, 8, 84);
		this.mover.registerRule((stack, slot) -> slot >= this.playerInvStart && findMatch(this.level, stack) != null, 0, 15);
		this.mover.registerRule((stack, slot) -> slot < this.playerInvStart, this.playerInvStart, this.hotbarStart + 9);
		this.registerInvShuffleRules();
	}

	@Override
	protected void addPlayerSlots(Inventory pInv, int x, int y) {
		this.playerInvStart = this.slots.size();
		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 9; column++) {
				this.addSlot(new Slot(pInv, column + row * 9 + 9, x + column * 18, y + row * 18) {
					@Override
					public void setChanged() {
						super.setChanged();
						if (SalvagingMenu.this.updateButtons != null) SalvagingMenu.this.updateButtons.run();
					}
				});
			}
		}

		this.hotbarStart = this.slots.size();
		for (int row = 0; row < 9; row++) {
			this.addSlot(new Slot(pInv, row, x + row * 18, y + 58) {

				@Override
				public void setChanged() {
					super.setChanged();
					if (SalvagingMenu.this.updateButtons != null) SalvagingMenu.this.updateButtons.run();
				}

			});
		}
	}

	public void setButtonUpdater(Runnable r) {
		this.updateButtons = r;
	}

	@Override
	public boolean stillValid(Player pPlayer) {
		if (this.level.isClientSide) return true;
		return this.level.getBlockState(this.pos).getBlock() == Apoth.Blocks.SALVAGING_TABLE;
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		if (!this.level.isClientSide) {
			this.clearContainer(player, new RecipeWrapper(this.invSlots));
		}
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		if (id == 0) {
			salvageAll();
			player.level.playSound(null, player.blockPosition(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.BLOCKS, 0.99F, this.level.random.nextFloat() * 0.25F + 1F);
			player.level.playSound(null, player.blockPosition(), SoundEvents.AMETHYST_CLUSTER_STEP, SoundSource.BLOCKS, 0.34F, this.level.random.nextFloat() * 0.2F + 0.8F);
			player.level.playSound(null, player.blockPosition(), SoundEvents.SMITHING_TABLE_USE, SoundSource.BLOCKS, 0.45F, this.level.random.nextFloat() * 0.5F + 0.75F);
			return true;
		}
		return super.clickMenuButton(player, id);
	}

	protected void giveItem(Player player, ItemStack stack) {
		if (!player.isAlive() || player instanceof ServerPlayer && ((ServerPlayer) player).hasDisconnected()) {
			player.drop(stack, false);
		} else {
			Inventory inventory = player.getInventory();
			if (inventory.player instanceof ServerPlayer) {
				inventory.placeItemBackInInventory(stack);
			}
		}
	}

	protected void salvageAll() {
		for (int i = 0; i < 15; i++) {
			Slot s = this.getSlot(i);
			ItemStack stack = s.getItem();
			List<ItemStack> outputs = salvageItem(this.level, stack);
			s.set(ItemStack.EMPTY);
			for (ItemStack out : outputs) {
				for (int outSlot = 0; outSlot < 6; outSlot++) {
					if (out.isEmpty()) break;
					out = this.tile.output.insertItem(outSlot, out, false);
				}
				if (!out.isEmpty()) this.giveItem(this.player, out);
			}
		}
	}

	public static int getSalvageCount(SalvagingRecipe.OutputData output, ItemStack stack, Random rand) {
		int[] counts = getSalvageCounts(output, stack);
		return rand.nextInt(counts[0], counts[1] + 1);
	}

	public static int[] getSalvageCounts(SalvagingRecipe.OutputData output, ItemStack stack) {
		int[] out = {output.min, output.max};
		if (stack.isDamageableItem()) {
			out[1] = Math.max(out[0], Math.round((float) (out[1] * (stack.getMaxDamage() - stack.getDamageValue())) / stack.getMaxDamage()));
		}
		return out;
	}

	public static List<ItemStack> salvageItem(Level level, ItemStack stack) {
		var recipe = findMatch(level, stack);
		if (recipe == null) return Collections.emptyList();
		List<ItemStack> outputs = new ArrayList<>();
		for (SalvagingRecipe.OutputData d : recipe.getOutputs()) {
			ItemStack out = d.stack.copy();
			out.setCount(getSalvageCount(d, stack, level.random));
			outputs.add(out);
		}
		return outputs;
	}

	public static List<ItemStack> getBestPossibleSalvageResults(Level level, ItemStack stack) {
		var recipe = findMatch(level, stack);
		if (recipe == null) return Collections.emptyList();
		List<ItemStack> outputs = new ArrayList<>();
		for (SalvagingRecipe.OutputData d : recipe.getOutputs()) {
			ItemStack out = d.stack.copy();
			out.setCount(getSalvageCounts(d, stack)[1]);
			outputs.add(out);
		}
		return outputs;
	}

	@Nullable
	public static SalvagingRecipe findMatch(Level level, ItemStack stack) {
		for (var recipe : level.getRecipeManager().getAllRecipesFor(Apoth.RecipeTypes.SALVAGING)) {
			if (recipe.matches(stack)) return recipe;
		}
		return null;
	}

}
