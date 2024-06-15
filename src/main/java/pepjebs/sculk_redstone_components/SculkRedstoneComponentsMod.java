package pepjebs.sculk_redstone_components;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pepjebs.sculk_redstone_components.blocks.CalibratorBlock;
import pepjebs.sculk_redstone_components.blocks.RetainerBlock;
import pepjebs.sculk_redstone_components.blocks.ShriekerBlock;

import java.util.List;

public class SculkRedstoneComponentsMod implements ModInitializer {

    public static String MOD_ID = "sculk_redstone_components";
    public static Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static Identifier CALIBRATOR_ID = Identifier.of(MOD_ID, "calibrator");
    public static Identifier RETAINER_ID = Identifier.of(MOD_ID, "retainer");
    public static Identifier SHRIEKER_ID = Identifier.of(MOD_ID, "shrieker");

    @Override
    public void onInitialize() {
        RetainerBlock retainer = new RetainerBlock(FabricBlockSettings.create());
        Registry.register(Registries.BLOCK,
                RETAINER_ID,
                retainer);
        Item retainerItem = Registry.register(
                Registries.ITEM,
                RETAINER_ID,
                new BlockItem(retainer, new Item.Settings()));
        ShriekerBlock shrieker = new ShriekerBlock(FabricBlockSettings.create().nonOpaque());
        Registry.register(Registries.BLOCK,
                SHRIEKER_ID,
                shrieker);
        Item shriekerItem = Registry.register(
                Registries.ITEM,
                SHRIEKER_ID,
                new BlockItem(shrieker, new Item.Settings()));
        CalibratorBlock calibrator = new CalibratorBlock(FabricBlockSettings.create());
        Registry.register(Registries.BLOCK,
                CALIBRATOR_ID,
                calibrator);
        Item calibratorItem = Registry.register(
                Registries.ITEM,
                CALIBRATOR_ID,
                new BlockItem(calibrator, new Item.Settings()));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> {
            content.addAfter(Items.COMPARATOR, calibratorItem);
            content.addAfter(calibratorItem, retainerItem);
            content.addAfter(retainerItem, shriekerItem);
        });
    }
}
