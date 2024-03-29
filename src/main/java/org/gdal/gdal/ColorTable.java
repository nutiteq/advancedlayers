/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.6
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.gdal.gdal;

public class ColorTable implements Cloneable {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected ColorTable(long cPtr, boolean cMemoryOwn) {
    if (cPtr == 0)
        throw new RuntimeException();
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }
  
  protected static long getCPtr(ColorTable obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        gdalJNI.delete_ColorTable(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  private Object parentReference;

  /* Ensure that the GC doesn't collect any parent instance set from Java */
  protected void addReference(Object reference) {
    parentReference = reference;
  }

  public Object clone()
  {
      return Clone();
  }


  public ColorTable(int palette) {
    this(gdalJNI.new_ColorTable__SWIG_0(palette), true);
  }

  public ColorTable() {
    this(gdalJNI.new_ColorTable__SWIG_1(), true);
  }

  public ColorTable Clone() {
    long cPtr = gdalJNI.ColorTable_Clone(swigCPtr, this);
    return (cPtr == 0) ? null : new ColorTable(cPtr, true);
  }

  public int GetPaletteInterpretation() {
    return gdalJNI.ColorTable_GetPaletteInterpretation(swigCPtr, this);
  }

  public int GetCount() {
    return gdalJNI.ColorTable_GetCount(swigCPtr, this);
  }

  public int GetColorEntry(int entry) {
    return gdalJNI.ColorTable_GetColorEntry(swigCPtr, this, entry);
  }

  public void SetColorEntry(int entry, int centry) {
    gdalJNI.ColorTable_SetColorEntry(swigCPtr, this, entry, centry);
  }

  public void CreateColorRamp(int nStartIndex, int startcolor, int nEndIndex, int endcolor) {
    gdalJNI.ColorTable_CreateColorRamp(swigCPtr, this, nStartIndex, startcolor, nEndIndex, endcolor);
  }

}
