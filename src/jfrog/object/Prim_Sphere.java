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

public class Prim_Sphere extends Base {
	public float Radius;
	public float PosX;			 public float PosY;    public float PosZ;	
	public int NPhi; public int NTheta;		
		
	public int ChunkId() {	return 41030; }
	public boolean isCompactible() {return true;}	

	public String toString() {
		return "Prim_Sphere [Radius=" + Radius + ", PosX=" + PosX + ", PosY="
				+ PosY + ", PosZ=" + PosZ + ", NPhi=" + NPhi + ", NTheta="
				+ NTheta + "]";
	}
	
	public int readData(URL url, DataInputStream din, Base obj, Base parent, int bytesToRead, int fileOffset, int level) throws IOException {
		int [] BytesRead = {0};
		id 		= Factory.getUInt  (din, BytesRead);
		Radius  = Factory.getFloat (din, BytesRead);
		PosX    = Factory.getFloat (din, BytesRead);
		PosY    = Factory.getFloat (din, BytesRead);
		PosZ    = Factory.getFloat (din, BytesRead);			
		NPhi    = Factory.getUShort(din, BytesRead);			
		NTheta  = Factory.getUShort(din, BytesRead);
		return BytesRead[0];
	}
	
	public ByteBuffer writeData(){		
		ByteBuffer toReturn = ByteBuffer.allocate(24);		
		Factory.putUInt  (toReturn, id);
		Factory.putFloat (toReturn, Radius);
		Factory.putFloat (toReturn, PosX);
		Factory.putFloat (toReturn, PosY);
		Factory.putFloat (toReturn, PosZ);
		Factory.putUShort(toReturn, NPhi);
		Factory.putUShort(toReturn, NTheta);				
		return toReturn;		
	}			

	@Override
	public void draw(GL2 gl) {
		if(VBO_Vertices[0]==0){										
			float [] positionData   = new float[4*NPhi*NTheta*3];	

			int Index=0;
			float dthe = 3.1416f/NTheta;
			float dphi = 6.2832f/NPhi;
			for(int j=0;j<NTheta;j++){	
			for(int i=0;i<NPhi;i++){				
				float phi1 = i*dphi;			float phi2 = phi1+dphi;
				float the1 = -1.5708f + j*dthe;	float the2 = the1+dthe;
				positionData[Index+0 ] = (float)(Radius*Math.cos(the1)*Math.cos(phi1));
				positionData[Index+1 ] = (float)(Radius*Math.cos(the1)*Math.sin(phi1));
				positionData[Index+2 ] = (float)(Radius*Math.sin(the1));
				positionData[Index+3 ] = (float)(Radius*Math.cos(the1)*Math.cos(phi2));
				positionData[Index+4 ] = (float)(Radius*Math.cos(the1)*Math.sin(phi2));
				positionData[Index+5 ] = (float)(Radius*Math.sin(the1));
				positionData[Index+6 ] = (float)(Radius*Math.cos(the2)*Math.cos(phi2));
				positionData[Index+7 ] = (float)(Radius*Math.cos(the2)*Math.sin(phi2));
				positionData[Index+8 ] = (float)(Radius*Math.sin(the2));
				positionData[Index+9 ] = (float)(Radius*Math.cos(the2)*Math.cos(phi1));
				positionData[Index+10] = (float)(Radius*Math.cos(the2)*Math.sin(phi1));
				positionData[Index+11] = (float)(Radius*Math.sin(the2));		
				if(j==0){
					//Special treatment for the poles
					positionData[Index+0 ] = 0;
					positionData[Index+1 ] = 0;
					positionData[Index+2 ] = (float)(Radius*-1);
					positionData[Index+3 ] = (float)(Radius*Math.cos(the2)*Math.cos(phi2));
					positionData[Index+4 ] = (float)(Radius*Math.cos(the2)*Math.sin(phi2));
					positionData[Index+5 ] = (float)(Radius*Math.sin(the2));
					positionData[Index+6 ] = (float)(Radius*Math.cos(the2)*Math.cos(phi1));
					positionData[Index+7 ] = (float)(Radius*Math.cos(the2)*Math.sin(phi1));
					positionData[Index+8 ] = (float)(Radius*Math.sin(the2));
					positionData[Index+9 ] = (float)(Radius*Math.cos(the2)*Math.cos(phi1));
					positionData[Index+10] = (float)(Radius*Math.cos(the2)*Math.sin(phi1));
					positionData[Index+11] = (float)(Radius*Math.sin(the2));
				}
				if(j==NTheta-1){
					//Special treatment for the poles
					positionData[Index+0 ] = (float)(Radius*Math.cos(the1)*Math.cos(phi1));
					positionData[Index+1 ] = (float)(Radius*Math.cos(the1)*Math.sin(phi1));
					positionData[Index+2 ] = (float)(Radius*Math.sin(the1));
					positionData[Index+3 ] = (float)(Radius*Math.cos(the1)*Math.cos(phi2));
					positionData[Index+4 ] = (float)(Radius*Math.cos(the1)*Math.sin(phi2));
					positionData[Index+5 ] = (float)(Radius*Math.sin(the1));
					positionData[Index+6 ] = 0;
					positionData[Index+7 ] = 0;
					positionData[Index+8 ] = Radius;
					positionData[Index+9 ] = 0;
					positionData[Index+10] = 0;
					positionData[Index+11] = Radius;			
				}
				Index+=12;	
			}}	
			
			gl.glPushMatrix();
			gl.glLoadIdentity();
			gl.glTranslatef(PosX,PosY,PosZ);				
			TransformVerticesArray(gl, positionData);
			gl.glPopMatrix();			
				
			int  [] CubeIndex  	= new int [4*NPhi*NTheta];
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
				gl.glDrawElements( GL2.GL_QUADS, 4*NPhi*NTheta, GL2.GL_UNSIGNED_INT, CubeIndexBuf);
				gl.glEndList();			
			}
		}else if(VBO_Vertices[0]>0){
			if(jfrog.Common.support_VBO){
				gl.glBindBuffer( GL2.GL_ELEMENT_ARRAY_BUFFER, VBO_Vertices[0] );
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[2] );	gl.glNormalPointer(    GL2.GL_FLOAT, 0, 0 );				
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[1] );	gl.glVertexPointer( 3, GL2.GL_FLOAT, 0, 0 );							
				gl.glDrawElements( GL2.GL_QUADS, 4*NPhi*NTheta, GL2.GL_UNSIGNED_INT, 0);
			}else{			
				gl.glCallList(VBO_Vertices[0]);						
			}			
		}
	}
}
