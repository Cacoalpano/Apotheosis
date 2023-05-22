package shadows.apotheosis.potion;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolderRegistry;
import shadows.apotheosis.Apoth;
import shadows.apotheosis.Apotheosis;
import shadows.apotheosis.Apotheosis.ApotheosisConstruction;
import shadows.apotheosis.Apotheosis.ApotheosisReloadEvent;
import shadows.apotheosis.potion.compat.CuriosCompat;
import shadows.apotheosis.potion.potions.KnowledgeEffect;
import shadows.apotheosis.potion.potions.SunderingEffect;
import shadows.placebo.config.Configuration;
import top.theillusivec4.curios.api.SlotTypeMessage;

public class PotionModule {

	public static final Logger LOG = LogManager.getLogger("Apotheosis : Potion");
	public static final ResourceLocation POTION_TEX = new ResourceLocation(Apotheosis.MODID, "textures/potions.png");

	static int knowledgeMult = 4;
	static boolean charmsInCuriosOnly = false;

	@SubscribeEvent
	public void preInit(ApotheosisConstruction e) {
		this.reload(null);
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			FMLJavaModLoadingContext.get().getModEventBus().register(new PotionModuleClient());
		});
		InterModComms.sendTo("curios", "REGISTER_TYPE", () -> new SlotTypeMessage.Builder("charm").size(1).build());

	}

	@SubscribeEvent
	public void init(FMLCommonSetupEvent e) {
		e.enqueueWork(() -> {
			PotionBrewing.addMix(Potions.AWKWARD, Items.SHULKER_SHELL, Apoth.Potions.RESISTANCE);
			PotionBrewing.addMix(Apoth.Potions.RESISTANCE, Items.REDSTONE, Apoth.Potions.LONG_RESISTANCE);
			PotionBrewing.addMix(Apoth.Potions.RESISTANCE, Items.GLOWSTONE_DUST, Apoth.Potions.STRONG_RESISTANCE);

			PotionBrewing.addMix(Apoth.Potions.RESISTANCE, Items.FERMENTED_SPIDER_EYE, Apoth.Potions.SUNDERING);
			PotionBrewing.addMix(Apoth.Potions.LONG_RESISTANCE, Items.FERMENTED_SPIDER_EYE, Apoth.Potions.LONG_SUNDERING);
			PotionBrewing.addMix(Apoth.Potions.STRONG_RESISTANCE, Items.FERMENTED_SPIDER_EYE, Apoth.Potions.STRONG_SUNDERING);
			PotionBrewing.addMix(Apoth.Potions.SUNDERING, Items.REDSTONE, Apoth.Potions.LONG_SUNDERING);
			PotionBrewing.addMix(Apoth.Potions.SUNDERING, Items.GLOWSTONE_DUST, Apoth.Potions.STRONG_SUNDERING);

			PotionBrewing.addMix(Potions.AWKWARD, Items.GOLDEN_APPLE, Apoth.Potions.ABSORPTION);
			PotionBrewing.addMix(Apoth.Potions.ABSORPTION, Items.REDSTONE, Apoth.Potions.LONG_ABSORPTION);
			PotionBrewing.addMix(Apoth.Potions.ABSORPTION, Items.GLOWSTONE_DUST, Apoth.Potions.STRONG_ABSORPTION);

			PotionBrewing.addMix(Potions.AWKWARD, Items.MUSHROOM_STEW, Apoth.Potions.HASTE);
			PotionBrewing.addMix(Apoth.Potions.HASTE, Items.REDSTONE, Apoth.Potions.LONG_HASTE);
			PotionBrewing.addMix(Apoth.Potions.HASTE, Items.GLOWSTONE_DUST, Apoth.Potions.STRONG_HASTE);

			PotionBrewing.addMix(Apoth.Potions.HASTE, Items.FERMENTED_SPIDER_EYE, Apoth.Potions.FATIGUE);
			PotionBrewing.addMix(Apoth.Potions.LONG_HASTE, Items.FERMENTED_SPIDER_EYE, Apoth.Potions.LONG_FATIGUE);
			PotionBrewing.addMix(Apoth.Potions.STRONG_HASTE, Items.FERMENTED_SPIDER_EYE, Apoth.Potions.STRONG_FATIGUE);
			PotionBrewing.addMix(Apoth.Potions.FATIGUE, Items.REDSTONE, Apoth.Potions.LONG_FATIGUE);
			PotionBrewing.addMix(Apoth.Potions.FATIGUE, Items.GLOWSTONE_DUST, Apoth.Potions.STRONG_FATIGUE);

			if (Apoth.Items.SKULL_FRAGMENT != null) PotionBrewing.addMix(Potions.AWKWARD, Apoth.Items.SKULL_FRAGMENT, Apoth.Potions.WITHER);
			else PotionBrewing.addMix(Potions.AWKWARD, Items.WITHER_SKELETON_SKULL, Apoth.Potions.WITHER);
			PotionBrewing.addMix(Apoth.Potions.WITHER, Items.REDSTONE, Apoth.Potions.LONG_WITHER);
			PotionBrewing.addMix(Apoth.Potions.WITHER, Items.GLOWSTONE_DUST, Apoth.Potions.STRONG_WITHER);

			PotionBrewing.addMix(Potions.AWKWARD, Items.EXPERIENCE_BOTTLE, Apoth.Potions.KNOWLEDGE);
			PotionBrewing.addMix(Apoth.Potions.KNOWLEDGE, Items.REDSTONE, Apoth.Potions.LONG_KNOWLEDGE);
			PotionBrewing.addMix(Apoth.Potions.KNOWLEDGE, Items.EXPERIENCE_BOTTLE, Apoth.Potions.STRONG_KNOWLEDGE);

			PotionBrewing.addMix(Potions.AWKWARD, Apoth.Items.LUCKY_FOOT, Potions.LUCK);
		});
		Apotheosis.HELPER.registerProvider(factory -> {
			Ingredient fireRes = Apotheosis.potionIngredient(Potions.FIRE_RESISTANCE);
			Ingredient abs = Apotheosis.potionIngredient(Apoth.Potions.STRONG_ABSORPTION);
			Ingredient res = Apotheosis.potionIngredient(Apoth.Potions.RESISTANCE);
			Ingredient regen = Apotheosis.potionIngredient(Potions.STRONG_REGENERATION);
			factory.addShaped(Items.ENCHANTED_GOLDEN_APPLE, 3, 3, fireRes, regen, fireRes, abs, Items.GOLDEN_APPLE, abs, res, abs, res);
		});

		MinecraftForge.EVENT_BUS.addListener(this::drops);
		MinecraftForge.EVENT_BUS.addListener(this::xp);
		MinecraftForge.EVENT_BUS.addListener(this::reload);
	}

	@SubscribeEvent
	public void items(Register<Item> e) {
		e.getRegistry().registerAll(new LuckyFootItem().setRegistryName(Apotheosis.MODID, "lucky_foot"), new PotionCharmItem().setRegistryName(Apotheosis.MODID, "potion_charm"));
	}

	@SubscribeEvent
	public void types(Register<Potion> e) {
		//Formatter::off
		e.getRegistry().registerAll(
				new Potion("resistance", new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 3600)).setRegistryName(Apotheosis.MODID, "resistance"),
				new Potion("resistance", new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 9600)).setRegistryName(Apotheosis.MODID, "long_resistance"),
				new Potion("resistance", new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1800, 1)).setRegistryName(Apotheosis.MODID, "strong_resistance"),
				new Potion("absorption", new MobEffectInstance(MobEffects.ABSORPTION, 1200, 1)).setRegistryName(Apotheosis.MODID, "absorption"),
				new Potion("absorption", new MobEffectInstance(MobEffects.ABSORPTION, 3600, 1)).setRegistryName(Apotheosis.MODID, "long_absorption"),
				new Potion("absorption", new MobEffectInstance(MobEffects.ABSORPTION, 600, 3)).setRegistryName(Apotheosis.MODID, "strong_absorption"),
				new Potion("haste", new MobEffectInstance(MobEffects.DIG_SPEED, 3600)).setRegistryName(Apotheosis.MODID, "haste"),
				new Potion("haste", new MobEffectInstance(MobEffects.DIG_SPEED, 9600)).setRegistryName(Apotheosis.MODID, "long_haste"),
				new Potion("haste", new MobEffectInstance(MobEffects.DIG_SPEED, 1800, 1)).setRegistryName(Apotheosis.MODID, "strong_haste"),
				new Potion("fatigue", new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 3600)).setRegistryName(Apotheosis.MODID, "fatigue"),
				new Potion("fatigue", new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 9600)).setRegistryName(Apotheosis.MODID, "long_fatigue"),
				new Potion("fatigue", new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 1800, 1)).setRegistryName(Apotheosis.MODID, "strong_fatigue"),
				new Potion("wither", new MobEffectInstance(MobEffects.WITHER, 3600)).setRegistryName(Apotheosis.MODID, "wither"),
				new Potion("wither", new MobEffectInstance(MobEffects.WITHER, 9600)).setRegistryName(Apotheosis.MODID, "long_wither"),
				new Potion("wither", new MobEffectInstance(MobEffects.WITHER, 1800, 1)).setRegistryName(Apotheosis.MODID, "strong_wither"),
				new Potion("sundering", new MobEffectInstance(Apoth.Effects.SUNDERING, 3600)).setRegistryName(Apotheosis.MODID, "sundering"),
				new Potion("sundering", new MobEffectInstance(Apoth.Effects.SUNDERING, 9600)).setRegistryName(Apotheosis.MODID, "long_sundering"),
				new Potion("sundering", new MobEffectInstance(Apoth.Effects.SUNDERING, 1800, 1)).setRegistryName(Apotheosis.MODID, "strong_sundering"),
				new Potion("knowledge", new MobEffectInstance(Apoth.Effects.KNOWLEDGE, 2400)).setRegistryName(Apotheosis.MODID, "knowledge"),
				new Potion("knowledge", new MobEffectInstance(Apoth.Effects.KNOWLEDGE, 4800)).setRegistryName(Apotheosis.MODID, "long_knowledge"),
				new Potion("knowledge", new MobEffectInstance(Apoth.Effects.KNOWLEDGE, 1200, 1)).setRegistryName(Apotheosis.MODID, "strong_knowledge"));
		//Formatter::on
	}

	@SubscribeEvent
	public void potions(Register<MobEffect> e) {
		e.getRegistry().register(new SunderingEffect().setRegistryName(Apotheosis.MODID, "sundering"));
		e.getRegistry().register(new KnowledgeEffect().setRegistryName(Apotheosis.MODID, "knowledge"));
		ObjectHolderRegistry.applyObjectHolders(r -> r.getNamespace().equals(Apotheosis.MODID) && (r.getPath().equals("sundering") || r.getPath().equals("knowledge")));
	}

	@SubscribeEvent
	public void serializers(Register<RecipeSerializer<?>> e) {
		e.getRegistry().register(PotionCharmRecipe.Serializer.INSTANCE.setRegistryName(Apoth.Items.POTION_CHARM.getRegistryName()));
		e.getRegistry().register(PotionEnchantingRecipe.SERIALIZER.setRegistryName("potion_charm_enchanting"));
	}

	@SubscribeEvent
	public void imcEvent(InterModEnqueueEvent e) {
		if (ModList.get().isLoaded("curios")) CuriosCompat.sendIMC();
	}

	public void drops(LivingDropsEvent e) {
		if (e.getEntityLiving() instanceof Rabbit) {
			Rabbit rabbit = (Rabbit) e.getEntityLiving();
			if (rabbit.level.random.nextFloat() < 0.045F + 0.045F * e.getLootingLevel()) {
				e.getDrops().clear();
				e.getDrops().add(new ItemEntity(rabbit.level, rabbit.getX(), rabbit.getY(), rabbit.getZ(), new ItemStack(Apoth.Items.LUCKY_FOOT)));
			}
		}
	}

	public void xp(LivingExperienceDropEvent e) {
		if (e.getAttackingPlayer() != null && e.getAttackingPlayer().getEffect(Apoth.Effects.KNOWLEDGE) != null) {
			int level = e.getAttackingPlayer().getEffect(Apoth.Effects.KNOWLEDGE).getAmplifier() + 1;
			int curXp = e.getDroppedExperience();
			int newXp = curXp + e.getOriginalExperience() * level * knowledgeMult;
			e.setDroppedExperience(newXp);
		}
	}

	public void reload(ApotheosisReloadEvent e) {
		Configuration config = new Configuration(new File(Apotheosis.configDir, "potion.cfg"));
		knowledgeMult = config.getInt("Knowledge XP Multiplier", "general", knowledgeMult, 1, Integer.MAX_VALUE, "The strength of Ancient Knowledge.  This multiplier determines how much additional xp is granted.");
		charmsInCuriosOnly = config.getBoolean("Restrict Charms to Curios", "general", charmsInCuriosOnly, "If Potion Charms will only work when in a curios slot, instead of in the inventory.");

		String[] defExt = new String[] { MobEffects.NIGHT_VISION.getRegistryName().toString(), MobEffects.HEALTH_BOOST.getRegistryName().toString() };
		String[] names = config.getStringList("Extended Potion Charms", "general", defExt, "A list of effects that, when as charms, will be applied and reapplied at a longer threshold to avoid issues at low durations, like night vision.");
		PotionCharmItem.EXTENDED_POTIONS.clear();
		for (String s : names) {
			try {
				PotionCharmItem.EXTENDED_POTIONS.add(new ResourceLocation(s));
			} catch (ResourceLocationException ex) {
				LOG.error("Invalid extended potion charm entry {} will be ignored.", s);
			}
		}

		if (e == null && config.hasChanged()) config.save();
	}

}