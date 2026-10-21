package net.chaolux.delightlegacy.common.event;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.chaolux.delightlegacy.registry.ModBlocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.UseHoeEvent;

public class CommonEvents {
    @SubscribeEvent
    public void onUseHoe(UseHoeEvent useHoeEvent) {
        if(useHoeEvent.world.getBlock(useHoeEvent.x,useHoeEvent.y,useHoeEvent.z) != ModBlocks.RICH_SOIL.get()) return;
        if(!useHoeEvent.world.isAirBlock(useHoeEvent.x,useHoeEvent.y + 1,useHoeEvent.z)) return;
        if(!useHoeEvent.world.isRemote) useHoeEvent.world.setBlock(useHoeEvent.x,useHoeEvent.y,useHoeEvent.z,ModBlocks.RICH_SOIL_FARMLAND.get(),0,3);
        useHoeEvent.setResult(Event.Result.ALLOW);
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new CommonEvents());
    }
}
