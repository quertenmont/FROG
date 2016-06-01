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

public class Prim_PartialSphere extends Base {
	public float Radius;
	public float PosX;			 public float PosY;    public float PosZ;		
	public float Phi1;			 float Phi2;	
	public float Theta1;		 float Theta2;	
	public int NPhi; public int NTheta;	public float ROT;	
		
	public int ChunkId() {	return 41031; }
	public boolean isCompactible() {return true;}	

	public String toString() {
		return "Prim_PartialSphere [Radius=" + Radius + ", PosX=" + PosX
				+ ", PosY=" + PosY + ", PosZ=" + PosZ + ", Phi1=" + Phi1
				+ ", Phi2=" + Phi2 + ", Theta1=" + Theta1 + ", Theta2="
				+ Theta2 + ", NPhi=" + NPhi + ", NTheta=" + NTheta + ", ROT="
				+ ROT + "]";
	}
	
	public int readData(URL url, DataInputStream din, Base obj, Base parent, int bytesToRead, int fileOffset, int level) throws IOException {
		int [] BytesRead = {0};
		id 		= Factory.getUInt  (din, BytesRead);
		Radius  = Factory.getFloat (din, BytesRead);
		PosX    = Factory.getFloat (din, BytesRead);
		PosY    = Factory.getFloat (din, BytesRead);
		PosZ    = Factory.getFloat (din, BytesRead);			
		Phi1    = Factory.getFloat (din, BytesRead);		
		Phi2    = Factory.getFloat (din, BytesRead);
		Theta1  = Factory.getFloat (din, BytesRead);
		Theta2  = Factory.getFloat (din, BytesRead);		
		NPhi    = Factory.getUShort(din, BytesRead);			
		NTheta  = Factory.getUShort(din, BytesRead);
		ROT	    = Factory.getFloat (din, BytesRead);		
		
//		NPhi = 50;
//		NTheta = 50;
		return BytesRead[0];
	}
	
	public ByteBuffer writeData(){		
		ByteBuffer toReturn = ByteBuffer.allocate(44);		
		Factory.putUInt  (toReturn, id);
		Factory.putFloat (toReturn, Radius);
		Factory.putFloat (toReturn, PosX);
		Factory.putFloat (toReturn, PosY);
		Factory.putFloat (toReturn, PosZ);
		Factory.putFloat (toReturn, Phi1);		
		Factory.putFloat (toReturn, Phi2);
		Factory.putFloat (toReturn, Theta1);
		Factory.putFloat (toReturn, Theta2);		
		Factory.putUShort(toReturn, NPhi);
		Factory.putUShort(toReturn, NTheta);
		Factory.putFloat (toReturn, ROT);		
		return toReturn;		
	}			

