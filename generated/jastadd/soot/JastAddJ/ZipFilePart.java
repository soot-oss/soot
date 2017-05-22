package soot.JastAddJ;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.zip.*;
import java.io.*;

/**
 * Loads class files from a zip file (Jar file)
 * @ast class
 * 
 */
public class ZipFilePart extends PathPart {

	private Set<String> set;
	private File file;

	public boolean hasPackage(String name) {
		return set.contains(name);
	}
	
	public ZipFilePart(File file) throws IOException {
		this.set = new HashSet<String>();
		this.file = file;
		ZipFile zipFile = null;
		try{
			zipFile = new ZipFile(file);
			// process all entries in the zip file
			for (Enumeration<? extends ZipEntry> e = zipFile.entries() ; e.hasMoreElements() ;) {
				ZipEntry entry = (ZipEntry)e.nextElement();
				String pathName = new File(entry.getName()).getParent();
				if(pathName != null)
					pathName = pathName.replace(File.separatorChar, '.');
				if(!set.contains(pathName)) {
					int pos = 0;
					while(pathName != null && -1 != (pos = pathName.indexOf('.', pos + 1))) {
						String n = pathName.substring(0, pos);
						if(!set.contains(n)) {
							set.add(n);
						}
					}
					set.add(pathName);
				}
				set.add(entry.getName());
			}
		} finally {
			if(zipFile != null)
				zipFile.close();
		}
	}

	public boolean selectCompilationUnit(String canonicalName) throws IOException {
		ZipFile zipFile = null;
		boolean success = false;
		try{
			zipFile = new ZipFile(file);
			String name = canonicalName.replace('.', '/'); // ZipFiles always use '/' as separator
			name = name + fileSuffix();
			if(set.contains(name)) {
				ZipEntry zipEntry = zipFile.getEntry(name);
				if(zipEntry != null && !zipEntry.isDirectory()) {
					is = new ZipEntryInputStreamWrapper(zipFile,zipEntry);
					age = zipEntry.getTime();
					pathName = zipFile.getName();
					relativeName = name + fileSuffix();
					fullName = canonicalName;
					success = true;
				}
			}
		} finally {
			if(zipFile != null && !success)
				zipFile.close();
		}
		return success;
	}
	
	/** Wrapper class for the input stream of a ZipFile entry. All methods are passed through
	 * to the underlining ZipFile entry input stream. However, close has the additional 
	 * functionality of ensuring that the ZipFile is closed along with the input stream
	 * when close is called.
	 */
	public static class ZipEntryInputStreamWrapper extends InputStream {

		private ZipFile zipFile;
		private InputStream entryInputStream;
		
		public ZipEntryInputStreamWrapper(ZipFile zipFile, ZipEntry zipEntry) throws IOException {
			this.zipFile = zipFile;
			this.entryInputStream = zipFile.getInputStream(zipEntry);
		}

		@Override
		public int read() throws IOException {
			return entryInputStream.read();
		}
		
		@Override
		public int read(byte[] b) throws IOException {
			return entryInputStream.read(b);
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			return entryInputStream.read(b, off, len);
		}

		@Override
		public long skip(long n) throws IOException {
			return entryInputStream.skip(n);
		}

		@Override
		public int available() throws IOException {
			return entryInputStream.available();
		}

		@Override
		public void close() throws IOException {
			try{
				entryInputStream.close();
				//Don't set to null so calls after close will still pass to the InputStream
			} finally {
				if(zipFile != null){
					zipFile.close();
					zipFile = null;
				}
			}
		}

		@Override
		public synchronized void mark(int readlimit) {
			entryInputStream.mark(readlimit);
		}

		@Override
		public synchronized void reset() throws IOException {
			entryInputStream.reset();
		}

		@Override
		public boolean markSupported() {
			return entryInputStream.markSupported();
		}
		
	}

}
