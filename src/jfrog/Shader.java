package jfrog;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES2;

public class Shader {

	static public String loadSource(String shaderPath){
		String code = "";
		
		try {		
			URL url = jfrog.Common.GetURL(shaderPath);		
			if(url==null){
				System.out.printf("can't find shader file: %s\n", shaderPath);
				return code;
			}
			InputStream in = url.openStream();
			Reader din = new InputStreamReader(new BufferedInputStream(in));		
			BufferedReader reader = new BufferedReader(din);

			while (true){
				String Line = reader.readLine();
				if(Line==null)break;
				code += Line + "\n";
			}
			reader.close();			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return code;
	}
	
	
	
	static public boolean loadShader(GL2 gl, int shaderIds[], int shaderType, String shaderPath, boolean isPath){
		
		int shaderIndex = 0;
		if(shaderType==GL2.GL_FRAGMENT_SHADER){
			shaderIndex = 2;			
		}else if(shaderType == GL2.GL_VERTEX_SHADER){
			shaderIndex = 1;			
		}else{
			System.out.printf("Unknown shader Type\n");
			return false;
		}
		
		String code;
		if(isPath){
			code = loadSource(shaderPath);
		}else{
			code = shaderPath;
		}
		if(code.equals(""))return false;		
		
		if(shaderIds[shaderIndex]>0)gl.glDeleteShader(shaderIds[shaderIndex]);
		shaderIds[shaderIndex] = gl.glCreateShader(shaderType);
						
		gl.glShaderSource(shaderIds[shaderIndex], 1, new String []{code}, new int []{code.length()}, 0);
		gl.glCompileShader(shaderIds[shaderIndex]);		
	
		int [] compileStatus = {0};
	    gl.glGetShaderiv(shaderIds[shaderIndex], GL2ES2.GL_COMPILE_STATUS, compileStatus, 0);
	    if(compileStatus[0] != GL2.GL_TRUE){
	    	int [] infoLength = {0};
	    	gl.glGetShaderiv(shaderIds[shaderIndex], GL2ES2.GL_INFO_LOG_LENGTH, infoLength, 0);
	    	
	    	byte [] log = new byte[infoLength[0]];
	    	gl.glGetShaderInfoLog(shaderIds[shaderIndex], infoLength[0], infoLength, 0, log, 0);
	    	System.out.printf("compillation problem with shader (%s):\n",shaderPath);
	    	for(int i=0;i<infoLength[0];i++){System.out.printf("%c", log[i]);}System.out.printf("\n");
	    	return false;
	    }				
		return true;
	}
	
	static public void loadShaderProgram(GL2 gl, int shaderIds[], String vertexShaderPath, String fragmentShaderPath, boolean isPath){
		if(!loadShader(gl, shaderIds, GL2.GL_VERTEX_SHADER  , vertexShaderPath, isPath))return;
		if(!loadShader(gl, shaderIds, GL2.GL_FRAGMENT_SHADER, fragmentShaderPath, isPath))return;
		
		if(shaderIds[0]>0)gl.glDeleteProgram(shaderIds[0]);
		
		shaderIds[0] = gl.glCreateProgram();
		gl.glAttachShader(shaderIds[0], shaderIds[1]);
		gl.glAttachShader(shaderIds[0], shaderIds[2]);		
		gl.glLinkProgram(shaderIds[0]);
		
		int [] linkStatus = {0};
	    gl.glGetProgramiv(shaderIds[0], GL2ES2.GL_LINK_STATUS, linkStatus, 0);
	    if(linkStatus[0] != GL2.GL_TRUE){
	    	int [] infoLength = {0};
	    	gl.glGetProgramiv(shaderIds[0], GL2ES2.GL_INFO_LOG_LENGTH, infoLength, 0);
	    	
	    	byte [] log = new byte[infoLength[0]];
	    	gl.glGetProgramInfoLog(shaderIds[0], infoLength[0], infoLength, 0, log, 0);
	    	System.out.printf("linking problem with shader program:\n");
	    	for(int i=0;i<infoLength[0];i++){System.out.printf("%c", log[i]);}System.out.printf("\n");
	    	gl.glDeleteProgram(shaderIds[0]);
	    	return;
	    }/*else{
	    	int [] infoLength = {0};
	    	gl.glGetProgramiv(shaderIds[0], GL2ES2.GL_INFO_LOG_LENGTH, infoLength, 0);
	    	
	    	byte [] log = new byte[infoLength[0]];
	    	gl.glGetProgramInfoLog(shaderIds[0], infoLength[0], infoLength, 0, log, 0);
	    	System.out.printf("linking warning with shader program:\n");
	    	for(int i=0;i<infoLength[0];i++){System.out.printf("%c", log[i]);}System.out.printf("\n");	    	
	    }*/
	}
	
	static public void sendUniform1f(GL2ES2 gl, int shaderId, String name, float value){
		int loc = gl.glGetUniformLocation(shaderId, name);
		if(loc<0){System.out.printf("shader variable %s is not found for program %d\n", name, shaderId); return;}
		gl.glUniform1f(loc, value);		 		
	}
	
	static public void sendUniform1i(GL2ES2 gl, int shaderId, String name, int value){
		int loc = gl.glGetUniformLocation(shaderId, name);
		if(loc<0){System.out.printf("shader variable %s is not found for program %d\n", name, shaderId); return;}
		gl.glUniform1i(loc, value);		 		
	}	
	
}
