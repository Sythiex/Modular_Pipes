package com.sythiex.modularpipes.network;

import com.sythiex.modularpipes.ModularPipes;
import com.sythiex.modularpipes.render.ItemPipeRenderer;
import com.sythiex.modularpipes.render.TileEntityPipeRenderer;
import com.sythiex.modularpipes.tileentities.TileEntityPipe;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxyMP extends CommonProxyMP
{
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		super.init(event);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPipe.class, new TileEntityPipeRenderer());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModularPipes.modularPipe), new ItemPipeRenderer());
	}
}
