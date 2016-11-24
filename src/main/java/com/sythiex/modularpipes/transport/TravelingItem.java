package com.sythiex.modularpipes.transport;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;

public class TravelingItem
{
	public ItemStack itemStack;
	public TravelDestination destination;
	public int travelTime;
	
	public TravelingItem(ItemStack itemStack, TravelDestination destination)
	{
		this.itemStack = itemStack;
		this.destination = destination;
		this.travelTime = 10;
	}
	
	public TravelingItem(NBTTagCompound tag)
	{
		NBTTagCompound itemStackTag = tag.getCompoundTag("itemStack");
		NBTTagCompound destTag = tag.getCompoundTag("destination");
		NBTTagCompound timeTag = tag.getCompoundTag("time");
		
		System.out.println("itemStackTag: " + itemStackTag.toString());
		
		this.itemStack = ItemStack.loadItemStackFromNBT(itemStackTag);
		this.destination = new TravelDestination(destTag);
		this.travelTime = timeTag.getInteger("time");
	}
	
	/**
	 * writes the TravelingItem to a NBTTagList
	 * 
	 * @return tag
	 */
	public void writeToNBT(NBTTagCompound tag)
	{
		NBTTagCompound itemStackTag = new NBTTagCompound();
		NBTTagCompound timeTag = new NBTTagCompound();
		NBTTagCompound destTag = new NBTTagCompound();
		
		if(this != null)
		{
			this.itemStack.writeToNBT(itemStackTag);
			this.destination.writeToNBT(destTag);
			timeTag.setInteger("time", this.travelTime);
			
			tag.setTag("itemStack", itemStackTag);
			tag.setTag("destination", destTag);
			tag.setTag("time", timeTag);
		}
	}
	
	@Override
	public String toString()
	{
		return itemStack.toString() + ", travelTime: " + travelTime + " ," + destination.toString();
	}
}
