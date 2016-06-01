package jfrog.object;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Map;

import com.jogamp.opengl.GL2;

import jfrog.Style;
import jfrog.Style.StylePalette;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

public class Base implements Cloneable {
	int pickingId = 0;
	public Texture tex = null;	
	int [] VBO_Vertices = new int[5];
	public byte  visibleFlag = 0;
	public boolean expandedFlag = false;
	private Style style = null;
	
	public long   id = -1;
	public String name = "unknown";		
	
	public Base			mother;
	ArrayList<Base> daughters = new ArrayList<Base>();
	
	public int ChunkId() {	return 55556; }
	public boolean isCompactible() {return true;}	
	public boolean isCollection() {return false;}

	public double value = 0;
	public boolean hasValue = false;
	private FileOutputStream fileOutputStream;	
	
	
	public String toString() {
		return String.valueOf(ChunkId());
	}
	
	public String toTreeLabel() {
		if(name.equals("unknown"))return toString();
		return name;
	}
		
	public Base(){}
	
	public int readData(URL url, DataInputStream din, Base obj, jfrog.object.Base parent, int bytesToRead, int fileOffset, int level) throws IOException {
		int [] BytesRead = {0};		
		BytesRead[0] += jfrog.object.Factory.read(url,din,this, bytesToRead-BytesRead[0], fileOffset+BytesRead[0], level);
		return BytesRead[0];
	}		
	
	public int read(URL url, DataInputStream din, jfrog.object.Base parent, int bytesToRead, int fileOffset, int level) throws IOException {
		mother = parent;
		if(parent!=null){
			parent.addDaughter(this);
			if(parent.visibleFlag==1)visibleFlag=1;
		}
		return readData(url, din, this, parent, bytesToRead, fileOffset, level);				
	}
		
	public ByteBuffer writeData(){		
		return ByteBuffer.allocate(0);
	}	
	
	public ByteBuffer writeDaughtersData(){		
		if(daughters.size()==0)return ByteBuffer.allocate(0);
				
		ByteBuffer daughterBuffer [] = new ByteBuffer[daughters.size()];
		int daughterBufferSize = 0;
		for(int i=0;i<daughters.size();i++){
			daughterBuffer[i] = (daughters.get(i)).write();
			daughterBufferSize += daughterBuffer[i].position(); 
		}
		
		ByteBuffer toReturn = ByteBuffer.allocate(daughterBufferSize);
		int PreviousChunkId = -1;  int PreviousSizeBlock = 2; 
		int Size = 0;  
		for(int i=0;i<daughters.size();i++){
			boolean Compacting = daughters.get(i).ChunkId() == PreviousChunkId && daughters.get(i).isCompactible();			
			daughterBuffer[i].flip();
			if(Compacting){	daughterBuffer[i].position(6);	
			}else{			PreviousSizeBlock = toReturn.position()+2;		}
			Size += daughterBuffer[i].remaining();
			toReturn.put(daughterBuffer[i]);
			if(Compacting)toReturn.putInt(PreviousSizeBlock, Integer.reverseBytes(Size) );
			PreviousChunkId = daughters.get(i).ChunkId();
		}
		return toReturn;
	}			
		
	public ByteBuffer write(){		
		ByteBuffer data 		= writeData();
		ByteBuffer daughterData = writeDaughtersData();		
		ByteBuffer toReturn = ByteBuffer.allocate(6 + data.position() + daughterData.position());
		Factory.putUShort(toReturn, ChunkId());
		Factory.putInt(toReturn, 0);		
		data.flip();
		toReturn.put(data);
		daughterData.flip();
		toReturn.put(daughterData);
		toReturn.putInt(2,Integer.reverseBytes(toReturn.position()));
		return toReturn;
	}	
		
	public void write(URL url){
		try {
			File file = new File(url.toURI());
		    fileOutputStream = new FileOutputStream(file, false);
			FileChannel wChannel = fileOutputStream.getChannel();
			ByteBuffer data = write();
			data.flip();		    
		    wChannel.write(data);
		    wChannel.close();
		} catch (IOException e) {
			System.out.println("IOException : " + e);			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}		
	}


