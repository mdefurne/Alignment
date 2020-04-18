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
	private boolean GoodToGo;
	
	public Mille(String dataFile) throws IOException {
		fos = new FileOutputStream(dataFile);
		out = new BufferedOutputStream(fos);
		dos=new DataOutputStream(out);
		BufferPos=-1;
		BufferInt=new ArrayList<Integer>();
		BufferFloat=new ArrayList<Float>();
		numWordsToWrite=0;
		GoodToGo=true;
	}
	
	public void mille(double[] Loc, double[] glob, int[] label, double rMeas, double sigma) throws IOException {
		BufferPos++;
		BufferFloat.add((float)rMeas);
		BufferInt.add(0);
		if (rMeas==Double.NaN||(float)rMeas==Float.NaN) GoodToGo=false;
		
		for (int i=0; i<Loc.length; i++) {
			if (Loc[i]!=0&&Loc[i]!=Double.NaN&&(float)Loc[i]!=Float.NaN&&Math.abs(Loc[i])<10000) {
				BufferPos++;	
				BufferFloat.add((float)Loc[i]);
				BufferInt.add(i+1);
			}
			else GoodToGo=false;
		}
		
		BufferPos++;
		BufferFloat.add((float)sigma);
		BufferInt.add(0);
		if (sigma==Double.NaN||(float)sigma==Float.NaN) GoodToGo=false;
		
		for (int i=0; i<glob.length; i++) {
			if (glob[i]!=0&&glob[i]!=Double.NaN&&(float)glob[i]!=Float.NaN&&Math.abs(glob[i])<10000) {
				BufferPos++;
				BufferFloat.add((float)glob[i]);
				BufferInt.add(label[i]);
			}
			if (glob[i]==Double.NaN||(float)glob[i]==Float.NaN||Math.abs(glob[i])>10000) GoodToGo=false;
		}
	}
	
	public void end() throws IOException {
		if (GoodToGo) {
			numWordsToWrite=(BufferPos+1)*2;
			writeIntLE(numWordsToWrite);
			for (int i=0; i<BufferFloat.size();i++)	{
				if (BufferFloat.get(i)==Float.NaN) PrintNaNError();
				writeFloatLE(BufferFloat.get(i));
			}
			for (int i=0; i<BufferInt.size();i++) {
				if (BufferInt.get(i)<0) PrintNaNError();
				writeIntLE(BufferInt.get(i));
			}
					
			dos.flush();
			out.flush();
		}
		this.kill();
	}
	
	public void kill() {
		BufferPos=-1;
		BufferFloat.clear();
		BufferInt.clear();
		GoodToGo=true;
	}
	
	public void newSet() {
		BufferPos=0;
		BufferFloat.add((float) 0.0);
		BufferInt.add(0);
		GoodToGo=true;
	}
	
	public static void close() throws IOException {
		dos.close();
		out.close();
		fos.close();
	}
	
	public static void writeIntLE(int value) throws IOException {
		  dos.writeByte(value & 0xFF);
		  dos.writeByte((value >> 8) & 0xFF);
		  dos.writeByte((value >> 16) & 0xFF);
		  dos.writeByte((value >> 24) & 0xFF);
	}
	
	public static void writeFloatLE(float value) throws IOException {
		writeIntLE(Float.floatToRawIntBits(value));
	}
	
	public void PrintNaNError() {
		System.out.println("/////////////////////////////////////////////////////");
		System.out.println("/////////////////////////////////////////////////////");
		System.out.println("/////////////////////////////////////////////////////");
		System.out.println("/////////////////////////////////////////////////////");
		System.out.println("/////////////////////////////////////////////////////");
		System.out.println("/////////////////////////////////////////////////////");
		System.out.println("/////////////////////////////////////////////////////");
		System.out.println("/////////////////////////////////////////////////////");
		System.out.println("ATTEENNTTIOONNN................ NaN written!!!!!!!");
		System.out.println(numWordsToWrite);
		for (int i=0; i<BufferFloat.size();i++) {
			if (i<BufferFloat.size()-1) System.out.print(BufferFloat.get(i)+" ");
			else System.out.println(BufferFloat.get(i));
		}
		for (int i=0; i<BufferInt.size();i++) {
			if (i<BufferInt.size()-1) System.out.print(BufferInt.get(i)+" ");
			else System.out.println(BufferInt.get(i));
		}
		System.out.println("/////////////////////////////////////////////////////");
		System.out.println("/////////////////////////////////////////////////////");
		System.out.println("/////////////////////////////////////////////////////");
		System.out.println("/////////////////////////////////////////////////////");
		System.out.println("/////////////////////////////////////////////////////");
		System.out.println("/////////////////////////////////////////////////////");
		System.out.println("/////////////////////////////////////////////////////");
		System.out.println("/////////////////////////////////////////////////////");
	}
	 

}
