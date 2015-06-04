package com.sohu.hc.featsel.estimate;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class EstimateTmpData implements Writable {

	public float x;
	public float y;
	public float p;
	
	public EstimateTmpData() { }
	
	public EstimateTmpData(float x, float y, float p) {
		this.x = x;
		this.y = y;
		this.p = p;
	}
	
	@Override
	public void readFields(DataInput in) throws IOException {
		x = in.readFloat();
		y = in.readFloat();
		p = in.readFloat();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeFloat(x);
		out.writeFloat(y);
		out.writeFloat(p);
	}

	@Override
	public String toString() {
		return "IntermediateData [x=" + x + ", y=" + y + ", p=" + p + "]";
	}
	
}
