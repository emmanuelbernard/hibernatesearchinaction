package com.manning.hsia.dvdstore.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.Query;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.SearchFactory;
import org.testng.annotations.Test;

import com.manning.hsia.dvdstore.action.SearchingAction;
import com.manning.hsia.dvdstore.action.SearchingActionImpl;
import com.manning.hsia.dvdstore.model.Item;
import com.manning.hsia.dvdstore.util.SessionHolder;

import static org.easymock.EasyMock.*;  //static imports make EasyMock easier 

/**
 * Example 9.17
 */
public class HibernateSearchUnitTest {
	
	@Test(groups="hibernatesearch")
	public void testSearch() throws Exception {
		
		SearchingAction action = new SearchingActionImpl(); //create tested service
		
		FullTextQuery query = createMock(FullTextQuery.class);  //create mock for each object used
		FullTextSession session = createMock(FullTextSession.class);
		SearchFactory factory = createMock(SearchFactory.class);
		
		expect( session.getSearchFactory() )  //define expected calls
				.andReturn(factory);   
		expect( factory.getAnalyzer(Item.class) )
				.andReturn( new StandardAnalyzer() );  //and return results 
		expect( session.createFullTextQuery( 
				            isA(Query.class),   //potentially restrict parameters
				            eq(Item.class) ) 
			  ).andReturn(query);
		expect( query.setProjection("title") ).andReturn(query); //should call projection and return self
		
		List<Object[]> results = new ArrayList<Object[]>();  //build query results
		results.add(new Object[] {"The incredibles"} );
		expect( query.list() ).andReturn(results);  //associates it to query execution
		
		SessionHolder.setFullTextSession(session);  //pass mock objects to the service
		
		replay(factory);   //prepare mocks for listening
		replay(query);
		replay(session);
		
		List<String> titles = action.getTitleFromMatchingItems("title:incredibles"); //service executed using mocks
		
		assert 1 == titles.size() : "should have two match, not " + titles.size();  //check results based on mocks
		assert "The incredibles".equals( titles.get(0) ) : "The incredibles";
	}

}
