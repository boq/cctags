package boq.cctags.tag;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import boq.cctags.CCTags;
import boq.cctags.cc.TurtlePeripheralType;
import boq.cctags.tag.EntityTagsListener.TagProperty;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemManipulator extends Item {

    public ItemManipulator(int id) {
        super(id);
        setMaxDamage(0);
        setHasSubtypes(false);
        setCreativeTab(CCTags.instance.tabTags);
        setUnlocalizedName("tag-manipulator");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister registry) {
        itemIcon = registry.registerIcon("cctags:manipulator");
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityLiving entity) {
        if (entity.worldObj.isRemote)
            return false;

        TagProperty prop = EntityTagsListener.getProperty(entity);

        if (prop != null && prop.tagData != null) {
            ItemStack item = CCTags.instance.itemTag.createFromData(prop.tagData);
            prop.tagData = null;
            entity.entityDropItem(item, 0);
            return true;
        }

        return false;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void getSubItems(int id, CreativeTabs tab, List result) {
        super.getSubItems(id, tab, result);
        TurtlePeripheralType.addManipulatorTurtles(result);
    }
}
