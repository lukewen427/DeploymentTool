package uk.ac.ncl.cs.esc.read;

import java.util.Iterator;
import java.util.Set;

public class BlockSet {
	Set<Block> blocks;
	public BlockSet(Set<Block> blocks){
		this.blocks=blocks;
	}
	public Block getBlock(String blockId){
		Block theblock=null;
		Iterator<Block> getblock=blocks.iterator();
		while(getblock.hasNext()){
		Block	block=getblock.next();
			String theblockid=block.getBlockId();
			if(theblockid.equals(blockId)){
				theblock=block;
			}
		}
		
		return theblock;
	}
}
