package com.sythiex.modularpipes.render;

import org.lwjgl.opengl.GL11;

import com.sythiex.modularpipes.ModularPipes;
import com.sythiex.modularpipes.transport.TileEntityPipe;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityPipeRenderer extends TileEntitySpecialRenderer
{
	// The model of your block
	private final ModelPipeBase modelPipeBase;
	private final ModelPipeConnector modelPipeConnector;
	
	public TileEntityPipeRenderer()
	{
		this.modelPipeBase = new ModelPipeBase();
		this.modelPipeConnector = new ModelPipeConnector();
	}
	
	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float scale)
	{
		renderPipeBase(tile, x, y, z, scale);
				
		//renders connectors if pipe is connected
		if(((TileEntityPipe) tile).connectedSides[ForgeDirection.DOWN.ordinal()] == 1)
			renderPipeConnectorDown(tile, x, y, z, scale);
		if(((TileEntityPipe) tile).connectedSides[ForgeDirection.UP.ordinal()] == 1)
			renderPipeConnectorUp(tile, x, y, z, scale);
		if(((TileEntityPipe) tile).connectedSides[ForgeDirection.NORTH.ordinal()] == 1)
			renderPipeConnectorNorth(tile, x, y, z, scale);
		if(((TileEntityPipe) tile).connectedSides[ForgeDirection.SOUTH.ordinal()] == 1)
			renderPipeConnectorSouth(tile, x, y, z, scale);
		if(((TileEntityPipe) tile).connectedSides[ForgeDirection.WEST.ordinal()] == 1)
			renderPipeConnectorWest(tile, x, y, z, scale);
		if(((TileEntityPipe) tile).connectedSides[ForgeDirection.EAST.ordinal()] == 1)
			renderPipeConnectorEast(tile, x, y, z, scale);
	}
	
	private void renderPipeBase(TileEntity tile, double x, double y, double z, float scale)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y - 0.5F, (float) z + 0.5F);
		ResourceLocation texture = (new ResourceLocation(ModularPipes.MODID + ":textures/blocks/PipeBase.png"));
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		GL11.glPushMatrix();
		this.modelPipeBase.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}
	
	private void renderPipeConnectorDown(TileEntity tile, double x, double y, double z, float scale)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 1.5F);
		ResourceLocation texture = (new ResourceLocation(ModularPipes.MODID + ":textures/blocks/PipeConnector.png"));
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		GL11.glPushMatrix();
		GL11.glRotatef(-90F, 1F, 0F, 0F);
		this.modelPipeConnector.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}
	
	private void renderPipeConnectorUp(TileEntity tile, double x, double y, double z, float scale)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z - 0.5F);
		ResourceLocation texture = (new ResourceLocation(ModularPipes.MODID + ":textures/blocks/PipeConnector.png"));
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		GL11.glPushMatrix();
		GL11.glRotatef(90F, 1F, 0F, 0F);
		this.modelPipeConnector.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}
	
	private void renderPipeConnectorEast(TileEntity tile, double x, double y, double z, float scale)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y - 0.5F, (float) z + 0.5F);
		ResourceLocation texture = (new ResourceLocation(ModularPipes.MODID + ":textures/blocks/PipeConnector.png"));
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		GL11.glPushMatrix();
		GL11.glRotatef(-90F, 0F, 1F, 0F);
		this.modelPipeConnector.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}
	
	private void renderPipeConnectorWest(TileEntity tile, double x, double y, double z, float scale)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y - 0.5F, (float) z + 0.5F);
		ResourceLocation texture = (new ResourceLocation(ModularPipes.MODID + ":textures/blocks/PipeConnector.png"));
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		GL11.glPushMatrix();
		GL11.glRotatef(90F, 0F, 1F, 0F);
		this.modelPipeConnector.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}
	
	private void renderPipeConnectorNorth(TileEntity tile, double x, double y, double z, float scale)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y - 0.5F, (float) z + 0.5F);
		ResourceLocation texture = (new ResourceLocation(ModularPipes.MODID + ":textures/blocks/PipeConnector.png"));
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		GL11.glPushMatrix();
		this.modelPipeConnector.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}
	
	private void renderPipeConnectorSouth(TileEntity tile, double x, double y, double z, float scale)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y - 0.5F, (float) z + 0.5F);
		ResourceLocation texture = (new ResourceLocation(ModularPipes.MODID + ":textures/blocks/PipeConnector.png"));
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		GL11.glPushMatrix();
		GL11.glRotatef(180F, 0F, 1F, 0F);
		this.modelPipeConnector.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}
	
	/*
	 * // The PushMatrix tells the renderer to "start" doing something.
	 * GL11.glPushMatrix();
	 * // This is setting the initial location.
	 * GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
	 * // This is the texture of your block. It's pathed to be the same place as your other blocks here.
	 * ResourceLocation textureBase = (new ResourceLocation(ModularPipes.MODID + ":textures/blocks/PipeBase.png"));
	 * //ResourceLocation textureConnector = (new ResourceLocation(ModularPipes.MODID +
	 * ":textures/blocks/PipeConnector.png"));
	 * 
	 * // binding the textures
	 * Minecraft.getMinecraft().renderEngine.bindTexture(textureBase);
	 * //Minecraft.getMinecraft().renderEngine.bindTexture(textureConnector);
	 * 
	 * // This rotation part is very important! Without it, your model will render upside-down! And for some reason you
	 * DO
	 * // need PushMatrix again!
	 * GL11.glPushMatrix();
	 * GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
	 * // A reference to your Model file. Again, very important.
	 * this.modelPipeBase.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
	 * //this.modelPipeConnector.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
	 * // Tell it to stop rendering for both the PushMatrix's
	 * GL11.glPopMatrix();
	 * GL11.glPopMatrix();
	 */
	
	// Set the lighting stuff, so it changes it's brightness properly.
	/*
	 * private void adjustLightFixture(World world, int i, int j, int k, Block block)
	 * {
	 * Tessellator tess = Tessellator.instance;
	 * // float brightness = block.getBlockBrightness(world, i, j, k);
	 * // As of MC 1.7+ block.getBlockBrightness() has become block.getLightValue():
	 * float brightness = block.getLightValue(world, i, j, k);
	 * int skyLight = world.getLightBrightnessForSkyBlocks(i, j, k, 0);
	 * int modulousModifier = skyLight % 65536;
	 * int divModifier = skyLight / 65536;
	 * tess.setColorOpaque_F(brightness, brightness, brightness);
	 * OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) modulousModifier, divModifier);
	 * }
	 */
	
	/*
	 * private void adjustRotatePivotViaMeta(World world, int x, int y, int z)
	 * {
	 * int meta = world.getBlockMetadata(x, y, z);
	 * GL11.glPushMatrix();
	 * GL11.glRotatef(meta * (-90), 0.0F, 0.0F, 1.0F);
	 * GL11.glPopMatrix();
	 * }
	 */
}