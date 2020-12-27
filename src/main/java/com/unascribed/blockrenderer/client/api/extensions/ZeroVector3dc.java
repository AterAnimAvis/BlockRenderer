package com.unascribed.blockrenderer.client.api.extensions;

import com.unascribed.blockrenderer.client.api.vendor.joml._Access;
import com.unascribed.blockrenderer.client.api.vendor.joml.Math;
import com.unascribed.blockrenderer.client.api.vendor.joml.Matrix3dc;
import com.unascribed.blockrenderer.client.api.vendor.joml.Matrix3fc;
import com.unascribed.blockrenderer.client.api.vendor.joml.Matrix3x2dc;
import com.unascribed.blockrenderer.client.api.vendor.joml.Matrix3x2fc;
import com.unascribed.blockrenderer.client.api.vendor.joml.Matrix4dc;
import com.unascribed.blockrenderer.client.api.vendor.joml.Matrix4fc;
import com.unascribed.blockrenderer.client.api.vendor.joml.Matrix4x3dc;
import com.unascribed.blockrenderer.client.api.vendor.joml.Matrix4x3fc;
import com.unascribed.blockrenderer.client.api.vendor.joml.Quaterniond;
import com.unascribed.blockrenderer.client.api.vendor.joml.Quaterniondc;
import com.unascribed.blockrenderer.client.api.vendor.joml.Vector3d;
import com.unascribed.blockrenderer.client.api.vendor.joml.Vector3dc;
import com.unascribed.blockrenderer.client.api.vendor.joml.Vector3f;
import com.unascribed.blockrenderer.client.api.vendor.joml.Vector3fc;
import com.unascribed.blockrenderer.client.api.vendor.joml.Vector3i;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.text.NumberFormat;

public class ZeroVector3dc implements Vector3dc {

  public static ZeroVector3dc INSTANCE = new ZeroVector3dc();

  private ZeroVector3dc() {}

  @Override
  public double x() {
    return 0;
  }

  @Override
  public double y() {
    return 0;
  }

  @Override
  public double z() {
    return 0;
  }

  @Override
  public ByteBuffer get(ByteBuffer buffer) {
    return get(buffer.position(), buffer);
  }

  @Override
  public ByteBuffer get(int index, ByteBuffer buffer) {
    buffer.putDouble(index, 0).putDouble(index+8, 0).putDouble(index+16, 0);
    return buffer;
  }

  @Override
  public DoubleBuffer get(DoubleBuffer buffer) {
    return get(buffer.position(), buffer);
  }

  @Override
  public DoubleBuffer get(int index, DoubleBuffer buffer) {
    buffer.put(index, 0).put(index+1, 0).put(index+2, 0);
    return buffer;
  }

  @Override
  public FloatBuffer get(FloatBuffer buffer) {
    return get(buffer.position(), buffer);
  }

  @Override
  public FloatBuffer get(int index, FloatBuffer buffer) {
    buffer.put(index, 0).put(index+1, 0).put(index+2, 0);
    return buffer;
  }

  @Override
  public ByteBuffer getf(ByteBuffer buffer) {
    return getf(buffer.position(), buffer);
  }

  @Override
  public ByteBuffer getf(int index, ByteBuffer buffer) {
    buffer.putFloat(index, 0).putFloat(index+4, 0).putFloat(index+8, 0);
    return buffer;
  }

  @Override
  public Vector3d sub(Vector3dc v, Vector3d dest) {
    dest.x = -v.x();
    dest.y = -v.y();
    dest.z = -v.z();
    return dest;
  }

  @Override
  public Vector3d sub(Vector3fc v, Vector3d dest) {
    dest.x = -v.x();
    dest.y = -v.y();
    dest.z = -v.z();
    return dest;
  }

  @Override
  public Vector3d sub(double x, double y, double z, Vector3d dest) {
    dest.x = -x;
    dest.y = -y;
    dest.z = -z;
    return dest;
  }