	public void print(int Level){
		if(Level>3)return;
		for(int i=0;i<Level;i++)System.out.printf("  ");
		System.out.printf("%s (%d)\n", this.toString(),daughters.size());
		for(int i=0;i<daughters.size();i++){
			Base d = daughters.get(i);
			if(d!=null)d.print(Level+1);
		}
	}
	
	public void addDaughter(Base obj){		
		daughters.add(obj);
		obj.mother = this;
	}
	
	public ArrayList<Base> getDaughters(){
		return daughters;
	}		
	
	public Base getDaughter(int index){		
		return daughters.get(index);
	}	
	
	public int getDaughtersSize(){
		return daughters.size();
	}
	
	public float getPosX() {return 0;}
	public float getPosY() {return 0;}
	public float getPosZ() {return 0;}	
	
	public Base findId(long detid){
		if(detid==this.id){
			return this;
		}else{
			for(int i=0;i<daughters.size();i++){
				Base found = daughters.get(i).findId(detid);
				if(found!=null)return found;			
			}		
		}
		return null;
	}
	
	public Base findCollOf(int chunkId, boolean onlyFirstElement){
		if(!isCollection())return null;
				
		if(onlyFirstElement && daughters.size()>0){
			Base daughter = daughters.get(0);
			if(daughter.ChunkId()==chunkId)return this;
		}else{
			for(int i=0;i<daughters.size();i++){
				Base daughter = daughters.get(i);
				if(daughter.ChunkId()==chunkId)return this;
			}			
		}
		
		for(int i=0;i<daughters.size();i++){
			Base found = daughters.get(i).findCollOf(chunkId, onlyFirstElement);
			if(found!=null)return found;
		}		
		return null;
	}	
	
	public void fillMapOfId(Map<Long, ArrayList<Base> > map){
		if(id>0)jfrog.Common.putObjectsWithId(id, map, this);
		for(int i=0;i<daughters.size();i++){
			daughters.get(i).fillMapOfId(map);
		}		
	}
	
	public ArrayList<Base> findAllId(long detid){
		ArrayList<Base> toReturn = new ArrayList<Base>();
		if(detid==this.id)toReturn.add(this);		
		for(int i=0;i<daughters.size();i++){
			toReturn.addAll(daughters.get(i).findAllId(detid));
		}
		return toReturn;
	}
	
	public void draw(GL2 gl){					
	}
	
	public void deepDraw(GL2 gl){
		draw(gl);
		for(int i=0;i<daughters.size();i++){
			if(daughters.get(i).isVisible()>0 && daughters.get(i).style==null)daughters.get(i).deepDraw(gl);						
		}
	}
	
	public void deepDrawWithValue(GL2 gl, StylePalette palette){
		if(hasValue){	palette.colorFromValue(gl, value);		
//		}else{			gl.glColor4f(getStyle().color[0],getStyle().color[1],getStyle().color[2],getStyle().color[3]);
		}else{			gl.glColor4fv(getStyle().color,0);
		}
		draw(gl);				
		for(int i=0;i<daughters.size();i++){
			if(daughters.get(i).isVisible()>0 && daughters.get(i).style==null)daughters.get(i).deepDrawWithValue(gl, palette);						
		}
	}	
	
	public void forceDeepDraw(GL2 gl){
		draw(gl);
		for(int i=0;i<daughters.size();i++){
			if(daughters.get(i).style==null)daughters.get(i).forceDeepDraw(gl);						
		}
	}	
	
	public void deepPickingDraw(GL2 gl){
		gl.glColor3ub((byte)((pickingId&0xFF0000)>>16), (byte)((pickingId&0x00FF00)>>8), (byte)(pickingId&0x0000FF));
		draw(gl);
		for(int i=0;i<daughters.size();i++){
			if(daughters.get(i).isVisible()>0)daughters.get(i).deepPickingDraw(gl);						
		}
	}	
	
	public void fillVisibleObject(ArrayList<Base> objects){
		if(isVisible()==1)objects.add(this);
		if(isVisible()==2){
			for(int i=0;i<daughters.size();i++){
				daughters.get(i).fillVisibleObject(objects);					
			}
		}
	}	
	
	public void fillObjectList(ArrayList<Base> objects){
		if(!isCollection())objects.add(this);
		for(int i=0;i<daughters.size();i++){
			if(daughters.get(i).isVisible()>0 && daughters.get(i).style==null)daughters.get(i).fillObjectList(objects);						
		}
	}		
	
