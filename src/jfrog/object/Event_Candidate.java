
package jfrog.object;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jogamp.opengl.GL2;

import com.jogamp.opengl.util.GLBuffers;

public class Event_Candidate extends Base{
	long pdgId;
	float p, eta, phi;
	
	public int ChunkId() {	return 15100; }
	public boolean isCompactible() {return true;}    

	public String toString() {
		return String.format(
				"Event_Candidate [pdgId=%s, p=%s, eta=%s, phi=%s]", pdgId, p,
				eta, phi);
	}
	
	
	public int readData(URL url, DataInputStream din, Base obj, Base parent, int bytesToRead, int fileOffset, int level) throws IOException {
		int [] BytesRead = {0};
		pdgId   = Factory.getUInt  (din, BytesRead);
		p  		= Factory.getFloat (din, BytesRead);
		eta    	= Factory.getFloat (din, BytesRead);
		phi  	= Factory.getFloat (din, BytesRead);
		
		return BytesRead[0];
	}
	
	public ByteBuffer writeData(){		
		ByteBuffer toReturn = ByteBuffer.allocate(16);		
		Factory.putUInt  (toReturn, pdgId);
		Factory.putFloat (toReturn, p);
		Factory.putFloat (toReturn, eta);
		Factory.putFloat (toReturn, phi);				
		return toReturn;		
	}
	


