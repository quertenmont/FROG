package jfrog.object;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

public class BaseColl extends Base {	
	public int ChunkId() {	return 55555; }
	public boolean isCompactible() {return false;}	
	public boolean isCollection() {return true;}	
			
	public BaseColl(){}
			
	public BaseColl(URL url){
		if(url==null)return;		
		name = url.getPath().substring(url.getPath().lastIndexOf("/")+1);
		try {
			DataInputStream din = new DataInputStream(new BufferedInputStream(url.openStream()));
			jfrog.object.Factory.read(url,din,this, 0, 0, 0);
			if (din != null){din.close();}												

			if(daughters.size()-1>=0){
				Base son=daughters.get(daughters.size()-1);
				this.daughters.remove(daughters.size()-1);
				for(int i=0;i<son.getDaughtersSize();i++){addDaughter(son.getDaughter(i));}
				son = null;
			}
		} catch (FileNotFoundException fe) {
			System.out.println("FileNotFoundException : " + fe);			
		} catch (IOException ioe) {
			System.out.println("IOException : " + ioe);			
			ioe.printStackTrace();
		}  	
	}	
	
	public int readData(URL url, DataInputStream din, Base obj, jfrog.object.Base parent, int bytesToRead, int fileOffset, int level) throws IOException {
		int [] BytesRead = {0};		
		BytesRead[0] += jfrog.object.Factory.read(url,din,this, bytesToRead-BytesRead[0], fileOffset+BytesRead[0], level);
		return BytesRead[0];
	}			
		
}
