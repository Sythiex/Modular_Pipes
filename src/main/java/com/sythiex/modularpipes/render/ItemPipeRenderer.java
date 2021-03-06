package com.sythiex.modularpipes.render;

import org.lwjgl.opengl.GL11;

import com.sythiex.modularpipes.ModularPipes;
import com.sythiex.modularpipes.transport.TileEntityPipe;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

public class ItemPipeRenderer implements IItemRenderer
{
	
	private TileEntityPipe tile = new TileEntityPipe();
	
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		switch(type)
		{
		case ENTITY:
			return true;
		case EQUIPPED:
			return true;
		case EQUIPPED_FIRST_PERSON:
			return true;
		case INVENTORY:
			return true;
		default:
			return false;
		}
	}
	
	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
	{
		return true;
	}
	
	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		ModelPipeBase model = new ModelPipeBase();
		
		GL11.glPushMatrix();
		if(type == ItemRenderType.EQUIPPED_FIRST_PERSON)
		{
			GL11.glTranslatef(0.5F, 1.8F, 0.5F);
		}
		else if(type == ItemRenderType.ENTITY)
		{
			GL11.glTranslatef(0.0F, 1.7F, 0.0F);
			GL11.glScalef(1.5F, 1.5F, 1.5F);
		}
		else
		{
			GL11.glTranslatef(0.5F, 1.9F, 0.5F);
			GL11.glScalef(1.5F, 1.5F, 1.5F);
		}
		
		Minecraft.getMinecraft().getTextureManager()
				.bindTexture(new ResourceLocation(ModularPipes.MODID + ":textures/blocks/PipeBase.png"));
				
		GL11.glPushMatrix();
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		
		model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		
		GL11.glPopMatrix();
		GL11.glPopMatrix();
		
	}
}