	@Override
	public void draw(GL2 gl) {
		if(VBO_Vertices[0]==0){										
			float [] positionData   = new float[(4+4)*NPhi*NTheta*3];	

			int Index=0;
			float dphi =	6.283185307f/NPhi;
			float dthe =	3.141592653f/NTheta;

			for(float the=Theta1; the<=Theta2-dthe;the+=dthe){
			for(float phi=Phi1;   phi<=Phi2  -dphi;phi+=dphi){	
				positionData[Index+0 ] = (float)(Radius*Math.cos(the)*Math.cos(phi));
				positionData[Index+1 ] = (float)(Radius*Math.cos(the)*Math.sin(phi));
				positionData[Index+2 ] = (float)(Radius*Math.sin(the));
				positionData[Index+3 ] = (float)(Radius*Math.cos(the)*Math.cos(phi+dphi));
				positionData[Index+4 ] = (float)(Radius*Math.cos(the)*Math.sin(phi+dphi));
				positionData[Index+5 ] = (float)(Radius*Math.sin(the));
				positionData[Index+6 ] = (float)(Radius*Math.cos(the+dthe)*Math.cos(phi+dphi));
				positionData[Index+7 ] = (float)(Radius*Math.cos(the+dthe)*Math.sin(phi+dphi));
				positionData[Index+8 ] = (float)(Radius*Math.sin(the+dthe));
				positionData[Index+9 ] = (float)(Radius*Math.cos(the+dthe)*Math.cos(phi));
				positionData[Index+10] = (float)(Radius*Math.cos(the+dthe)*Math.sin(phi));
				positionData[Index+11] = (float)(Radius*Math.sin(the+dthe));
				positionData[Index+12] = (float)(Radius*Math.cos(the)*Math.cos(phi));
				positionData[Index+13] = (float)(Radius*Math.cos(the)*Math.sin(phi));
				positionData[Index+14] = (float)(Radius*Math.sin(the));
				positionData[Index+15] = (float)(Radius*Math.cos(the+dthe)*Math.cos(phi));
				positionData[Index+16] = (float)(Radius*Math.cos(the+dthe)*Math.sin(phi));
				positionData[Index+17] = (float)(Radius*Math.sin(the+dthe));
				positionData[Index+18] = (float)(Radius*Math.cos(the+dthe)*Math.cos(phi+dphi));
				positionData[Index+19] = (float)(Radius*Math.cos(the+dthe)*Math.sin(phi+dphi));
				positionData[Index+20] = (float)(Radius*Math.sin(the+dthe));
				positionData[Index+21] = (float)(Radius*Math.cos(the)*Math.cos(phi+dphi));
				positionData[Index+22] = (float)(Radius*Math.cos(the)*Math.sin(phi+dphi));
				positionData[Index+23] = (float)(Radius*Math.sin(the));
				Index+=24;
			}}	
			
			gl.glPushMatrix();
			gl.glLoadIdentity();
			gl.glTranslatef(PosX,PosY,PosZ);
			gl.glRotatef(ROT,0,1,0);				
			TransformVerticesArray(gl, positionData);
			gl.glPopMatrix();			
				
			int  [] CubeIndex  	= new int [(4+4)*NPhi*NTheta];
			for(int i=0;i<CubeIndex.length;i++)CubeIndex[i] = (int)i;			
			float [] normalData = SetNormalArray(positionData, CubeIndex, 0,          (4+4)*NPhi*NTheta, 4);

			IntBuffer CubeIndexBuf      = GLBuffers.newDirectIntBuffer(CubeIndex);
			FloatBuffer positionDataBuf = GLBuffers.newDirectFloatBuffer(positionData);			
			FloatBuffer normalDataBuf   = GLBuffers.newDirectFloatBuffer(normalData);		
						
			if(jfrog.Common.support_VBO){					
				gl.glGenBuffers( 3, VBO_Vertices, 0);							// Get A Valid Name
				gl.glBindBuffer( GL2.GL_ELEMENT_ARRAY_BUFFER, VBO_Vertices[0] );			// Bind The Buffer
				gl.glBufferData( GL2.GL_ELEMENT_ARRAY_BUFFER, CubeIndexBuf.capacity() * 4, CubeIndexBuf, GL2.GL_STATIC_DRAW );				
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[1] );			// Bind The Buffer
				gl.glBufferData( GL2.GL_ARRAY_BUFFER, positionDataBuf.capacity() * 4, positionDataBuf, GL2.GL_STATIC_DRAW );
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[2] );			// Bind The Buffer
				gl.glBufferData( GL2.GL_ARRAY_BUFFER, normalDataBuf.capacity() * 4, normalDataBuf, GL2.GL_STATIC_DRAW );				
			}else{
				VBO_Vertices[0] = gl.glGenLists(1);			
				gl.glNewList(VBO_Vertices[0], GL2.GL_COMPILE_AND_EXECUTE);
				
				gl.glNormalPointer( GL2.GL_FLOAT, 0, normalDataBuf );				
				gl.glVertexPointer( 3, GL2.GL_FLOAT, 0, positionDataBuf );			
				gl.glDrawElements( GL2.GL_QUADS, (4+4)*NPhi*NTheta, GL2.GL_UNSIGNED_INT, CubeIndexBuf);
				gl.glEndList();			
			}
		}else if(VBO_Vertices[0]>0){
			if(jfrog.Common.support_VBO){
				gl.glBindBuffer( GL2.GL_ELEMENT_ARRAY_BUFFER, VBO_Vertices[0] );
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[2] );	gl.glNormalPointer(    GL2.GL_FLOAT, 0, 0 );				
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[1] );	gl.glVertexPointer( 3, GL2.GL_FLOAT, 0, 0 );							
				gl.glDrawElements( GL2.GL_QUADS, (4+4)*NPhi*NTheta, GL2.GL_UNSIGNED_INT, 0);
			}else{			
				gl.glCallList(VBO_Vertices[0]);						
			}			
		}
	}
}
