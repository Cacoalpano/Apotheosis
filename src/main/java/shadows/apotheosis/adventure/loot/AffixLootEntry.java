package shadows.apotheosis.adventure.loot;

import java.util.Set;

import com.google.gson.annotations.SerializedName;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import shadows.placebo.json.DimWeightedJsonReloadListener.IDimWeighted;
import shadows.placebo.json.PlaceboJsonReloadListener.TypeKeyedBase;

/**
 * A loot entry represents a possible item that can come out of a loot roll.
 * It is classified into a type, which is used to determine possible affixes.
 */
public final class AffixLootEntry extends TypeKeyedBase<AffixLootEntry> implements IDimWeighted, LootRarity.Clamped {

	protected int weight;
	protected float quality;
	protected ItemStack stack;
	protected LootCategory type;
	protected Set<ResourceLocation> dimensions;
	@SerializedName("min_rarity")
	protected LootRarity minRarity;
	@SerializedName("max_rarity")
	protected LootRarity maxRarity;
	protected Set<String> stages;

	public AffixLootEntry(int weight, float quality, ItemStack stack, LootCategory type, Set<ResourceLocation> dimensions, LootRarity min, LootRarity max) {
		this.weight = weight;
		this.quality = quality;
		this.stack = stack;
		this.type = type;
		this.dimensions = dimensions;
		this.minRarity = min;
		this.maxRarity = max;
	}

	@Override
	public int getWeight() {
		return this.weight;
	}

	@Override
	public float getQuality() {
		return this.quality;
	}

	public ItemStack getStack() {
		return this.stack.copy();
	}

	public LootCategory getType() {
		return this.type;
	}

	@Override
	public Set<ResourceLocation> getDimensions() {
		return this.dimensions;
	}

	@Override
	public LootRarity getMinRarity() {
		return this.minRarity;
	}

	@Override
	public LootRarity getMaxRarity() {
		return this.maxRarity;
	}

}