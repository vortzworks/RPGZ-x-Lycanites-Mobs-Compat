package com.vortz.rpgzlycanites;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(RpgzLycanitesCompat.MOD_ID)
public class RpgzLycanitesCompat {
    public static final String MOD_ID = "rpgzlycanitescompat";
    public static final Logger LOGGER = LogUtils.getLogger();

    public RpgzLycanitesCompat() {
        LOGGER.info("RpgZ x Lycanites compat loaded");
    }
}
