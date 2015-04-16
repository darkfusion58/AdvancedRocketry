package zmaster587.advancedRocketry.util;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class StorageChunk {
	Block blocks[][][];
	int metas[][][];
	int sizeX, sizeY, sizeZ;

	ArrayList<NBTTagCompound> tile;

	private StorageChunk(int xSize, int ySize, int zSize) {
		blocks = new Block[xSize][ySize][zSize];
		metas = new int[xSize][ySize][zSize];

		sizeX = xSize;
		sizeY = ySize;
		sizeZ = zSize;

		tile = new ArrayList<NBTTagCompound>();
	}

	public static StorageChunk copyWorldBB(World world, AxisAlignedBB bb) {
		StorageChunk ret = new StorageChunk((int)(bb.maxX - bb.minX + 1), (int)(bb.maxY - bb.minY + 1), (int)(bb.maxZ - bb.minZ + 1));

		for(int x = (int)bb.minX; x <= bb.maxX; x++) {
			for(int z = (int)bb.minZ; z <= bb.maxZ; z++) {
				for(int y = (int)bb.minY; y<= bb.maxY; y++) {


					ret.blocks[x - (int)bb.minX][y - (int)bb.minY][z - (int)bb.minZ] = world.getBlock(x, y, z);
					ret.metas[x - (int)bb.minX][y - (int)bb.minY][z - (int)bb.minZ] = world.getBlockMetadata(x, y, z);

					TileEntity entity = world.getTileEntity(x, y, z);
					if(entity != null) {
						NBTTagCompound nbt = new NBTTagCompound();
						entity.writeToNBT(nbt);

						//Transform tileEntity coords
						nbt.setInteger("x",nbt.getInteger("x") - (int)bb.minX);
						nbt.setInteger("y",nbt.getInteger("y") - (int)bb.minY);
						nbt.setInteger("z",nbt.getInteger("z") - (int)bb.minZ);

						ret.tile.add(nbt);
					}
				}
			}
		}

		return ret;
	}

	public void pasteInWorld(World world, int xCoord, int yCoord ,int zCoord) {

		for(int x = 0; x < sizeX; x++) {
			for(int z = 0; z < sizeZ; z++) {
				for(int y = 0; y< sizeY; y++) {

					if(blocks[x][y][z] != null)
						world.setBlock(xCoord + x, yCoord + y, zCoord + z, blocks[x][y][z], metas[x][y][z], 3);
				}
			}
		}
		
		for(NBTTagCompound nbt : tile) {
			
			int tmpX = nbt.getInteger("x") + xCoord;
			int tmpY = nbt.getInteger("y") + yCoord;
			int tmpZ = nbt.getInteger("z") + zCoord;
			
			nbt.setInteger("x",tmpX);
			nbt.setInteger("y",tmpY);
			nbt.setInteger("z",tmpZ);
			
			//TileEntity entity = TileEntity.createAndLoadEntity(nbt);
			
			//world.removeTileEntity(xCoord, yCoord, zCoord);
			//world.addTileEntity(entity);
			TileEntity entity = world.getTileEntity(tmpX, tmpY, tmpZ);
			
			entity.readFromNBT(nbt);
		}
	}
}
