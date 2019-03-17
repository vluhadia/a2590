package app;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

import ac.ArithmeticDecoder;
import io.InputStreamBitSource;
import io.InsufficientBitsLeftException;

public class StaticACDecodeTextFile {

	public static void main(String[] args) throws IOException {
		String input_file_name = "data/static-compressed.dat";
		String output_file_name = "data/reuncompressed.txt";

		FileInputStream fis = new FileInputStream(input_file_name);

		InputStreamBitSource bit_source = new InputStreamBitSource(fis);

		// Read in symbol counts and set up model
		
		int[] symbol_counts = new int[511];
		Integer[] symbols = new Integer[511];
		
		for (int i=0; i<511; i++) {
			try {
				symbol_counts[i] = bit_source.next(32);
			} catch (InsufficientBitsLeftException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println(i);
			}
			symbols[i] = i;
		}

		FreqCountIntegerSymbolModel model = 
				new FreqCountIntegerSymbolModel(symbols, symbol_counts);
		
		// Read in number of symbols encoded

		int num_symbols = 0;
		try {
			num_symbols = bit_source.next(32);
		} catch (InsufficientBitsLeftException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Read in range bit width and setup the decoder

		int range_bit_width = 0;
		try {
			range_bit_width = bit_source.next(8);
		} catch (InsufficientBitsLeftException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArithmeticDecoder<Integer> decoder = new ArithmeticDecoder<Integer>(range_bit_width);

		// Decode and produce output.
		
		System.out.println("Uncompressing file: " + input_file_name);
		System.out.println("Output file: " + output_file_name);
		System.out.println("Range Register Bit Width: " + range_bit_width);
		System.out.println("Number of symbols: " + num_symbols);
		
		FileOutputStream fos = new FileOutputStream(output_file_name);

		for (int i=0; i<num_symbols; i++) {
			int sym = 0;
			try {
				sym = decoder.decode(model, bit_source);
			} catch (InsufficientBitsLeftException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fos.write(sym);
		}

		System.out.println("Done.");
		fos.flush();
		fos.close();
		fis.close();
	}
}
