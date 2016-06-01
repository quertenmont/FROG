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

public class Prim_Tube extends Base {
	public float RadiusIn;	public float RadiusOut;
	public float PosX;		public float PosY;    public float PosZ;	
	public float LengthX;	public float LengthY; public float LengthZ;
	public int   NPhi;		public byte  Endcap; 	
	public float Phi0;
		
	public int ChunkId() {	return 41041; }
	public boolean isCompactible() {return true;}	


	public String toString() {
		return String
				.format("Prim_Tube [RadiusIn=%s, RadiusOut=%s, PosX=%s, PosY=%s, PosZ=%s, LengthX=%s, LengthY=%s, LengthZ=%s, NPhi=%s, Endcap=%s, Phi0=%s]",
						RadiusIn, RadiusOut, PosX, PosY, PosZ, LengthX,
						LengthY, LengthZ, NPhi, Endcap, Phi0);
	}
	
	
	
	public int readData(URL url, DataInputStream din, Base obj, Base parent, int bytesToRead, int fileOffset, int level) throws IOException {
		int [] BytesRead = {0};
		id 			= Factory.getUInt  (din, BytesRead);
		RadiusIn	= Factory.getFloat (din, BytesRead);
		RadiusOut	= Factory.getFloat (din, BytesRead);
		PosX    	= Factory.getFloat (din, BytesRead);
		PosY    	= Factory.getFloat (din, BytesRead);
		PosZ    	= Factory.getFloat (din, BytesRead);
		LengthX    	= Factory.getFloat (din, BytesRead);
		LengthY    	= Factory.getFloat (din, BytesRead);
		LengthZ    	= Factory.getFloat (din, BytesRead);					
		NPhi    	= Factory.getUShort(din, BytesRead);			
		Endcap    	= Factory.getByte  (din, BytesRead);
		Phi0    	= Factory.getFloat (din, BytesRead);
		return BytesRead[0];
	}
	
	public ByteBuffer writeData(){		
		ByteBuffer toReturn = ByteBuffer.allocate(43);		
		Factory.putUInt  (toReturn, id);
		Factory.putFloat (toReturn, RadiusIn);
		Factory.putFloat (toReturn, RadiusOut);		
		Factory.putFloat (toReturn, PosX);
		Factory.putFloat (toReturn, PosY);
		Factory.putFloat (toReturn, PosZ);
		Factory.putFloat (toReturn, LengthX);		
		Factory.putFloat (toReturn, LengthY);
		Factory.putFloat (toReturn, LengthZ);		
		Factory.putUShort(toReturn, NPhi);
		Factory.putByte  (toReturn, Endcap);
		Factory.putFloat (toReturn, Phi0);
				
		return toReturn;		
	}			

