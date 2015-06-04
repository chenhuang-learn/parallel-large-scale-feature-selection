package com.sohu.hc.featsel.evaluate;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class EvaluateTmpData implements Writable {
	public float y;
	public float p;
	public float p_new;
	
	public EvaluateTmpData() { }
	
	public EvaluateTmpData(float y, float p, float p_new) {
		this.y = y;
		this.p = p;
		this.p_new = p_new;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		y = in.readFloat();
		p = in.readFloat();
		p_new = in.readFloat();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeFloat(y);
		out.writeFloat(p);
		out.writeFloat(p_new);
	}

	@Override
	public String toString() {
		return "EvaluateTmpData [y=" + y + ", p=" + p + ", p_new=" + p_new
				+ "]";
	}
}
