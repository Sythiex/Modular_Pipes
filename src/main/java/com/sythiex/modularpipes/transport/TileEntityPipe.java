package com.sythiex.modularpipes.transport;

import java.util.ArrayList;
import java.util.List;

import com.cofh.lib.util.helpers.InventoryHelper;
import com.cofh.lib.util.position.BlockPosition;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityPipe extends TileEntity
{
	public byte[] connectedSides = new byte[ForgeDirection.VALID_DIRECTIONS.length];
	public ArrayList<TravelingItem> travelingItems = new ArrayList<TravelingItem>();
	public BlockPosition position;
	public boolean updateConnections = true;
	public boolean updateClient = true;
	public boolean updatePosition = true;
	
	public TileEntityPipe()
	{
	}
	
	@Override
	public void updateEntity()
	{
		if(!this.worldObj.isRemote && this.worldObj.getWorldTime() % 20 == 0)
		{
			if(updatePosition)
			{
				position = new BlockPosition(this);
			}
			
			updateTravelingItems();
			updateConnections();
			
			// look to extract item
			for(int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i++)
			{
				if(this.isSideConnected(ForgeDirection.VALID_DIRECTIONS[i]))
				{
					System.out.println("current block position: " + this.position.toString());
					
					int x = position.x + ForgeDirection.VALID_DIRECTIONS[i].offsetX;
					int y = position.y + ForgeDirection.VALID_DIRECTIONS[i].offsetY;
					int z = position.z + ForgeDirection.VALID_DIRECTIONS[i].offsetZ;
					BlockPosition neighbor = new BlockPosition(x, y, z);
					
					System.out.println("neighbor block position: " + neighbor.toString());
					System.out.println("starting extraction to the " + ForgeDirection.VALID_DIRECTIONS[i]);
					
					TravelingItem item = extractItem(neighbor, ForgeDirection.VALID_DIRECTIONS[i].getOpposite());
					System.out.println("extracted item is " + item);
					if(item != null)
					{
						System.out.println("adding item to travelingItems");
						travelingItems.add(item);
					}
				}
			}
			
			NBTTagCompound tag = new NBTTagCompound();
			
			updateClient();
			markDirty();
		}
	}
	
	private void updateTravelingItems()
	{
		if(!travelingItems.isEmpty() && travelingItems != null)
		{
			for(int i = 0; i < travelingItems.size(); i++)
			{
				System.out.println("updating traveling item " + travelingItems.get(i).toString());
				travelingItems.get(i).travelTime -= 1;
				if(travelingItems.get(i).travelTime <= 0)
				{
					if(transferItem(travelingItems.get(i)))
					{
						System.out.println("item transfered");
						travelingItems.remove(i);
					}
					else
					{
						System.out.println("trying to insert item...");
						ItemStack stack = insertItem(travelingItems.get(i));
						if(stack == null)
						{
							System.out.println("entire stack inserted!");
							travelingItems.remove(i);
						}
						else
						{
							System.out.println("entire stack NOT accepted; remaining: " + stack.toString());
							dropItemStack(stack, this.position);
							travelingItems.remove(i);
						}
					}
				}
			}
		}
	}
	
	/**
	 * @param neighborPos
	 * @param side is relative to IInventory, NOT pipe
	 * @return TravelingItem
	 */
	private TravelingItem extractItem(BlockPosition neighborPos, ForgeDirection side)
	{
		System.out.println("starting extraction at " + neighborPos.toString());
		TileEntity tile = neighborPos.getTileEntity(this.worldObj);
		if(tile instanceof IInventory)
		{
			IInventory invTile = (IInventory) tile;
			
			System.out.println("neighbor is IInventory");
			for(int slot = 0; slot < invTile.getSizeInventory(); slot++)
			{
				ItemStack itemStack = copyItemStackFromSlot(invTile, side.ordinal(), slot);
				if(itemStack != null)
				{
					System.out.println("attemping to remove item");
					TravelingItem item = createTravelingItem(itemStack, side.getOpposite());
					System.out.println("traveling item created: " + item);
					return item;
				}
			}
			System.out.println("inventory is empty, returning null");
			return null;
		}
		System.out.println("neighbor is not IInventory, returning null");
		return null;
	}
	
	private ItemStack insertItem(TravelingItem item)
	{
		ForgeDirection side = item.destination.travelInstructions.get(0);
		IInventory tile = (IInventory) BlockPosition.getAdjacentTileEntity(this, side);
		System.out.println("inserting " + item.itemStack.toString() + " at " + tile.toString());
		if(tile instanceof IInventory)
		{
			return InventoryHelper.insertItemStackIntoInventory(tile, item.itemStack, side.ordinal());
		}
		else
		{
			System.out.println("ERROR: insert failed at " + tile.toString());
			return item.itemStack;
		}
	}
	
	private void dropItemStack(ItemStack itemStack, BlockPosition position)
	{
		this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, position.x, position.y, position.z, itemStack));
	}
	
	/**
	 * @param itemStack
	 * @param position
	 * @param sideFrom
	 * @param distance
	 * @param path
	 * @return
	 */
	private DistancePositionPath findNearestExit(ItemStack itemStack, BlockPosition position, ForgeDirection sideFrom,
			int distance, ArrayList<ForgeDirection> path)
	{
		System.out.println("starting to find nearest exit...");
		TileEntity currentTile = position.getTileEntity(this.worldObj);
		System.out.println("current tile: " + currentTile.toString());
		if(currentTile instanceof TileEntityPipe)
		{
			System.out.println("current tile is pipe");
			distance += 1;
			System.out.println("distance is now " + distance);
			ArrayList<DistancePositionPath> returns = new ArrayList<DistancePositionPath>();
			
			for(int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i++)
			{
				if(((TileEntityPipe) currentTile).isSideConnected(ForgeDirection.VALID_DIRECTIONS[i])
						&& ForgeDirection.VALID_DIRECTIONS[i] != sideFrom)
				{
					System.out.println("checking if " + ForgeDirection.VALID_DIRECTIONS[i].toString()
							+ " neighbor is IInventory or pipe");
					TileEntity neighborTile = position.getAdjacentTileEntity(currentTile,
							ForgeDirection.VALID_DIRECTIONS[i]);
					if(neighborTile instanceof IInventory)
					{
						System.out.println("neighbor is IInventory!");
						System.out.println("simulating stack insertion...");
						ItemStack testStack = InventoryHelper.simulateInsertItemStackIntoInventory((IInventory) neighborTile,
								itemStack, ForgeDirection.VALID_DIRECTIONS[i].ordinal());
								
						System.out.println("testStack is " + testStack);
						
						position.x += ForgeDirection.VALID_DIRECTIONS[i].offsetX;
						position.y += ForgeDirection.VALID_DIRECTIONS[i].offsetY;
						position.z += ForgeDirection.VALID_DIRECTIONS[i].offsetZ;
						path.add(ForgeDirection.VALID_DIRECTIONS[i]);
						
						System.out.println("testing if whole stack was accepted...");
						if(testStack != itemStack)
						{
							System.out.println("whole stack accepted!");
							System.out
									.println("DPP is " + new DistancePositionPath(distance + 1, position, path).toString());
							return new DistancePositionPath(distance + 1, position, path);
						}
					}
					else if(neighborTile instanceof TileEntityPipe)
					{
						System.out.println("neighbor is pipe, moving along...");
						
						position.x += ForgeDirection.VALID_DIRECTIONS[i].offsetX;
						position.y += ForgeDirection.VALID_DIRECTIONS[i].offsetY;
						position.z += ForgeDirection.VALID_DIRECTIONS[i].offsetZ;
						path.add(ForgeDirection.VALID_DIRECTIONS[i]);
						
						returns.add(findNearestExit(itemStack, position, ForgeDirection.VALID_DIRECTIONS[i].getOpposite(),
								distance, path));
					}
				}
			}
			
			if(!returns.isEmpty() && returns != null)
			{
				System.out.println("tile " + currentTile.toString() + " has returns: " + returns.toString());
				System.out.println("finding shortest distance in returns...");
				int shortestDistance = 1000;
				int index = -1;
				for(int i = 0; i < returns.size(); i++)
				{
					if(returns.get(i).distance < shortestDistance)
					{
						shortestDistance = returns.get(i).distance;
						System.out.println("shortest distance is now " + shortestDistance);
						index = i;
					}
				}
				System.out.println("returning: " + returns.get(index).toString());
				return returns.get(index);
			}
			
		}
		System.out.println("returning null");
		return null;
	}
	
	private class DistancePositionPath
	{
		public int distance;
		public BlockPosition position;
		public ArrayList<ForgeDirection> path;
		
		public DistancePositionPath(int distance, BlockPosition position, ArrayList<ForgeDirection> path)
		{
			this.distance = distance;
			this.position = position;
			this.path = path;
		}
	}
	
	/**
	 * transfers items between pipes and inventories
	 * 
	 * @param travelingItem
	 * @return if transfer was successful
	 */
	private boolean transferItem(TravelingItem item)
	{
		int x = this.xCoord;
		int y = this.yCoord;
		int z = this.zCoord;
		int neighborX = item.destination.travelInstructions.get(0).offsetX;
		int neighborY = item.destination.travelInstructions.get(0).offsetY;
		int neighborZ = item.destination.travelInstructions.get(0).offsetZ;
		TileEntity neighborTile = this.worldObj.getTileEntity(neighborX, neighborY, neighborZ);
		System.out.println(String.format("begining item transfer between %s and %s", this.position.toString(),
				BlockPosition.getAdjacentTileEntity(this, item.destination.travelInstructions.get(0))));
				
		if(neighborTile instanceof TileEntityPipe)
		{
			((TileEntityPipe) neighborTile).passItem(item);
			((TileEntityPipe) neighborTile).updateClient = true;
			travelingItems.remove(item);
			updateClient = true;
			return true;
		}
		return false;
	}
	
	/**
	 * adds a travelingItem to this pipe
	 * 
	 * @param item
	 */
	public void passItem(TravelingItem item)
	{
		item.travelTime = 10;
		item.destination.travelInstructions.remove(0);
		travelingItems.add(item);
	}
	
	/**
	 * 
	 * @param itemStack
	 * @param side (relative to pipe)
	 * @return TravelingItem
	 */
	private TravelingItem createTravelingItem(ItemStack itemStack, ForgeDirection side)
	{
		DistancePositionPath dpp = findNearestExit(itemStack, this.position, side, 0, new ArrayList<ForgeDirection>());
		if(dpp != null)
		{
			TravelDestination td = new TravelDestination(this.position, dpp.position, dpp.path);
			return new TravelingItem(itemStack, td);
		}
		return null;
	}
	
	public static ItemStack copyItemStackFromSlot(IInventory inventory, int side, int slot)
	{
		if(inventory == null)
		{
			return null;
		}
		ItemStack retStack = null;
		
		if(inventory instanceof ISidedInventory)
		{
			ISidedInventory sidedInv = (ISidedInventory) inventory;
			int slots[] = sidedInv.getAccessibleSlotsFromSide(side);
			if(sidedInv.getStackInSlot(slot) != null && sidedInv.canExtractItem(slot, sidedInv.getStackInSlot(slot), side))
			{
				retStack = sidedInv.getStackInSlot(slot).copy();
				// sidedInv.setInventorySlotContents(slot, null);
			}
		}
		else
		{
			if(inventory.getStackInSlot(slot) != null)
			{
				retStack = inventory.getStackInSlot(slot).copy();
				// inventory.setInventorySlotContents(slot, null);
			}
		}
		if(retStack != null)
		{
			inventory.markDirty();
		}
		return retStack;
	}
	
	public void shouldUpdateConnections()
	{
		updateConnections = true;
		updateClient = true;
	}
	
	/**
	 * updates connected sides
	 */
	private void updateConnections()
	{
		if(updateConnections)
		{
			updateConnections = false;
			
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
	
	/**
	 * checks if a side is connected
	 * 
	 * @param side
	 * @return true if side is connected
	 */
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
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		System.out.println("starting write to NBT");
		super.writeToNBT(tag);
		
		tag.setByteArray("connectedSides", connectedSides);
		// saves the size of the list for reading the tags in readFromNBT()
		if(!this.travelingItems.isEmpty() && this.travelingItems != null)
		{
			tag.setInteger("numberOfItems", this.travelingItems.size());
			System.out.println("tag numberOfItems set to " + this.travelingItems.size());
		}
		else
		{
			tag.setInteger("numberOfItems", 0);
			System.out.println("tag numberOfItems set to 0");
		}
		
		// adds all TravelingItems to a NBTTagList
		NBTTagList tagList = new NBTTagList();
		System.out.println("writing travelingItems to NBT");
		System.out.println("travelingItems size is " + travelingItems.size());
		if(this.travelingItems.size() > 0)
		{
			for(int i = 0; i < this.travelingItems.size(); i++)
			{
				System.out.println("i is " + i);
				NBTTagCompound iTag = new NBTTagCompound();
				this.travelingItems.get(i).writeToNBT(iTag);
				tagList.appendTag(iTag);
			}
		}
		tag.setTag("travelingItems", tagList);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		System.out.println("starting read from NBT");
		super.readFromNBT(tag);
		
		this.connectedSides = tag.getByteArray("connectedSides");
		
		NBTTagList tagList = tag.getTagList("travelingItems", 10);
		int numberOfItems = tag.getInteger("numberOfItems");
		System.out.println("tag numberOfItems is " + numberOfItems);
		System.out.println("numberOfItems > 0 returns " + (numberOfItems > 0));
		if(numberOfItems > 0)
		{
			for(int i = 0; i < numberOfItems; i++)
			{
				System.out.println("loop for item " + i);
				NBTTagCompound iTag = tagList.getCompoundTagAt(i);
				TravelingItem travelingItem = new TravelingItem(iTag);
				this.travelingItems.add(travelingItem);
			}
		}
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
}
