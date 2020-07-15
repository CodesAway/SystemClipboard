package info.codesaway.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Utility class to simplify using the system's clipboard.
 *
 * <p>This class wraps functionality available from the {@link Clipboard} class
 * to allow copying and retrieving a string, a list of files, or an image.</p>
 *
 * <p>There exists methods to test if the
 * clipboard is empty, or if not empty, if the clipboard can be represented as a
 * string, text, a list of files, or an
 * image.</p>
 *
 * <p>The {@link #clear()} method (or alternatively, the {@link #empty()}
 * method) provides a way to clear
 * the clipboard, removing its contents.</p>
 */
public class SystemClipboard {
	/**
	 * Never instantiate a utility class
	 *
	 * @throws AssertionError
	 *             always
	 */
	private SystemClipboard() {
		throw new AssertionError();
	}

	/**
	 * Set as the clipboard's contents to clear the clipboard.
	 */
	/*
	 * original source
	 *
	 * http://www.jroller.com/alexRuiz/entry/clearing_the_system_clipboard
	 */
	private static final Transferable empty = new Transferable() {
		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[0];
		}

		@Override
		public boolean isDataFlavorSupported(final DataFlavor flavor) {
			return false;
		}

		@Override
		public Object getTransferData(final DataFlavor flavor)
				throws UnsupportedFlavorException {
			throw new UnsupportedFlavorException(flavor);
		}
	};

	/**
	 * Clears the clipboard.
	 */
	public static void clear() {
		try {
			SystemClipboard.setContents(empty, null);
		} catch (IllegalStateException e) {
		}
	}

	/**
	 * Clears the clipboard. This method is identical to {@link #clear()}.
	 */
	public static void empty() {
		SystemClipboard.clear();
	}

	/**
	 * Returns <code>true</code> if, and only if,
	 * <span style="white-space: nowrap"
	 * >{@link #getAvailableDataFlavors()}.length</span> is 0.
	 *
	 * @return <code>true</code> if <span style="white-space: nowrap">
	 *         {@link #getAvailableDataFlavors()}.length</span> is 0, otherwise
	 *         <code>false</code>
	 */
	public static boolean isEmpty() {
		return SystemClipboard.getAvailableDataFlavors().length == 0;
	}

	/**
	 * You must copy something.
	 *
	 * <p>
	 * This method exists to prevent compiling code which calls
	 * {@link #copy(File...)}, passing zero arguments.
	 * </p>
	 */
	// @SuppressWarnings("unused")
	// private static void copy()
	// {
	// throw new AssertionError();
	// }

	/**
	 * Copies the given character sequence to the clipboard.
	 * {@link DataFlavor#stringFlavor} is the <code>DataFlavor</code> used
	 * for the copy operation.
	 *
	 * <p><b>Note</b>: if <code>text</code> is <code>null</code>, the clipboard
	 * will be {@link #clear() cleared} instead.</p>
	 *
	 * @param text
	 *            the character sequence to copy
	 */
	public static void copy(final CharSequence text) {
		if (text == null) {
			clear();
		} else {
			SystemClipboard.setContents(new StringSelection(text.toString()),
					null);
		}
	}

	/**
	 * Copies the given files to the clipboard.
	 * {@link DataFlavor#javaFileListFlavor} is the <code>DataFlavor</code>
	 * used for the copy operation.
	 *
	 * <p><b>Note</b>: if <code>files</code> is <code>null</code>, the clipboard
	 * will be {@link #clear() cleared} instead.</p>
	 *
	 * @param files
	 *            the <code>File</code>s to copy
	 */
	public static void copy(final File... files) {
		if (files == null) {
			clear();
		} else {
			SystemClipboard.setContents(new FilesSelection(files), null);
		}
	}

	/**
	 * Copies the given files to the clipboard.
	 * {@link DataFlavor#javaFileListFlavor} is the <code>DataFlavor</code>
	 * used for the copy operation.
	 *
	 * <p><b>Note</b>: if <code>files</code> is <code>null</code>, the clipboard
	 * will be {@link #clear() cleared} instead.</p>
	 *
	 * @param files
	 *            the <code>File</code>s to copy
	 */
	public static void copy(final Collection<File> files) {
		if (files == null) {
			clear();
		} else {
			SystemClipboard.setContents(new FilesSelection(files), null);
		}
	}

	/**
	 * Copies the given image to the clipboard. {@link DataFlavor#imageFlavor}
	 * is the <code>DataFlavor</code>
	 * used for the copy operation.
	 *
	 * <p><b>Note</b>: if <code>image</code> is <code>null</code>, the clipboard
	 * will be {@link #clear() cleared} instead.</p>
	 *
	 * @param image
	 *            the <code>Image</code> to copy
	 */
	public static void copy(final Image image) {
		if (image == null) {
			clear();
		} else {
			SystemClipboard.setContents(new ImageSelection(image), null);
		}
	}

	/**
	 * Returns the clipboard's contents as text.
	 *
	 * <p>For a <code>String</code>, this is the string itself. For a
	 * <code>List</code> of <code>File</code>s, it is the list of the filenames,
	 * using the line separator string to delimit each filename.</p>
	 *
	 * <p>The line separator string is defined by the system property
	 * <code>line.separator</code>, and is not necessarily a single newline
	 * character (<code>'\n'</code>).</p>
	 *
	 * @return the clipboard's contents as text, or <code>null</code> if the
	 *         clipboard's contents cannot be expressed as text.
	 * @see #asString()
	 * @see #asFilenames()
	 * @see System#getProperty(String)
	 */
	public static String asText() {
		String stringText = asString();

		if (stringText != null) {
			return stringText;
		}

		String filenamesText = asFilenames();

		if (filenamesText != null) {
			return filenamesText;
		}

		return null;
	}

	/**
	 * Returns whether the clipboard's contents can be represented as text.
	 *
	 * @return <code>true</code> if the current clipboard contents can be
	 *         provided as either {@link DataFlavor#stringFlavor} or
	 *         {@link DataFlavor#javaFileListFlavor}; <code>false</code>
	 *         otherwise
	 */
	public static boolean isText() {
		return SystemClipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)
				||
				SystemClipboard
						.isDataFlavorAvailable(DataFlavor.javaFileListFlavor);
	}

	/**
	 * Returns the clipboard's contents
	 * with the {@link DataFlavor#stringFlavor}.
	 *
	 * @return the clipboard's contents if they can be expressed as a
	 *         <code>String</code>, or <code>null</code> if they cannot.
	 */
	public static String asString() {
		return (String) getClipboard(DataFlavor.stringFlavor);
	}

	/**
	 * Returns whether the clipboard's contents can be represented as a
	 * <code>String</code>.
	 *
	 * @return <code>true</code> if the current clipboard contents can be
	 *         provided as {@link DataFlavor#stringFlavor}; <code>false</code>
	 *         otherwise
	 */
	public static boolean isString() {
		return SystemClipboard.isDataFlavorAvailable(DataFlavor.stringFlavor);
	}

	/**
	 * Returns the clipboard's contents
	 * with the {@link DataFlavor#javaFileListFlavor}.
	 *
	 * <p><b>Note</b>: the returned list cannot be modified - an
	 * {@link UnsupportedOperationException} will be thrown if any attempt is
	 * made. This is done to prevent modification to the clipboard's internal
	 * data, since such modifications <b>do not</b> change the clipboard.
	 * However, you can create a new list containing the returned files, modify
	 * this new list, and copy it at a later time - this will set the clipboard
	 * (to the new list's contents).</p>
	 *
	 * @return the clipboard's contents if they can be expressed as a
	 *         <code>List</code> of
	 *         <code>File</code>s, or <code>null</code> if they cannot.
	 */
	@SuppressWarnings("unchecked")
	public static List<File> asFiles() {
		return (List<File>) getClipboard(DataFlavor.javaFileListFlavor);
	}

	/**
	 * Returns whether the clipboard's contents can be represented as a
	 * <code>List</code> of <code>File</code>s.
	 *
	 * @return <code>true</code> if the current clipboard contents can be
	 *         provided as {@link DataFlavor#javaFileListFlavor};
	 *         <code>false</code>
	 *         otherwise
	 */
	public static boolean isFiles() {
		return SystemClipboard
				.isDataFlavorAvailable(DataFlavor.javaFileListFlavor);
	}

	/**
	 * Returns the clipboard's contents with the
	 * {@link DataFlavor#javaFileListFlavor} as a list of filenames, using the
	 * line separator string to delimit each filename.
	 *
	 * <p>The line separator string is defined by the system property
	 * <code>line.separator</code>, and is not necessarily a single newline
	 * character (<code>'\n'</code>).</p>
	 *
	 * @return the clipboard's contents if they can be expressed as a
	 *         list of filenames, or <code>null</code> if
	 *         they cannot.
	 *
	 * @see System#getProperty(String)
	 */
	public static String asFilenames() {
		return asFilenames(System.getProperty("line.separator"));
	}

	/**
	 * Returns the clipboard's contents with the
	 * {@link DataFlavor#javaFileListFlavor} as a list of filenames, using the
	 * specified delimiter to delimit each filename.
	 *
	 * @param delimiter
	 *            the string used to delimit each filename
	 *
	 * @return the clipboard's contents if they can be expressed as a
	 *         list of filenames, or <code>null</code> if
	 *         they cannot.
	 */
	public static String asFilenames(final String delimiter) {
		List<File> files = asFiles();

		if (files == null) {
			return null;
		}

		StringBuilder filesText = new StringBuilder();

		for (File file : files) {
			if (filesText.length() != 0) {
				filesText.append(delimiter);
			}

			filesText.append(file);
		}

		return filesText.toString();
	}

	/**
	 * Returns the clipboard's contents
	 * with the {@link DataFlavor#imageFlavor}.
	 *
	 * @return the clipboard's contents if they can be expressed as an
	 *         <code>Image</code>, or <code>null</code> if they cannot.
	 */
	public static Image asImage() {
		return (Image) getClipboard(DataFlavor.imageFlavor);
	}

	/**
	 * Returns whether the clipboard's contents can be represented as an
	 * <code>Image</code>.
	 *
	 * @return <code>true</code> if the current clipboard contents can be
	 *         provided as {@link DataFlavor#imageFlavor}; <code>false</code>
	 *         otherwise
	 */
	public static boolean isImage() {
		return SystemClipboard.isDataFlavorAvailable(DataFlavor.imageFlavor);
	}

	/**
	 * Returns the system clipboard.
	 *
	 * @return the system clipboard
	 */
	public static java.awt.datatransfer.Clipboard clipboard() {
		return Toolkit.getDefaultToolkit().getSystemClipboard();
	}

	/**
	 * Registers the specified <code>FlavorListener</code> to receive
	 * <code>FlavorEvent</code>s from the system clipboard.
	 * If <code>listener</code> is <code>null</code>, no exception
	 * is thrown and no action is performed.
	 *
	 * @param listener
	 *            the listener to be added
	 *
	 * @see #removeFlavorListener
	 * @see #getFlavorListeners
	 * @see FlavorListener
	 * @see FlavorEvent
	 */
	public static void addFlavorListener(final FlavorListener listener) {
		clipboard().addFlavorListener(listener);
	}

	/**
	 * Returns an array of <code>DataFlavor</code>s in which the current
	 * contents of the system clipboard can be provided. If there are no
	 * <code>DataFlavor</code>s available, this method returns a zero-length
	 * array.
	 *
	 * @return an array of <code>DataFlavor</code>s in which the current
	 *         contents of the system clipboard can be provided
	 *
	 * @throws IllegalStateException
	 *             if the system clipboard is currently unavailable
	 */
	public static DataFlavor[] getAvailableDataFlavors() {
		return clipboard().getAvailableDataFlavors();
	}

	/**
	 * Returns a transferable object representing the current contents
	 * of the system clipboard. If the clipboard currently has no contents,
	 * it returns <code>null</code>. The parameter Object requestor is
	 * not currently used. The method throws
	 * <code>IllegalStateException</code> if the clipboard is currently
	 * unavailable. For example, on some platforms, the system clipboard is
	 * unavailable while it is accessed by another application.
	 *
	 * @param requestor
	 *            the object requesting the clip data (not used)
	 * @return the current transferable object on the clipboard
	 * @throws IllegalStateException
	 *             if the clipboard is currently unavailable
	 * @see java.awt.Toolkit#getSystemClipboard
	 */
	public static Transferable getContents(final Object requestor) {
		return clipboard().getContents(requestor);
	}

	/**
	 * Returns the system clipboard's contents in the requested
	 * <code>DataFlavor</code> if possible.
	 *
	 * @param flavor
	 *            the requested flavor for the data
	 * @return the data in the requested flavor, or <code>null</code> if
	 *         the data doesn't support the given <code>DataFlavor</code>
	 * @throws NullPointerException
	 *             if <code>flavor</code> is <code>null</code>
	 */
	/*
	 * modified (slightly) from original
	 *
	 * http://www.devx.com/Java/Article/22326/0/page/3
	 */
	public static Object getClipboard(final DataFlavor flavor) {
		// get the contents on the clipboard in a transferable object
		Transferable clipboardContents = SystemClipboard.getContents(null);

		// check if clipboard is empty
		if (clipboardContents == null) {
			return null;
		} else {
			try {
				if (clipboardContents.isDataFlavorSupported(flavor)) {
					return clipboardContents.getTransferData(flavor);
				}
			} catch (UnsupportedFlavorException ufe) {
				// ufe.printStackTrace();
			} catch (IOException ioe) {
				// ioe.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Returns an object representing the current contents of the system
	 * clipboard in the specified <code>DataFlavor</code>.
	 * The class of the object returned is defined by the representation
	 * class of <code>flavor</code>.
	 *
	 * @param flavor
	 *            the requested <code>DataFlavor</code> for the contents
	 *
	 * @return an object representing the current contents of the system
	 *         clipboard in the specified <code>DataFlavor</code>
	 *
	 * @throws NullPointerException
	 *             if <code>flavor</code> is <code>null</code>
	 * @throws IllegalStateException
	 *             if this clipboard is currently unavailable
	 * @throws UnsupportedFlavorException
	 *             if the requested <code>DataFlavor</code>
	 *             is not available
	 * @throws IOException
	 *             if the data in the requested <code>DataFlavor</code>
	 *             can not be retrieved
	 *
	 * @see DataFlavor#getRepresentationClass
	 */
	public static Object getData(final DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		return clipboard().getData(flavor);
	}

	/**
	 * Returns an array of all the <code>FlavorListener</code>s currently
	 * registered on the system clipboard.
	 *
	 * @return all of the system clipboard's <code>FlavorListener</code>s or an
	 *         empty array if no listeners are currently registered
	 * @see #addFlavorListener
	 * @see #removeFlavorListener
	 * @see FlavorListener
	 * @see FlavorEvent
	 */
	public static FlavorListener[] getFlavorListeners() {
		return clipboard().getFlavorListeners();
	}

	/**
	 * Returns the name of the system clipboard.
	 *
	 * @return the name of the system clipboard
	 *
	 * @see java.awt.Toolkit#getSystemClipboard
	 */
	public static String getName() {
		return clipboard().getName();
	}

	/**
	 * Returns whether or not the current contents of the system clipboard can
	 * be provided in the specified <code>DataFlavor</code>.
	 *
	 * @param flavor
	 *            the requested <code>DataFlavor</code> for the contents
	 *
	 * @return <code>true</code> if the current contents of the system clipboard
	 *         can be provided in the specified <code>DataFlavor</code>;
	 *         <code>false</code> otherwise
	 *
	 * @throws NullPointerException
	 *             if <code>flavor</code> is <code>null</code>
	 * @throws IllegalStateException
	 *             if the system clipboard is currently unavailable
	 */
	public static boolean isDataFlavorAvailable(final DataFlavor flavor) {
		return clipboard().isDataFlavorAvailable(flavor);
	}

	/**
	 * Removes the specified <code>FlavorListener</code> so that it no longer
	 * receives <code>FlavorEvent</code>s from the system clipboard.
	 * This method performs no function, nor does it throw an exception, if
	 * the listener specified by the argument was not previously added to the
	 * system clipboard.
	 * If <code>listener</code> is <code>null</code>, no exception
	 * is thrown and no action is performed.
	 *
	 * @param listener
	 *            the listener to be removed
	 *
	 * @see #addFlavorListener
	 * @see #getFlavorListeners
	 * @see FlavorListener
	 * @see FlavorEvent
	 */
	public static void removeFlavorListener(final FlavorListener listener) {
		clipboard().removeFlavorListener(listener);
	}

	/**
	 * Sets the current contents of the system clipboard to the specified
	 * transferable object and registers the specified clipboard owner
	 * as the owner of the new contents.
	 * <p>
	 * If there is an existing owner different from the argument
	 * <code>owner</code>, that owner is notified that it no longer
	 * holds ownership of the clipboard contents via an invocation
	 * of <code>ClipboardOwner.lostOwnership()</code> on that owner.
	 * An implementation of <code>setContents()</code> is free not
	 * to invoke <code>lostOwnership()</code> directly from this method.
	 * For example, <code>lostOwnership()</code> may be invoked later on
	 * a different thread. The same applies to <code>FlavorListener</code>s
	 * registered on this clipboard.
	 * <p>
	 * The method throws <code>IllegalStateException</code> if the clipboard
	 * is currently unavailable. For example, on some platforms, the system
	 * clipboard is unavailable while it is accessed by another application.
	 *
	 * @param contents
	 *            the transferable object representing the
	 *            clipboard content
	 * @param owner
	 *            the object which owns the clipboard content
	 * @throws IllegalStateException
	 *             if the system clipboard is currently unavailable
	 * @see java.awt.Toolkit#getSystemClipboard
	 */
	public static void setContents(final Transferable contents, final ClipboardOwner owner) {
		clipboard().setContents(contents, owner);
	}

	/**
	 * A <code>Transferable</code> which implements the capability required
	 * to transfer a <code>List</code> of <code>File</code>s.
	 *
	 * <p>
	 * This <code>Transferable</code> properly supports
	 * {@link DataFlavor#javaFileListFlavor} and all equivalent flavors. No
	 * other
	 * <code>DataFlavor</code>s are supported.</p>
	 */
	private static class FilesSelection implements Transferable {
		/**
		 * the <code>List&lt;File&gt;</code> object which will be housed by this
		 * <code>FilesSelection</code>
		 */
		private final List<File> files;

		/**
		 * Creates a <code>Transferable</code> capable of transferring
		 * the specified <code>File</code>s.
		 *
		 * @param files
		 *            the files to transfer.
		 */
		public FilesSelection(final File... files) {
			List<File> tmp = new ArrayList<>(files.length);

			for (File file : files) {
				tmp.add(file);
			}

			this.files = Collections.unmodifiableList(tmp);
		}

		/**
		 * Creates a <code>Transferable</code> capable of transferring
		 * the specified <code>File</code>s.
		 *
		 * @param files
		 *            the files to transfer
		 */
		public FilesSelection(final Collection<? extends File> files) {
			List<File> tmp = new ArrayList<>(files);
			this.files = Collections.unmodifiableList(tmp);
		}

		/**
		 * Returns an array of flavors in which this <code>Transferable</code>
		 * can provide the data.
		 *
		 * <p>Only {@link DataFlavor#javaFileListFlavor} is supported.</p>
		 *
		 * @return an array of length one, whose element is
		 *         <code>DataFlavor.javaFileListFlavor</code>
		 */
		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] { DataFlavor.javaFileListFlavor };
		}

		/**
		 * Returns whether the requested flavor is supported by this
		 * <code>Transferable</code>.
		 *
		 * @param flavor
		 *            the requested flavor for the data
		 * @return true if <code>flavor</code> is equal to
		 *         <code>DataFlavor.javaFileListFlavor</code>; false otherwise
		 * @throws NullPointerException
		 *             if flavor is <code>null</code>
		 */
		@Override
		public boolean isDataFlavorSupported(final DataFlavor flavor) {
			return flavor.equals(DataFlavor.javaFileListFlavor);
		}

		/**
		 * Returns the <code>List&lt;File&gt;</code> object housed by this
		 * <code>FilesSelection</code>
		 *
		 * @param flavor
		 *            the requested flavor for the data
		 * @return the data in the requested flavor
		 * @throws UnsupportedFlavorException
		 *             if the requested data flavor is not equivalent to
		 *             <code>DataFlavor.javaFileListFlavor</code>
		 * @throws NullPointerException
		 *             if flavor is <code>null</code>
		 */
		@Override
		public List<File> getTransferData(final DataFlavor flavor)
				throws UnsupportedFlavorException {
			if (flavor.equals(DataFlavor.javaFileListFlavor)) {
				return this.files;
			} else {
				throw new UnsupportedFlavorException(flavor);
			}
		}
	}

	/**
	 * A <code>Transferable</code> which implements the capability required
	 * to transfer an <code>Image</code>.
	 *
	 * <p>
	 * This <code>Transferable</code> properly supports
	 * {@link DataFlavor#imageFlavor} and all equivalent flavors. No
	 * other
	 * <code>DataFlavor</code>s are supported.</p>
	 */
	/*
	 * modified (slightly) from original
	 *
	 * http://www.devx.com/Java/Article/22326/1954
	 */
	private static class ImageSelection implements Transferable {
		/**
		 * the <code>Image</code> object which will be housed by this
		 * <code>ImageSelection</code>
		 */
		private final Image image;

		/**
		 * Creates a <code>Transferable</code> capable of transferring
		 * the specified <code>Image</code>.
		 *
		 * @param image
		 *            the image to transfer.
		 */
		public ImageSelection(final Image image) {
			this.image = image;
		}

		/**
		 * Returns an array of flavors in which this <code>Transferable</code>
		 * can provide the data.
		 *
		 * <p>Only {@link DataFlavor#imageFlavor} is supported.</p>
		 *
		 * @return an array of length one, whose element is
		 *         <code>DataFlavor.imageFlavor</code>
		 */
		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] { DataFlavor.imageFlavor };
		}

		/**
		 * Returns whether the requested flavor is supported by this
		 * <code>Transferable</code>.
		 *
		 * @param flavor
		 *            the requested flavor for the data
		 * @return true if <code>flavor</code> is equal to
		 *         <code>DataFlavor.imageFlavor</code>; false otherwise
		 * @throws NullPointerException
		 *             if flavor is <code>null</code>
		 */
		@Override
		public boolean isDataFlavorSupported(final DataFlavor flavor) {
			return flavor.equals(DataFlavor.imageFlavor);
		}

		/**
		 * Returns the <code>Image</code> object housed by this
		 * <code>ImageSelection</code>
		 *
		 * @param flavor
		 *            the requested flavor for the data
		 * @return the data in the requested flavor
		 * @throws UnsupportedFlavorException
		 *             if the requested data flavor is not equivalent to
		 *             <code>DataFlavor.imageFlavor</code>
		 * @throws NullPointerException
		 *             if flavor is <code>null</code>
		 */
		@Override
		public Image getTransferData(final DataFlavor flavor)
				throws UnsupportedFlavorException {
			if (flavor.equals(DataFlavor.imageFlavor)) {
				return this.image;
			} else {
				throw new UnsupportedFlavorException(flavor);
			}
		}
	}
}