package com.sythiex.modularpipes.transport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cofh.api.block.IDismantleable;
import com.sythiex.modularpipes.ModularPipes;
import com.sythiex.modularpipes.utils.MatrixTranformations;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockPipe extends Block implements ITileEntityProvider, IDismantleable
{
	public enum Part
	{
		Pipe, Module
	}
	
	public BlockPipe(String name)
	{
		super(Material.iron);
		this.setBlockName(name);
		this.setCreativeTab(ModularPipes.tabMP);
		this.setStepSound(soundTypeMetal);
		this.setBlockTextureName("iron_block");
		this.isBlockContainer = true;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new TileEntityPipe();
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
	{
		super.onNeighborBlockChange(world, x, y, z, block);
		TileEntityPipe tile = (TileEntityPipe) world.getTileEntity(x, y, z);
		if(tile != null)
		{
			tile.shouldUpdateConnections();
		}
	}
	
	@Override
	public boolean canDismantle(EntityPlayer player, World world, int x, int y, int z)
	{
		return true;
	}
	
	@Override
	public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, World world, int x, int y, int z, boolean returnDrops)
	{
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		drops.add(new ItemStack(ModularPipes.modularPipe));
		if(returnDrops)
		{
			for(ItemStack stack : drops)
			{
				player.inventory.addItemStackToInventory(stack);
			}
		}
		else
		{
			if(!player.capabilities.isCreativeMode)
			{
				for(ItemStack item : drops)
				{
					world.spawnEntityInWorld(new EntityItem(world, x, y, z, item));
				}
			}
			
			world.setBlockToAir(x, y, z);
			world.removeTileEntity(x, y, z);
		}
		return drops;
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int metadata)
	{
		super.breakBlock(world, x, y, z, block, metadata);
		world.removeTileEntity(x, y, z);
	}
	
	@Override
	public boolean onBlockEventReceived(World world, int x, int y, int z, int eventID, int eventParam)
	{
		super.onBlockEventReceived(world, x, y, z, eventID, eventParam);
		TileEntity tileentity = world.getTileEntity(x, y, z);
		return tileentity != null ? tileentity.receiveClientEvent(eventID, eventParam) : false;
	}
	
	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB axisAlignedBB, List list,
			Entity entity)
	{
		this.setBlockBounds(0.25F, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F);
		super.addCollisionBoxesToList(world, x, y, z, axisAlignedBB, list, entity);
		
		TileEntityPipe tile = (TileEntityPipe) world.getTileEntity(x, y, z);
		if(tile instanceof TileEntityPipe)
		{
			if(tile.isSideConnected(ForgeDirection.WEST))
			{
				setBlockBounds(0.0F, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F);
				super.addCollisionBoxesToList(world, x, y, z, axisAlignedBB, list, entity);
			}
			
			if(tile.isSideConnected(ForgeDirection.EAST))
			{
				setBlockBounds(0.25F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F);
				super.addCollisionBoxesToList(world, x, y, z, axisAlignedBB, list, entity);
			}
			
			if(tile.isSideConnected(ForgeDirection.DOWN))
			{
				setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.75F, 0.75F);
				super.addCollisionBoxesToList(world, x, y, z, axisAlignedBB, list, entity);
			}
			
			if(tile.isSideConnected(ForgeDirection.UP))
			{
				setBlockBounds(0.25F, 0.25F, 0.25F, 0.75F, 1.0F, 0.75F);
				super.addCollisionBoxesToList(world, x, y, z, axisAlignedBB, list, entity);
			}
			
			if(tile.isSideConnected(ForgeDirection.NORTH))
			{
				setBlockBounds(0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 0.75F);
				super.addCollisionBoxesToList(world, x, y, z, axisAlignedBB, list, entity);
			}
			
			if(tile.isSideConnected(ForgeDirection.SOUTH))
			{
				setBlockBounds(0.25F, 0.25F, 0.25F, 0.75F, 0.75F, 1.0F);
				super.addCollisionBoxesToList(world, x, y, z, axisAlignedBB, list, entity);
			}
		}
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
	{
		RaytraceResult rayTraceResult = doRayTrace(world, x, y, z, Minecraft.getMinecraft().thePlayer);
		
		if(rayTraceResult != null && rayTraceResult.boundingBox != null)
		{
			AxisAlignedBB box = rayTraceResult.boundingBox;
			switch(rayTraceResult.hitPart)
			{
			case Module:
			{
				float scale = 0.0F;
				box = box.expand(scale, scale, scale);
				break;
			}
			case Pipe:
			{
				float scale = 0.0F;
				box = box.expand(scale, scale, scale);
				break;
			}
			}
			return box.getOffsetBoundingBox(x, y, z);
		}
		return super.getSelectedBoundingBoxFromPool(world, x, y, z).expand(-.75F, -.75F, -.75F);
	}
	
	@Override
	public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 origin, Vec3 direction)
	{
		RaytraceResult raytraceResult = doRayTrace(world, x, y, z, origin, direction);
		
		if(raytraceResult == null)
		{
			return null;
		}
		else
		{
			return raytraceResult.movingObjectPosition;
		}
	}
	
	private AxisAlignedBB getBoundingBox(ForgeDirection side)
	{
		float min = 0.25F;
		float max = 0.75F;
		
		if(side == ForgeDirection.UNKNOWN)
		{
			return AxisAlignedBB.getBoundingBox(min, min, min, max, max, max);
		}
		
		float[][] bounds = new float[3][2];
		// X START - END
		bounds[0][0] = min;
		bounds[0][1] = max;
		// Y START - END
		bounds[1][0] = 0;
		bounds[1][1] = min;
		// Z START - END
		bounds[2][0] = min;
		bounds[2][1] = max;
		
		MatrixTranformations.transform(bounds, side);
		return AxisAlignedBB.getBoundingBox(bounds[0][0], bounds[1][0], bounds[2][0], bounds[0][1], bounds[1][1],
				bounds[2][1]);
	}
	
	private void setBlockBounds(AxisAlignedBB bb)
	{
		setBlockBounds((float) bb.minX, (float) bb.minY, (float) bb.minZ, (float) bb.maxX, (float) bb.maxY, (float) bb.maxZ);
	}
	
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}
	
	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}
	
	@Override
	public int getRenderType()
	{
		return -1;
	}
	
	@Override
	public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z)
	{
		return false;
	}
	
	@Override
	public boolean isNormalCube()
	{
		return false;
	}
	
	public static class RaytraceResult
	{
		public final Part hitPart;
		public final MovingObjectPosition movingObjectPosition;
		public final AxisAlignedBB boundingBox;
		public final ForgeDirection sideHit;
		
		RaytraceResult(Part hitPart, MovingObjectPosition movingObjectPosition, AxisAlignedBB boundingBox,
				ForgeDirection side)
		{
			this.hitPart = hitPart;
			this.movingObjectPosition = movingObjectPosition;
			this.boundingBox = boundingBox;
			this.sideHit = side;
		}
		
		@Override
		public String toString()
		{
			return String.format("RayTraceResult: %s, %s", hitPart == null ? "null" : hitPart.name(),
					boundingBox == null ? "null" : boundingBox.toString());
		}
	}
	
	public RaytraceResult doRayTrace(World world, int x, int y, int z, EntityPlayer player)
	{
		double reachDistance = 5;
		
		if(player instanceof EntityPlayerMP)
		{
			reachDistance = ((EntityPlayerMP) player).theItemInWorldManager.getBlockReachDistance();
		}
		
		double eyeHeight = world.isRemote ? player.getEyeHeight() - player.getDefaultEyeHeight() : player.getEyeHeight();
		Vec3 lookVec = player.getLookVec();
		Vec3 origin = Vec3.createVectorHelper(player.posX, player.posY + eyeHeight, player.posZ);
		Vec3 direction = origin.addVector(lookVec.xCoord * reachDistance, lookVec.yCoord * reachDistance,
				lookVec.zCoord * reachDistance);
				
		return doRayTrace(world, x, y, z, origin, direction);
	}
	
	private RaytraceResult doRayTrace(World world, int x, int y, int z, Vec3 origin, Vec3 direction)
	{
		TileEntityPipe tile = (TileEntityPipe) world.getTileEntity(x, y, z);
		if(tile instanceof TileEntityPipe)
		{
			MovingObjectPosition[] hits = new MovingObjectPosition[7];
			AxisAlignedBB[] boxes = new AxisAlignedBB[7];
			ForgeDirection[] sideHit = new ForgeDirection[7];
			Arrays.fill(sideHit, ForgeDirection.UNKNOWN);
			
			for(ForgeDirection side : ForgeDirection.values())
			{
				if(side == ForgeDirection.UNKNOWN || tile.isSideConnected(side))
				{
					AxisAlignedBB bb = getBoundingBox(side);
					setBlockBounds(bb);
					boxes[side.ordinal()] = bb;
					hits[side.ordinal()] = super.collisionRayTrace(world, x, y, z, origin, direction);
					sideHit[side.ordinal()] = side;
				}
			}
			
			// get closest hit
			double minLengthSquared = Double.POSITIVE_INFINITY;
			int minIndex = -1;
			for(int i = 0; i < hits.length; i++)
			{
				MovingObjectPosition hit = hits[i];
				if(hit == null)
				{
					continue;
				}
				
				double lengthSquared = hit.hitVec.squareDistanceTo(origin);
				
				if(lengthSquared < minLengthSquared)
				{
					minLengthSquared = lengthSquared;
					minIndex = i;
				}
			}
			
			setBlockBounds(0, 0, 0, 1, 1, 1);
			
			if(minIndex == -1)
			{
				return null;
			}
			else
			{
				Part hitPart;
				
				if(minIndex < 7)
				{
					hitPart = Part.Pipe;
				}
				else
				{
					hitPart = Part.Module;
				}
				return new RaytraceResult(hitPart, hits[minIndex], boxes[minIndex], sideHit[minIndex]);
			}
		}
		return null;
	}
}
