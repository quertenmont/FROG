package jfrog.object;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jogamp.opengl.GL2;

import java.lang.Math;

import com.jogamp.opengl.util.GLBuffers;

public class Prim_Circle extends Base {
	float Radius;
	float PosX;    float PosY;  float PosZ;					
	float DirX;	   float DirY;	float DirZ;	
	int NPhi;	
		
	public int ChunkId() {	return 43030; }
	public boolean isCompactible() {return true;}	

	public String toString() {
		return "Prim_Circle [Radius=" + Radius + ", PosX=" + PosX + ", PosY="
				+ PosY + ", PosZ=" + PosZ + ", DirX=" + DirX + ", DirY=" + DirY
				+ ", DirZ=" + DirZ + ", NPhi=" + NPhi + "]";
	}
	
	public int readData(URL url, DataInputStream din, Base obj, Base parent, int bytesToRead, int fileOffset, int level) throws IOException {
		int [] BytesRead = {0};
		id 			= Factory.getUInt  (din, BytesRead);
		Radius		= Factory.getFloat (din, BytesRead);
		PosX    	= Factory.getFloat (din, BytesRead);
		PosY    	= Factory.getFloat (din, BytesRead);
		PosZ    	= Factory.getFloat (din, BytesRead);
		DirX    	= Factory.getFloat (din, BytesRead);
		DirY    	= Factory.getFloat (din, BytesRead);
		DirZ    	= Factory.getFloat (din, BytesRead);					
		NPhi    	= Factory.getUShort(din, BytesRead);			
		return BytesRead[0];
	}
	
	public ByteBuffer writeData(){		
		ByteBuffer toReturn = ByteBuffer.allocate(34);		
		Factory.putUInt  (toReturn, id);
		Factory.putFloat (toReturn, Radius);		
		Factory.putFloat (toReturn, PosX);
		Factory.putFloat (toReturn, PosY);
		Factory.putFloat (toReturn, PosZ);
		Factory.putFloat (toReturn, DirX);		
		Factory.putFloat (toReturn, DirY);
		Factory.putFloat (toReturn, DirZ);		
		Factory.putUShort(toReturn, NPhi);
		return toReturn;		
	}			

	public void draw(GL2 gl) {
		if(VBO_Vertices[0]==0){										
			float [] positionData   = new float[3*(int)NPhi];	

			float dphi  = 6.283185307f/NPhi;
			int Index=0;
			for(float phi=0; phi<=6.2831f;phi+=dphi){			
				positionData[Index+0] = (float)(Radius*Math.cos(phi));
				positionData[Index+1] = (float)(Radius*Math.sin(phi));
				positionData[Index+2] = 0;				
				Index+=3;
			}
			
			float D     = (float)Math.sqrt(DirX*DirX+DirY*DirY+DirZ*DirZ);
			float Dt    = (float)Math.sqrt(DirX*DirX+DirZ*DirZ);
			float rotPhi,rotTheta;
			if(D ==0){rotTheta = 0;}else{rotTheta = (float)(57.29f*Math.asin(DirY/D));}
			if(Dt==0){rotPhi   = 0;}else{rotPhi   = (float)(57.29f*Math.asin(DirX/Dt));}
			gl.glPushMatrix();
			gl.glLoadIdentity();
			gl.glTranslatef(PosX,PosY,PosZ);		
			gl.glRotatef(rotPhi  ,0,1,0);
			gl.glRotatef(rotTheta,1,0,0);		
			TransformVerticesArray(gl, positionData);
			gl.glPopMatrix();			
			
				
			int  [] CubeIndex  	= new int [positionData.length/3];
			for(int i=0;i<CubeIndex.length;i++)CubeIndex[i] = (short)i;		

			IntBuffer CubeIndexBuf    = GLBuffers.newDirectIntBuffer(CubeIndex);
			FloatBuffer positionDataBuf = GLBuffers.newDirectFloatBuffer(positionData);			
			FloatBuffer normalDataBuf   = GLBuffers.newDirectFloatBuffer(positionData.length);	
						
			if(jfrog.Common.support_VBO){					
				gl.glGenBuffers( 3, VBO_Vertices, 0);							// Get A Valid Name
				gl.glBindBuffer( GL2.GL_ELEMENT_ARRAY_BUFFER, VBO_Vertices[0] );			// Bind The Buffer
				gl.glBufferData( GL2.GL_ELEMENT_ARRAY_BUFFER, CubeIndexBuf.capacity() * 4,CubeIndexBuf, GL2.GL_STATIC_DRAW );				
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[1] );			// Bind The Buffer
				gl.glBufferData( GL2.GL_ARRAY_BUFFER, positionDataBuf.capacity() * 4, positionDataBuf, GL2.GL_STATIC_DRAW );
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[2] );			// Bind The Buffer
				gl.glBufferData( GL2.GL_ARRAY_BUFFER, normalDataBuf.capacity() * 4, normalDataBuf, GL2.GL_STATIC_DRAW );				
			}else{
				VBO_Vertices[0] = gl.glGenLists(1);			
				gl.glNewList(VBO_Vertices[0], GL2.GL_COMPILE_AND_EXECUTE);
				
				gl.glNormalPointer( GL2.GL_FLOAT, 0, normalDataBuf );				
				gl.glVertexPointer( 3, GL2.GL_FLOAT, 0, positionDataBuf );
				gl.glDrawElements( GL2.GL_LINE_LOOP, NPhi, GL2.GL_UNSIGNED_INT, CubeIndexBuf);
				gl.glEndList();			
			}
		}else if(VBO_Vertices[0]>0){
			if(jfrog.Common.support_VBO){
				gl.glBindBuffer( GL2.GL_ELEMENT_ARRAY_BUFFER, VBO_Vertices[0] );
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[2] );	gl.glNormalPointer(    GL2.GL_FLOAT, 0, 0 );				
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[1] );	gl.glVertexPointer( 3, GL2.GL_FLOAT, 0, 0 );							
				gl.glDrawElements( GL2.GL_LINE_LOOP    , NPhi, GL2.GL_UNSIGNED_INT, 0);				
			}else{			
				gl.glCallList(VBO_Vertices[0]);						
			}			
		}
	}
}

