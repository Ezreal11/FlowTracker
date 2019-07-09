package edu.nju.ics.frontier.util;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;

public class VirtualFileUtil {
    /**
     * get document from a virtual file
     * @param file a virtual file
     * @return the corresponding document
     */
    public static Document getDocument(VirtualFile file) {
        if (file == null || (!file.exists())) {
            return null;
        }
        return FileDocumentManager.getInstance().getDocument(file);
    }

    /**
     * get the virtual file from a document
     * @param document a document
     * @return the corresponding virtual file
     */
    public static VirtualFile getFile(Document document) {
        if (document == null) {
            return null;
        }
        return FileDocumentManager.getInstance().getFile(document);
    }

    /**
     * get the MD5-encoded path of a virtual file
     * @param file a virtual file
     * @return the MD5-encoded path of the virtual file
     */
    public static String getPath(VirtualFile file) {
        boolean exist = (file == null) ? false : file.exists();
        String path = exist ? file.getPath() : null;
        return (path == null) ? path : StringUtil.md5Encode(path);
    }

    /**
     * get the length of a virtual file
     * @param file a virtual file
     * @return the length of a virtual file
     */
    public static long getFileLength(VirtualFile file) {
        boolean exist = (file == null) ? false : file.exists();
        long fileLen = exist ? file.getLength() : -1;
        return fileLen;
    }

    /**
     * get the string form of the length of a virtual file
     * @param file a virtual file
     * @return the string form of the length of the virtual file
     */
    public static String getFileLengthString(VirtualFile file) {
        long fileLen = getFileLength(file);
        String fileLenStr = (fileLen == -1) ? null : String.valueOf(fileLen);
        return fileLenStr;
    }

    /**
     * get the length of a document
     * @param document a document
     * @return the length of the document
     */
    public static int getDocumentLength(Document document) {
        int docLen = (document == null) ? -1 : document.getTextLength();
        return docLen;
    }

    /**
     * get the string form of the length of a document
     * @param document a document
     * @return the string form of the length of the document
     */
    public static String getDocumentLengthString(Document document) {
        int docLen = getDocumentLength(document);
        String docLenStr = (docLen == -1) ? null : String.valueOf(docLen);
        return docLenStr;
    }

    /**
     * get the line of code of a document
     * @param document a document
     * @return the line of code of a document
     */
    public static int getDocumentLine(Document document) {
        int line = (document == null) ? -1 : document.getLineCount();
        return line;
    }

    /**
     * get the string form of the line of code of a document
     * @param document a document
     * @return the string form of the line of code of a document
     */
    public static String getDocumentLineString(Document document) {
        int line = getDocumentLine(document);
        String lineStr = (line == -1) ? null : String.valueOf(line);
        return lineStr;
    }
}
