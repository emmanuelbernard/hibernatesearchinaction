package com.manning.hsia.dvdstore.ex12_4;

import org.apache.lucene.search.DefaultSimilarity;

public class ScoringTestSimilarity extends DefaultSimilarity {
   @Override
   public float tf(float freq) {
      return 1.0F;
   }
}
