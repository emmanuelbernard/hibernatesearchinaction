package com.manning.hsia.dvdstore.ex12_15;

import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.search.*;

public class MyTermQuery extends TermQuery {
    private Term term;

    public class MyWeight implements Weight {
        private Similarity similarity;
        private float value;
        private float idf;
        private float queryNorm;
        private float queryWeight;

        public MyWeight(Searcher searcher) throws IOException {
            this.similarity = getSimilarity(searcher);
            idf = similarity.idf(term, searcher);
        }

        public Query getQuery() { return MyTermQuery.this; }
        public float getValue() { return value; }

        public float sumOfSquaredWeights() throws IOException {
            queryWeight = idf * getBoost();
            return queryWeight * queryWeight;
        }

        public void normalize(float queryNorm) {
            this.queryNorm = queryNorm;
            queryWeight *= queryNorm;
            value = queryWeight * idf;
        }

        public Scorer scorer(IndexReader reader) throws IOException {
            TermDocs termDocs = reader.termDocs(term);

            if (termDocs == null)
              return null;

            return new MyTermScorer(this, termDocs, similarity,
                                  reader.norms(term.field()));
        }

        public Explanation explain(IndexReader reader, int doc) throws IOException {
            ComplexExplanation result = new ComplexExplanation();
            result.setDescription("weight("+getQuery()+" in "+doc+"), product of:");

            Explanation idfExpl =
              new Explanation(idf, "idf(docFreq=" + reader.docFreq(term) + ")");

            // explain query weight
            Explanation queryExpl = new Explanation();
            queryExpl.setDescription("queryWeight(" + getQuery() + "), product of:");

            Explanation boostExpl = new Explanation(getBoost(), "boost");
            if (getBoost() != 1.0f)
              queryExpl.addDetail(boostExpl);
            queryExpl.addDetail(idfExpl);

            Explanation queryNormExpl = new Explanation(queryNorm,"queryNorm");
            queryExpl.addDetail(queryNormExpl);

            queryExpl.setValue(boostExpl.getValue() *
                               idfExpl.getValue() *
                               queryNormExpl.getValue());

            result.addDetail(queryExpl);

            // explain field weight
            String field = term.field();
            ComplexExplanation fieldExpl = new ComplexExplanation();
            fieldExpl.setDescription("fieldWeight("+term+" in "+doc+
                                     "), product of:");

            Explanation tfExpl = scorer(reader).explain(doc);
            fieldExpl.addDetail(tfExpl);
            fieldExpl.addDetail(idfExpl);

            Explanation fieldNormExpl = new Explanation();
            byte[] fieldNorms = reader.norms(field);
            float fieldNorm =
              fieldNorms!=null ? Similarity.decodeNorm(fieldNorms[doc]) : 0.0f;
            fieldNormExpl.setValue(fieldNorm);
            fieldNormExpl.setDescription("fieldNorm(field="+field+", doc="+doc+")");
            fieldExpl.addDetail(fieldNormExpl);

            fieldExpl.setMatch(Boolean.valueOf(tfExpl.isMatch()));
            fieldExpl.setValue(tfExpl.getValue() *
                               idfExpl.getValue() *
                               fieldNormExpl.getValue());

            result.addDetail(fieldExpl);
            result.setMatch(fieldExpl.getMatch());

            // combine them
            result.setValue(queryExpl.getValue() * fieldExpl.getValue());

            if (queryExpl.getValue() == 1.0f)
              return fieldExpl;

            return result;
        }
    }

    public MyTermQuery(Term t) {
        super(t);
        term = t;
    }

    @Override
    protected Weight createWeight(Searcher searcher) throws IOException {
        return new MyWeight(searcher);
    }

    public boolean equals(Object o) {
      if (!(o instanceof TermQuery))
        return false;
      MyTermQuery other = (MyTermQuery)o;
      return (this.getBoost() == other.getBoost())
        && this.term.equals(other.term);
    }

    /** Returns a hash code value for this object.*/
    public int hashCode() {
      return Float.floatToIntBits(getBoost()) ^ term.hashCode();
    }
}
