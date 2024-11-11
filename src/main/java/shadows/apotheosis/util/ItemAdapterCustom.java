package shadows.apotheosis.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import shadows.placebo.json.ItemAdapter;

import java.util.Optional;

public class ItemAdapterCustom extends ItemAdapter {

    public static final Gson ITEM_READER = new GsonBuilder().registerTypeAdapter(ItemStack.class, INSTANCE).registerTypeAdapter(CompoundTag.class, ItemAdapter.INSTANCE).registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer()).create();

    //Formatter::off
    public static final Codec<ItemStack> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    ForgeRegistries.ITEMS.getCodec().fieldOf("item").forGetter(ItemStack::getItem),
                    Codec.intRange(0, 64).optionalFieldOf("count", 1).forGetter(ItemStack::getCount),
                    CompoundTag.CODEC.optionalFieldOf("nbt").forGetter(stack -> Optional.ofNullable(stack.getTag())),
                    CompoundTag.CODEC.optionalFieldOf("cap_nbt").forGetter(ItemAdapterCustom::getCapNBT))
            .apply(inst, (item, count, nbt, capNbt) -> {var stack = new ItemStack(item, count, capNbt.orElse(null)); stack.setTag(nbt.orElse(null)); return stack;})
    );
    //Formatter::on

    private static Optional<CompoundTag> getCapNBT(ItemStack stack) {
        CompoundTag written = stack.save(new CompoundTag());
        if (written.contains("ForgeCaps")) return Optional.of(written.getCompound("ForgeCaps"));
        return Optional.empty();
    }
}
