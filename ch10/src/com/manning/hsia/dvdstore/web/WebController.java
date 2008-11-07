package com.manning.hsia.dvdstore.web;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.manning.hsia.dvdstore.action.ItemAction;
import com.manning.hsia.dvdstore.action.ItemActionImpl;
import com.manning.hsia.dvdstore.model.Item;


public class WebController extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException {
		String ejbName; // = ItemActionImpl.class.getName() + "/local";
		ejbName = "dvdstore-slave/ItemActionImpl/local";
		StringBuilder out = new StringBuilder("<html><head><title>Index item</title></head><body>");
		try {
			out.append("Acquiring initial context<br/>");
			InitialContext context = new InitialContext();
			out.append("Looking up EJB: ").append(ejbName).append("<br/>");
			ItemAction action = (ItemAction) context.lookup(ejbName);
			Item item = new Item();
			item.setDescription("This movie is the answer to life");
			item.setTitle("Life et caetera date" + new Date() );
			item.setDistributor( action.getDistributor(1) );
			item.setEan("123456789012");
			item.setImageURL("http://multimedia.fnac.com/multimedia/images_produits/grandes110/1/4/1/3384442143141.gif");
			item.setPrice(new BigDecimal(23));
			out.append("saving new item<br/>");
			action.addNewItem(item);
			out.append("Saving done<br/>");
		}
		catch(Exception e) {
			//response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			out.append("<br/>Error while adding new item:<br/>")
				.append(e.toString());
		}
		out.append("</body>");
		response.getWriter().print(out.toString());
		response.getWriter().flush();
	}
}
