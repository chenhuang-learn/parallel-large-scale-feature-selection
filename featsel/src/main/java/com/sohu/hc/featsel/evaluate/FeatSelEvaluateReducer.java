package com.sohu.hc.featsel.evaluate;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class FeatSelEvaluateReducer extends Reducer<Text, EvaluateTmpData, Text, Text> {
	
	@Override
	public void reduce(Text key, Iterable<EvaluateTmpData> values, Context context) throws IOException, InterruptedException {
		AUC aucOld = new AUC();
		AUC aucNew = new AUC();
		LogLoss logLossOld = new LogLoss();
		LogLoss logLossNew = new LogLoss();
		for(EvaluateTmpData data : values) {
			aucOld.addSample(data.y, data.p);
			aucNew.addSample(data.y, data.p_new);
			logLossOld.addSample(data.y, data.p);
			logLossNew.addSample(data.y, data.p_new);
		}
		float aucImpro = aucNew.calAUC() - aucOld.calAUC();
		float logLossRedu = logLossOld.getAverageLoss() - logLossNew.getAverageLoss();
		context.write(key, new Text(aucImpro + ":" + logLossRedu));
	}
	
}
