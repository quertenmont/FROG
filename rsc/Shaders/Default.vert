#version 120

varying vec4 diffuse,ambientGlobal,ambient;
varying vec3 normal,lightDir,halfVector;
varying vec2 vTexCoord;
varying float dist;

uniform float Time=999999;
	
void main(){
	vTexCoord = gl_MultiTexCoord0.xy;


	vec4 ecPos;
	vec3 aux;
		
	normal = normalize(gl_NormalMatrix * gl_Normal);
		
	/* these are the new lines of code to compute the light's direction */
	ecPos = gl_ModelViewMatrix * gl_Vertex;
	aux = vec3(gl_LightSource[0].position-ecPos);
	lightDir = normalize(aux);
	dist = length(aux);
	
	halfVector = normalize(gl_LightSource[0].halfVector.xyz);
		
	/* Compute the diffuse, ambient and globalAmbient terms */
	diffuse = gl_Color * gl_LightSource[0].diffuse;  //gl_FrontMaterial.diffuse instead of gl_Color
		
	/* The ambient terms have been separated since one of them */
	/* suffers attenuation */
	ambient = gl_Color * gl_LightSource[0].ambient;  //gl_FrontMaterial.ambient instead of gl_Color
	ambientGlobal = gl_LightModel.ambient * gl_Color;

//	gl_Position   = gl_ProjectionMatrix * gl_ModelViewMatrix * gl_Vertex;
	gl_Position   = gl_ModelViewProjectionMatrix * gl_Vertex;
	gl_FrontColor = gl_Color;

	if(length(gl_Vertex.xyz)/30>Time){ //Division by 30 to have time in ns.
		ambient.a = 0;
		ambientGlobal.a = 0;
		diffuse.a=0;
		gl_Position.z = -1;
	}

} 