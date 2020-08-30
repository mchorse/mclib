#version 120

varying vec2 pass_coord;

void main()
{
    gl_Position = ftransform();

    gl_TexCoord[0] = gl_MultiTexCoord0;
}