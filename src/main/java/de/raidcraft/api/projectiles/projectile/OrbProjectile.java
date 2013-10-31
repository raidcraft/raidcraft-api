package de.raidcraft.api.projectiles.projectile;

import de.raidcraft.api.projectiles.event.CustomProjectileHitEvent;
import net.minecraft.server.v1_6_R3.AxisAlignedBB;
import net.minecraft.server.v1_6_R3.Block;
import net.minecraft.server.v1_6_R3.Entity;
import net.minecraft.server.v1_6_R3.EntityExperienceOrb;
import net.minecraft.server.v1_6_R3.EntityHuman;
import net.minecraft.server.v1_6_R3.EntityLiving;
import net.minecraft.server.v1_6_R3.EnumMovingObjectType;
import net.minecraft.server.v1_6_R3.IProjectile;
import net.minecraft.server.v1_6_R3.MathHelper;
import net.minecraft.server.v1_6_R3.MinecraftServer;
import net.minecraft.server.v1_6_R3.MovingObjectPosition;
import net.minecraft.server.v1_6_R3.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftLivingEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Projectile made from exp orb entity. Warning! Becouse of minecraft bug client
 * see orb after delay of about 1 second. I couldn't find any solution for that
 * :/
 */
public class OrbProjectile extends EntityExperienceOrb implements CustomProjectile, IProjectile {

    private EntityLiving shooter;
    private String name;
    private int lastTick;
    private int age;

    /**
     * Instantiates a new orb projectile.
     *
     * @param name    projectile name
     * @param loc     location of projectile (sets position of projectile and shoots
     *                in pitch and yaw direction)
     * @param shooter projectile shooter
     * @param power   projectile power
     */
    public OrbProjectile(String name, Location loc, LivingEntity shooter, float power) {

        super(((CraftWorld) loc.getWorld()).getHandle(), loc.getX(), loc.getY(), loc.getZ(), 0);
        this.shooter = ((CraftLivingEntity) shooter).getHandle();
        this.name = name;
        lastTick = MinecraftServer.currentTick;
        this.a(0.25F, 0.25F);
        setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        locX -= (MathHelper.cos(yaw / 180.0F * 3.1415927F) * 0.16F);
        locY -= 0.10000000149011612D;
        locZ -= (MathHelper.sin(yaw / 180.0F * 3.1415927F) * 0.16F);
        setPosition(locX, locY, locZ);
        height = 0.0F;
        float f = 0.4F;
        motX = (-MathHelper.sin(yaw / 180.0F * 3.1415927F) * MathHelper.cos(pitch / 180.0F * 3.1415927F) * f);
        motZ = (MathHelper.cos(yaw / 180.0F * 3.1415927F) * MathHelper.cos(pitch / 180.0F * 3.1415927F) * f);
        motY = (-MathHelper.sin(pitch / 180.0F * 3.1415927F) * f);
        shoot(motX, motY, motZ, power * 1.5F, 1.0F);
        world.addEntity(this);
    }

    /**
     * Instantiates a new orb projectile.
     *
     * @param name    projectile name
     * @param shooter projectile shooter (it uses entity's location to set x, y, z,
     *                pitch and yaw of projectile)
     * @param power   projectile power
     */
    public OrbProjectile(String name, LivingEntity shooter, float power) {

        super(((CraftLivingEntity) shooter).getHandle().world, shooter.getLocation().getX(), shooter.getLocation().getX(), shooter.getLocation().getX(), 0);
        this.shooter = ((CraftLivingEntity) shooter).getHandle();
        this.name = name;
        lastTick = MinecraftServer.currentTick;
        this.a(0.25F, 0.25F);
        setPositionRotation(shooter.getLocation().getX(), shooter.getLocation().getY() + shooter.getEyeHeight(), shooter.getLocation().getZ(), shooter.getLocation().getYaw(), shooter.getLocation().getPitch());
        locX -= (MathHelper.cos(yaw / 180.0F * 3.1415927F) * 0.16F);
        locY -= 0.10000000149011612D;
        locZ -= (MathHelper.sin(yaw / 180.0F * 3.1415927F) * 0.16F);
        setPosition(locX, locY, locZ);
        height = 0.0F;
        float f = 0.4F;
        motX = (-MathHelper.sin(yaw / 180.0F * 3.1415927F) * MathHelper.cos(pitch / 180.0F * 3.1415927F) * f);
        motZ = (MathHelper.cos(yaw / 180.0F * 3.1415927F) * MathHelper.cos(pitch / 180.0F * 3.1415927F) * f);
        motY = (-MathHelper.sin(pitch / 180.0F * 3.1415927F) * f);
        shoot(motX, motY, motZ, power * 1.5F, 1.0F);
        world.addEntity(this);
    }

    @Override
    public void shoot(double d0, double d1, double d2, float f, float f1) {

        float f2 = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);

