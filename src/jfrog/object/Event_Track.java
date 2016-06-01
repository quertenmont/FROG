
package jfrog.object;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import com.jogamp.opengl.GL2;

import jfrog.Style;

import java.lang.Math;

import com.jogamp.opengl.util.GLBuffers;

public class Event_Track extends Base{
	byte coll; float p; float pt; float chi2;	
	ArrayList<Event_Hit> hits = null;
    int nPoints;

	public int ChunkId() {	return 12110; }
	public boolean isCompactible() {return false;}    

	public String toString() {
		return String.format(
				"Event_Track [p=%s, pt=%s, chi2=%s, nHits=%s, coll=%s]",
				p, pt, chi2, hits==null ? 0 : hits.size(), coll);
	}
	
	
	public int readData(URL url, DataInputStream din, Base obj, Base parent, int bytesToRead, int fileOffset, int level) throws IOException {
		int [] BytesRead = {0};
		coll    = Factory.getByte  (din, BytesRead);
		p  		= Factory.getFloat (din, BytesRead);
		pt    	= Factory.getFloat (din, BytesRead);
		chi2   	= Factory.getFloat (din, BytesRead);
		
		BytesRead[0] += jfrog.object.Factory.read(url,din,this, bytesToRead-BytesRead[0], fileOffset+BytesRead[0], level);
		
		hits = new ArrayList<Event_Hit>();			 
		for(int d=0; d < daughters.size(); d++ ){
			Event_Hit hit = (Event_Hit)getDaughter(d);
			if(hit==null)continue;
			hits.add(hit);
		}		
		
		return BytesRead[0];
	}
	
	public ByteBuffer writeData(){		
		ByteBuffer toReturn = ByteBuffer.allocate(20);		
		Factory.putByte  (toReturn, coll);
		Factory.putFloat (toReturn, p);
		Factory.putFloat (toReturn, pt);
		Factory.putFloat (toReturn, chi2);				
		return toReturn;		
	}
	
	public float getPosX() {return hits.size()==0 ? 0 : (hits.get(0).getPosX() + hits.get(hits.size()-1).getPosX())/2.0f;}
	public float getPosY() {return hits.size()==0 ? 0 : (hits.get(0).getPosY() + hits.get(hits.size()-1).getPosY())/2.0f;}
	public float getPosZ() {return hits.size()==0 ? 0 : (hits.get(0).getPosZ() + hits.get(hits.size()-1).getPosZ())/2.0f;}	

