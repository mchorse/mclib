package mchorse.mclib.utils.shaders;

import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL20;

import java.nio.charset.Charset;

public class Shader
{
	public int programId = -1;

	public void compile(String vertexPath, String fragmentPath, boolean code) throws Exception
	{
		if (this.programId != -1)
		{
			return;
		}

		this.programId = GL20.glCreateProgram();

		int vertex = this.createShader(vertexPath, GL20.GL_VERTEX_SHADER, code);
		int fragment = this.createShader(fragmentPath, GL20.GL_FRAGMENT_SHADER, code);

		GL20.glLinkProgram(this.programId);

		if (GL20.glGetProgrami(this.programId, GL20.GL_LINK_STATUS) == 0)
		{
			throw new Exception("Error linking Shader code: " + GL20.glGetProgramInfoLog(this.programId, 1024));
		}

		if (vertex != 0)
		{
			GL20.glDetachShader(this.programId, vertex);
		}

		if (fragment != 0)
		{
			GL20.glDetachShader(this.programId, fragment);
		}

		GL20.glValidateProgram(this.programId);

		if (GL20.glGetProgrami(this.programId, GL20.GL_VALIDATE_STATUS) == 0)
		{
			System.err.println("Warning validating Shader code: " + GL20.glGetProgramInfoLog(this.programId, 1024));
		}

		GL20.glDeleteShader(vertex);
		GL20.glDeleteShader(fragment);
	}

	/**
	 * Shared code for compiling a shader program's shader
	 */
	protected int createShader(String shaderPath, int shaderType, boolean code) throws Exception
	{
		String shaderCode = code ? shaderPath : IOUtils.toString(this.getClass().getResourceAsStream(shaderPath), Charset.defaultCharset());
		int shaderId = GL20.glCreateShader(shaderType);

		if (shaderId == 0)
		{
			throw new Exception("Error creating shader. Type: " + shaderType);
		}

		GL20.glShaderSource(shaderId, shaderCode);
		GL20.glCompileShader(shaderId);

		if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == 0)
		{
			throw new Exception("Error compiling Shader code: " + GL20.glGetShaderInfoLog(shaderId, 1024));
		}

		GL20.glAttachShader(this.programId, shaderId);

		return shaderId;
	}

	public void bind()
	{
		GL20.glUseProgram(this.programId);
	}

	public void unbind()
	{
		GL20.glUseProgram(0);
	}
}