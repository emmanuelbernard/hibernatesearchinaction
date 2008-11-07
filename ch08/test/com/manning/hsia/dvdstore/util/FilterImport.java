package com.manning.hsia.dvdstore.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FilterImport {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		filterImport();
		//linkTable();
	}

	private static void filterImport() {
		try {
			File file = new File("./test/import.orig.sql");
			File export = new File("./test/import2.sql");
			
			FileReader input = new FileReader(file);
			FileWriter output = new FileWriter(export); 
			BufferedReader reader = new BufferedReader(input);
			BufferedWriter writer = new BufferedWriter(output);
			String line =  reader.readLine();
			while (line != null) {
				if ( line.startsWith("delete from PRODUCTS") 
						|| line.startsWith("insert into PRODUCTS ") 
						|| line.startsWith("delete from CATEGORIES")
						|| line.startsWith("insert into CATEGORIES")
						|| line.startsWith("delete from PRODUCT_CATEGORY")
						|| line.startsWith("insert into PRODUCT_CATEGORY")) {
					writer.write(line);
					writer.newLine();
				}
				line = reader.readLine();
			}
			
			doLinktable(writer);
			
			writer.close();
			reader.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void doLinktable(BufferedWriter writer) throws IOException {
		writer.newLine();
		writer.write("delete from PRODUCTS_DISTRIBUTORS");
		writer.newLine();
		writer.write("delete from distributor");
		writer.newLine();
		
		writer.write("insert into distributor(id, name, stockName) values (1, 'Universal picture', 'UVV')");
		writer.newLine();
		writer.write("insert into distributor(id, name, stockName) values (2, 'Sony picture', 'SNE')");
		writer.newLine();
		writer.write("insert into distributor(id, name, stockName) values (3, 'Warner', 'TWX')");
		writer.newLine();
		
		for (int i = 1 ; i <= 479; i++) {
			String line = "insert into PRODUCTS_DISTRIBUTORS(PROD_ID, distributor_id) values (" + i + "," + (i%3+1) + " )";
			writer.write(line);
			writer.newLine();
		}
	}
	
	private static void linkTable() {
		try {
			File export = new File("./test/import2.sql");
			
			FileWriter output = new FileWriter(export); 
			BufferedWriter writer = new BufferedWriter(output);
			doLinktable(writer);
			writer.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
