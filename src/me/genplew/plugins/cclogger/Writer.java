package me.genplew.plugins.cclogger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Writer {

	private CCLogger plugin;

	public Writer(CCLogger plugins) {
		this.plugin = plugins;
	}

	public void createFile(File file, String[] i) {
		BufferedWriter buffwriter = null;
		FileWriter filewriter = null;
		try {
			file.createNewFile();
			filewriter = new FileWriter(file, true);
			buffwriter = new BufferedWriter(filewriter);

			for (String string : i) {
				buffwriter.write(string);
				buffwriter.newLine();
			}

			buffwriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (buffwriter != null) {
					buffwriter.close();
				}
				if (filewriter != null) {
					filewriter.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void writeFile(String[] i, File type) {
		File log = type;
		BufferedWriter buffwriter = null;
		FileWriter filewriter = null;
		try {
			filewriter = new FileWriter(log, true);
			buffwriter = new BufferedWriter(filewriter);

			for (String s : i) {
				buffwriter.write(s);
				buffwriter.newLine();
			}

			buffwriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}