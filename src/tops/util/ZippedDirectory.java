/*
   Copyright 2012 Marc Lijour
    This file is part of TOPSMDB.

    TOPSMDB is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
  
    TOPSMDB is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package tops.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;


public class ZippedDirectory {
	private static Logger logger = Logger.getLogger(ZippedDirectory.class);
	private File dir2zip = null;
	private File targetZip = null;
	private List<File> fileList = null;
	
	
	public ZippedDirectory(File dir2zip, File targetZip) {
		this.dir2zip = dir2zip;
		this.targetZip = targetZip;		
		
		// fill array with all files contained in the directory to zip
		fileList = new ArrayList<File>();
		ls(dir2zip, fileList);
	}

	
	public File create() throws IOException {
		ZipOutputStream zos = null;
		
		try {
			zos = new ZipOutputStream(new FileOutputStream(targetZip));
			//logger.debug("Starting to zip to " + targetZip.getAbsolutePath());
			
			for(File file : fileList)
				if(!file.isDirectory())
					addToZip(zos, file);
					
		} catch (IOException e) {
			logger.error(e.getMessage());
			
		}finally{
			try {
				zos.close();
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
		
		if(!targetZip.exists())
			throw new IOException("Unable to create zip file " + targetZip.getName());
		
		return targetZip;
	}

	private void addToZip(ZipOutputStream zos, File file) throws FileNotFoundException, IOException {
		FileInputStream fis = new FileInputStream(file);
		// relative path
		String filePath = file.getCanonicalPath().substring(dir2zip.getCanonicalPath().length() + 1,
				file.getCanonicalPath().length());
		
		zos.putNextEntry(new ZipEntry(filePath));
		byte[] buf = new byte[1024];
		int len;
        while ((len = fis.read(buf)) > 0)
           zos.write(buf, 0, len);
        
        zos.flush();
        zos.closeEntry();
		fis.close();
	}


	private void ls(File dir2zip2, List<File> fileList) {
		File[] files = dir2zip2.listFiles();
		for(File file : files) {
			fileList.add(file);
			if(file.isDirectory())
				ls(file, fileList);
		}
	}
	
}
