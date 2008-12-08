package com.manning.hsia.dvdstore.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class FilterImport {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//filterImport();
		linkTable();
	}

	private static void filterImport() {
		try {
			File file = new File("./test/import.sql");
			File export = new File("./test/import2.sql");
			
			FileReader input = new FileReader(file);
			FileWriter output = new FileWriter(export); 
			BufferedReader reader = new BufferedReader(input);
			BufferedWriter writer = new BufferedWriter(output);
			String line =  reader.readLine();
			while (line != null) {
				if ( line.startsWith("delete from PRODUCTS") || line.startsWith("insert into PRODUCTS ")) {
					writer.write(line);
					writer.newLine();
				}
				line = reader.readLine();
			}
			writer.close();
			reader.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void linkTable() {
		try {
			File export = new File("./test/import3.sql");
			
			FileWriter output = new FileWriter(export); 
			BufferedWriter writer = new BufferedWriter(output);
			for (int i = 1 ; i <= 479; i++) {
				String line = "insert into PRODUCTS_DISTRIBUTORS(PROD_ID, distributor_id) values (" + i + "," + (i%3+1) + " )";
				writer.write(line);
				writer.newLine();
			}
			writer.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
