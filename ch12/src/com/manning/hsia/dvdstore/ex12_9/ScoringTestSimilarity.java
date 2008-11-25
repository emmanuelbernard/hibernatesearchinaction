package com.manning.hsia.dvdstore.ex12_9;

import org.apache.lucene.search.DefaultSimilarity;

public class ScoringTestSimilarity extends DefaultSimilarity
{
   @Override
   public float coord(int overlap, int maxOverlap)
   {
      if (overlap == 2)
      {
         return 0.5F;
      }
      if (overlap == 1)
      {
         return 2.0F;
      }
      return 0.0F;
   }
}
