package com.sohu.hc.featsel.estimate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class FeatSelEstimateMapper extends Mapper<LongWritable, Text, Text, EstimateTmpData> {
	private Map<String, Float> model;
	
	@Override
	public void setup(Context context) throws IOException {
		model = new HashMap<String, Float>();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File("modelPath")), "UTF-8"));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] fields = line.trim().split("\\s+");
			if(fields.length != 2) {
				br.close();
				throw new IOException("invalid line: " + line);
			}
			model.put(fields[0], Float.parseFloat(fields[1]));
		}
		br.close();
	}
	
	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		String[] fields = value.toString().trim().split("\\s+");
		float y = Float.parseFloat(fields[0]) > 0 ? 1.f : 0.f;
		Map<String, Float> newFeatures = new HashMap<String, Float>();
		double decisionValue = 0.;
		for(int i=1; i<fields.length; i++) {
			String[] subFields = fields[i].split(":");
			if(subFields.length != 2) {
				throw new IOException("invalid field: " + fields[i]);
			}
			if(model.containsKey(subFields[0])) {
				decisionValue += model.get(subFields[0]) * Float.parseFloat(subFields[1]);
			} else {
				newFeatures.put(subFields[0], Float.parseFloat(subFields[1]));
			}
		}
		float p = (float) (1 / (1 + Math.exp(-decisionValue)));
		for(Entry<String, Float> e : newFeatures.entrySet()) {
			context.write(new Text(e.getKey()), new EstimateTmpData(e.getValue(), y, p));
		}
	}
}
