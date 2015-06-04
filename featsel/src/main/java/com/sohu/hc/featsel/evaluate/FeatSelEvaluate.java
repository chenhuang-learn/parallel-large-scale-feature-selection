package com.sohu.hc.featsel.evaluate;

import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class FeatSelEvaluate {
	
	public static void main(String[] args) throws Exception {
		// dataPath, ModelPath, newModelPath, reducerNum
		Configuration conf = new Configuration();
		
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		if(otherArgs.length != 5) {
			System.out.println("hadoop jar xx.jar <dataPath> <modelPath> <newModelPath> <resultPath> <numReduceTasks>");
			return;
		}
		
		String dataPath = args[0];
		String modelPath = args[1];
		String newModelPath = args[2];
		String resultPath = args[3];
		int reduceNum = Integer.parseInt(args[4]);
		
		Job job = Job.getInstance(conf);
		job.setJarByClass(FeatSelEvaluate.class);
		job.setJobName("feat_sel_evaluate");
		
		FileInputFormat.addInputPath(job, new Path(dataPath));
		FileOutputFormat.setOutputPath(job, new Path(resultPath));
		job.addCacheFile(new URI(new Path(modelPath).toString() + "#modelPath"));
		job.addCacheFile(new URI(new Path(newModelPath).toString() + "#newModelPath"));
		
		// mapperclass, reducerclass, mapoutputkey/value, outputkey/value
		job.setMapperClass(FeatSelEvaluateMapper.class);
		job.setReducerClass(FeatSelEvaluateReducer.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(EvaluateTmpData.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		// numreducetasks
		job.setNumReduceTasks(reduceNum);
		
		job.waitForCompletion(true);
	}
	
}
