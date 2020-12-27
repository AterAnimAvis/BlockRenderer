package com.unascribed.blockrenderer.client.impl;

import com.unascribed.blockrenderer.client.api.Bounds;
import com.unascribed.blockrenderer.client.api.BoundsProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.passive.BatEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VanillaBoundsProvider implements BoundsProvider {

    private static final Map<EntityType<?>, Bounds> fixed = new HashMap<>();
    private static final List<EntityType<?>> calculated = new ArrayList<>();

    static {
        reload();
    }

    //TODO: Port these to JavaScript
    private static void reload() {
        calculated.clear();
        fixed.clear();

        //TODO: calculated.add(EntityType.AREA_EFFECT_CLOUD);
        calculated.add(EntityType.ARMOR_STAND);
        //TODO: calculated.add(EntityType.ARROW);
        calculated.add(EntityType.BAT);
        fixed.put(EntityType.BEE, box(1.1, 1.2, 1.1)); //TODO PORT
        fixed.put(EntityType.BLAZE, box(1.2, 2.2, 1.2)); //TODO PORT
        //TODO: fixed.put(EntityType.BOAT, ...);
        fixed.put(EntityType.CAT, Bounds.of(1, 1.1, 2, 0, 0, 0.5));

        //TODO: EntityType.CAVE_SPIDER;
        //TODO: EntityType.CHICKEN;
        //TODO: EntityType.COD;
        //TODO: EntityType.COW;
        //TODO: EntityType.CREEPER;
        //TODO: EntityType.DONKEY;
        //TODO: EntityType.DOLPHIN;
        //TODO: EntityType.DRAGON_FIREBALL;
        //TODO: EntityType.DROWNED;
        //TODO: EntityType.ELDER_GUARDIAN;
        //TODO: EntityType.END_CRYSTAL;
        //TODO: EntityType.ENDER_DRAGON;
        //TODO: EntityType.ENDERMAN;
        //TODO: EntityType.ENDERMITE;
        //TODO: EntityType.EVOKER_FANGS;
        //TODO: EntityType.EVOKER;
        //TODO: EntityType.EXPERIENCE_ORB;
        //TODO: EntityType.EYE_OF_ENDER;
        //TODO: EntityType.FALLING_BLOCK;
        //TODO: EntityType.FIREWORK_ROCKET;
        //TODO: EntityType.FOX;
        //TODO: EntityType.GHAST;
        //TODO: EntityType.GIANT;
        //TODO: EntityType.GUARDIAN;
        //TODO: EntityType.HORSE;
        //TODO: EntityType.HUSK;
        //TODO: EntityType.ILLUSIONER;
        //TODO: EntityType.ITEM;
        //TODO: EntityType.ITEM_FRAME;
        //TODO: EntityType.FIREBALL;
        //TODO: EntityType.LEASH_KNOT;
        //TODO: EntityType.LLAMA;
        //TODO: EntityType.LLAMA_SPIT;
        //TODO: EntityType.MAGMA_CUBE;
        //TODO: EntityType.MINECART;
        //TODO: EntityType.CHEST_MINECART;
        //TODO: EntityType.COMMAND_BLOCK_MINECART;
        //TODO: EntityType.FURNACE_MINECART;
        //TODO: EntityType.HOPPER_MINECART;
        //TODO: EntityType.SPAWNER_MINECART;
        //TODO: EntityType.TNT_MINECART;
        //TODO: EntityType.MULE;
        //TODO: EntityType.MOOSHROOM;
        //TODO: EntityType.OCELOT;
        //TODO: EntityType.PAINTING;
        //TODO: EntityType.PANDA;
        //TODO: EntityType.PARROT;
        //TODO: EntityType.PIG;
        //TODO: EntityType.PUFFERFISH;
        //TODO: EntityType.ZOMBIE_PIGMAN;
        //TODO: EntityType.POLAR_BEAR;
        //TODO: EntityType.TNT;
        //TODO: EntityType.RABBIT;
        //TODO: EntityType.SALMON;
        //TODO: EntityType.SHEEP;
        //TODO: EntityType.SHULKER;
        //TODO: EntityType.SHULKER_BULLET;
        //TODO: EntityType.SILVERFISH;
        //TODO: EntityType.SKELETON;
        //TODO: EntityType.SKELETON_HORSE;
        //TODO: EntityType.SLIME;
        //TODO: EntityType.SMALL_FIREBALL;
        //TODO: EntityType.SNOW_GOLEM;
        //TODO: EntityType.SNOWBALL;
        //TODO: EntityType.SPECTRAL_ARROW;
        //TODO: EntityType.SPIDER;
        //TODO: EntityType.SQUID;
        //TODO: EntityType.STRAY;
        //TODO: EntityType.TRADER_LLAMA;
        //TODO: EntityType.TROPICAL_FISH;
        //TODO: EntityType.TURTLE;
        //TODO: EntityType.EGG;
        //TODO: EntityType.ENDER_PEARL;
        //TODO: EntityType.EXPERIENCE_BOTTLE;
        //TODO: EntityType.POTION;
        //TODO: EntityType.TRIDENT;
        //TODO: EntityType.VEX;
        //TODO: EntityType.VILLAGER;
        //TODO: EntityType.IRON_GOLEM;
        //TODO: EntityType.VINDICATOR;
        //TODO: EntityType.PILLAGER;
        //TODO: EntityType.WANDERING_TRADER;
        //TODO: EntityType.WITCH;
        //TODO: EntityType.WITHER;
        //TODO: EntityType.WITHER_SKELETON;
        //TODO: EntityType.WITHER_SKULL;
        //TODO: EntityType.WOLF;
        //TODO: EntityType.ZOMBIE;
        //TODO: EntityType.ZOMBIE_HORSE;
        //TODO: EntityType.ZOMBIE_VILLAGER;
        //TODO: EntityType.PHANTOM;
        //TODO: EntityType.RAVAGER;
        //TODO: EntityType.LIGHTNING_BOLT;
        //TODO: EntityType.PLAYER;
        //TODO: EntityType.FISHING_BOBBER;

    }

    @Override
    public Bounds bounds(Entity entity, Bounds current) {
        if (fixed.containsKey(entity.getType())) {
            return fixed.get(entity.getType());
        }

        //TODO: if (entity.getType() == EntityType.AREA_EFFECT_CLOUD)
        //TODO: if (entity.getType() == EntityType.ARROW)

        if (entity.getType() == EntityType.ARMOR_STAND) {
            ArmorStandEntity stand = (ArmorStandEntity) entity;
            //TODO: If Pose is default and not wearing armor / holding items: return stand.isSmall() ? box(0.4, 1.3, 0.4) : box(0.75, 2.2, 0.75);
            return stand.isSmall() ? Bounds.of(2, 2, 2) : Bounds.of(3, 4, 3);
        }

        if (entity.getType() == EntityType.BAT) {
            BatEntity bat = (BatEntity) entity;
            double height = bat.getIsBatHanging() ? 0.9 : 0.5;
            return Bounds.of(0.9, height, 0.9, 0, -0.5, 0);
        }

        return current;
    }

    @Override
    public boolean validFor(Entity entity) {
        reload(); // TODO: Remove after DEV

        EntityType<?> type = entity.getType();
        return fixed.containsKey(type) || calculated.contains(type);
    }

    private static Bounds box(double x, double y, double z) {
        return new Bounds.Instance(x, y, z);
    }
}
