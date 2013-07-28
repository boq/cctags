package boq.cctags.tag;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import boq.cctags.CCTags;
import boq.cctags.tag.EntityTagsListener.TagProperty;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemReader extends Item {

    public ItemReader(int id) {
        super(id);
        setMaxDamage(0);
        setHasSubtypes(false);
        setCreativeTab(CCTags.instance.tabTags);
        setUnlocalizedName("tag-reader");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister registry) {
        itemIcon = registry.registerIcon("cctags:handheld");
    }


    public boolean interact(ItemStack stack, EntityPlayer player, EntityLiving entity) {
        if (player instanceof EntityPlayerSP)
            return false;

        TagProperty prop = EntityTagsListener.getProperty(entity);

        if (prop != null && prop.tagData != null) {
            if (!player.worldObj.isRemote)
                player.sendChatToPlayer(prop.tagData.tagDescription(entity));

            return true;
        }

        return false;
    }
}
