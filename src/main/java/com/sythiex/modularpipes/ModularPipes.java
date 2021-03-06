package com.sythiex.modularpipes;

import com.sythiex.modularpipes.network.CommonProxyMP;
import com.sythiex.modularpipes.transport.BlockPipe;
import com.sythiex.modularpipes.transport.TileEntityPipe;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.config.Configuration;

@Mod(modid = ModularPipes.MODID, version = ModularPipes.VERSION)

public class ModularPipes
{
	@SidedProxy(clientSide = "com.sythiex.modularpipes.network.ClientProxyMP",
			serverSide = "com.sythiex.modularpipes.network.ServerProxyVS")
	public static CommonProxyMP proxy;
	
	public static final String MODID = "modularpipes";
	public static final String VERSION = "0.1.0";
	
	public static CreativeTabs tabMP = new CreativeTabMP("modularpipes");
	
	public static Block modularPipe;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		config.save();
		
		registerBlocks();
		
		GameRegistry.registerTileEntity(TileEntityPipe.class, "tileEntityPipe");
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.init(event);
	}
	
	private void registerBlocks()
	{
		modularPipe = new BlockPipe("modularPipe");
		GameRegistry.registerBlock(modularPipe, "modularPipe");
	}
}
