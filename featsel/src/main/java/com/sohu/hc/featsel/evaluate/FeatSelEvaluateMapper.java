package com.sohu.hc.featsel.evaluate;

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

public class FeatSelEvaluateMapper extends Mapper<LongWritable, Text, Text, EvaluateTmpData> {
	private Map<String, Float> model;
	private Map<String, Map<String, Float>> featureClass;

	@Override
	public void setup(Context context) throws IOException {
		model = new HashMap<String, Float>();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File("modelPath")), "UTF-8"));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] fields = line.trim().split("\\s+");
			if (fields.length != 2) {
				br.close();
				throw new IOException("invalid line: " + line);
			}
			model.put(fields[0], Float.parseFloat(fields[1]));
		}
		br.close();
		
		featureClass = new HashMap<String, Map<String, Float>>();
		BufferedReader brn = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File("newModelPath")), "UTF-8"));
		while ((line = brn.readLine()) != null) {
			String[] fields = line.trim().split("\\s+");
			if (fields.length != 2) {
				brn.close();
				throw new IOException("invalid line: " + line);
			}
			String featureClassName = fields[0].split("-")[0];
			if (!featureClass.containsKey(featureClassName)) {
				featureClass.put(featureClassName, new HashMap<String, Float>());
			}
			featureClass.get(featureClassName).put(fields[0], Float.parseFloat(fields[1]));
		}
		brn.close();
	}

	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		String[] fields = value.toString().trim().split("\\s+");
		float y = Float.parseFloat(fields[0]) > 0 ? 1.f : 0.f;
		double decisionValue = 0.;
		Map<String, Float> featureClassResponse = new HashMap<String, Float>();
		for (String featureClassName : featureClass.keySet()) {
			featureClassResponse.put(featureClassName, 0.f);
		}
		for (int i = 1; i < fields.length; i++) {
			String[] subFields = fields[i].split(":");
			if (subFields.length != 2) {
				throw new IOException("invalid field: " + fields[i]);
			}
			if (model.containsKey(subFields[0])) {
				decisionValue += model.get(subFields[0]) * Float.parseFloat(subFields[1]);
			} else {
				String classKey = subFields[0].split("-")[0];
				if (featureClassResponse.containsKey(classKey)) {
					if (featureClass.get(classKey).containsKey(subFields[0])) {
						float newResponse = featureClassResponse.get(classKey)
								+ Float.parseFloat(subFields[1])
								* featureClass.get(classKey).get(subFields[0]);
						featureClassResponse.put(classKey, newResponse);
					}
				}
			}
		}
		float p = (float) (1 / (1 + Math.exp(-decisionValue)));
		for (Entry<String, Float> e : featureClassResponse.entrySet()) {
			float p_new = (float) (1 / (1 + Math.exp(-(decisionValue + e.getValue()))));
			context.write(new Text(e.getKey()), new EvaluateTmpData(y, p, p_new));
		}
	}

}
