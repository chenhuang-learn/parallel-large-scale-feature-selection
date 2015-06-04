package com.sohu.hc.featsel.evaluate;

public class LogLoss {
	private double totalLoss;
	private int sampleNum;
	
	public LogLoss() {
		totalLoss = 0.0;
		sampleNum = 0;
	}
	
	public void addSample(float y, float p) {
		double p_ = Math.max(Math.min(p,  1-1e-15), 1e-15);
		totalLoss += y > 0 ? -Math.log(p_) : -Math.log(1. - p_);
		sampleNum += 1;
	}
	
	public float getTotalLoss() {
		return (float) totalLoss;
	}
	
	public float getAverageLoss() {
		if(sampleNum == 0) return 0.f;
		return (float) (totalLoss/sampleNum);
	}
	
}
