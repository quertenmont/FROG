varying vec4 diffuse,ambientGlobal,ambient;
varying vec3 normal,lightDir,halfVector;
varying vec2 vTexCoord;
varying float dist;

uniform float Time=1000000;
	
void main(){
	vTexCoord = gl_MultiTexCoord0;

	vec4 ecPos;
	vec3 aux;
		
	normal = normalize(gl_NormalMatrix * gl_Normal);
		
	ecPos = gl_ModelViewMatrix * gl_Vertex;
	aux = vec3(gl_LightSource[0].position-ecPos);
	lightDir = normalize(aux);
	dist = length(aux);
	
	halfVector = normalize(gl_LightSource[0].halfVector.xyz);
		
	diffuse = gl_FrontMaterial.diffuse * gl_LightSource[0].diffuse;
		
	ambient = gl_FrontMaterial.ambient * gl_LightSource[0].ambient;
	ambientGlobal = gl_LightModel.ambient * gl_FrontMaterial.ambient;

	gl_Vertex.y = length(gl_Vertex.xy);
	gl_Position   = gl_ModelViewProjectionMatrix * gl_Vertex;
	gl_FrontColor = gl_Color;

	if(length(gl_Vertex.xyz)/30>Time){ //Division by 30 to have time in ns.
		ambient.a = 0;
		ambientGlobal.a = 0;
		diffuse.a=0;
		gl_Position.z = -1;
	}


} 
