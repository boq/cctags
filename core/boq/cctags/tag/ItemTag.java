package boq.cctags.tag;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.input.Keyboard;

import boq.cctags.CCTags;
import boq.cctags.Constants;
import boq.cctags.tag.EntityTagsListener.TagProperty;
import boq.cctags.tag.TagIcons.IconData;
import boq.cctags.tag.TagLibrary.LibEntry;
import boq.utils.misc.PlayerOrientation;
import boq.utils.misc.Rotation;

import com.google.common.base.Strings;

import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemTag extends Item {

    private final static String CATEGORY_TAG = "Category";
    private final static String COMMENT_TAG = "Comment";

    public ItemStack createFromData(TagData data) {
        int damage = ItemTagUtils.calculateDamage(data.tagType, data.tagSize);
        ItemStack result = new ItemStack(this, 1, damage);
        ItemTagUtils.writeData(result, data);
        return result;
    }

    public ItemTag(int par1) {
        super(par1);
        setUnlocalizedName("cctag");
        setCreativeTab(CCTags.instance.tabTags);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister registry) {
        itemIcon = registry.registerIcon("cctags:tag-marker");
        for (TagType type : TagType.VALUES)
            type.registerIcons(registry);

        for (TagSize size : TagSize.VALUES)
            size.registerIcons(registry);

        TagIcons.instance.registerIcons(registry);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List description, boolean extended) {
        TagSize size = ItemTagUtils.getSize(stack);

        final LanguageRegistry reg = LanguageRegistry.instance();
        String sizeTemplate = reg.getStringLocalization("cctag.size");
        description.add(String.format(sizeTemplate, size.name));

        NBTTagCompound tag = ItemTagUtils.getItemTag(stack);

        if (tag.hasKey(CATEGORY_TAG)) {
            String category = "cctag.category." + tag.getString(CATEGORY_TAG);
            description.add(String.format(
                    reg.getStringLocalization("cctag.category"),
                    reg.getStringLocalization(category)
                    ));
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            if (tag.hasKey(TagData.TAG_ICON)) {
                String icon = tag.getString(TagData.TAG_ICON);
                IconData data = TagIcons.instance.getIconData(icon);
                description.add(data.getDescription(reg));
            }

            if (tag.hasKey(COMMENT_TAG))
                description.add("\u00A7o" + tag.getString(COMMENT_TAG) + "\u00A7r");
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return ItemTagUtils.getType(stack).unlocalizedName;
    }

    @Override
    public String getItemDisplayName(ItemStack stack) {
        String label = ItemTagUtils.getTag(stack, TagData.TAG_LABEL, null);

        if (Strings.isNullOrEmpty(label))
            return super.getItemDisplayName(stack);

        return label;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(int id, CreativeTabs tab, List results) {
        for (TagType type : TagType.VALUES)
            for (TagSize size : TagSize.VALUES)
                if (size.visible()) {
                    ItemStack stack = new ItemStack(id, 1, ItemTagUtils.calculateDamage(type, size));
                    ItemTagUtils.setupDefaultTags(stack);
                    results.add(stack);
                }

        if (CCTags.config.SHOW_LIBRARY)
            for (LibEntry e : TagLibrary.instance.getLibrary().values()) {
                ItemStack stack = new ItemStack(id, 1, e.size.ordinal());
                TagData data = new TagData();
                data.color = e.color;
                data.contents = e.contents;
                data.label = e.label;
                data.icon = e.icon;

                NBTTagCompound tag = ItemTagUtils.getItemTag(stack);
                data.writeToNBT(tag, ItemTagUtils.nbtOnlySelector);

                if (e.category != null)
                    tag.setString(CATEGORY_TAG, e.category);

                if (e.comment != null)
                    tag.setString(COMMENT_TAG, e.comment);

                results.add(stack);
            }

    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    public int getRenderPasses(int metadata) {
        return 3;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        return (renderPass == 1) ? ItemTagUtils.getColor(stack) : Constants.COLOR_WHITE;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIconFromDamageForRenderPass(int damage, int pass) {
        switch (pass) {
            case 0:
                return ItemTagUtils.getType(damage).backgroundIcon;
            case 2:
                return ItemTagUtils.getSize(damage).icon;
            default:
                return itemIcon;

        }
    }

    public boolean isBlockSideAvailable(ForgeDirection dir, World world, int x, int y, int z) {
        for (EntityTag e : TagUtils.getBlockTags(world, x, y, z))
            if (e.data.side == dir)
                return false;

        return true;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float xOffset, float yOffset, float zOffset) {
        if (world.isRemote || !player.canPlayerEdit(x, y, z, side, stack))
            return false;

        TagData data = ItemTagUtils.createFromStack(stack);

        data.side = ForgeDirection.VALID_DIRECTIONS[side];

        if (!isBlockSideAvailable(data.side, world, x, y, z))
            return false;

        switch (data.side) {
            case UP: {
                PlayerOrientation orientation = PlayerOrientation.getEntityOrientation(player);
                data.rotation = Rotation.playerToSwitchOrientation(orientation, false);
                break;
            }

            case DOWN: {
                PlayerOrientation orientation = PlayerOrientation.getEntityOrientation(player);
                data.rotation = Rotation.playerToSwitchOrientation(orientation, true).opposite();
                break;
            }

            default:
                data.rotation = Rotation.R0;
        }

        EntityTag tag = new EntityTag(world, data);
        tag.setPosition(x + 0.5, y + 0.5, z + 0.5);

        world.spawnEntityInWorld(tag);

        stack.stackSize--;
        return true;
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityLiving entity) {
        TagProperty prop = EntityTagsListener.getProperty(entity);

        if (prop != null && prop.tagData == null) {
            prop.tagData = ItemTagUtils.createFromStack(stack);
            stack.stackSize--;
            return true;
        }

        return false;
    }
}