	public byte isVisible(){return visibleFlag;}	
	public void setVisible(int state){						
		if(state<=1){					
			if(visibleFlag==state)return;
			for(int i=0;i<daughters.size();i++){
				daughters.get(i).setVisible(state);						
			}
			visibleFlag = (byte)state;			
			if(mother!=null)mother.setVisible((byte)2);					
		}else{
			boolean [] states= new boolean [] {false, false, false};
			for(int i=0;i<daughters.size();i++){
				states[daughters.get(i).isVisible()] = true;
				if(states[2] || (states[0] && states[1]) )break;							
			}
			
			if(states[2] || (states[0] && states[1]) ){
				visibleFlag=2;				
			}else if(states[0]){
				visibleFlag=0;
			}else if(states[1]){
				visibleFlag=1;
			}else{
				// do nothing;
			}				
			if(mother!=null)mother.setVisible((byte)2);
		}			
	}
	
	public void setStyle(Style style, boolean updateCollection, boolean checkIfBooked){
		if(checkIfBooked && !jfrog.Common.styles.contains(style))jfrog.Common.styles.add(style);
				
		if(style==null || mother==null || mother.getStyle()!=style){
			if(updateCollection){
				if(this.style!=null){					
					this.style.objList.remove(this);
					//If only this object where in the collection, remove it
					if(this.style.objList.size()==0){jfrog.Common.styles.remove(this.style);}	
				}
				if(style!=null)style.objList.add(this);
			}
			this.style = style;			
		}
		
		for(int i=0;i<daughters.size();i++){
			daughters.get(i).setStyle(null, updateCollection, false);
			//if(daughters.get(i).style!=null)daughters.get(i).setStyle(null, updateCollection, false);
		}
	}	
	
/*	public void updateStyleColl(boolean EraseColl){
		if(EraseColl)jfrog.Common.styles.clear();
		if(style!=null){
			if((mother==null || (mother!=null && mother.style!=style)) && !jfrog.Common.styles.contains(style)){
					style.objList.clear();
					jfrog.Common.styles.add(style);
			}
			this.style.objList.add(this);
		}
		
		for(int i=0;i<daughters.size();i++){
			daughters.get(i).updateStyleColl(false);						
		}										
	}	
*/	
	
	public boolean isDaughterOf(int DetId){
		if(mother==null)return false;
		if(mother.id==DetId)return true;
		return mother.isDaughterOf(DetId);
	}
	
	public boolean isDaughterOf(String Name){
		if(mother==null)return false;
		if(mother.name.equals(Name))return true;
		return mother.isDaughterOf(Name);
	}	
	
	public void setValue(double value){setValue(value, null);}
	public void setValue(double value, StylePalette palette){
		Style style = this.getStyle();
		if(style!=null){
			style.hasValue= true;
			style.palette = palette;
			//style.palette.updateRange(value);
		}
		
		this.value = value;
		this.hasValue = true;
		for(int i=0;i<daughters.size();i++){
			daughters.get(i).setValue(value, palette);						
		}
	}		

