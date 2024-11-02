package shadows.apotheosis.adventure.affix.salvaging;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import shadows.apotheosis.Apoth;
import shadows.placebo.json.ItemAdapter;

import java.util.ArrayList;
import java.util.List;

public class SalvagingRecipe implements Recipe<Container> {

    protected final ResourceLocation id;
    protected final Ingredient input;
    protected final List<OutputData> outputs;


    public SalvagingRecipe(ResourceLocation id, List<OutputData> outputs, Ingredient input) {
        this.id = id;
        this.outputs = outputs;
        this.input = input;
    }

    public boolean matches(ItemStack stack) {
        return input.test(stack);
    }

    public Ingredient getInput() {
        return this.input;
    }

    public List<OutputData> getOutputs() {
        return this.outputs;
    }

    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(Container container) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return false;
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Apoth.RecipeTypes.SALVAGING;
    }

    public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<SalvagingRecipe> {

        public static final Serializer INSTANCE = new Serializer();

        @Override
        public SalvagingRecipe fromJson(ResourceLocation id, JsonObject obj) {
            var outputs = getOutputData(obj);
            Ingredient input = Ingredient.fromJson(obj.get("input"));
            return new SalvagingRecipe(id, outputs, input);
        }


        @Override
        public SalvagingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            var outputs = OutputData.LIST_CODEC.decode(NbtOps.INSTANCE, buf.readNbt().get("outputs")).result().get().getFirst();
            Ingredient input = Ingredient.fromNetwork(buf);
            return new SalvagingRecipe(id, outputs, input);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, SalvagingRecipe recipe) {
            Tag outputs = OutputData.LIST_CODEC.encodeStart(NbtOps.INSTANCE, recipe.outputs).get().left().get();
            CompoundTag netWrapper = new CompoundTag();
            netWrapper.put("outputs", outputs);
            buf.writeNbt(netWrapper);
            recipe.input.toNetwork(buf);
        }

        private List<OutputData> getOutputData(JsonObject json) {
            List<OutputData> list = new ArrayList<>();
            JsonArray array = GsonHelper.getAsJsonArray(json, "outputs");
            for (JsonElement element : array) {
                list.add(OutputData.parse(element.getAsJsonObject()));
            }
            return list;
        }
    }


    public static class OutputData {

        public static Codec<OutputData> CODEC = RecordCodecBuilder.create(inst -> inst
                .group(
                        ItemStack.CODEC.fieldOf("stack").forGetter(d -> d.stack),
                        Codec.intRange(0, 64).fieldOf("min_count").forGetter(d -> d.min),
                        Codec.intRange(0, 64).fieldOf("max_count").forGetter(d -> d.max))
                .apply(inst, OutputData::new));

        public static Codec<List<OutputData>> LIST_CODEC = Codec.list(CODEC);

        ItemStack stack;
        int min, max;

        OutputData(ItemStack stack, int min, int max) {
            this.stack = stack;
            this.min = min;
            this.max = max;
            Preconditions.checkArgument(max >= min);
            this.stack.setCount(1);
        }

        public ItemStack getStack() {
            return this.stack;
        }

        public int getMin() {
            return this.min;
        }

        public int getMax() {
            return this.max;
        }

        public static OutputData parse(JsonObject obj) {
            ItemStack stack = ItemStack.EMPTY;
            JsonElement item = obj.get("stack");
            if (item != null && item.isJsonObject()) {
                stack = ItemAdapter.ITEM_READER.fromJson(item, ItemStack.class);
            }
            int min = obj.get("min_count").getAsInt();
            int max = obj.get("max_count").getAsInt();
            return new OutputData(stack, min, max);
        }


    }
}
