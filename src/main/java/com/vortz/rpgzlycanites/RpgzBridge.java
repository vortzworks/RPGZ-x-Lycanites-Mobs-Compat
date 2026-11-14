package com.vortz.rpgzlycanites;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Talks to RpgZ without compiling against it. RpgZ adds the method
 * rpgZ_forge$addingInventoryItems(ItemStack) to every Mob (via its IInventoryAccess
 * interface); that puts a stack into the corpse's loot inventory.
 */
public final class RpgzBridge {
    private static final String METHOD_NAME = "rpgZ_forge$addingInventoryItems";
    private static final Method MISSING;
    private static final Map<Class<?>, Method> CACHE = new ConcurrentHashMap<>();
    private static boolean warned = false;

    static {
        try {
            MISSING = RpgzBridge.class.getDeclaredMethod("noop");
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static void noop() {
    }

    private RpgzBridge() {
    }

    /** Returns true if the stack was handed to RpgZ's corpse inventory. */
    public static boolean tryCapture(LivingEntity entity, ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        Method method = CACHE.computeIfAbsent(entity.getClass(), RpgzBridge::lookup);
        if (method == MISSING) {
            return false;
        }
        try {
            method.invoke(entity, stack);
            return true;
        } catch (Throwable t) {
            if (!warned) {
                warned = true;
                RpgzLycanitesCompat.LOGGER.warn("Could not hand drop to RpgZ, dropping normally", t);
            }
            return false;
        }
    }

    private static Method lookup(Class<?> type) {
        try {
            return type.getMethod(METHOD_NAME, ItemStack.class);
        } catch (NoSuchMethodException e) {
            RpgzLycanitesCompat.LOGGER.warn("RpgZ corpse inventory not found on {}; is RpgZ installed?", type.getName());
            return MISSING;
        }
    }
}
