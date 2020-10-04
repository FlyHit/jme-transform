package transform.shape;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

/**
 * 圆、圆弧 Mesh 参数化
 *
 * @author Administrator
 */
public class Circle extends Mesh {

	// 圆心角
	public float angle;
	// 半径
	public float radial;
	// 圆的段数
	public int radialSamples;
	// 设置圆弧时是否画出封闭边
	public boolean close;
	// 中心
	private Vector3f center;
	// 厚度(线条粗细)
	private float ply = 0.005f;

	public Circle() {
	}

	public Circle(Vector3f center, float angle, float radial, int radialSamples, boolean close) {
		super();
		this.center = center;
		this.angle = angle;
		this.radial = radial;
		this.radialSamples = radialSamples;
		this.close = close;
		updateGeometry(center, angle, radial, radialSamples, close);
	}

	/**
	 * 计算面法向量
	 *
	 * @param a 三角面点1
	 * @param b 三角面点2
	 * @param c 三角面点3
	 * @return
	 */
	private static Vector3f computeNormal(Vector3f a, Vector3f b, Vector3f c) {
		Vector3f ba = a.subtract(b); // 点b到a 向量ba
		Vector3f bc = c.subtract(b); // 点b到c 向量bc
		// 向量积 aa*bb，面法向量
		Vector3f normal = new Vector3f();
		normal.x = ba.y * bc.z - ba.z * bc.y;
		normal.y = ba.z * bc.x - ba.x * bc.z;
		normal.z = ba.x * bc.y - ba.y * bc.x;
		return normal.normalizeLocal();

	}

	@Override
	public void setMode(Mode mode) {
		// TODO Auto-generated method stub
//		super.setMode(mode);
		if (mode.equals(Mode.Lines)) {
			close = false;
			updateGeometry(center, angle, radial, radialSamples, close);
		}
		if (mode.equals(Mode.Triangles)) {
			close = true;
			updateGeometry(center, angle, radial, radialSamples, close);
		}
		if (mode.equals(Mode.Points)) {
			super.setMode(mode);
//			updateGeometry(center, angle, radial, radialSamples, true);
		}
	}