	@Override
	public void draw(GL2 gl) {
		if(VBO_Vertices[0]==0){										
			float [] positionData   = new float[3*(4+4+4+4)*(int)NPhi];	

			int Index=0;
			float Lt    = (float)Math.sqrt(LengthX*LengthX+LengthZ*LengthZ);
			float L     = (float)Math.sqrt(LengthX*LengthX+LengthY*LengthY+LengthZ*LengthZ);
			float dphi  = 6.283185307f/NPhi;	
			for(float phi=0; phi<=6.2831f;phi+=dphi){			
				positionData[Index+0 ] = (float)(RadiusOut*Math.cos(phi));
				positionData[Index+1 ] = (float)(RadiusOut*Math.sin(phi));
				positionData[Index+2 ] = -L*0.5f;				
				positionData[Index+3 ] = (float)(RadiusOut*Math.cos(phi+dphi));
				positionData[Index+4 ] = (float)(RadiusOut*Math.sin(phi+dphi));
				positionData[Index+5 ] = -L*0.5f;				
				positionData[Index+6 ] = (float)(RadiusOut*Math.cos(phi+dphi));
				positionData[Index+7 ] = (float)(RadiusOut*Math.sin(phi+dphi));
				positionData[Index+8 ] = L*0.5f;				
				positionData[Index+9 ] = (float)(RadiusOut*Math.cos(phi));
				positionData[Index+10] = (float)(RadiusOut*Math.sin(phi));
				positionData[Index+11] = L*0.5f;			
				Index+=12;
			}
			for(float phi=0; phi<=6.2831f;phi+=dphi){			
				positionData[Index+0 ] = (float)(RadiusIn*Math.cos(phi));
				positionData[Index+1 ] = (float)(RadiusIn*Math.sin(phi));
				positionData[Index+2 ] = -L*0.5f;	
				positionData[Index+3 ] = (float)(RadiusIn*Math.cos(phi));
				positionData[Index+4 ] = (float)(RadiusIn*Math.sin(phi));
				positionData[Index+5 ] = L*0.5f;
				positionData[Index+6 ] = (float)(RadiusIn*Math.cos(phi+dphi));
				positionData[Index+7 ] = (float)(RadiusIn*Math.sin(phi+dphi));
				positionData[Index+8 ] = L*0.5f;
				positionData[Index+9 ] = (float)(RadiusIn*Math.cos(phi+dphi));
				positionData[Index+10] = (float)(RadiusIn*Math.sin(phi+dphi));
				positionData[Index+11] = -L*0.5f;				
				Index+=12;
			}
			for(float phi=0; phi<=6.2831f;phi+=dphi){	
				positionData[Index+0 ] = (float)(RadiusOut*Math.cos(phi));
				positionData[Index+1 ] = (float)(RadiusOut*Math.sin(phi));
				positionData[Index+2 ] = -L*0.5f;		
				positionData[Index+3 ] = (float)(RadiusIn*Math.cos(phi));
				positionData[Index+4 ] = (float)(RadiusIn*Math.sin(phi));
				positionData[Index+5 ] = -L*0.5f;		
				positionData[Index+6 ] = (float)(RadiusIn*Math.cos(phi+dphi));
				positionData[Index+7 ] = (float)(RadiusIn*Math.sin(phi+dphi));
				positionData[Index+8 ] = -L*0.5f;				
				positionData[Index+9 ] = (float)(RadiusOut*Math.cos(phi+dphi));
				positionData[Index+10] = (float)(RadiusOut*Math.sin(phi+dphi));
				positionData[Index+11] = -L*0.5f;					
				Index+=12;
			}
			for(float phi=0; phi<=6.2831f;phi+=dphi){			
				positionData[Index+0 ] = (float)(RadiusIn*Math.cos(phi));
				positionData[Index+1 ] = (float)(RadiusIn*Math.sin(phi));
				positionData[Index+2 ] = L*0.5f;	
				positionData[Index+3 ] = (float)(RadiusOut*Math.cos(phi));
				positionData[Index+4 ] = (float)(RadiusOut*Math.sin(phi));
				positionData[Index+5 ] = L*0.5f;
				positionData[Index+6 ] = (float)(RadiusOut*Math.cos(phi+dphi));
				positionData[Index+7 ] = (float)(RadiusOut*Math.sin(phi+dphi));
				positionData[Index+8 ] = L*0.5f;
				positionData[Index+9 ] = (float)(RadiusIn*Math.cos(phi+dphi));
				positionData[Index+10] = (float)(RadiusIn*Math.sin(phi+dphi));
				positionData[Index+11] = L*0.5f;				
				Index+=12;
			}			
			

			float rotPhi,rotTheta;
			if(L ==0){rotTheta = 0;}else{rotTheta = 57.29f* (float)Math.asin((double)LengthY/L);}
			if(Lt==0){rotPhi   = 0;}else{rotPhi   = 57.29f* (float)Math.asin((double)LengthX/Lt);}
			gl.glPushMatrix();
			gl.glLoadIdentity();
			gl.glTranslatef(PosX,PosY,PosZ);		
			gl.glRotatef(rotPhi  ,0,1,0);
			gl.glRotatef(rotTheta,1,0,0);		
			TransformVerticesArray(gl, positionData);
			gl.glPopMatrix();			
			
				
			int  [] CubeIndex  	= new int [positionData.length/3];
			for(int i=0;i<CubeIndex.length;i++)CubeIndex[i] = (short)i;			
			float [] normalData = SetNormalArray(positionData, CubeIndex, 0, CubeIndex.length, 4);

			IntBuffer CubeIndexBuf    = GLBuffers.newDirectIntBuffer(CubeIndex);
			FloatBuffer positionDataBuf = GLBuffers.newDirectFloatBuffer(positionData);			
			FloatBuffer normalDataBuf   = GLBuffers.newDirectFloatBuffer(normalData);		
						
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
				gl.glDrawElements( GL2.GL_QUADS, (4+4+4+4)*(int)NPhi, GL2.GL_UNSIGNED_INT, CubeIndexBuf);
				gl.glEndList();			
			}
		}else if(VBO_Vertices[0]>0){
			if(jfrog.Common.support_VBO){
				gl.glBindBuffer( GL2.GL_ELEMENT_ARRAY_BUFFER, VBO_Vertices[0] );
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[2] );	gl.glNormalPointer(    GL2.GL_FLOAT, 0, 0 );				
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[1] );	gl.glVertexPointer( 3, GL2.GL_FLOAT, 0, 0 );							
				gl.glDrawElements( GL2.GL_QUADS, (4+4+4+4)*(int)NPhi, GL2.GL_UNSIGNED_INT, 0);
			}else{			
				gl.glCallList(VBO_Vertices[0]);						
			}			
		}
	}
}

