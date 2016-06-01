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

public class Prim_Cylinder extends Base {
	public float Radius;
	public float PosX;		public float PosY;    public float PosZ;	
	public float LengthX;	public float LengthY; public float LengthZ;
	public int   NPhi;		public byte  Endcap; 	
		
	public int ChunkId() {	return 41040; }
	public boolean isCompactible() {return true;}	

	@Override
	public String toString() {
		return String
				.format("Prim_Cylinder [Radius=%s, PosX=%s, PosY=%s, PosZ=%s, LengthX=%s, LengthY=%s, LengthZ=%s, NPhi=%s, Endcap=%s]",
						Radius, PosX, PosY, PosZ, LengthX, LengthY, LengthZ,
						NPhi, Endcap);
	}
	public int readData(URL url, DataInputStream din, Base obj, Base parent, int bytesToRead, int fileOffset, int level) throws IOException {
		int [] BytesRead = {0};
		id 			= Factory.getUInt  (din, BytesRead);
		Radius		= Factory.getFloat (din, BytesRead);
		PosX    	= Factory.getFloat (din, BytesRead);
		PosY    	= Factory.getFloat (din, BytesRead);
		PosZ    	= Factory.getFloat (din, BytesRead);
		LengthX    	= Factory.getFloat (din, BytesRead);
		LengthY    	= Factory.getFloat (din, BytesRead);
		LengthZ    	= Factory.getFloat (din, BytesRead);					
		NPhi    	= Factory.getUShort(din, BytesRead);			
		Endcap    	= Factory.getByte  (din, BytesRead);
		return BytesRead[0];
	}
	
	public ByteBuffer writeData(){		
		ByteBuffer toReturn = ByteBuffer.allocate(35);		
		Factory.putUInt  (toReturn, id);
		Factory.putFloat (toReturn, Radius);		
		Factory.putFloat (toReturn, PosX);
		Factory.putFloat (toReturn, PosY);
		Factory.putFloat (toReturn, PosZ);
		Factory.putFloat (toReturn, LengthX);		
		Factory.putFloat (toReturn, LengthY);
		Factory.putFloat (toReturn, LengthZ);		
		Factory.putUShort(toReturn, NPhi);
		Factory.putByte  (toReturn, Endcap);
		return toReturn;		
	}			

