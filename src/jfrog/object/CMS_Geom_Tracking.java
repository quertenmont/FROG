package jfrog.object;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jogamp.opengl.GL2;

import com.jogamp.opengl.util.GLBuffers;


public class CMS_Geom_Tracking extends Base {
	public float Trapezo;
	public float PosX;    public float PosY;    public float PosZ;
	public float WidthX;  public float WidthY;  public float WidthZ;
	public float LengthX; public float LengthY; public float LengthZ;
	public float ThickX;  public float ThickY;  public float ThickZ;	
	
	float [] tmppositionData;
	float [] tmpnormalData;
	int [] CubeIndex;
	
	public int ChunkId() {	return 21100;}
	public boolean isCompactible() {return true;}		
	
	public String toString() {
		return "TrackerMod [detId=" + id + ", PosX=" + PosX + ", PosY="
				+ PosY + ", PosZ=" + PosZ + "]";
	}
	
	public String toTreeLabel() {	
		return "TrackerMod (" + id + ")" + this.daughters.size();
	}			

	public int readData(URL url, DataInputStream din, Base obj, Base parent, int bytesToRead, int fileOffset, int level) throws IOException {		
		int [] BytesRead = {0};
		
		id 	= Factory.getUInt (din, BytesRead);
		Trapezo = Factory.getFloat(din, BytesRead);
		PosX    = Factory.getFloat(din, BytesRead);
		PosY    = Factory.getFloat(din, BytesRead);
		PosZ    = Factory.getFloat(din, BytesRead);			
		WidthX  = Factory.getFloat(din, BytesRead);			
		WidthY  = Factory.getFloat(din, BytesRead);
		WidthZ  = Factory.getFloat(din, BytesRead);
		LengthX = Factory.getFloat(din, BytesRead);
		LengthY = Factory.getFloat(din, BytesRead);
		LengthZ = Factory.getFloat(din, BytesRead);
		ThickX  = Factory.getFloat(din, BytesRead);
		ThickY  = Factory.getFloat(din, BytesRead);
		ThickZ  = Factory.getFloat(din, BytesRead);


		double RightFrame = Math.signum(ThickX * (WidthY*LengthZ - WidthZ*LengthY) + ThickY * (WidthZ*LengthX - WidthX*LengthZ) + ThickZ * (WidthX*LengthY - WidthY*LengthX) );
		if(RightFrame<0)System.out.printf("Projection on X = %f\n", RightFrame );
		
		return BytesRead[0];
	}
	
	public ByteBuffer writeData(){		
		ByteBuffer toReturn = ByteBuffer.allocate(56);		 
		Factory.putUInt (toReturn, id);
		Factory.putFloat(toReturn, Trapezo);
		Factory.putFloat(toReturn, PosX);
		Factory.putFloat(toReturn, PosY);
		Factory.putFloat(toReturn, PosZ);
		Factory.putFloat(toReturn, WidthX);
		Factory.putFloat(toReturn, WidthY);
		Factory.putFloat(toReturn, WidthZ);
		Factory.putFloat(toReturn, LengthX);
		Factory.putFloat(toReturn, LengthY);
		Factory.putFloat(toReturn, LengthZ);
		Factory.putFloat(toReturn, ThickX);
		Factory.putFloat(toReturn, ThickY);
		Factory.putFloat(toReturn, ThickZ);				
		return toReturn;		
	}	
	
	public float getPosX() {return PosX;}
	public float getPosY() {return PosY;}
	public float getPosZ() {return PosZ;}	
			 
