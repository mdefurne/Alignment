package HipoWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;

public class Mille {
	 
	private static FileOutputStream fos;
	private static BufferedOutputStream out;
	private static DataOutputStream dos;
	private ArrayList<Integer> BufferInt;
	private ArrayList<Float> BufferFloat;
	private int numWordsToWrite;
	private int BufferPos;
	
	public Mille(String dataFile) throws IOException {
		fos = new FileOutputStream(dataFile);
		out = new BufferedOutputStream(fos);
		dos=new DataOutputStream(out);
		BufferPos=-1;
		BufferInt=new ArrayList<Integer>();
		BufferFloat=new ArrayList<Float>();
		numWordsToWrite=0;
		
	}
	
	public void mille(double[] Loc, double[] glob, int[] label, double rMeas, double sigma) throws IOException {
		BufferPos++;
		BufferFloat.add((float)rMeas);
		BufferInt.add(0);
		
		for (int i=0; i<Loc.length; i++) {
			if (Loc[i]!=0) {
				BufferPos++;	
				BufferFloat.add((float)Loc[i]);
				BufferInt.add(i+1);
			}
		}
		
		BufferPos++;
		BufferFloat.add((float)sigma);
		BufferInt.add(0);
		
		for (int i=0; i<glob.length; i++) {
			if (glob[i]!=0) {
				BufferPos++;
				BufferFloat.add((float)glob[i]);
				BufferInt.add(label[i]);
			}
		}
	}
	
	public void end() throws IOException {
		numWordsToWrite=(BufferPos+1)*2;
		dos.write(numWordsToWrite);
		for (int i=0; i<BufferFloat.size();i++) dos.writeFloat(BufferFloat.get(i));
		for (int i=0; i<BufferFloat.size();i++) dos.write(BufferInt.get(i));
		dos.flush();
		out.flush();
		BufferFloat.clear();
		BufferInt.clear();
		BufferPos=-1;
	}
	
	public void kill() {
		BufferPos=-1;
	}
	
	public void newSet() {
		BufferPos=0;
		BufferFloat.add((float) 0.0);
		BufferInt.add(0);
	}
	
	public static void close() throws IOException {
		dos.close();
		out.close();
		fos.close();
	}
	 

}