	public void draw(GL2 gl) {					
		if(VBO_Vertices[0]==0){
			float [] positionData = new float[6*(4+3+3+3)*3]; 											
			float [] normalData   = new float[6*(4+3+3+3)*3]; 
			int  [] CubeIndex  	= new int[6*(4+3+3+3)  ]; 

			float dphi 	  = (float)Math.PI/3;
			float L1      = (float)Math.log(1+p)*15;			
			float L2      = L1*0.8f;
			float Radius1 = L1*0.05f;
			float Radius2 = Radius1 / 2;
		
			int Index=0;
			for(float phi=0; phi<=6.2831f;phi+=dphi){
				positionData[Index+0 ] = (float)(Radius2*Math.cos(phi));		
				positionData[Index+1 ] = (float)(Radius2*Math.sin(phi));
				positionData[Index+2 ] = 0;
				positionData[Index+3 ] = (float)(Radius2*Math.cos(phi+dphi));
				positionData[Index+4 ] = (float)(Radius2*Math.sin(phi+dphi));
				positionData[Index+5 ] = 0;
				positionData[Index+6 ] = (float)(Radius2*Math.cos(phi+dphi));
				positionData[Index+7 ] = (float)(Radius2*Math.sin(phi+dphi));
				positionData[Index+8 ] = L2;
				positionData[Index+9 ] = (float)(Radius2*Math.cos(phi));		
				positionData[Index+10] = (float)(Radius2*Math.sin(phi));
				positionData[Index+11] = L2;
				Index+=12;
			}
			for(float phi=0; phi<=6.2831f;phi+=dphi){
				positionData[Index+0 ] = 0;
				positionData[Index+1 ] = 0;
				positionData[Index+2 ] = L1;
				positionData[Index+3 ] = (float)(Radius1*Math.cos(phi));
				positionData[Index+4 ] = (float)(Radius1*Math.sin(phi));
				positionData[Index+5 ] = L2;
				positionData[Index+6 ] = (float)(Radius1*Math.cos(phi+dphi));
				positionData[Index+7 ] = (float)(Radius1*Math.sin(phi+dphi));
				positionData[Index+8 ] = L2;
				Index+=9;
			}
			for(float phi=0; phi<=6.2831f;phi+=dphi){
				positionData[Index+0 ] = 0;
				positionData[Index+1 ] = 0;
				positionData[Index+2 ] = L2;
				positionData[Index+3 ] = (float)(Radius1*Math.cos(phi+dphi));
				positionData[Index+4 ] = (float)(Radius1*Math.sin(phi+dphi));
				positionData[Index+5 ] = L2;
				positionData[Index+6 ] = (float)(Radius1*Math.cos(phi));
				positionData[Index+7 ] = (float)(Radius1*Math.sin(phi));
				positionData[Index+8 ] = L2;
				Index+=9;
			}
			for(float phi=0; phi<=6.2831f;phi+=dphi){
				positionData[Index+0 ] = 0;
				positionData[Index+1 ] = 0;
				positionData[Index+2 ] = 0;
				positionData[Index+3 ] = (float)(Radius2*Math.cos(phi+dphi));
				positionData[Index+4 ] = (float)(Radius2*Math.sin(phi+dphi));
				positionData[Index+5 ] = 0;
				positionData[Index+6 ] = (float)(Radius2*Math.cos(phi));
				positionData[Index+7 ] = (float)(Radius2*Math.sin(phi));
				positionData[Index+8 ] = 0;
				Index+=9;
			}

			gl.glPushMatrix();
			gl.glLoadIdentity();
			gl.glRotatef(57.29f*phi                         ,0,0,1);
			gl.glRotatef(57.29f*jfrog.Common.Coord_EtaToTheta(eta),0,1,0);	
			TransformVerticesArray(gl, positionData);
			gl.glPopMatrix();
			
			for(short i=0;i<CubeIndex.length;i++){CubeIndex[i] = i;}			
			SetNormalArray(positionData, CubeIndex,         0, 6*4, 4, normalData);
			SetNormalArray(positionData, CubeIndex, 6*      4, 6*3, 3, normalData);						
			SetNormalArray(positionData, CubeIndex, 6*(  4+3), 6*3, 3, normalData);					
			SetNormalArray(positionData, CubeIndex, 6*(4+3+3), 6*3, 3, normalData);

			IntBuffer CubeIndexBuf    = GLBuffers.newDirectIntBuffer(CubeIndex);
			FloatBuffer positionDataBuf = GLBuffers.newDirectFloatBuffer(positionData);					
			FloatBuffer normalDataBuf   = GLBuffers.newDirectFloatBuffer(normalData);

			if(jfrog.Common.support_VBO){					
				gl.glGenBuffers( 4, VBO_Vertices, 0);								// Get A Valid Name
				gl.glBindBuffer( GL2.GL_ELEMENT_ARRAY_BUFFER, VBO_Vertices[0] );	// Bind The Buffer
				gl.glBufferData( GL2.GL_ELEMENT_ARRAY_BUFFER, CubeIndexBuf.capacity() * 4,CubeIndexBuf, GL2.GL_STATIC_DRAW );				
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[1] );			// Bind The Buffer
				gl.glBufferData( GL2.GL_ARRAY_BUFFER, positionDataBuf.capacity() * 4, positionDataBuf, GL2.GL_STATIC_DRAW );
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[2] );			// Bind The Buffer
				gl.glBufferData( GL2.GL_ARRAY_BUFFER, normalDataBuf.capacity() * 4, normalDataBuf, GL2.GL_STATIC_DRAW );							
			}else{
				VBO_Vertices[0] = gl.glGenLists(1);			
				gl.glNewList(VBO_Vertices[0], GL2.GL_COMPILE_AND_EXECUTE);

				gl.glNormalPointer  (    GL2.GL_FLOAT, 0, normalDataBuf );				
				gl.glVertexPointer  ( 3, GL2.GL_FLOAT, 0, positionDataBuf );			
				gl.glDrawElements   (    GL2.GL_QUADS    , 6*4, GL2.GL_UNSIGNED_INT, CubeIndexBuf); CubeIndexBuf.position(6*4);
				gl.glDrawElements   (    GL2.GL_TRIANGLES, 6*3, GL2.GL_UNSIGNED_INT, CubeIndexBuf); CubeIndexBuf.position(6*7);				
				gl.glDrawElements   (    GL2.GL_TRIANGLES, 6*3, GL2.GL_UNSIGNED_INT, CubeIndexBuf); CubeIndexBuf.position(6*10);
				gl.glDrawElements   (    GL2.GL_TRIANGLES, 6*3, GL2.GL_UNSIGNED_INT, CubeIndexBuf);				
				gl.glEndList();			
			}
		}else if(VBO_Vertices[0]>0){
			if(jfrog.Common.support_VBO){
				gl.glBindBuffer( GL2.GL_ELEMENT_ARRAY_BUFFER, VBO_Vertices[0] );
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[2] );	gl.glNormalPointer  (    GL2.GL_FLOAT, 0, 0 );							
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[1] );	gl.glVertexPointer  ( 3, GL2.GL_FLOAT, 0, 0 );				
				gl.glDrawElements   (    GL2.GL_QUADS    , 6*4, GL2.GL_UNSIGNED_INT, 0);	
				gl.glDrawElements   (    GL2.GL_TRIANGLES, 6*3, GL2.GL_UNSIGNED_INT, 2*6*4);				
				gl.glDrawElements   (    GL2.GL_TRIANGLES, 6*3, GL2.GL_UNSIGNED_INT, 2*6*7);
				gl.glDrawElements   (    GL2.GL_TRIANGLES, 6*3, GL2.GL_UNSIGNED_INT, 2*6*10);								
			}else{			
				gl.glCallList(VBO_Vertices[0]);						
			}
		}
	}	
	
		
	
}