  @Override
  public Vector3d add(Vector3dc v, Vector3d dest) {
    dest.x = v.x();
    dest.y = v.y();
    dest.z = v.z();
    return dest;
  }

  @Override
  public Vector3d add(Vector3fc v, Vector3d dest) {
    dest.x = v.x();
    dest.y = v.y();
    dest.z = v.z();
    return dest;
  }

  @Override
  public Vector3d add(double x, double y, double z, Vector3d dest) {
    dest.x = x;
    dest.y = y;
    dest.z = z;
    return dest;
  }

  @Override
  public Vector3d fma(Vector3dc a, Vector3dc b, Vector3d dest) {
    dest.x = a.x() * b.x();
    dest.y = a.y() * b.y();
    dest.z = a.z() * b.z();
    return dest;
  }

  @Override
  public Vector3d fma(double a, Vector3dc b, Vector3d dest) {
    dest.x = a * b.x();
    dest.y = a * b.y();
    dest.z = a * b.z();
    return dest;
  }

  @Override
  public Vector3d fma(Vector3dc a, Vector3fc b, Vector3d dest) {
    dest.x = a.x() * b.x();
    dest.y = a.y() * b.y();
    dest.z = a.z() * b.z();
    return dest;
  }

  @Override
  public Vector3d fma(Vector3fc a, Vector3fc b, Vector3d dest) {
    dest.x = a.x() * b.x();
    dest.y = a.y() * b.y();
    dest.z = a.z() * b.z();
    return dest;
  }

  @Override
  public Vector3d fma(double a, Vector3fc b, Vector3d dest) {
    dest.x = a * b.x();
    dest.y = a * b.y();
    dest.z = a * b.z();
    return dest;
  }

  @Override
  public Vector3d mulAdd(Vector3dc a, Vector3dc b, Vector3d dest) {
    dest.x = b.x();
    dest.y = b.y();
    dest.z = b.z();
    return dest;
  }

  @Override
  public Vector3d mulAdd(double a, Vector3dc b, Vector3d dest) {
    dest.x = b.x();
    dest.y = b.y();
    dest.z = b.z();
    return dest;
  }

  @Override
  public Vector3d mulAdd(Vector3fc a, Vector3dc b, Vector3d dest) {
    dest.x = b.x();
    dest.y = b.y();
    dest.z = b.z();
    return dest;
  }

  @Override
  public Vector3d mul(Vector3fc v, Vector3d dest) {
    return get(dest);
  }

  @Override
  public Vector3d mul(Vector3dc v, Vector3d dest) {
    return get(dest);
  }

  @Override
  public Vector3d div(Vector3fc v, Vector3d dest) {
    return get(dest);
  }

  @Override
  public Vector3d div(Vector3dc v, Vector3d dest) {
    return get(dest);
  }

  @Override
  public Vector3d mulProject(Matrix4dc mat, double w, Vector3d dest) {
    return get(dest);
  }

  @Override
  public Vector3d mulProject(Matrix4dc mat, Vector3d dest) {
    return get(dest);
  }

  @Override
  public Vector3d mulProject(Matrix4fc mat, Vector3d dest) {
    return get(dest);
  }

  @Override
  public Vector3d mul(Matrix3dc mat, Vector3d dest) {
    return get(dest);
  }

  @Override
  public Vector3d mul(Matrix3fc mat, Vector3d dest) {
    return get(dest);
  }

  @Override
  public Vector3d mul(Matrix3x2dc mat, Vector3d dest) {
    return get(dest);
  }

  @Override
  public Vector3d mul(Matrix3x2fc mat, Vector3d dest) {
    return get(dest);
  }

  @Override
  public Vector3d mulTranspose(Matrix3dc mat, Vector3d dest) {
    return get(dest);
  }

  @Override
  public Vector3d mulTranspose(Matrix3fc mat, Vector3d dest) {
    return get(dest);
  }

