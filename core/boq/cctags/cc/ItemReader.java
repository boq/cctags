package boq.cctags.cc;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatMessageComponent;
import boq.cctags.CCTags;
import boq.cctags.tag.EntityTagsListener;
import boq.cctags.tag.EntityTagsListener.TagProperty;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemReader extends Item {

    public ItemReader(int id) {
        super(id);
        setMaxDamage(0);
        setHasSubtypes(true);
        setCreativeTab(CCTags.instance.tabTags);
        setUnlocalizedName("tag-reader");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister registry) {
        itemIcon = registry.registerIcon("cctags:handheld");
    }

    @Override
    public boolean func_111207_a(ItemStack stack, EntityPlayer player, EntityLivingBase entity) {
        TagProperty prop = EntityTagsListener.getProperty(entity);

        if (prop != null && prop.tagData != null) {
            if (!player.worldObj.isRemote) {
                Object[] params = prop.tagData.tagDescription(entity);
                player.sendChatToPlayer(ChatMessageComponent.func_111082_b("handheld.desc", params));
            }
            return true;
        }

        return false;
    }
}