	public void updateGeometry(Vector3f center, float angle, float radial, int samples, boolean close) {
		// 由段数计算单位角度
		float deltaAngle = angle / radialSamples * 0.0174532930555555f;        //转换为单位弧度
		float currentAngle = 0f; // 角度从0以单位角度位为增量加至目标角度angle
		// 默认close为false；圆线
		float innerRadius = radial;
		float outterRadius = radial + ply;
		if (close) {
			innerRadius = 0f;
			outterRadius = radial;
		}

		Vector3f[] vertices;
		// 每个中间的曲面片段有上下左右4个面，8个三角形 24个顶点数，首尾端片段在中间基础上多1个面
		if (angle < 360f) {
			// 有首尾片段
			vertices = new Vector3f[samples * 24 + 12];
		} else {
			// 无首尾片段
			vertices = new Vector3f[samples * 24];
		}
		// 中间的单位曲面片段的顶点
		for (int i = 0; i < samples; i++) {
			float cosA = (float) Math.cos(currentAngle);
			float sinA = (float) Math.sin(currentAngle);
			currentAngle += deltaAngle;
			float cosA2 = (float) Math.cos(currentAngle);
			float sinA2 = (float) Math.sin(currentAngle);
			// 下表面片段
			vertices[24 * i] = new Vector3f(cosA * innerRadius + center.x, center.y, sinA * innerRadius + center.z);
			vertices[24 * i + 1] = new Vector3f(cosA * outterRadius + center.x, center.y,
					sinA * outterRadius + center.z);
			vertices[24 * i + 2] = new Vector3f(cosA2 * innerRadius + center.x, center.y,
					sinA2 * innerRadius + center.z);
			vertices[24 * i + 3] = new Vector3f(cosA * outterRadius + center.x, center.y,
					sinA * outterRadius + center.z);
			vertices[24 * i + 4] = new Vector3f(cosA2 * outterRadius + center.x, center.y,
					sinA2 * outterRadius + center.z);
			vertices[24 * i + 5] = new Vector3f(cosA2 * innerRadius + center.x, center.y,
					sinA2 * innerRadius + center.z);
			// 上表面片段
			vertices[24 * i + 6] = new Vector3f(
					cosA * innerRadius + center.x,
					center.y + ply,
					sinA * innerRadius + center.z
			);
			vertices[24 * i + 7] = new Vector3f(
					cosA2 * innerRadius + center.x,
					center.y + ply,
					sinA2 * innerRadius + center.z
			);
			vertices[24 * i + 8] = new Vector3f(
					cosA * outterRadius + center.x,
					center.y + ply,
					sinA * outterRadius + center.z
			);
			vertices[24 * i + 9] = new Vector3f(
					cosA * outterRadius + center.x,
					center.y + ply,
					sinA * outterRadius + center.z
			);
			vertices[24 * i + 10] = new Vector3f(
					cosA2 * innerRadius + center.x,
					center.y + ply,
					sinA2 * innerRadius + center.z
			);
			vertices[24 * i + 11] = new Vector3f(
					cosA2 * outterRadius + center.x,
					center.y + ply,
					sinA2 * outterRadius + center.z
			);
			// 内弧表面片段
			vertices[24 * i + 12] = new Vector3f(cosA2 * innerRadius + center.x, center.y,
					sinA2 * innerRadius + center.z);
			vertices[24 * i + 13] = new Vector3f(cosA2 * innerRadius + center.x, center.y + ply,
					sinA2 * innerRadius + center.z);
			vertices[24 * i + 14] = new Vector3f(cosA * innerRadius + center.x, center.y,
					sinA * innerRadius + center.z);
			vertices[24 * i + 15] = new Vector3f(cosA * innerRadius + center.x, center.y,
					sinA * innerRadius + center.z);
			vertices[24 * i + 16] = new Vector3f(cosA2 * innerRadius + center.x, center.y + ply,
					sinA2 * innerRadius + center.z);
			vertices[24 * i + 17] = new Vector3f(cosA * innerRadius + center.x, center.y + ply,
					sinA * innerRadius + center.z);
			// 外弧表面片段
			vertices[24 * i + 18] = new Vector3f(cosA * outterRadius + center.x, center.y,
					sinA * outterRadius + center.z);
			vertices[24 * i + 19] = new Vector3f(cosA * outterRadius + center.x, center.y + ply,
					sinA * outterRadius + center.z);
			vertices[24 * i + 20] = new Vector3f(cosA2 * outterRadius + center.x, center.y,
					sinA2 * outterRadius + center.z);
			vertices[24 * i + 21] = new Vector3f(cosA2 * outterRadius + center.x, center.y,
					sinA2 * outterRadius + center.z);
			vertices[24 * i + 22] = new Vector3f(cosA * outterRadius + center.x, center.y + ply,
					sinA * outterRadius + center.z);
			vertices[24 * i + 23] = new Vector3f(cosA2 * outterRadius + center.x, center.y + ply,
					sinA2 * outterRadius + center.z);
		}
		// 首尾片段面顶点
		if (angle < 360f) {
			float cosA3 = (float) Math.cos(0.0174532931f * angle);
			float sinA3 = (float) Math.sin(0.0174532931f * angle);
			// 首部横截面
			vertices[24 * samples] = new Vector3f(innerRadius + center.x, center.y, center.z);
			vertices[24 * samples + 1] = new Vector3f(innerRadius + center.x, center.y + ply, center.z);
			vertices[24 * samples + 2] = new Vector3f(outterRadius + center.x, center.y, center.z);
			vertices[24 * samples + 3] = new Vector3f(outterRadius + center.x, center.y, center.z);
			vertices[24 * samples + 4] = new Vector3f(innerRadius + center.x, center.y + ply, center.z);
			vertices[24 * samples + 5] = new Vector3f(outterRadius + center.x, center.y + ply, center.z);
			// 尾部横截面
			vertices[24 * samples + 6] = new Vector3f(cosA3 * outterRadius + center.x, center.y,
					sinA3 * outterRadius + center.z);
			vertices[24 * samples + 7] = new Vector3f(cosA3 * outterRadius + center.x, center.y + ply,
					sinA3 * outterRadius + center.z);
			vertices[24 * samples + 8] = new Vector3f(cosA3 * innerRadius + center.x, center.y,
					sinA3 * innerRadius + center.z);
			vertices[24 * samples + 9] = new Vector3f(cosA3 * innerRadius + center.x, center.y,
					sinA3 * innerRadius + center.z);
			vertices[24 * samples + 10] = new Vector3f(cosA3 * outterRadius + center.x, center.y + ply,
					sinA3 * outterRadius + center.z);
			vertices[24 * samples + 11] = new Vector3f(cosA3 * innerRadius + center.x, center.y + ply,
					sinA3 * innerRadius + center.z);
		}
		// 顶点法向量
		Vector3f[] normals = new Vector3f[vertices.length];
		for (int i = 0; i < samples; i++) {
			// 下表面片段顶点法向量
			normals[24 * i] = Vector3f.UNIT_Y.negate();
			normals[24 * i + 1] = Vector3f.UNIT_Y.negate();
			normals[24 * i + 2] = Vector3f.UNIT_Y.negate();
			normals[24 * i + 3] = Vector3f.UNIT_Y.negate();
			normals[24 * i + 4] = Vector3f.UNIT_Y.negate();
			normals[24 * i + 5] = Vector3f.UNIT_Y.negate();
			// 上表面片段顶点法向量
			normals[24 * i + 6] = Vector3f.UNIT_Y;
			normals[24 * i + 7] = Vector3f.UNIT_Y;
			normals[24 * i + 8] = Vector3f.UNIT_Y;
			normals[24 * i + 9] = Vector3f.UNIT_Y;
			normals[24 * i + 10] = Vector3f.UNIT_Y;
			normals[24 * i + 11] = Vector3f.UNIT_Y;
			// 内弧表面片段顶点法向量
			normals[24 * i + 12] = center.add(vertices[24 * i + 12].negate()).normalize();
			normals[24 * i + 13] = center.add(vertices[24 * i + 12].negate()).normalize();
			normals[24 * i + 14] = center.add(vertices[24 * i + 14].negate()).normalize();
			normals[24 * i + 15] = center.add(vertices[24 * i + 14].negate()).normalize();
			normals[24 * i + 16] = center.add(vertices[24 * i + 12].negate()).normalize();
			normals[24 * i + 17] = center.add(vertices[24 * i + 14].negate()).normalize();
			// 外弧表面片段顶点法向量
			normals[24 * i + 18] = vertices[24 * i + 18].add(center.negate()).normalize();
			normals[24 * i + 19] = vertices[24 * i + 18].add(center.negate()).normalize();
			normals[24 * i + 20] = vertices[24 * i + 20].add(center.negate()).normalize();
			normals[24 * i + 21] = vertices[24 * i + 20].add(center.negate()).normalize();
			normals[24 * i + 22] = vertices[24 * i + 18].add(center.negate()).normalize();
			normals[24 * i + 23] = vertices[24 * i + 20].add(center.negate()).normalize();

		}
		if (angle < 360f) {
			// 首部横截面 （从世界坐标X轴开始）
			Vector3f normal1 = computeNormal(vertices[24 * samples + 1], vertices[24 * samples],
					vertices[24 * samples + 2]);
			normals[24 * samples] = normal1;
			normals[24 * samples + 1] = normal1;
			normals[24 * samples + 2] = normal1;
			normals[24 * samples + 3] = normal1;
			normals[24 * samples + 4] = normal1;
			normals[24 * samples + 5] = normal1;
			// 尾部横截面
			Vector3f normal2 = computeNormal(vertices[24 * samples + 11], vertices[24 * samples + 8],
					vertices[24 * samples + 6]);
			normals[24 * samples + 6] = normal2.negate();
			normals[24 * samples + 7] = normal2.negate();
			normals[24 * samples + 8] = normal2.negate();
			normals[24 * samples + 9] = normal2.negate();
			normals[24 * samples + 10] = normal2.negate();
			normals[24 * samples + 11] = normal2.negate();
		}

		// 三角面顶点索引
		int[] indices = new int[vertices.length];
		for (int j = 0; j < indices.length; j++) {
			indices[j] = j;
		}

		// 纹理坐标
		Vector2f[] texCoords = new Vector2f[vertices.length];
		for (int i = 0; i < samples; i++) {
			// 下表面片段顶点法向量
			texCoords[24 * i] = new Vector2f(vertices[24 * i].getX() / radial, vertices[24 * i].getZ() / radial);
			texCoords[24 * i + 1] = new Vector2f(vertices[24 * i + 1].getX() / radial, vertices[24 * i + 1].getZ() / radial);
			texCoords[24 * i + 2] = new Vector2f(vertices[24 * i + 2].getX() / radial, vertices[24 * i + 2].getZ() / radial);
			texCoords[24 * i + 3] = new Vector2f(vertices[24 * i + 3].getX() / radial, vertices[24 * i + 3].getZ() / radial);
			texCoords[24 * i + 4] = new Vector2f(vertices[24 * i + 4].getX() / radial, vertices[24 * i + 4].getZ() / radial);
			texCoords[24 * i + 5] = new Vector2f(vertices[24 * i + 5].getX() / radial, vertices[24 * i + 5].getZ() / radial);
			// 上表面片段顶点法向量
			texCoords[24 * i + 6] = new Vector2f(vertices[24 * i + 6].getX() / radial, vertices[24 * i + 6].getZ() / radial);
			texCoords[24 * i + 7] = new Vector2f(vertices[24 * i + 7].getX() / radial, vertices[24 * i + 7].getZ() / radial);
			texCoords[24 * i + 8] = new Vector2f(vertices[24 * i + 8].getX() / radial, vertices[24 * i + 8].getZ() / radial);
			texCoords[24 * i + 9] = new Vector2f(vertices[24 * i + 9].getX() / radial, vertices[24 * i + 9].getZ() / radial);
			texCoords[24 * i + 10] = new Vector2f(vertices[24 * i + 10].getX() / radial, vertices[24 * i + 10].getZ() / radial);
			texCoords[24 * i + 11] = new Vector2f(vertices[24 * i + 11].getX() / radial, vertices[24 * i + 11].getZ() / radial);
		}


		setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
		setBuffer(Type.Normal, 3, BufferUtils.createFloatBuffer(normals));
		setBuffer(Type.Index, 3, indices);
//		setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoords));
		updateBound();
		updateCounts();
		setStatic();
	}

	public Vector3f getCenter() {
		return center;
	}

	public void setCenter(Vector3f center) {
		this.center = center;
	}

	public int getRadialSamples() {
		return radialSamples;
	}

	public void setRadialSamples(int radialSamples) {
		this.radialSamples = radialSamples;
	}

	public float getRadial() {
		return radial;
	}

	public void setRadial(float radial) {
		this.radial = radial;
	}

	public float getAngle() {
		return angle;
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}

	public boolean isClose() {
		return close;
	}

	public void setClose(boolean close) {
		this.close = close;
	}

	public float getPly() {
		return ply;
	}

	public void setPly(float ply) {
		this.ply = ply;
	}

}