  @Override
  public Vector3d mulPosition(Matrix4dc mat, Vector3d dest) {
    dest.x = mat.m30();
    dest.y = mat.m31();
    dest.z = mat.m32();
    return dest;
  }

  @Override
  public Vector3d mulPosition(Matrix4fc mat, Vector3d dest) {
    dest.x = mat.m30();
    dest.y = mat.m31();
    dest.z = mat.m32();
    return dest;
  }

  @Override
  public Vector3d mulPosition(Matrix4x3dc mat, Vector3d dest) {
    dest.x = mat.m30();
    dest.y = mat.m31();
    dest.z = mat.m32();
    return dest;
  }

  @Override
  public Vector3d mulPosition(Matrix4x3fc mat, Vector3d dest) {
    dest.x = mat.m30();
    dest.y = mat.m31();
    dest.z = mat.m32();
    return dest;
  }

  @Override
  public Vector3d mulTransposePosition(Matrix4dc mat, Vector3d dest) {
    dest.x = mat.m03();
    dest.y = mat.m13();
    dest.z = mat.m23();
    return dest;
  }

  @Override
  public Vector3d mulTransposePosition(Matrix4fc mat, Vector3d dest) {
    dest.x = mat.m03();
    dest.y = mat.m13();
    dest.z = mat.m23();
    return dest;
  }

  @Override
  public double mulPositionW(Matrix4fc mat, Vector3d dest) {
    dest.x = mat.m30();
    dest.y = mat.m31();
    dest.z = mat.m32();
    return mat.m33();
  }

  @Override
  public double mulPositionW(Matrix4dc mat, Vector3d dest) {
    dest.x = mat.m30();
    dest.y = mat.m31();
    dest.z = mat.m32();
    return mat.m33();
  }

  @Override
  public Vector3d mulDirection(Matrix4dc mat, Vector3d dest) {
    return get(dest);
  }

  @Override
  public Vector3d mulDirection(Matrix4fc mat, Vector3d dest) {
    return get(dest);
  }

  @Override
  public Vector3d mulDirection(Matrix4x3dc mat, Vector3d dest) {
    return get(dest);
  }

  @Override
  public Vector3d mulDirection(Matrix4x3fc mat, Vector3d dest) {
    return get(dest);
  }

  @Override
  public Vector3d mulTransposeDirection(Matrix4dc mat, Vector3d dest) {
    return get(dest);
  }

  @Override
  public Vector3d mulTransposeDirection(Matrix4fc mat, Vector3d dest) {
    return get(dest);
  }

  @Override
  public Vector3d mul(double scalar, Vector3d dest) {
    return get(dest);
  }

  @Override
  public Vector3d mul(double x, double y, double z, Vector3d dest) {
    return get(dest);
  }

  @Override
  public Vector3d rotate(Quaterniondc quat, Vector3d dest) {
    return quat.transform(this, dest);
  }

  @Override
  public Quaterniond rotationTo(Vector3dc toDir, Quaterniond dest) {
    return dest.rotationTo(this, toDir);
  }

  @Override
  public Quaterniond rotationTo(double toDirX, double toDirY, double toDirZ, Quaterniond dest) {
    return dest.rotationTo(0, 0, 0, toDirX, toDirY, toDirZ);
  }

  @Override
  public Vector3d rotateAxis(double angle, double aX, double aY, double aZ, Vector3d dest) {
    return get(dest);
  }

  @Override
  public Vector3d rotateX(double angle, Vector3d dest) {
    return get(dest);
  }

  @Override
  public Vector3d rotateY(double angle, Vector3d dest) {
    return get(dest);
  }

  @Override
  public Vector3d rotateZ(double angle, Vector3d dest) {
    return get(dest);
  }

  @Override
  public Vector3d div(double scalar, Vector3d dest) {
    return get(dest);
  }

