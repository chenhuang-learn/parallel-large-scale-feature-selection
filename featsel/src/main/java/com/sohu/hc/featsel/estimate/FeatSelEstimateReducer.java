package com.sohu.hc.featsel.estimate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class FeatSelEstimateReducer extends Reducer<Text, EstimateTmpData, Text, FloatWritable> {
	private float lambda;
	
	@Override
	public void setup(Context context) {
		lambda = context.getConfiguration().getFloat("featsel_lambda", 0.f);
	}
	
	@Override
	public void reduce(Text key, Iterable<EstimateTmpData> values, Context context) throws IOException, InterruptedException {
		List<EstimateTmpData> l = new ArrayList<EstimateTmpData>();
		int posiNum = 0, negaNum = 0;
		for(EstimateTmpData value : values) {
			if(value.y > 0) {
				posiNum += 1;
			} else {
				negaNum += 1;
			}
			double p = Math.min(1-1e-15, Math.max(value.p, 1e-15));
			EstimateTmpData data = new EstimateTmpData(value.x, value.y, (float)Math.log(p/(1-p)));
			l.add(data);
		}
		double ratio = (double)Math.min(negaNum, posiNum) / l.size();
		if(ratio < 0.05) ratio = 0.05;
		double eps = 0.01 * ratio;
		
		double beta = 0.;
		double gnorm_init = 0.;
		for (int i = 0; i < 20; i++) {
			double derivative_1 = -2 * lambda * beta;
			double derivative_2 = -2 * lambda;
			for(EstimateTmpData data : l) {
				double p_prime = Math.exp(data.p + beta) / (1 + Math.exp(data.p + beta));
				derivative_1 += data.x * (data.y - p_prime);
				derivative_2 += -p_prime * (1 - p_prime) * data.x * data.x;
			}
			if(i == 0) {
				gnorm_init = Math.abs(derivative_1);
			} else {
				if(Math.abs(derivative_1) < eps * gnorm_init) {
					break;
				}
			}
			beta -= derivative_1 / derivative_2;
		}
		
		context.write(key, new FloatWritable((float)beta));
	}
	
}