	protected void SetNormalArray(float [] VerticesArray, float [] NormalsArray, int FirstVertex, int NVertices, int PolygonSideCount){
		float [] tmpNormalsArray = new float [NormalsArray.length];
		
		for(int i=0;i<NVertices/PolygonSideCount;i++){
			int offset = (FirstVertex + i*PolygonSideCount)*3;
			//unsigned int offset = FirstVertex + i*PolygonSideCount*3;
			float NX=0, NY=0, NZ=0;
			if(PolygonSideCount>=3){
				float C1X = VerticesArray[offset+0];
				float C1Y = VerticesArray[offset+1];
				float C1Z = VerticesArray[offset+2];
				float C2X = VerticesArray[offset+3];
				float C2Y = VerticesArray[offset+4];
				float C2Z = VerticesArray[offset+5];
				float C3X = VerticesArray[offset+6];
				float C3Y = VerticesArray[offset+7];
				float C3Z = VerticesArray[offset+8];
				NX = (C1Y-C2Y)*(C2Z-C3Z) - (C1Z-C2Z)*(C2Y-C3Y);
				NY = (C1Z-C2Z)*(C2X-C3X) - (C1X-C2X)*(C2Z-C3Z);
				NZ = (C1X-C2X)*(C2Y-C3Y) - (C1Y-C2Y)*(C2X-C3X);
				double N = Math.sqrt(NX*NX+NY*NY+NZ*NZ);
				NX /= N;NY /= N;NZ /= N;	
			}
			for(int j=0;j<PolygonSideCount;j++){
				tmpNormalsArray[offset+3*j+0]=NX;
				tmpNormalsArray[offset+3*j+1]=NY;
				tmpNormalsArray[offset+3*j+2]=NZ;
				
				NormalsArray[offset+3*j+0]=NX;
				NormalsArray[offset+3*j+1]=NY;
				NormalsArray[offset+3*j+2]=NZ;				
			}
		}
		/*
		for(int i=0;i<NVertices*3;i+=3){
			int N=0;
			NormalsArray[i+0] = 0;
			NormalsArray[i+1] = 0;
			NormalsArray[i+2] = 0;			
			for(int j=0;j<NVertices*3;j+=3){
				if(VerticesArray[i+0] == VerticesArray[j+0] && VerticesArray[i+1] == VerticesArray[j+1] && VerticesArray[i+2] == VerticesArray[j+2]){
					N++;
					NormalsArray[i+0] += tmpNormalsArray[j+0];
					NormalsArray[i+1] += tmpNormalsArray[j+1];
					NormalsArray[i+2] += tmpNormalsArray[j+2];
				}				
			}
			double norm = Math.sqrt(NormalsArray[i+0]*NormalsArray[i+0]+NormalsArray[i+1]*NormalsArray[i+1]+NormalsArray[i+2]*NormalsArray[i+2]);
			if(norm==0)continue;			
			System.out.printf("N = %d\n",N);
			NormalsArray[i+0] /= norm;
			NormalsArray[i+1] /= norm;
			NormalsArray[i+2] /= norm;			
		}*/		 		
	}	
	
	
	protected float []  SetNormalArray(float [] Vertices, short [] Indices, int FirstIndex, int NIndex, int NSides){
		return SetNormalArray(Vertices, Indices, FirstIndex, NIndex, NSides, null);
	}
	
	protected float []  SetNormalArray(float [] Vertices, short [] Indices, int FirstIndex, int NIndex, int NSides, float [] Normals){
		float [] faceNormals = new float [3*NIndex/NSides];
		
		for(int i=0;i<NIndex/NSides;i++){
			int offset = FirstIndex + i*NSides;
			float C1X = Vertices[3*Indices[offset+0]+0];
			float C1Y = Vertices[3*Indices[offset+0]+1];
			float C1Z = Vertices[3*Indices[offset+0]+2];				
			float C2X = Vertices[3*Indices[offset+1]+0];
			float C2Y = Vertices[3*Indices[offset+1]+1];
			float C2Z = Vertices[3*Indices[offset+1]+2];
			float C3X = Vertices[3*Indices[offset+2]+0];
			float C3Y = Vertices[3*Indices[offset+2]+1];
			float C3Z = Vertices[3*Indices[offset+2]+2];
			//faceNormals[i*3+0] = (C1Y-C2Y)*(C2Z-C3Z) - (C1Z-C2Z)*(C2Y-C3Y);
			faceNormals[i*3+0] = (C1Y-C2Y)*(C2Z-C3Z) - (C1Z-C2Z)*(C2Y-C3Y);
			faceNormals[i*3+1] = (C1Z-C2Z)*(C2X-C3X) - (C1X-C2X)*(C2Z-C3Z);
			faceNormals[i*3+2] = (C1X-C2X)*(C2Y-C3Y) - (C1Y-C2Y)*(C2X-C3X);
			double norm = Math.sqrt(faceNormals[i*3+0]*faceNormals[i*3+0]+faceNormals[i*3+1]*faceNormals[i*3+1]+faceNormals[i*3+2]*faceNormals[i*3+2]);
			faceNormals[i*3+0] /= norm;
			faceNormals[i*3+1] /= norm;
			faceNormals[i*3+2] /= norm;	
		}
		
		if(Normals==null)Normals = new float[Vertices.length];							
		//for(int i=0;i<Normals.length;i++){Normals[i] = 0;}	
		for(int i=0;i<NIndex/NSides;i++){
			int offset = FirstIndex + i*NSides;
			for(int c=0;c<NSides;c++){
				Normals[3*Indices[offset+c]+0] += faceNormals[i*3+0]; 
				Normals[3*Indices[offset+c]+1] += faceNormals[i*3+1];
				Normals[3*Indices[offset+c]+2] += faceNormals[i*3+2];
			}						
		}
		
		for(int i=0;i<Normals.length/3;i++){
			double norm = Math.sqrt(Normals[3*i+0]*Normals[3*i+0]+Normals[3*i+1]*Normals[3*i+1]+Normals[3*i+2]*Normals[3*i+2]);
			if(norm==0)continue;
			Normals[3*i+0] /= norm;
			Normals[3*i+1] /= norm;
			Normals[3*i+2] /= norm;					 		
		}				
		return Normals;
	}
	
