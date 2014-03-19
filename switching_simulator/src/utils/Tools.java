package utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

public class Tools {
	
	private Tools(){
		//Not-instantiable class: only static methods
	}
	
	public static InformationSet loadFile(String folder, String name) throws FileNotFoundException{
		
		InformationSet result = new InformationSet();
		File file = new File("Data"+File.separator+folder+File.separator+name);
		if(!file.exists()){
			throw new FileNotFoundException("Missing specified input file");
		}
		FileInputStream stream = new FileInputStream(file);
		DataInputStream inputStream = new DataInputStream(stream);
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		String line = null;
		try {
			while((line = br.readLine())!=null){
				String[] couple = line.split(" ");
				result.addSample(new Date(Long.valueOf(couple[1])), Double.valueOf(couple[0]));
				//System.out.println(Long.valueOf(couple[1])+ " " + Double.valueOf(couple[0]));
			}
			br.close();
			return result;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} 
	}

}
