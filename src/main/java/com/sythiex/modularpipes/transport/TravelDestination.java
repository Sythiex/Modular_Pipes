package com.sythiex.modularpipes.transport;

import java.util.ArrayList;

import com.cofh.lib.util.position.BlockPosition;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class TravelDestination
{
	public BlockPosition startingPosition;
	public BlockPosition endingPosition;
	public ArrayList<ForgeDirection> travelInstructions = new ArrayList();
	
	public TravelDestination(BlockPosition startingPos, BlockPosition endPos, ArrayList<ForgeDirection> instructions)
	{
		this.startingPosition = startingPos;
		this.endingPosition = endPos;
		this.travelInstructions = instructions;
	}
	
	public TravelDestination(NBTTagCompound tag)
	{
		System.out.println("Creating TravelDestination");
		this.startingPosition = new BlockPosition(tag.getCompoundTag("start"));
		this.endingPosition = new BlockPosition(tag.getCompoundTag("end"));
		
		byte instruc[] = tag.getByteArray("instructions");
		System.out.println("instruc[]: " + instruc.toString());
		
		if(instruc != null)
		{
			ArrayList<ForgeDirection> tempList = new ArrayList();
			System.out.println("instruc length is " + instruc.length);
			for(int i = 0; i < instruc.length; i++)
			{
				System.out.println("i is " + i);
				tempList.add(ForgeDirection.VALID_DIRECTIONS[instruc[i]]);
			}
			this.travelInstructions.addAll(tempList);
		}
	}
	
	public void writeToNBT(NBTTagCompound tag)
	{
		NBTTagCompound startTag = new NBTTagCompound();
		NBTTagCompound endTag = new NBTTagCompound();
		NBTTagCompound instructionTag = new NBTTagCompound();
		
		if(travelInstructions.size() > 0)
		{
			byte instructions[] = new byte[travelInstructions.size()];
			for(int i = 0; i < travelInstructions.size(); i++)
			{
				instructions[i] = (byte) travelInstructions.get(i).ordinal();
			}
			tag.setByteArray("instructions", instructions);
		}
		else
		{
			tag.setByteArray("instructions", null);
		}
		
		this.startingPosition.writeToNBT(startTag);
		this.endingPosition.writeToNBT(endTag);
		
		tag.setTag("start", startTag);
		tag.setTag("end", endTag);
	}
	
	@Override
	public String toString()
	{
		return String.format("starting position: %s, ending position: %s, instructions: %s", startingPosition,
				endingPosition, travelInstructions);
	}
}