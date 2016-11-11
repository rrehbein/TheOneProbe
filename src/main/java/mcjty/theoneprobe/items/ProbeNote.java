package mcjty.theoneprobe.items;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.proxy.GuiProxy;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ProbeNote extends Item {

    public ProbeNote() {
        setUnlocalizedName(TheOneProbe.MODID + ".probenote");
        setRegistryName("probenote");
        setCreativeTab(TheOneProbe.tabProbe);
        setMaxStackSize(1);
        GameRegistry.register(this);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if (world.isRemote) {
            if (player.isSneaking()) {
                player.openGui(TheOneProbe.instance, GuiProxy.GUI_CONFIG, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
            } else {
                player.openGui(TheOneProbe.instance, GuiProxy.GUI_NOTE, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}