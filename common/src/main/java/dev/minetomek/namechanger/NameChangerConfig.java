package dev.minetomek.namechanger;

import net.blay09.mods.balm.platform.config.reflection.Config;

@Config(value = NameChanger.MOD_ID)
public class NameChangerConfig {
    public boolean nameConflictWarningEnabled = true;
    public boolean forbidNameConflicts = false;
}