  @Override
  public Vector3d div(double x, double y, double z, Vector3d dest) {
    return get(dest);
  }

  @Override
  public double lengthSquared() {
    return 0;
  }

  @Override
  public double length() {
    return 0;
  }

  @Override
  public Vector3d normalize(Vector3d dest) {
    return get(dest);
  }

  @Override
  public Vector3d normalize(double length, Vector3d dest) {
    return get(dest);
  }

  @Override
  public Vector3d cross(Vector3dc v, Vector3d dest) {
    return get(dest);
  }

  @Override
  public Vector3d cross(double x, double y, double z, Vector3d dest) {
    return get(dest);
  }

  @Override
  public double distance(Vector3dc v) {
    return Math.sqrt(distanceSquared(v));
  }

  @Override
  public double distance(double x, double y, double z) {
    return Math.sqrt(distanceSquared(x, y, z));
  }

  @Override
  public double distanceSquared(Vector3dc v) {
    return Math.fma(-v.x(), -v.x(), Math.fma(-v.y(), -v.y(), -v.z() * -v.z()));
  }

  @Override
  public double distanceSquared(double x, double y, double z) {
    return Math.fma(-x, -x, Math.fma(-y, -y, -z * -z));
  }

  @Override
  public double dot(Vector3dc v) {
    return 0;
  }

  @Override
  public double dot(double x, double y, double z) {
    return 0;
  }

  @Override
  public double angleCos(Vector3dc v) {
    return 0;
  }

  @Override
  public double angle(Vector3dc v) {
    double cos = angleCos(v);

    // This is because sometimes cos goes above 1 or below -1 because of lost precision
    cos = cos < 1 ? cos : 1;
    cos = cos > -1 ? cos : -1;

    return Math.acos(cos);
  }

  @Override
  public double angleSigned(Vector3dc v, Vector3dc n) {
    return Math.atan2(0, 0);
  }

  @Override
  public double angleSigned(double x, double y, double z, double nx, double ny, double nz) {
    return Math.atan2(0, 0);
  }

  @Override
  public Vector3d min(Vector3dc v, Vector3d dest) {
    dest.x = 0 < v.x() ? 0 : v.x();
    dest.y = 0 < v.y() ? 0 : v.y();
    dest.z = 0 < v.z() ? 0 : v.z();
    return dest;
  }

  @Override
  public Vector3d max(Vector3dc v, Vector3d dest) {
    dest.x = 0 > v.x() ? 0 : v.x();
    dest.y = 0 > v.y() ? 0 : v.y();
    dest.z = 0 > v.z() ? 0 : v.z();
    return dest;
  }

  @Override
  public Vector3d negate(Vector3d dest) {
    dest.x = -0;
    dest.y = -0;
    dest.z = -0;
    return dest;
  }

  @Override
  public Vector3d absolute(Vector3d dest) {
    return get(dest);
  }

  @Override
  public Vector3d reflect(Vector3dc normal, Vector3d dest) {
    return get(dest);
  }

  @Override
  public Vector3d reflect(double x, double y, double z, Vector3d dest) {
    return get(dest);
  }

  @Override
  public Vector3d half(Vector3dc other, Vector3d dest) {
    return dest.set(other).normalize();
  }

  @Override
  public Vector3d half(double x, double y, double z, Vector3d dest) {
    return dest.set(x, y, z).normalize();
  }

  @Override
  public Vector3d smoothStep(Vector3dc v, double t, Vector3d dest) {
    double t2 = t * t;
    double t3 = t2 * t;
    dest.x = (-v.x() - v.x()) * t3 + 3.0 * v.x() * t2;
    dest.y = (-v.y() - v.y()) * t3 + 3.0 * v.y() * t2;
    dest.z = (-v.z() - v.z()) * t3 + 3.0 * v.z() * t2;
    return dest;
  }