	protected float []  SetNormalArray(float [] Vertices, int [] Indices, int FirstIndex, int NIndex, int NSides){
		return SetNormalArray(Vertices, Indices, FirstIndex, NIndex, NSides, null);
	}	
	
	protected float []  SetNormalArray(float [] Vertices, int [] Indices, int FirstIndex, int NIndex, int NSides, float [] Normals){
		float [] faceNormals = new float [3*NIndex/NSides];
		
		for(int i=0;i<NIndex/NSides;i++){
			int offset = FirstIndex + i*NSides;
			float C1X = Vertices[3*Indices[offset+0]+0];
			float C1Y = Vertices[3*Indices[offset+0]+1];
			float C1Z = Vertices[3*Indices[offset+0]+2];				
			float C2X = Vertices[3*Indices[offset+1]+0];
			float C2Y = Vertices[3*Indices[offset+1]+1];
			float C2Z = Vertices[3*Indices[offset+1]+2];
			float C3X = Vertices[3*Indices[offset+2]+0];
			float C3Y = Vertices[3*Indices[offset+2]+1];
			float C3Z = Vertices[3*Indices[offset+2]+2];
			//faceNormals[i*3+0] = (C1Y-C2Y)*(C2Z-C3Z) - (C1Z-C2Z)*(C2Y-C3Y);
			faceNormals[i*3+0] = (C1Y-C2Y)*(C2Z-C3Z) - (C1Z-C2Z)*(C2Y-C3Y);
			faceNormals[i*3+1] = (C1Z-C2Z)*(C2X-C3X) - (C1X-C2X)*(C2Z-C3Z);
			faceNormals[i*3+2] = (C1X-C2X)*(C2Y-C3Y) - (C1Y-C2Y)*(C2X-C3X);
			double norm = Math.sqrt(faceNormals[i*3+0]*faceNormals[i*3+0]+faceNormals[i*3+1]*faceNormals[i*3+1]+faceNormals[i*3+2]*faceNormals[i*3+2]);
			faceNormals[i*3+0] /= norm;
			faceNormals[i*3+1] /= norm;
			faceNormals[i*3+2] /= norm;	
		}
		
		if(Normals==null)Normals = new float[Vertices.length];							
		//for(int i=0;i<Normals.length;i++){Normals[i] = 0;}	
		for(int i=0;i<NIndex/NSides;i++){
			int offset = FirstIndex + i*NSides;
			for(int c=0;c<NSides;c++){
				Normals[3*Indices[offset+c]+0] += faceNormals[i*3+0]; 
				Normals[3*Indices[offset+c]+1] += faceNormals[i*3+1];
				Normals[3*Indices[offset+c]+2] += faceNormals[i*3+2];
			}						
		}
		
		for(int i=0;i<Normals.length/3;i++){
			double norm = Math.sqrt(Normals[3*i+0]*Normals[3*i+0]+Normals[3*i+1]*Normals[3*i+1]+Normals[3*i+2]*Normals[3*i+2]);
			if(norm==0)continue;
			Normals[3*i+0] /= norm;
			Normals[3*i+1] /= norm;
			Normals[3*i+2] /= norm;					 		
		}				
		return Normals;
	}	
	
