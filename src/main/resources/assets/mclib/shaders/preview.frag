#version 120

/**
 * Preview shader
 *
 * This shader is responsible for previewing filters provided by
 * the multi-skin system
 */

uniform sampler2D texture;
uniform vec2 size;
uniform int pixelate;
uniform vec4 color;

float mod(float a, float b)
{
    return a - b * floor(a/b);
}

void main()
{
    vec2 coord = gl_TexCoord[0].xy * size;

    coord.x = floor(coord.x);
    coord.y = floor(coord.y);

    coord.x -= mod(coord.x, pixelate);
    coord.y -= mod(coord.y, pixelate);
    coord /= size;

    gl_FragColor = texture2D(texture, coord) * color;
}