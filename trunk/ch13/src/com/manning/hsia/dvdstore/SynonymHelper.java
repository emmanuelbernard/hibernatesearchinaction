package com.manning.hsia.dvdstore;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;

import java.io.*;
import java.util.*;

import com.manning.hsia.ch13.Synonym;

public class SynonymHelper {
	private FullTextSession session;
	private Transaction tx;

	public Query expandQuery(String query,
	                         FullTextSession session,
	                         Analyzer a,
	                         String field,
	                         float boost)
		throws IOException {
		Set<String> synsList = getSynonyms(query, session, a);
		BooleanQuery bq = new BooleanQuery();

		for (String synonym : synsList) {  // add in unique synonyms
			TermQuery tq = new TermQuery(new Term(field, synonym));
			tq.setBoost(boost);
			bq.add(tq, BooleanClause.Occur.SHOULD);
		}
		return bq;
	}

	public Set<String> getSynonyms(String query,
	                               FullTextSession session,
	                               Analyzer a)
		throws IOException {
		Set<String> querySet = new HashSet(); // avoid dups
		TokenStream ts = a.tokenStream("word", new StringReader(query));
		Token t;
		String anaQuery;
		while ((t = ts.next()) != null) {
			anaQuery = new String(t.termBuffer(), 0, t.termLength());
			querySet.add(anaQuery);
		}

		BooleanQuery bq = new BooleanQuery();
		for (String str : querySet) {
			TermQuery tq = new TermQuery(new Term("word", str));
			bq.add(tq, BooleanClause.Occur.SHOULD);
		}
		org.hibernate.search.FullTextQuery hibQuery = session.createFullTextQuery(bq, Synonym.class);
		hibQuery.setProjection("syn");

		List<Object[]> results = hibQuery.list();
		Set<String> alreadyPresent = new HashSet();

		for (Object[] obj : results) {
			StringTokenizer st = new StringTokenizer((String) obj[0], " ");
			while (st.hasMoreElements()) {
				String syn = (String) st.nextElement();
				alreadyPresent.add(syn);
			}
		}
		alreadyPresent.addAll(querySet);   // add original query terms
		return alreadyPresent;
	}

	public void buildSynonymIndex(FullTextSession session, String synFile) throws IOException {
		this.session = session;
		if (!(new File(synFile)).canRead()) {
			throw new IOException("Prolog file is not readable: " + synFile);
		}
		final FileInputStream fis = new FileInputStream(synFile);
		final BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String lineIn;

		final Map<String, List<String>> word2Groups = new TreeMap();// maps a word to all the "groups" it's in
		final Map<String, List<String>> group2Words = new TreeMap();// maps a group to all the words in it

		while ((lineIn = br.readLine()) != null) {
			if (!lineIn.startsWith("s(")) {                          // syntax check
				throw new IOException("Wrong input format " + lineIn);
			}

			lineIn = lineIn.substring(2);                            // parse line
			String id = lineIn.substring(0, lineIn.indexOf(','));
			int quote1 = lineIn.indexOf('\'');
			int quote2 = lineIn.lastIndexOf('\'');
			String word = lineIn.substring(quote1 + 1, quote2).toLowerCase();

			List<String> list = word2Groups.get(word);
			if (list == null) {
				list = new LinkedList();
				list.add(id);
				word2Groups.put(word, list);
			} else {
				list.add(id);
			}

			list = group2Words.get(id);
			if (list == null) {
				list = new LinkedList();
				list.add(word);
				group2Words.put(id, list);
			} else {
				list.add(word);
			}
		}

		fis.close();
		br.close();

		// create the index
		index(word2Groups, group2Words);
	}

	private static boolean isWord(String s) {
		int len = s.length();
		for (int i = 0; i < len; i++) {
			if (!Character.isLetter(s.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	private void index(Map<String, List<String>> word2Groups,
	                   Map<String, List<String>> group2Words)
		throws IOException {
		try {
			Iterator iter = word2Groups.keySet().iterator();
			tx = session.beginTransaction();

			int counter = 0;
			while (iter.hasNext()) { // for each word
				counter++;
				String word2GroupsKey = (String) iter.next();
				Synonym syn = new Synonym();

				int n = index(word2Groups, group2Words, word2GroupsKey, syn);
				if (n > 0) {
					syn.setWord(word2GroupsKey);
					session.save(syn);
				}
				if (counter % 1000 == 0) { // 100 at a time
					tx.commit();
					session.clear();
					tx = session.beginTransaction();
				}
			}
			tx.commit();
		}
		finally {
			session.clear();
			session.getSearchFactory().optimize();
		}
	}

	private int index(Map<String, List<String>> word2Groups,
	                  Map<String, List<String>> group2Words,
	                  String word2GroupsKey,
	                  Synonym syn) {
		List keys = word2Groups.get(word2GroupsKey); // get list of key#'s
		Iterator iter = keys.iterator();
		Set alreadyPresent = new TreeSet();

		while (iter.hasNext()) { // for each key# pass fill up 'alreadyPresent' with all words
			alreadyPresent.addAll(group2Words.get(iter.next())); // get list of words
		}
		int num = 0;
		alreadyPresent.remove(word2GroupsKey); // word is it's own synonym
		Iterator it = alreadyPresent.iterator();
		while (it.hasNext()) {
			String cur = (String) it.next();
			if (cur.startsWith("flick knife")) {
				System.out.println("");
			}
			if (!isWord(cur)) { // don't store things with spaces or non-alphas
				continue;
			}
			num++;
			if (syn.getSyn() != null) {
				syn.setSyn(syn.getSyn() + " " + cur);
			} else {
				syn.setSyn(cur);
			}
		}
		return num;
	}
}