	void TransformVerticesArray(GL2 gl, float [] VerticesArray){
	    float [] m = new float[16];
	    gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, m, 0);

		for(int i=0;i<VerticesArray.length/3;i++){
			int offset = i*3;
			float A = m[0 ]*VerticesArray[offset+0] + m[4 ]*VerticesArray[offset+1] + m[8 ]*VerticesArray[offset+2] + m[12]*1.0f;
			float B = m[1 ]*VerticesArray[offset+0] + m[5 ]*VerticesArray[offset+1] + m[9 ]*VerticesArray[offset+2] + m[13]*1.0f;
			float C = m[2 ]*VerticesArray[offset+0] + m[6 ]*VerticesArray[offset+1] + m[10]*VerticesArray[offset+2] + m[14]*1.0f;
			VerticesArray[offset+0] = A;
			VerticesArray[offset+1] = B;
			VerticesArray[offset+2] = C;
		}
	}	


	public void loadTexture(GL2 gl){
		Style style = getStyle();
		if(tex!=null || style==null || style.texture==null)return;
		if(style.tex!=null){
			this.tex = style.tex;
			return;
		}
		
		String fileToOpen =  style.texture;				
		fileToOpen = fileToOpen.replaceAll("@DetId", String.valueOf(id));
		URL url = jfrog.Common.GetURL(fileToOpen);
		if(url==null){
			System.out.printf("Texture for module %d was not found at URL %s\n", id, fileToOpen);
			
			if(Style.texDefault==null){
			    BufferedImage image = new BufferedImage(256, 256,  BufferedImage.TYPE_INT_RGB);      
			    Graphics2D g2 = image.createGraphics();
			    g2.setColor(Color.WHITE);
			    g2.fillRect(0, 0, 255, 255);
			    g2.setColor(Color.RED);
			    g2.setStroke(new BasicStroke(5));
			    g2.drawLine(0, 0, 255, 255);
			    g2.drawLine(0, 255, 255, 0);
			    g2.dispose();			    
			    Style.texDefault = AWTTextureIO.newTexture(gl.getGLProfile(), image, false);
			}			
			tex = Style.texDefault; 
			return;
		}

		try {	
			BufferedInputStream bis= new BufferedInputStream(url.openStream());
			this.tex = TextureIO.newTexture(bis, false, null);
		} catch (IOException e) {
			e.printStackTrace();
		}			
		
		//Same texture for all objects of that style		
		if(fileToOpen.equals(style.texture))style.tex = this.tex; 								
	}
	
	
/*	
	public void keepOnlyStructure(){
		for(int i=0;i<daughters.size();i++){
			if(daughters.get(i).isCollection()){
				daughters.get(i).keepOnlyStructure();				
			}else{
				if(daughters.get(i).style!=null)daughters.get(i).style.objList.remove(daughters.get(i));
				daughters.remove(i);
				i--;							
			}
		}	
	}
*/	
			
	public Object clone(){		
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean isStyleFromParent(){
		return style==null;
	}
	
	public Style getStyle() {
		if(style!=null)return style;
		if(mother==null)return null;
		return mother.getStyle();		
	}
	
	public void setStyle(Style style) {
		this.style = style;
	}
	
	public void print(){
		if(this.getStyle()!=null){System.out.printf("%s - is coloured\n", toString()); return; }
		
		for(int i=0;i<daughters.size();i++){
			daughters.get(i).print();
		}				
	}
	
	public void fillStyleMap(Map<jfrog.Style, ArrayList<jfrog.object.Base> > map){
		fillStyleMap(map, true);
	}
	protected void fillStyleMap(Map<jfrog.Style, ArrayList<jfrog.object.Base> > map, boolean includeNull){						
		if(style!=null || includeNull){
			ArrayList<jfrog.object.Base> container = map.get(getStyle());
			if(container==null){
				container = new ArrayList<jfrog.object.Base>();
				map.put(getStyle(), container);
			}
			container.add(this);			
		}
		
		if(isCollection()){
			for(int i=0;i<daughters.size();i++){
				daughters.get(i).fillStyleMap(map, false);
			}
		}
		
	}
	public int getPickingId() {
		return pickingId;
	}
	public void setPickingId(int pickingId) {
		this.pickingId = pickingId;
	}
	
	
				
}
