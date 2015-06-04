package com.sohu.hc.featsel.estimate;

import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;


public class FeatSelEstimate {
	
	public static void main(String[] args) throws Exception {
		// dataPath, ModelPath, outputModelPath, lambda, reducerNum
		Configuration conf = new Configuration();
		
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		if(otherArgs.length != 5) {
			System.out.println("hadoop xx.jar <dataPath> <modelPath> <outputModelPath> <lambda> <reducerNum>");
			return;
		}
		String dataPath = args[0];
		String modelPath = args[1];
		String outputModelPath = args[2];
		float lambda = Float.parseFloat(args[3]);
		int reducerNum = Integer.parseInt(args[4]);
		
		Job job = Job.getInstance(conf);
		job.setJarByClass(FeatSelEstimate.class);
		job.setJobName("feat_sel_estimate");
		
		job.getConfiguration().setFloat("featsel_lambda", lambda);
		FileInputFormat.addInputPath(job, new Path(dataPath));
		FileOutputFormat.setOutputPath(job, new Path(outputModelPath));
		job.addCacheFile(new URI(new Path(modelPath).toString() + "#modelPath"));
		
		// mapperclass, reducerclass, mapoutputkey/value, outputkey/value
		job.setMapperClass(FeatSelEstimateMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(EstimateTmpData.class);
		job.setReducerClass(FeatSelEstimateReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(FloatWritable.class);
		
		// numreducetasks
		job.setNumReduceTasks(reducerNum);
		
		job.waitForCompletion(true);
	}
	
}
