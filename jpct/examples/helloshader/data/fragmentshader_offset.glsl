varying vec3 lightVec;
varying vec3 eyeVec;
varying vec2 texCoord;

uniform sampler2D textureUnit0;
uniform sampler2D textureUnit1;

uniform vec4 ambientColor;

uniform float invRadius;
uniform float heightScale;

void main ()
{
	vec4 vAmbient = gl_LightSource[0].ambient * gl_FrontMaterial.ambient;
	vec3 vVec = normalize(eyeVec);
	
	float height = texture2D(textureUnit1, texCoord).a;
	vec2 offset = vVec.xy * (height * 2.0 - 1.0) *heightScale;
	vec2 newTexCoord = texCoord + offset;
	
	vec4 base = texture2D(textureUnit0, newTexCoord);
	vec3 bump = normalize(texture2D(textureUnit1, newTexCoord).xyz * 2.0 - 1.0);
	
	
	float distSqr = dot(lightVec, lightVec);
	float att = clamp(1.0 - invRadius * sqrt(distSqr), 0.0, 1.0);
	vec3 lVec = lightVec * inversesqrt(distSqr);

	float diffuse = max(dot(lVec, bump), 0.0);
	vec4 vDiffuse = gl_LightSource[0].diffuse * diffuse;	

	float specular = pow(clamp(dot(reflect(-lVec, bump), vVec), 0.0, 1.0), 0.85);
	vec4 vSpecular = gl_LightSource[0].specular * specular;	
	
	gl_FragColor = (vAmbient*base + vDiffuse*base + vSpecular) * att*2.0;
}