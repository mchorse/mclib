#version 120

/**
 * Preview shader
 *
 * This shader is responsible for previewing filters provided by
 * the multi-skin system
 */

uniform sampler2D texture;
uniform sampler2D texture_background;
uniform vec2 size;
/* r = pixelate, b = erase, g = not used, a = not used */
uniform vec4 filters;
uniform vec4 color;

float mod(float a, float b)
{
    return a - b * floor(a / b);
}

void main()
{
    vec2 coord = gl_TexCoord[0].xy * size;

    coord.x = floor(coord.x);
    coord.y = floor(coord.y);

    int pixelate = int(filters.r);
    int erase = int(filters.g);

    if (erase == 1)
    {
        coord.x = mod(coord.x, 16);
        coord.y = mod(coord.y, 16) + 240;
        coord /= vec2(256, 256);

        gl_FragColor = (texture2D(texture, gl_TexCoord[0].xy).a > 0.6 ? 1 : 0) * texture2D(texture_background, coord);
    }
    else
    {
        coord.x -= mod(coord.x, pixelate);
        coord.y -= mod(coord.y, pixelate);
        coord /= size;

        gl_FragColor = texture2D(texture, coord) * color;
    }
}