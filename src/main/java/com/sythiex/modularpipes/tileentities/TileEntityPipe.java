package com.sythiex.modularpipes.tileentities;

import java.util.List;

import cofh.api.inventory.IInventoryHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityPipe extends TileEntity implements IInventoryHandler
{
	public byte[] connectedSides = new byte[ForgeDirection.VALID_DIRECTIONS.length];
	
	public boolean updateConnections = true;
	public boolean updateClient = true;
	
	public TileEntityPipe()
	{
	}
	
	@Override
	public void updateEntity()
	{
		updateConnections();
		updateClient();
	}
	
	public void shouldUpdateConnections()
	{
		updateConnections = true;
	}
	
	private void updateConnections()
	{
		if(updateConnections)
		{
			updateConnections = false;
			updateClient = true;
			
			for(int side = 0; side < ForgeDirection.VALID_DIRECTIONS.length; side++)
			{
				int x = this.xCoord + ForgeDirection.VALID_DIRECTIONS[side].offsetX;
				int y = this.yCoord + ForgeDirection.VALID_DIRECTIONS[side].offsetY;
				int z = this.zCoord + ForgeDirection.VALID_DIRECTIONS[side].offsetZ;
				TileEntity tile = this.worldObj.getTileEntity(x, y, z);
				
				if(tile instanceof TileEntityPipe || tile instanceof IInventory)
				{
					connectedSides[side] = 1;
				}
				else
				{
					connectedSides[side] = 0;
				}
			}
		}
	}
	
	public boolean isSideConnected(ForgeDirection side)
	{
		if(this.connectedSides[side.ordinal()] == 1)
		{
			return true;
		}
		return false;
	}
	
	private void updateClient()
	{
		if(updateClient)
		{
			updateClient = false;
			
			// Makes the server call getDescriptionPacket for a full data sync
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			markDirty();
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		
		tag.setByteArray("connectedSides", connectedSides);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		
		this.connectedSides = tag.getByteArray("connectedSides");
	}
	
	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound tag = new NBTTagCompound();
		this.writeToNBT(tag);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet)
	{
		NBTTagCompound tag = packet.func_148857_g();
		this.readFromNBT(tag);
	}
	
	@Override
	public ConnectionType canConnectInventory(ForgeDirection from)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ItemStack insertItem(ForgeDirection from, ItemStack item, boolean simulate)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ItemStack extractItem(ForgeDirection from, ItemStack item, boolean simulate)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ItemStack extractItem(ForgeDirection from, int maxExtract, boolean simulate)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<ItemStack> getInventoryContents(ForgeDirection from)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int getSizeInventory(ForgeDirection from)
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public boolean isEmpty(ForgeDirection from)
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean isFull(ForgeDirection from)
	{
		// TODO Auto-generated method stub
		return false;
	}
}
