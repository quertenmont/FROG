package jfrog.object;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jogamp.opengl.GL2;

import com.jogamp.opengl.util.GLBuffers;

public class CMS_Geom_Calo extends Base{
	float ProjFactor;
	float PosX;    float PosY;    float PosZ;					
	float WX;	   float WY;      float WZ;
	float HX;	   float HY;      float HZ;
	
	public int ChunkId() {	return 11999;	}
	public boolean isCompactible() {return true;}	
	
	public String toString() {
		return "CaloMod [detId=" + id + ", PosX=" + PosX + ", PosY=" + PosY
				+ ", PosZ=" + PosZ + "]";
	}

	public String toTreeLabel() {	
		return "CaloMod (" + id + ")";
	}		

	public int readData(URL url, DataInputStream din, Base obj, Base parent, int bytesToRead, int fileOffset, int level) throws IOException {
		int [] BytesRead = {0};

		id 			= Factory.getUInt (din, BytesRead);			
		ProjFactor	= Factory.getFloat(din, BytesRead);
		PosX    	= Factory.getFloat(din, BytesRead);
		PosY    	= Factory.getFloat(din, BytesRead);
		PosZ    	= Factory.getFloat(din, BytesRead);			
		WX  		= Factory.getFloat(din, BytesRead);			
		WY  		= Factory.getFloat(din, BytesRead);
		WZ	  		= Factory.getFloat(din, BytesRead);
		HX 			= Factory.getFloat(din, BytesRead);
		HY 			= Factory.getFloat(din, BytesRead);
		HZ 			= Factory.getFloat(din, BytesRead);
	
		//Make sure everything drawn in a right frame, otherwise will have problem with face culling.
		if((PosX * (WY*HZ - WZ*HY) + PosY * (WZ*HX - WX*HZ) + PosZ * (WX*HY - WY*HX))<0){WX*=-1; WY*=-1; WZ*=-1;}
		return BytesRead[0];
	}
		
	public ByteBuffer writeData(){		
		ByteBuffer toReturn = ByteBuffer.allocate(44);		
		Factory.putUInt (toReturn, id);
		Factory.putFloat(toReturn, ProjFactor);
		Factory.putFloat(toReturn, PosX);
		Factory.putFloat(toReturn, PosY);
		Factory.putFloat(toReturn, PosZ);
		Factory.putFloat(toReturn, WX);
		Factory.putFloat(toReturn, WY);
		Factory.putFloat(toReturn, WZ);
		Factory.putFloat(toReturn, HX);
		Factory.putFloat(toReturn, HY);
		Factory.putFloat(toReturn, HZ);				
		return toReturn;		
	}		

	public float getPosX() {return PosX;}
	public float getPosY() {return PosY;}
	public float getPosZ() {return PosZ;}	
	