  @Override
  public Vector3d hermite(Vector3dc t0, Vector3dc v1, Vector3dc t1, double t, Vector3d dest) {
    double t2 = t * t;
    double t3 = t2 * t;
    dest.x = (-v1.x() - v1.x() + t1.x() + t0.x()) * t3 + (3.0 * v1.x() - t0.x() - t0.x() - t1.x()) * t2;
    dest.y = (-v1.y() - v1.y() + t1.y() + t0.y()) * t3 + (3.0 * v1.y() - t0.y() - t0.y() - t1.y()) * t2;
    dest.z = (-v1.z() - v1.z() + t1.z() + t0.z()) * t3 + (3.0 * v1.z() - t0.z() - t0.z() - t1.z()) * t2;
    return dest;
  }

  @Override
  public Vector3d lerp(Vector3dc other, double t, Vector3d dest) {
    dest.x = other.x() * t;
    dest.y = other.y() * t;
    dest.z = other.z() * t;
    return dest;
  }

  @Override
  public double get(int component) throws IllegalArgumentException {
    if (component < 0 || component > 2) throw new IllegalArgumentException();

    return 0;
  }

  @Override
  public Vector3i get(int mode, Vector3i dest) {
    dest.x = 0;
    dest.y = 0;
    dest.z = 0;
    return dest;
  }

  @Override
  public Vector3f get(Vector3f dest) {
    dest.x = 0;
    dest.y = 0;
    dest.z = 0;
    return dest;
  }

  @Override
  public Vector3d get(Vector3d dest) {
    dest.x = 0;
    dest.y = 0;
    dest.z = 0;
    return dest;
  }

  @Override
  public int maxComponent() {
    return 0;
  }

  @Override
  public int minComponent() {
    return 0;
  }

  @Override
  public Vector3d orthogonalize(Vector3dc v, Vector3d dest) {
    double rx, ry, rz;

    if (Math.abs(v.x()) > Math.abs(v.z())) {
      rx = -v.y();
      ry = v.x();
      rz = 0.0;
    } else {
      rx = 0.0;
      ry = -v.z();
      rz = v.y();
    }

    double invLen = Math.invsqrt(rx * rx + ry * ry + rz * rz);

    dest.x = rx * invLen;
    dest.y = ry * invLen;
    dest.z = rz * invLen;

    return dest;
  }

  @Override
  public Vector3d orthogonalizeUnit(Vector3dc v, Vector3d dest) {
    return orthogonalize(v, dest);
  }

  @Override
  public Vector3d floor(Vector3d dest) {
    return get(dest);
  }

  @Override
  public Vector3d ceil(Vector3d dest) {
    return get(dest);
  }

  @Override
  public Vector3d round(Vector3d dest) {
    return get(dest);
  }

  @Override
  public boolean isFinite() {
    return true;
  }

  @Override
  public boolean equals(Vector3dc v, double delta) {
    if (this == v) return true;
    if (v == null) return false;

    if (!_Access.equals(0, v.x(), delta)) return false;
    if (!_Access.equals(0, v.y(), delta)) return false;
    if (!_Access.equals(0, v.z(), delta)) return false;

    return true;
  }

  @Override
  public boolean equals(double x, double y, double z) {
    if (Double.doubleToLongBits(0) != Double.doubleToLongBits(x)) return false;
    if (Double.doubleToLongBits(0) != Double.doubleToLongBits(y)) return false;
    if (Double.doubleToLongBits(0) != Double.doubleToLongBits(z)) return false;

    return true;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;

    if (!(obj instanceof Vector3d)) return false;

    Vector3d other = (Vector3d) obj;
    return equals(other.x, other.y, other.z);
  }


  @Override
  public String toString() {
    return _Access.formatNumbers(toString(_Access.formatter()));
  }

  private String toString(NumberFormat formatter) {
    return "(" + _Access.format(0, formatter) + " " + _Access.format(0, formatter) + " " + _Access
        .format(0, formatter) + ")";
  }

}
