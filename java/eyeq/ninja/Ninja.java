package eyeq.ninja;

import eyeq.ninja.event.NinjaEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import static eyeq.ninja.Ninja.MOD_ID;

@Mod(modid = MOD_ID, version = "1.0", dependencies = "after:eyeq_util")
public class Ninja {
    public static final String MOD_ID = "eyeq_ninja";

    @Mod.Instance(MOD_ID)
    public static Ninja instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new NinjaEventHandler());
    }
}
