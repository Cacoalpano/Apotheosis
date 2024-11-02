package shadows.apotheosis.adventure.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import shadows.apotheosis.Apoth;
import shadows.apotheosis.Apotheosis;
import shadows.apotheosis.adventure.affix.salvaging.SalvagingRecipe;

import java.util.Arrays;
import java.util.List;

public class SalvagingCategory implements IRecipeCategory<SalvagingRecipe> {

    public static final ResourceLocation TEXTURES = new ResourceLocation(Apotheosis.MODID, "textures/gui/salvage_jei.png");

    private static final ResourceLocation UID = Apotheosis.loc("fakesalvaging");

    private final Component title = new TranslatableComponent("title.apotheosis.salvaging");
    private final IDrawable background;
    private final IDrawable icon;

    public SalvagingCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.drawableBuilder(TEXTURES, 0, 0, 98, 74).addPadding(0, 0, 0, 0).build();
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Apoth.Blocks.SALVAGING_TABLE));
    }

    @Override
    public @NotNull RecipeType<SalvagingRecipe> getRecipeType() {
        return AdventureJEIPlugin.SALVAGING;
    }

    @Override
    public @NotNull Component getTitle() {
        return this.title;
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return this.background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void draw(SalvagingRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull PoseStack stack, double mouseX, double mouseY) {
        List<SalvagingRecipe.OutputData> outputs = recipe.getOutputs();
        Font font = Minecraft.getInstance().font;

        int idx = 0;
        for (var d : outputs) {
            stack.pushPose();
            stack.translate(0, 0, 200);
            String text = String.format("%d-%d", d.getMin(), d.getMax());

            float x = 59 + 18 * (idx % 2) + (16 - font.width(text) * 0.5F);
            float y = 23F + 18 * (idx / 2);

            float scale = 0.5F;

            stack.scale(scale, scale, 1);
            font.drawShadow(stack, text, x / scale, y / scale, 0xFFFFFF);

            idx++;
            stack.popPose();
        }
    }

    @Override
    public ResourceLocation getUid() {
        return this.UID;
    }

    @Override
    public Class<? extends SalvagingRecipe> getRecipeClass() {
        return SalvagingRecipe.class;
    }


    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SalvagingRecipe recipe, @NotNull IFocusGroup focuses) {
        List<ItemStack> input = Arrays.asList(recipe.getInput().getItems());
        builder.addSlot(RecipeIngredientRole.INPUT, 5, 29).addIngredients(VanillaTypes.ITEM_STACK, input);
        List<SalvagingRecipe.OutputData> outputs = recipe.getOutputs();
        int idx = 0;
        for (var d : outputs) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 59 + 18 * (idx % 2), 11 + 18 * (idx / 2)).addIngredient(VanillaTypes.ITEM_STACK, d.getStack());
            idx++;
        }
    }
}
