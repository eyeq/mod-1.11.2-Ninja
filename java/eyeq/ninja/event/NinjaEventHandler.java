package eyeq.ninja.event;

import eyeq.util.entity.player.EntityPlayerUtils;
import eyeq.util.oredict.UOreDictionary;
import net.minecraft.block.BlockLog;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NinjaEventHandler {
    @SubscribeEvent
    public void onLivingAttackedEvent(LivingAttackEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if(!(entity instanceof EntityPlayer)) {
            return;
        }
        DamageSource source = event.getSource();
        if(!(source instanceof EntityDamageSource)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) entity;
        if(player.isEntityInvulnerable(source)) {
            return;
        }
        if(player.capabilities.disableDamage && !source.canHarmInCreative()) {
            return;
        }

        InventoryPlayer inventory = player.inventory;
        ItemStack itemStack = getLog(inventory.mainInventory);
        if(itemStack == null) {
            itemStack = getLog(inventory.offHandInventory);
        }
        if(itemStack == null) {
            return;
        }

        Entity temp = source.getEntity();
        if(temp instanceof EntityDragonPart && ((EntityDragonPart) temp).entityDragonObj instanceof Entity) {
            temp = (Entity) ((EntityDragonPart) temp).entityDragonObj;
        }
        EntityLivingBase atEntity = null;
        if(temp instanceof EntityLivingBase) {
            atEntity = (EntityLivingBase) temp;
        }

        float yaw;
        if(atEntity != null) {
            yaw = atEntity.rotationYaw;
        } else {
            yaw = player.rotationYaw;
        }
        float r = (float) (yaw * Math.PI / 180);
        float cos = MathHelper.cos(r);
        float sin = MathHelper.sin(r);
        BlockPos pos = player.getPosition();
        player.setPositionAndUpdate(pos.getX() + cos * 2, pos.getY(), pos.getZ() + sin * 2);

        World world = player.world;
        BlockPos posa = pos.offset(EnumFacing.DOWN.getOpposite());
        EnumActionResult result = EntityPlayerUtils.onItemUse(player, world, itemStack, posa, EnumFacing.DOWN, new Vec3d(posa), EnumHand.MAIN_HAND);
        if(result  == EnumActionResult.FAIL) {
            player.setPositionAndUpdate(pos.getX(), pos.getY(), pos.getZ());
            return;
        }

        player.motionX = -cos;
        player.motionY = 0.4;
        player.motionZ = -sin;

        world.createExplosion(null, pos.getX(), pos.getY() + player.eyeHeight, pos.getZ(), 0, false);
        if(atEntity != null) {
            BlockPos distance = pos.subtract(atEntity.getPosition());
            int v = (int)Math.round(Math.pow(distance.getX(), 2) + Math.pow(distance.getY(), 2) + Math.pow(distance.getZ(), 2));
            if(v <= 16) {
                int health = (int)Math.ceil(atEntity.getHealth() / 2);
                atEntity.attackEntityFrom(DamageSource.causeExplosionDamage(player), health);
            }
        }
        event.setCanceled(true);
    }

    private ItemStack getLog(NonNullList<ItemStack> list) {
        for(ItemStack itemStack : list) {
            if(UOreDictionary.contains(itemStack, UOreDictionary.OREDICT_LOG)) {
                return itemStack;
            }
        }
        return null;
    }
}
