package pepjebs.sculk_redstone_components.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.registry.Registries;
import pepjebs.sculk_redstone_components.SculkRedstoneComponentsMod;

public class SculkRedstoneComponentsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.putBlock(
                Registries.BLOCK.get(SculkRedstoneComponentsMod.SHRIEKER_ID),
                BlockRenderLayer.CUTOUT
        );
    }
}
