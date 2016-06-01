#version 120

varying vec4 diffuse,ambientGlobal, ambient;
varying vec3 normal,lightDir,halfVector;
varying float dist;
varying vec2 vTexCoord;

uniform sampler2D texture0;
uniform int	  pickingMode=0;

	
void main()
{
	if(pickingMode!=0){
		gl_FragColor = gl_Color;
		return;
	}

	vec3 n,halfV,viewV,ldir;
	float NdotL,NdotHV;
	float att;

	//gl_FragColor = gl_Color;
	//gl_FragColor = vec4(0,1,1,1);		
	gl_FragColor = ambientGlobal  + ambient;

	// a fragment shader can't write a varying variable, hence we need
	//a new variable to store the normalized interpolated normal
	n = normalize(normal);
		
	//compute the dot product between normal and lightdir (normalized in the vtx shader)
	NdotL = abs(dot(n,lightDir));

	if(NdotL > 0.0) {
		gl_FragColor += diffuse * NdotL;
	}

	//Special treatement for texture mapping (only if coordinates are != 0)
	if(length(vTexCoord) > 0){	
		gl_FragColor  = gl_FragColor*texture2D(texture0, vTexCoord);
	}

	//Special treatement for point sprite
	if(length(gl_PointCoord) > 0){
		gl_FragColor  = gl_FragColor*texture2D(texture0, gl_PointCoord);
	}

}
