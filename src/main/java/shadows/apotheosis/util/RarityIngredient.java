package shadows.apotheosis.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shadows.apotheosis.adventure.affix.AffixHelper;
import shadows.apotheosis.adventure.loot.LootRarity;

public class RarityIngredient extends AbstractIngredient {

    protected final LootRarity rarity;

    public RarityIngredient(LootRarity rarity) {
        this.rarity = rarity;
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        return stack != null && AffixHelper.getRarity(stack) == this.rarity;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public @NotNull JsonElement toJson() {
        return new JsonObject();
    }

    public LootRarity getRarity() {
        return rarity;
    }

    public static class Serializer implements IIngredientSerializer<RarityIngredient> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public @NotNull RarityIngredient parse(@NotNull JsonObject json) {
            LootRarity rarity = LootRarity.byId(GsonHelper.getAsString(json, "rarity"));
            if (rarity == null) throw new JsonParseException("Invalid Rarity");
            return new RarityIngredient(rarity);
        }

        @Override
        public @NotNull RarityIngredient parse(FriendlyByteBuf buffer) {
            LootRarity rarity = LootRarity.byId(buffer.readUtf());
            return new RarityIngredient(rarity);
        }

        @Override
        public void write(FriendlyByteBuf buffer, RarityIngredient ingredient) {
            buffer.writeUtf(ingredient.rarity.id());
        }
    }
}
