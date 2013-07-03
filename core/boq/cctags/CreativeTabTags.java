package boq.cctags;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class CreativeTabTags extends CreativeTabs {

    public CreativeTabTags(int id) {
        super(id, "cctags");
    }

    @Override
    public ItemStack getIconItemStack()
    {
        return new ItemStack(CCTags.instance.itemTag);
    }
}