        d0 /= f2;
        d1 /= f2;
        d2 /= f2;
        d0 += random.nextGaussian() * 0.007499999832361937D * f1;
        d1 += random.nextGaussian() * 0.007499999832361937D * f1;
        d2 += random.nextGaussian() * 0.007499999832361937D * f1;
        d0 *= f;
        d1 *= f;
        d2 *= f;
        motX = d0;
        motY = d1;
        motZ = d2;
        float f3 = MathHelper.sqrt(d0 * d0 + d2 * d2);

        lastYaw = yaw = (float) (Math.atan2(d0, d2) * 180.0D / 3.1415927410125732D);
        lastPitch = pitch = (float) (Math.atan2(d1, f3) * 180.0D / 3.1415927410125732D);
    }

    @Override
    public EntityType getEntityType() {

        return EntityType.EXPERIENCE_ORB;
    }

    @Override
    public org.bukkit.entity.Entity getEntity() {

        return getBukkitEntity();
    }

    @Override
    public LivingEntity getShooter() {

        return (LivingEntity) shooter.getBukkitEntity();
    }

    @Override
    public String getProjectileName() {

        return name;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void l_() {

        y();
        int elapsedTicks = MinecraftServer.currentTick - this.lastTick;
        this.age += elapsedTicks;
        lastTick = MinecraftServer.currentTick;

        this.lastX = this.locX;
        this.lastY = this.locY;
        this.lastZ = this.locZ;
        this.motY -= 0.03999999910593033D;
        this.Z = i(this.locX, (this.boundingBox.b + this.boundingBox.e) / 2.0D, this.locZ);
        move(this.motX, this.motY, this.motZ);

        float f = 0.98F;

        if (this.onGround) {
            f = 0.5880001F;
            int i = this.world.getTypeId(MathHelper.floor(this.locX), MathHelper.floor(this.boundingBox.b) - 1, MathHelper.floor(this.locZ));

            if (i > 0) {
                f = Block.byId[i].frictionFactor * 0.98F;
            }
        }

        this.motX *= f;
        this.motY *= 0.9800000190734863D;
        this.motZ *= f;
        if (this.onGround) {
            this.motY *= -0.5D;
        }

        if (this.age >= 1000) {
            die();
        }

        Vec3D vec3d = world.getVec3DPool().create(locX, locY, locZ);
        Vec3D vec3d1 = world.getVec3DPool().create(locX + motX, locY + motY, locZ + motZ);
        MovingObjectPosition movingobjectposition = world.a(vec3d, vec3d1);

        vec3d = world.getVec3DPool().create(locX, locY, locZ);
        vec3d1 = world.getVec3DPool().create(locX + motX, locY + motY, locZ + motZ);
        if (movingobjectposition != null)
            vec3d1 = world.getVec3DPool().create(movingobjectposition.pos.c, movingobjectposition.pos.d, movingobjectposition.pos.e);

        if (!world.isStatic) {
            Entity entity = null;
            List list = world.getEntities(this, boundingBox.a(motX, motY, motZ).grow(1.5D, 1.5D, 1.5D));
            double d0 = 0.0D;
            EntityLiving entityliving = shooter;

            for (int j = 0; j < list.size(); ++j) {
                Entity entity1 = (Entity) list.get(j);

                if (entity1.L() && entity1 != entityliving) {
                    float f1 = 0.3F;
                    AxisAlignedBB axisalignedbb = entity1.boundingBox.grow(f1, f1, f1);
                    MovingObjectPosition movingobjectposition1 = axisalignedbb.a(vec3d, vec3d1);

                    if (movingobjectposition1 != null) {
                        double d1 = vec3d.distanceSquared(movingobjectposition1.pos);

                        if (d1 < d0 || d0 == 0.0D) {
                            entity = entity1;
                            d0 = d1;
                        }
                    }
                }
            }

            if (entity != null) movingobjectposition = new MovingObjectPosition(entity);
        }

        if (movingobjectposition != null) if (movingobjectposition.type == EnumMovingObjectType.TILE) {
            CustomProjectileHitEvent event = new CustomProjectileHitEvent(this, world.getWorld().getBlockAt(movingobjectposition.b, movingobjectposition.c, movingobjectposition.d), CraftBlock.notchToBlockFace(movingobjectposition.face));
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                die();
            }
        } else if (movingobjectposition.entity != null && movingobjectposition.entity instanceof EntityLiving) {
            LivingEntity living = (LivingEntity) movingobjectposition.entity.getBukkitEntity();
            CustomProjectileHitEvent event = new CustomProjectileHitEvent(this, living);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                die();
            }
        } else if (this.onGround) {
            CustomProjectileHitEvent event = new CustomProjectileHitEvent(this, getBukkitEntity().getLocation().getBlock().getRelative(BlockFace.DOWN), BlockFace.UP);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                die();
            }
        }
    }

    @Override
    public void b_(EntityHuman entityhuman) {

        if (entityhuman == shooter && age <= 3) return;
        LivingEntity living = entityhuman.getBukkitEntity();
        CustomProjectileHitEvent event = new CustomProjectileHitEvent(this, living);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            die();
        }
    }

    @Override
    public void setInvulnerable(boolean value) {

        try {
            Field f = getClass().getDeclaredField("invulnerable");
            f.setAccessible(true);
            f.set(this, value);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
