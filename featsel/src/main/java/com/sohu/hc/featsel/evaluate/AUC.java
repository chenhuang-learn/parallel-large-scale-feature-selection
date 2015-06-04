package com.sohu.hc.featsel.evaluate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AUC {
	private List<Float> positiveProbs;
	private List<Float> negativeProbs;

	public AUC() {
		positiveProbs = new ArrayList<Float>();
		negativeProbs = new ArrayList<Float>();
	}

	public void addSample(float y, float prob) {
		if (y > 0) {
			positiveProbs.add(prob);
		} else {
			negativeProbs.add(prob);
		}
	}

	public float calAUC() {
		Collections.sort(positiveProbs);
		Collections.sort(negativeProbs);
		int n0 = negativeProbs.size();
		int n1 = positiveProbs.size();
		if (n0 == 0 || n1 == 0) {
			return 0.5f;
		}
		// sacn the data
		int i0 = 0, i1 = 0, rank = 1;
		double rankSum = 0;
		while (i0 < n0 && i1 < n1) {
			float v0 = negativeProbs.get(i0);
			float v1 = positiveProbs.get(i1);
			if (v0 < v1) {
				i0++;
				rank++;
			} else if (v1 < v0) {
				i1++;
				rankSum += rank;
				rank++;
			} else {
				float tieScore = v0;
				// how many negatives are tied?
				int k0 = 0;
				while (i0 < n0 && negativeProbs.get(i0) == tieScore) {
					k0++;
					i0++;
				}
				// how many positives are tied?
				int k1 = 0;
				while (i1 < n1 && positiveProbs.get(i1) == tieScore) {
					k1++;
					i1++;
				}
				// we found k0+k1 tied values ranks in [rank, rank+k0+k1)
				rankSum += (rank + (k0 + k1 - 1) / 2.0) * k1;
				rank += k0 + k1;
			}
		}
		if (i1 < n1) {
			rankSum += (rank + (n1 - i1 - 1) / 2.0) * (n1 - i1);
			rank += n1 - i1;
		}
		return (float) ((rankSum / n1 - (n1 + 1) / 2.0) / n0);
	}
	
	public static void main(String[] args) {
		AUC a = new AUC();
		a.addSample(0, 0.1f);
		a.addSample(0, 0.2f);
		a.addSample(0, 0.4f);
		a.addSample(0, 0.6f);
		a.addSample(1, 0.5f);
		a.addSample(1, 0.8f);
		a.addSample(1, 0.7f);
		a.addSample(1, 0.9f);
		System.out.println(a.calAUC());
	}
	
}