	public void drawWF(GL2 gl) {	
		if(VBO_Vertices[0]==0){
			draw(gl);
		}else{
			if(jfrog.Common.support_VBO){
				gl.glBindBuffer( GL2.GL_ELEMENT_ARRAY_BUFFER, VBO_Vertices[4] );				
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, 0 );							
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[1] );	gl.glVertexPointer  ( 3, GL2.GL_FLOAT, 0, 0 );								
				gl.glDrawElements( GL2.GL_LINE_LOOP, 4, GL2.GL_UNSIGNED_INT, 0);												
			}else{			
				gl.glCallList(VBO_Vertices[4]);						
			}			
		}
	}
	
	public void draw(GL2 gl) {		
		//System.out.printf("DrawModule\n");
		
		if(VBO_Vertices[0]==0){
			loadTexture(gl);
			
			float TP  = Trapezo;	
			float C1X = PosX+WidthX*TP+LengthX+ThickX;	float C1Y = PosY+WidthY*TP+LengthY+ThickY;	float C1Z = PosZ+WidthZ*TP+LengthZ+ThickZ;
			float C2X = PosX+WidthX   -LengthX+ThickX;	float C2Y = PosY+WidthY   -LengthY+ThickY;	float C2Z = PosZ+WidthZ   -LengthZ+ThickZ;				 
			float C3X = PosX-WidthX   -LengthX+ThickX;	float C3Y = PosY-WidthY   -LengthY+ThickY;	float C3Z = PosZ-WidthZ   -LengthZ+ThickZ;
			float C4X = PosX-WidthX*TP+LengthX+ThickX;	float C4Y = PosY-WidthY*TP+LengthY+ThickY;	float C4Z = PosZ-WidthZ*TP+LengthZ+ThickZ;
			float C5X = PosX+WidthX*TP+LengthX-ThickX;	float C5Y = PosY+WidthY*TP+LengthY-ThickY;	float C5Z = PosZ+WidthZ*TP+LengthZ-ThickZ;
			float C6X = PosX+WidthX   -LengthX-ThickX;	float C6Y = PosY+WidthY   -LengthY-ThickY;	float C6Z = PosZ+WidthZ   -LengthZ-ThickZ;
			float C7X = PosX-WidthX   -LengthX-ThickX;	float C7Y = PosY-WidthY   -LengthY-ThickY;	float C7Z = PosZ-WidthZ   -LengthZ-ThickZ;
			float C8X = PosX-WidthX*TP+LengthX-ThickX;	float C8Y = PosY-WidthY*TP+LengthY-ThickY;	float C8Z = PosZ-WidthZ*TP+LengthZ-ThickZ;
							    	
			
			float [] positionData   = new float[]{
			C1X, C1Y,	C1Z,	C4X, C4Y,	C4Z,	C3X, C3Y,	C3Z,	C2X, C2Y,	C2Z,
			C5X, C5Y,	C5Z,	C6X, C6Y,	C6Z,	C7X, C7Y,	C7Z,	C8X, C8Y,	C8Z,
			C1X, C1Y,	C1Z,	C2X, C2Y,	C2Z,	C6X, C6Y,	C6Z,	C5X, C5Y,	C5Z,
			C4X, C4Y,	C4Z,	C8X, C8Y,	C8Z,	C7X, C7Y,	C7Z,	C3X, C3Y,	C3Z,								
			C1X, C1Y,	C1Z,	C5X, C5Y,	C5Z,	C8X, C8Y,	C8Z,	C4X, C4Y,	C4Z,		
			C2X, C2Y,	C2Z,	C3X, C3Y,	C3Z,	C7X, C7Y,	C7Z,	C6X, C6Y,	C6Z	};							
			int [] CubeIndex  = {0,1,2,3, 4,5,6,7, 8,9,10,11, 12,13,14,15, 16,17,18,19, 20,21,22,23};
			int [] CubeIndexWF= {0,1,2,3};			
			float [] normalData = SetNormalArray(positionData, CubeIndex, 0, CubeIndex.length, 4);
			
			float [] texData   = new float[]{
			1.0f, 0.0f, 	0.0f, 0.0f,	 	0.0f, 1.0f,		1.0f, 1.0f,
			1.0f, 0.0f, 	1.0f, 1.0f,	 	0.0f, 1.0f,		0.0f, 0.0f,							
			0.0f, 0.0f,		0.0f, 0.0f, 	0.0f, 0.0f,	 	0.0f, 0.0f,
			0.0f, 0.0f,		0.0f, 0.0f, 	0.0f, 0.0f,	 	0.0f, 0.0f,			
			0.0f, 0.0f,		0.0f, 0.0f, 	0.0f, 0.0f,	 	0.0f, 0.0f,		
			0.0f, 0.0f,		0.0f, 0.0f, 	0.0f, 0.0f,	 	0.0f, 0.0f	};				
									
			IntBuffer CubeIndexBuf    = GLBuffers.newDirectIntBuffer(CubeIndex);
			IntBuffer CubeIndexWFBuf    = GLBuffers.newDirectIntBuffer(CubeIndexWF);
			FloatBuffer positionDataBuf = GLBuffers.newDirectFloatBuffer(positionData);			
			FloatBuffer normalDataBuf   = GLBuffers.newDirectFloatBuffer(normalData);
			FloatBuffer texDataBuf 		= GLBuffers.newDirectFloatBuffer(texData);			
									
			if(jfrog.Common.support_VBO){					
				gl.glGenBuffers( 5, VBO_Vertices, 0);								// Get A Valid Name
				gl.glBindBuffer( GL2.GL_ELEMENT_ARRAY_BUFFER, VBO_Vertices[0] );	// Bind The Buffer
				gl.glBufferData( GL2.GL_ELEMENT_ARRAY_BUFFER, CubeIndexBuf.capacity() * 4,CubeIndexBuf, GL2.GL_STATIC_DRAW );				
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[1] );			// Bind The Buffer
				gl.glBufferData( GL2.GL_ARRAY_BUFFER, positionDataBuf.capacity() * 4, positionDataBuf, GL2.GL_STATIC_DRAW );
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[2] );			// Bind The Buffer
				gl.glBufferData( GL2.GL_ARRAY_BUFFER, normalDataBuf.capacity() * 4, normalDataBuf, GL2.GL_STATIC_DRAW );							
				if(tex!=null){
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[3] );			// Bind The Buffer
				gl.glBufferData( GL2.GL_ARRAY_BUFFER, texDataBuf.capacity() * 4, texDataBuf, GL2.GL_STATIC_DRAW );
				}
				gl.glBindBuffer( GL2.GL_ELEMENT_ARRAY_BUFFER, VBO_Vertices[4] );	// Bind The Buffer
				gl.glBufferData( GL2.GL_ELEMENT_ARRAY_BUFFER, CubeIndexWFBuf.capacity() * 4,CubeIndexBuf, GL2.GL_STATIC_DRAW );								
			}else{				
				VBO_Vertices[0] = gl.glGenLists(1);			
				gl.glNewList(VBO_Vertices[0], GL2.GL_COMPILE_AND_EXECUTE);				
				gl.glNormalPointer  (    GL2.GL_FLOAT, 0, normalDataBuf );				
				gl.glVertexPointer  ( 3, GL2.GL_FLOAT, 0, positionDataBuf );			
				if(tex!=null)
				gl.glTexCoordPointer(2,  GL2.GL_FLOAT, 0, texDataBuf);
				gl.glDrawElements   (    GL2.GL_QUADS, 24, GL2.GL_UNSIGNED_INT, CubeIndexBuf);
				gl.glEndList();							
				
				VBO_Vertices[4] = gl.glGenLists(1);			
				gl.glNewList(VBO_Vertices[4], GL2.GL_COMPILE_AND_EXECUTE);				
				gl.glNormalPointer  (    GL2.GL_FLOAT, 0, 0 );				
				gl.glVertexPointer  ( 3, GL2.GL_FLOAT, 0, positionDataBuf );			
				gl.glDrawElements   (    GL2.GL_LINE_LOOP, 4, GL2.GL_UNSIGNED_INT, CubeIndexWFBuf);
				gl.glEndList();								
			}
		}else if(VBO_Vertices[0]>0){
			if(jfrog.Common.support_VBO){
				//System.out.printf("Module List = %b\n", gl.glIsBuffer(VBO_Vertices[1]));
				
				
				if(tex!=null)tex.bind(gl);											
				gl.glBindBuffer( GL2.GL_ELEMENT_ARRAY_BUFFER, VBO_Vertices[0] );
				if(tex!=null){
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[3] );	gl.glTexCoordPointer( 2, GL2.GL_FLOAT, 0, 0 );
				}
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[2] );	gl.glNormalPointer  (    GL2.GL_FLOAT, 0, 0 );							
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[1] );	gl.glVertexPointer  ( 3, GL2.GL_FLOAT, 0, 0 );
								
				gl.glDrawElements( GL2.GL_QUADS, 24, GL2.GL_UNSIGNED_INT, 0);												
			}else{			
				gl.glCallList(VBO_Vertices[0]);						
			}			
		}
					
	}
	
	
}
