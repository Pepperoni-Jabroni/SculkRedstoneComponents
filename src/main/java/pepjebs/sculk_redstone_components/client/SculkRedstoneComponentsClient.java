package pepjebs.sculk_redstone_components.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.registry.Registries;
import pepjebs.sculk_redstone_components.SculkRedstoneComponentsMod;

public class SculkRedstoneComponentsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(
                Registries.BLOCK.get(SculkRedstoneComponentsMod.SHRIEKER_ID),
                RenderLayer.getCutout()
        );
    }
}