	@Override
	public void draw(GL2 gl) {
		if(VBO_Vertices[0]==0){										
			float [] positionData   = new float[3*(4+4+3+3)*(int)NPhi];	

			int Index=0;
			float Lt    = (float)Math.sqrt(LengthX*LengthX+LengthZ*LengthZ);
			float L     = (float)Math.sqrt(LengthX*LengthX+LengthY*LengthY+LengthZ*LengthZ);
			float dphi  = 6.283185307f/NPhi;	
			for(float phi=0; phi<=6.2831f;phi+=dphi){			
				positionData[Index+0 ] = (float)(Radius*Math.cos(phi));
				positionData[Index+1 ] = (float)(Radius*Math.sin(phi));
				positionData[Index+2 ] = -L*0.5f;				
				positionData[Index+3 ] = (float)(Radius*Math.cos(phi+dphi));
				positionData[Index+4 ] = (float)(Radius*Math.sin(phi+dphi));
				positionData[Index+5 ] = -L*0.5f;				
				positionData[Index+6 ] = (float)(Radius*Math.cos(phi+dphi));
				positionData[Index+7 ] = (float)(Radius*Math.sin(phi+dphi));
				positionData[Index+8 ] = L*0.5f;				
				positionData[Index+9 ] = (float)(Radius*Math.cos(phi));
				positionData[Index+10] = (float)(Radius*Math.sin(phi));
				positionData[Index+11] = L*0.5f;			
				Index+=12;
			}
			for(float phi=0; phi<=6.2831f;phi+=dphi){			
				positionData[Index+0 ] = (float)(Radius*Math.cos(phi));
				positionData[Index+1 ] = (float)(Radius*Math.sin(phi));
				positionData[Index+2 ] = -L*0.5f;	
				positionData[Index+3 ] = (float)(Radius*Math.cos(phi));
				positionData[Index+4 ] = (float)(Radius*Math.sin(phi));
				positionData[Index+5 ] = L*0.5f;
				positionData[Index+6 ] = (float)(Radius*Math.cos(phi+dphi));
				positionData[Index+7 ] = (float)(Radius*Math.sin(phi+dphi));
				positionData[Index+8 ] = L*0.5f;
				positionData[Index+9 ] = (float)(Radius*Math.cos(phi+dphi));
				positionData[Index+10] = (float)(Radius*Math.sin(phi+dphi));
				positionData[Index+11] = -L*0.5f;				
				Index+=12;
			}
			for(float phi=0; phi<=6.2831f;phi+=dphi){	
				positionData[Index+0 ] = (float)(Radius*Math.cos(phi));
				positionData[Index+1 ] = (float)(Radius*Math.sin(phi));
				positionData[Index+2 ] = L*0.5f;				
				positionData[Index+3 ] = (float)(Radius*Math.cos(phi+dphi));
				positionData[Index+4 ] = (float)(Radius*Math.sin(phi+dphi));
				positionData[Index+5 ] = L*0.5f;				
				positionData[Index+6 ] = 0;
				positionData[Index+7 ] = 0;
				positionData[Index+8 ] = L*0.5f;		
				Index+=9;
			}
			for(float phi=0; phi<=6.2831f;phi+=dphi){			
				positionData[Index+0 ] = (float)(Radius*Math.cos(phi+dphi));
				positionData[Index+1 ] = (float)(Radius*Math.sin(phi+dphi));
				positionData[Index+2 ] = -L*0.5f;	
				positionData[Index+3 ] = (float)(Radius*Math.cos(phi));
				positionData[Index+4 ] = (float)(Radius*Math.sin(phi));
				positionData[Index+5 ] = -L*0.5f;		
				positionData[Index+6 ] = 0;
				positionData[Index+7 ] = 0;
				positionData[Index+8 ] = -L*0.5f;		
				if(Endcap==1){
					positionData[Index+2 ]*=-1;
					positionData[Index+5 ]*=-1;
					positionData[Index+8 ]*=-1;
				}
				Index+=9;
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
			float [] normalData = new float [positionData.length];
			SetNormalArray(positionData, CubeIndex, 0,          (4+4)*NPhi, 4, normalData);	
			SetNormalArray(positionData, CubeIndex, (4+4)*NPhi, (3+3)*NPhi, 3, normalData);

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
				gl.glDrawElements( GL2.GL_QUADS    , 4*NPhi, GL2.GL_UNSIGNED_INT, CubeIndexBuf); CubeIndexBuf.position(4*NPhi);
				if(Endcap<2){
				gl.glDrawElements( GL2.GL_QUADS    , 4*NPhi, GL2.GL_UNSIGNED_INT, CubeIndexBuf); CubeIndexBuf.position((4+4)*NPhi);}
				if(Endcap>0){
				gl.glDrawElements( GL2.GL_TRIANGLES, 3*NPhi, GL2.GL_UNSIGNED_INT, CubeIndexBuf); CubeIndexBuf.position((4+4+3)*NPhi);}
				gl.glDrawElements( GL2.GL_TRIANGLES, 3*NPhi, GL2.GL_UNSIGNED_INT, CubeIndexBuf);
				gl.glEndList();			
			}
		}else if(VBO_Vertices[0]>0){
			if(jfrog.Common.support_VBO){
				gl.glBindBuffer( GL2.GL_ELEMENT_ARRAY_BUFFER, VBO_Vertices[0] );
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[2] );	gl.glNormalPointer(    GL2.GL_FLOAT, 0, 0 );				
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[1] );	gl.glVertexPointer( 3, GL2.GL_FLOAT, 0, 0 );							
				gl.glDrawElements( GL2.GL_QUADS    , 4*NPhi, GL2.GL_UNSIGNED_INT, 0);
				if(Endcap<2)
				gl.glDrawElements( GL2.GL_QUADS    , 4*NPhi, GL2.GL_UNSIGNED_INT, 4*NPhi*2);
				if(Endcap>0)
				gl.glDrawElements( GL2.GL_TRIANGLES, 3*NPhi, GL2.GL_UNSIGNED_INT, (4+4)*NPhi*2);				
				gl.glDrawElements( GL2.GL_TRIANGLES, 3*NPhi, GL2.GL_UNSIGNED_INT, (4+4+3)*NPhi*2);				
			}else{			
				gl.glCallList(VBO_Vertices[0]);						
			}			
		}
	}
}