	public void draw(GL2 gl) {			
		if(VBO_Vertices[0]==0){
			loadTexture(gl);
			
			float C1X = PosX-WX-HX;		float C1Y = PosY-WY-HY;		float C1Z = PosZ-WZ-HZ;
			float C2X = PosX+WX-HX;		float C2Y = PosY+WY-HY;		float C2Z = PosZ+WZ-HZ;	
			float C3X = PosX+WX+HX;		float C3Y = PosY+WY+HY;		float C3Z = PosZ+WZ+HZ;
			float C4X = PosX-WX+HX;		float C4Y = PosY-WY+HY;		float C4Z = PosZ-WZ+HZ;	
			float C5X = C1X*ProjFactor;	float C5Y = C1Y*ProjFactor;	float C5Z = C1Z*ProjFactor;
			float C6X = C2X*ProjFactor;	float C6Y = C2Y*ProjFactor;	float C6Z = C2Z*ProjFactor;	
			float C7X = C3X*ProjFactor;	float C7Y = C3Y*ProjFactor;	float C7Z = C3Z*ProjFactor;	
			float C8X = C4X*ProjFactor;	float C8Y = C4Y*ProjFactor;	float C8Z = C4Z*ProjFactor;			
			
			float [] positionData   = new float[]{
			C1X, C1Y,	C1Z,	C4X, C4Y,	C4Z,	C3X, C3Y,	C3Z,	C2X, C2Y,	C2Z,
			C5X, C5Y,	C5Z,	C6X, C6Y,	C6Z,	C7X, C7Y,	C7Z,	C8X, C8Y,	C8Z,
			C1X, C1Y,	C1Z,	C2X, C2Y,	C2Z,	C6X, C6Y,	C6Z,	C5X, C5Y,	C5Z,
			C4X, C4Y,	C4Z,	C8X, C8Y,	C8Z,	C7X, C7Y,	C7Z,	C3X, C3Y,	C3Z,								
			C1X, C1Y,	C1Z,	C5X, C5Y,	C5Z,	C8X, C8Y,	C8Z,	C4X, C4Y,	C4Z,		
			C2X, C2Y,	C2Z,	C3X, C3Y,	C3Z,	C7X, C7Y,	C7Z,	C6X, C6Y,	C6Z	};					    	
			int [] CubeIndex  = {0,1,2,3, 4,5,6,7, 8,9,10,11, 12,13,14,15, 16,17,18,19, 20,21,22,23};		
			float [] normalData = SetNormalArray(positionData, CubeIndex, 0, CubeIndex.length, 4);
			
			float [] texData   = new float[]{
			1.0f, 0.0f, 	0.0f, 0.0f,	 	0.0f, 1.0f,		1.0f, 1.0f,
			1.0f, 0.0f, 	1.0f, 1.0f,	 	0.0f, 1.0f,		0.0f, 0.0f,							
			0.0f, 0.0f,		0.0f, 0.0f, 	0.0f, 0.0f,	 	0.0f, 0.0f,
			0.0f, 0.0f,		0.0f, 0.0f, 	0.0f, 0.0f,	 	0.0f, 0.0f,			
			0.0f, 0.0f,		0.0f, 0.0f, 	0.0f, 0.0f,	 	0.0f, 0.0f,		
			0.0f, 0.0f,		0.0f, 0.0f, 	0.0f, 0.0f,	 	0.0f, 0.0f	};				
									
			IntBuffer CubeIndexBuf    = GLBuffers.newDirectIntBuffer(CubeIndex);
			FloatBuffer positionDataBuf = GLBuffers.newDirectFloatBuffer(positionData);			
			FloatBuffer normalDataBuf   = GLBuffers.newDirectFloatBuffer(normalData);
			FloatBuffer texDataBuf 		= GLBuffers.newDirectFloatBuffer(texData);			
									
			if(jfrog.Common.support_VBO){					
				gl.glGenBuffers( 4, VBO_Vertices, 0);										// Get A Valid Name
				gl.glBindBuffer( GL2.GL_ELEMENT_ARRAY_BUFFER, VBO_Vertices[0] );			// Bind The Buffer
				gl.glBufferData( GL2.GL_ELEMENT_ARRAY_BUFFER, CubeIndexBuf.capacity() * 4,CubeIndexBuf, GL2.GL_STATIC_DRAW );				
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[1] );					// Bind The Buffer
				gl.glBufferData( GL2.GL_ARRAY_BUFFER, positionDataBuf.capacity() * 4, positionDataBuf, GL2.GL_STATIC_DRAW );
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[2] );					// Bind The Buffer
				gl.glBufferData( GL2.GL_ARRAY_BUFFER, normalDataBuf.capacity() * 4, normalDataBuf, GL2.GL_STATIC_DRAW );
				if(tex!=null){
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[3] );					// Bind The Buffer
				gl.glBufferData( GL2.GL_ARRAY_BUFFER, texDataBuf.capacity() * 4, texDataBuf, GL2.GL_STATIC_DRAW );
				}
								
			}else{
				VBO_Vertices[0] = gl.glGenLists(1);			
				gl.glNewList(VBO_Vertices[0], GL2.GL_COMPILE_AND_EXECUTE);
				
				gl.glNormalPointer( GL2.GL_FLOAT, 0, normalDataBuf );				
				gl.glVertexPointer( 3, GL2.GL_FLOAT, 0, positionDataBuf );		
				if(tex!=null)
				gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, texDataBuf);				
				gl.glDrawElements( GL2.GL_QUADS, 24, GL2.GL_UNSIGNED_INT, CubeIndexBuf);
				gl.glEndList();			
			}
		}else if(VBO_Vertices[0]>0){
			if(jfrog.Common.support_VBO){
//				if(texture!=null)texture.enable(gl);
				if(tex!=null)tex.bind(gl);				
				
				gl.glBindBuffer( GL2.GL_ELEMENT_ARRAY_BUFFER, VBO_Vertices[0] );
				if(tex!=null){
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[3] );	gl.glTexCoordPointer( 2, GL2.GL_FLOAT, 0, 0 ); }				
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[2] );	gl.glNormalPointer  (    GL2.GL_FLOAT, 0, 0 );								
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[1] );	gl.glVertexPointer  ( 3, GL2.GL_FLOAT, 0, 0 );				
				gl.glDrawElements( GL2.GL_QUADS, 24, GL2.GL_UNSIGNED_INT, 0);
//				if(texture!=null)texture.disable(gl);				
			}else{			
				gl.glCallList(VBO_Vertices[0]);						
			}			
		}
	}	
	
	
}