	public void draw(GL2 gl) {					
		if(VBO_Vertices[0]==0){			 
			boolean doInterpollation = false;
			Style style = getStyle();
			if(style!=null)doInterpollation = style.interpolate;
			//if(pt<style.minPt){VBO_Vertices[0]=-1;return;}
			
			float [] positionData = null; 											 
			int [] CubeIndex    = null; 
								
			if(!doInterpollation || hits.size()<3){
				positionData = new float[hits.size()*3];											
				CubeIndex    = new int[hits.size()];
				
				for( int h=0; h < hits.size(); h++ ){
					positionData[h*3+0] = hits.get(h).x;
					positionData[h*3+1] = hits.get(h).y;
					positionData[h*3+2] = hits.get(h).z;
					CubeIndex   [h]     = (short)h;					
				}							
				nPoints = hits.size();
			}else{	// TRACK = CUBIC SPLINE
				float  CurrentLength = 0;
				float [] t = new float[hits.size()];
				t[0] = CurrentLength;
				for( int j=1; j < hits.size(); j++ ){			
					CurrentLength += Math.sqrt(Math.pow(hits.get(j-1).x - hits.get(j).x,2) + Math.pow(hits.get(j-1).y - hits.get(j).y,2) + Math.pow(hits.get(j-1).z - hits.get(j).z,2));
					t[j] = CurrentLength;
				}

				float [] ax  = new float[hits.size()];	float [] ay  = new float[hits.size()];	float [] az  = new float[hits.size()];
				float [] bx  = new float[hits.size()];	float [] by  = new float[hits.size()];	float [] bz  = new float[hits.size()];
				float [] cx  = new float[hits.size()];	float [] cy  = new float[hits.size()];	float [] cz  = new float[hits.size()];
				float [] dx  = new float[hits.size()];	float [] dy  = new float[hits.size()];	float [] dz  = new float[hits.size()];
				float [] hx  = new float[hits.size()-1];float [] hy  = new float[hits.size()-1];float [] hz  = new float[hits.size()-1];
				float [] alx = new float[hits.size()-1];float [] aly = new float[hits.size()-1];float [] alz = new float[hits.size()-1];
				float [] lx  = new float[hits.size()];	float [] ly  = new float[hits.size()];	float [] lz  = new float[hits.size()];
				float [] mux = new float[hits.size()];	float [] muy = new float[hits.size()];	float [] muz = new float[hits.size()];
				float [] Zx  = new float[hits.size()];	float [] Zy  = new float[hits.size()];	float [] Zz  = new float[hits.size()];

				for(int i=0; i < hits.size(); i++){
					ax[i] = hits.get(i).x;
					ay[i] = hits.get(i).y;
					az[i] = hits.get(i).z;
				}

				for(int i=0; i < hits.size()-1; i++){
					hx[i] = t[i+1] - t[i];
					hy[i] = t[i+1] - t[i];
					hz[i] = t[i+1] - t[i];
				}
				
				for(int i=1; i < hits.size()-1; i++){						
					alx[i] = (3*(ax[i+1] - ax[i])/hx[i]) - (3*(ax[i] - ax[i-1])/hx[i-1]);
					aly[i] = (3*(ay[i+1] - ay[i])/hy[i]) - (3*(ay[i] - ay[i-1])/hy[i-1]);
					alz[i] = (3*(az[i+1] - az[i])/hz[i]) - (3*(az[i] - az[i-1])/hz[i-1]);
				}

				lx[0]=1;	mux[0]=0;	Zx[0]=0;
				ly[0]=1;	muy[0]=0;	Zy[0]=0;
				lz[0]=1;	muz[0]=0;	Zz[0]=0;

				for(int i=1; i < hits.size()-1; i++){			
					lx[i] = 2*(t[i+1] - t[i-1]) - hx[i-1]*mux[i-1];
					ly[i] = 2*(t[i+1] - t[i-1]) - hy[i-1]*muy[i-1];
					lz[i] = 2*(t[i+1] - t[i-1]) - hz[i-1]*muz[i-1];

					mux[i] = hx[i] / lx[i];
					muy[i] = hy[i] / ly[i];
					muz[i] = hz[i] / lz[i];

					Zx[i]  = (alx[i] - hx[i-1]*Zx[i-1])/lx[i];
					Zy[i]  = (aly[i] - hy[i-1]*Zy[i-1])/ly[i];
					Zz[i]  = (alz[i] - hz[i-1]*Zz[i-1])/lz[i];			
				}

				lx[hits.size()-1]=1;	cx[hits.size()-1]=0;	Zx[hits.size()-1]=0;
				ly[hits.size()-1]=1;	cy[hits.size()-1]=0;	Zy[hits.size()-1]=0;
				lz[hits.size()-1]=1;	cz[hits.size()-1]=0;	Zz[hits.size()-1]=0;

				for(int j=(int)hits.size()-2; j >= 0; j--){
					cx[j] = Zx[j] - mux[j]*cx[j+1];
					cy[j] = Zy[j] - muy[j]*cy[j+1];
					cz[j] = Zz[j] - muz[j]*cz[j+1];

					bx[j] = ((ax[j+1] - ax[j])/hx[j]) - (hx[j]*(cx[j+1] + 2*cx[j])/3.0f);
					by[j] = ((ay[j+1] - ay[j])/hy[j]) - (hy[j]*(cy[j+1] + 2*cy[j])/3.0f);
					bz[j] = ((az[j+1] - az[j])/hz[j]) - (hz[j]*(cz[j+1] + 2*cz[j])/3.0f);

					dx[j] = (cx[j+1] - cx[j])/(3*hx[j]);
					dy[j] = (cy[j+1] - cy[j])/(3*hy[j]);
					dz[j] = (cz[j+1] - cz[j])/(3*hz[j]);
				}
				
				
				positionData = new float[(hits.size()+101)*3];											
				CubeIndex    = new int[(hits.size()+101)];				
			
				int Index=0;
				for( float L=0; L < CurrentLength; L+= CurrentLength/100.0f ){
					int I = -1;
					for(int i=0;i<hits.size()-1 && I<0;i++){
						if(L>=t[i] && L<=t[i+1])I=i;
					}
					if(I==-1){continue;	}

					float L1 = L-t[I];
					float L2 = L1*L1;
					float L3 = L1*L2;

					positionData[Index*3+0] = ax[I] + bx[I]*L1 + cx[I]*L2 + dx[I]*L3;
					positionData[Index*3+1] = ay[I] + by[I]*L1 + cy[I]*L2 + dy[I]*L3;
					positionData[Index*3+2] = az[I] + bz[I]*L1 + cz[I]*L2 + dz[I]*L3;
					CubeIndex   [Index]     = (short)Index;						 
					Index++;
				}			
				nPoints = Index;
			}
			
			
			IntBuffer CubeIndexBuf    = GLBuffers.newDirectIntBuffer(CubeIndex);
			FloatBuffer positionDataBuf = GLBuffers.newDirectFloatBuffer(positionData);					
			FloatBuffer normalDataBuf   = GLBuffers.newDirectFloatBuffer(positionData.length);

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
				gl.glDrawElements   (    GL2.GL_LINE_STRIP, nPoints, GL2.GL_UNSIGNED_INT, CubeIndexBuf);
				gl.glEndList();			
			}
		}else if(VBO_Vertices[0]>0){
			if(jfrog.Common.support_VBO){
				gl.glBindBuffer( GL2.GL_ELEMENT_ARRAY_BUFFER, VBO_Vertices[0] );
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[2] );	gl.glNormalPointer  (    GL2.GL_FLOAT, 0, 0 );							
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[1] );	gl.glVertexPointer  ( 3, GL2.GL_FLOAT, 0, 0 );				
				gl.glDrawElements( GL2.GL_LINE_STRIP,  nPoints, GL2.GL_UNSIGNED_INT, 0);								
			}else{			
				gl.glCallList(VBO_Vertices[0]);						
			}
		}
	}	
	
		
	
}
